package router;

import dynamicproxy.Subject;
import router.entity.User;
import routerapi.Router;
import sun.misc.ProxyGenerator;
import sun.misc.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author matingting
 */
public class RouterDemo {
    public static void main(String[] args){
        RouterService service = new Router().create(RouterService.class);
        service.startFirstActivity("上海",new User("mtt",18));
        createProxyClassFile();
    }

    public static void createProxyClassFile(){
        String name = "RouterProxy";
        byte[] data = ProxyGenerator.generateProxyClass(name,new Class[]{RouterService.class});

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
