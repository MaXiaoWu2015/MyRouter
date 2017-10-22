package inject;

public @interface Inject {
    String value() default "";
}
