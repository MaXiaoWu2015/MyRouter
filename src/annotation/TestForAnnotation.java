package annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Person
public class TestForAnnotation {
    @Person
    public String Check;

    public static void main(String[] args){

        boolean isAnnotation = TestForAnnotation.class.isAnnotationPresent(Person.class);
        if (isAnnotation){
            Person personClass = TestForAnnotation.class.getAnnotation(Person.class);
            System.out.println("class:"+personClass.role()+","+personClass.age());


            try {

                Field field = TestForAnnotation.class.getDeclaredField("Check");
                field.setAccessible(true);
                Person personVariable = field.getAnnotation(Person.class);
                System.out.println("variable:"+personVariable.role()+","+personVariable.age());


                Method method = TestForAnnotation.class.getDeclaredMethod("test");
                method.setAccessible(true);
                Person[] personMethod = method.getAnnotationsByType(Person.class);

                for (Person person:personMethod){
                    System.out.println("method:"+person.role()+","+person.age());
                }


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }


        }
    }


    @Person(role = "artist")
    @Person(role = "Programmer")
    @Person(role = "ProductManager")
    public void test(){

    }


    public TestForAnnotation() {
    }
}
