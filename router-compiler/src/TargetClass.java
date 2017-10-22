import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import inject.Inject;
import inject.InjectUriParam;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TargetClass {

    private List<FieldInjecting> fieldInjectings;


    private final TypeTools typeTools;

    private final TypeName targetTypeName;

    private final ClassName injectingClassName;

    public TargetClass(TypeTools typeTools, TypeName targetTypeName, ClassName injectingClassName) {

        fieldInjectings = new ArrayList<>();

        this.typeTools = typeTools;
        this.targetTypeName = targetTypeName;
        this.injectingClassName = injectingClassName;
    }


    /**
     * 生成inject方法代码
     * */
    //TODO:javapoet的使用
    private MethodSpec createInjectMethod(TypeName targetType) {

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetType, "target");

        for (FieldInjecting fieldInjecting : fieldInjectings) {

            if (fieldInjecting.getAnnotation().equals(Inject.class)){
                CodeBlock codeBlock = CodeBlock.builder()
                        .add("target.$L = ", fieldInjecting.getName())
                        .add("target.getIntent().")
                        .add(buildStatement(typeTools.convertType(fieldInjecting.getTypeMirror()), true), fieldInjecting.getParamKey())
                        .build();
                builder.addCode(codeBlock);
            }else if (fieldInjecting.getAnnotation().equals(InjectUriParam.class)){

                if (fieldInjecting.getTypeMirror().toString().equals(Constants.STRING)){
                    CodeBlock codeBlock = CodeBlock.builder()
                            .add("target.$L = ",fieldInjecting.getName())
                            .add("target.getIntent().getData().getQueryParameter($S);",fieldInjecting.getParamKey())
                            .build();
                    builder.addCode(codeBlock);
                } else{
                    //提取的是uri的参数,被注解的字段必须是String类型
                    throw new IllegalArgumentException(fieldInjecting.getName()+"must be a string type");
                }

            }


        }

        return builder.build();
    }
        private String buildStatement(int type, boolean isActivity) {
            String statement = "";
            switch (type) {
                case Constants.TYPE_KIND.TYPE_INTEGER:
                    statement += (isActivity ? ("getIntExtra($S, 0)") : ("getInt($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_LONG:
                    statement += (isActivity ? ("getLongExtra($S, 0)") : ("getLong($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_FLOAT:
                    statement += (isActivity ? ("getFloatExtra($S, 0)") : ("getFloat($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_DOUBLE:
                    statement += (isActivity ? ("getDoubleExtra($S, 0)") : ("getDouble($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_SHORT:
                    statement += (isActivity ? ("getShortExtra($S, (short) 0)") : ("getShort($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_BYTE:
                    statement += (isActivity ? ("getByteExtra($S, (byte) 0)") : ("getByte($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_BOOLEAN:
                    statement += (isActivity ? ("getBooleanExtra($S, false)") : ("getBoolean($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_STRING:
                    statement += (isActivity ? ("getStringExtra($S)") : ("getString($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_CHAR_SEQUENCE:
                    statement += (isActivity ? ("getCharSequenceExtra($S)") : ("getCharSequence($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_PARCELABLE:
                    statement += (isActivity ? ("getParcelableExtra($S)") : ("getParcelable($S)"));
                    break;

                case Constants.TYPE_KIND.TYPE_ARRAY_INT:
                    statement += (isActivity ? ("getIntArrayExtra($S)") : ("getIntArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LONG:
                    statement += (isActivity ? ("getLongArrayExtra($S)") : ("getLongArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_FLOAT:
                    statement += (isActivity ? ("getFloatArrayExtra($S)") : ("getFloatArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_DOUBLE:
                    statement += (isActivity ? ("getDoubleArrayExtra($S)") : ("getDoubleArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_SHORT:
                    statement += (isActivity ? ("getShortArrayExtra($S)") : ("getShortArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_BYTE:
                    statement += (isActivity ? ("getByteArrayExtra($S)") : ("getByteArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_BOOLEAN:
                    statement += (isActivity ? ("getBooleanArrayExtra($S)") : ("getBooleanArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_STRING:
                    statement += (isActivity ? ("getStringArrayExtra($S)") : ("getStringArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_CHAR_SEQUENCE:
                    statement += (isActivity ? ("getCharSequenceArrayExtra($S)") : ("getCharSequenceArray($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_PARCELABLE:
                    statement += (isActivity ? ("getParcelableArrayExtra($S)") : ("getParcelableArray($S)"));
                    break;

                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_INTEGER:
                    statement += (isActivity ? ("getIntegerArrayListExtra($S)") : ("getIntArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_LONG:
                    statement += (isActivity ? ("getLongArrayListExtra($S)") : ("getLongArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_FLOAT:
                    statement += (isActivity ? ("getFloatArrayListExtra($S)") : ("getFloatArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_DOUBLE:
                    statement += (isActivity ? ("getDoubleArrayListExtra($S)") : ("getDoubleArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_SHORT:
                    statement += (isActivity ? ("getShortArrayListExtra($S)") : ("getShortArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_BYTE:
                    statement += (isActivity ? ("getByteArrayListExtra($S)") : ("getByteArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_BOOLEAN:
                    statement += (isActivity ? ("getBooleanArrayListExtra($S)") : ("getBooleanArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_STRING:
                    statement += (isActivity ? ("getStringArrayListExtra($S)") : ("getStringArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_CHAR_SEQUENCE:
                    statement += (isActivity ? ("getCharSequenceArrayListExtra($S)") : ("getCharSequenceArrayList($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_ARRAY_LIST_PARCELABLE:
                    statement += (isActivity ? ("getParcelableArrayListExtra($S)") : ("getParcelableArrayList($S)"));
                    break;


                case Constants.TYPE_KIND.TYPE_SERIALIZABLE:
                    statement += (isActivity ? ("getSerializableExtra($S)") : ("getSerializable($S)"));
                    break;
                case Constants.TYPE_KIND.TYPE_OTHER_OBJECT:
                    statement = "$T.parseObject(" + (isActivity ? "substitute.getIntent()." : "getArguments(). ") + "getStringExtra($S), $T.class)";
                    break;
                default:break;
            }
            return statement + ";";
        }


    public void addField(FieldInjecting fieldInjecting) {
        fieldInjectings.add(fieldInjecting);
    }
}

























