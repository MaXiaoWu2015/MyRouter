package dynamicproxy;

import sun.misc.ProxyGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
/**
 * @author matingting
 * */
public class DynamicProxy {

    public static void main(String[] args){
        RealSubject real = new RealSubject();
        ProxyHandler proxyHandler = new ProxyHandler(real);
        Subject proxyInstance = (Subject) Proxy.newProxyInstance(RealSubject.class.getClassLoader(),new Class[]{Subject.class},proxyHandler);
        proxyInstance.doSomething();

        createProxyClassFile();
        System.out.println("haha");
    }

    public static void createProxyClassFile(){
        String name = "ProxySubject";
        byte[] data = ProxyGenerator.generateProxyClass(name,new Class[]{Subject.class});

        try {
            FileOutputStream out = new FileOutputStream(name + ".class");
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
