import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;

public class FieldInjecting {
    private String name;
    private TypeMirror typeMirror;
    private String paramKey;
    private Class<? extends Annotation> annotation;

    public FieldInjecting(String name, TypeMirror typeMirror, String paramKey, Class<? extends Annotation> annotation) {
        this.name = name;
        this.typeMirror = typeMirror;
        this.paramKey = paramKey;
        this.annotation = annotation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }
}
