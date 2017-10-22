package annotation.demo;

import java.lang.reflect.Method;

public class MainTest {

    public static void main(String [] args){

        NoBug testObj = new NoBug();

        Class clazz = NoBug.class;
        Method[] methods = clazz.getDeclaredMethods();

        StringBuilder log = new StringBuilder();

        for (Method method:methods){
            if (method.isAnnotationPresent(Jiecha.class)){
                try {
                    method.setAccessible(true);
                    method.invoke(testObj,null);

                } catch (Exception e) {
                    log.append(method.getName()).append("  ")
                            .append(e.toString());
                }
            }

        }
        System.out.println(log.toString());

    }
}
