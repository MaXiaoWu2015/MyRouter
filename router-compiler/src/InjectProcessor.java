
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import inject.Inject;
import inject.InjectUriParam;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
原理:
 编译时注解的核心依赖APT(Annotation Processing Tools)实现，
 原理是在某些代码元素上（如类型、函数、字段等）添加注解，在编译
 时编译器会检查AbstractProcessor的子类，并且调用该类型的process函数，
 然后将添加了注解的所有元素都传递到process函数中，使得开发人员可以在编译器进行相应的处理，
 例如，根据注解生成新的Java类，这也就是EventBus，Retrofit，Dagger等开源库的基本原理。



  @SupportedAnnotationTypes("inject.*") 当前类支持的注解类的路径
  @SupportedSourceVersion(SourceVersion.RELEASE_0) 支持的源码版本

 * */
@SupportedAnnotationTypes("inject.*")
@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor{


    public static final List<Class<? extends Annotation>>  ANNOTATIONS = Arrays.asList(InjectUriParam.class, Inject.class);


    private Filer filer;
    private Elements elementUtils;
    private TypeTools typeTools;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        typeTools = new TypeTools(elementUtils,processingEnv.getTypeUtils());

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        //解析注解
        //targetClassMap:类--->被注解的属性生成的代码
        Map<TypeElement,TargetClass> targetClassMap = findAndParseTargets(roundEnv);

        //解析完成后,生成的代码结构就有了,将其生成文件
        for (Map.Entry<TypeElement,TargetClass> entry:targetClassMap.entrySet()) {

            TypeElement element = entry.getKey();
            TargetClass targetClass = entry.getValue();

            JavaFile javaFile = targetClass.brewJava();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private Map<TypeElement, TargetClass> findAndParseTargets(RoundEnvironment roundEnv) {

        Map<TypeElement, TargetClass> targetClassMap = new LinkedHashMap<>();

        for (Class<? extends Annotation> annotationClazz:ANNOTATIONS){
            //找到所有被(支持的)注解的属性(Field)元素(Element代表语言元素,比如包、方法、类、属性等)
            for (Element element:roundEnv.getElementsAnnotatedWith(annotationClazz)){
//                if (!SuperficialValidation.validateElement(element)) continue;
                parseInjectParam(element,targetClassMap,annotationClazz);
            }


        }
        return targetClassMap;
    }

    private void parseInjectParam(Element element, Map<TypeElement, TargetClass> targetClassMap, Class<? extends Annotation> annotationClazz) {
        //对于被注解的参数进行验证(不能是private or static)
        boolean hasError = isInaccessibleViaGeneratedCode(annotationClazz,"field",element) || isInjectingInWrongPackage(annotationClazz,element);

        if (hasError){
            return;
        }

        TargetClass targetClass = getOrCreateTargetClass(targetClassMap,element);

        String paramkey = "";
        if (annotationClazz.equals(Inject.class)){
            paramkey = element.getAnnotation(Inject.class).value();
        }else {
            paramkey = element.getAnnotation(InjectUriParam.class).value();
        }

        if (paramkey.length() == 0){
            paramkey = element.getSimpleName().toString();
        }

        FieldInjecting fieldInjecting = new FieldInjecting(element.getSimpleName().toString()
        ,element.asType(),paramkey,annotationClazz);

        targetClass.addField(fieldInjecting);


    }

    private TargetClass getOrCreateTargetClass(Map<TypeElement, TargetClass> targetClassMap, Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        TargetClass targetClass = targetClassMap.get(typeElement);
        if (targetClass == null){

            //类上的泛型
            TypeName targetType = TypeName.get(typeElement.asType());


            String packageName = getPackageName(typeElement);
            System.out.println("targetTypeElement packageName:"+packageName);
            String className = getClassName(typeElement,packageName);
            System.out.println("targetTypeElement brew ClassName:"+className);
            ClassName bindingClassName = ClassName.get(packageName,className+"_RouterInjecting");

            targetClass = new TargetClass(typeTools,targetType,bindingClassName);
            targetClassMap.put(typeElement,targetClass);
        }

        return targetClass;

    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getPackageName(TypeElement typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }

    private boolean isInjectingInWrongPackage(Class<? extends Annotation> annotationClazz, Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = typeElement.getQualifiedName().toString();

        if (qualifiedName.startsWith("android.")){
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClazz.getSimpleName(), qualifiedName);
            return true;
        }


        if (qualifiedName.startsWith("java.")){
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClazz.getSimpleName(), qualifiedName);
            return true;
        }

        return false;

    }

    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClazz, String field, Element element) {
        Set<Modifier> modifiers =element.getModifiers();

        //得到的是属性所在的类,getEnclosingElement-----最里层的包围该元素的元素
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        if (modifiers.contains(Modifier.PRIVATE) ||modifiers.contains(Modifier.STATIC)){

            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClazz.getSimpleName(), field, typeElement.getQualifiedName(),
                    element.getSimpleName());

            return true;
        }


        if (typeElement.getKind() != ElementKind.CLASS){
            error(typeElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClazz.getSimpleName(), field, typeElement.getQualifiedName(),
                    element.getSimpleName());

            return true;
        }

        Set<Modifier> modifierSet = typeElement.getModifiers();
        if (modifierSet.contains(Modifier.PRIVATE) ||modifierSet.contains(Modifier.STATIC)){

            error(typeElement, "@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClazz.getSimpleName(), field, typeElement.getQualifiedName(),
                    element.getSimpleName());

            return true;
        }

        return false;
    }


    private void error(Element element,String message,Object...args){
        printMessage(Diagnostic.Kind.ERROR,element,message,args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0 ){
            message = String.format(message,args);
        }
        processingEnv.getMessager().printMessage(kind,message,element);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
