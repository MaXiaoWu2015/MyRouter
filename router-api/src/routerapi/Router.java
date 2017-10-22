package routerapi;

import router.FullUri;
import router.IntentParam;
import router.UriParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * @author matingting
 * */
public class Router {

    @SuppressWarnings("unchecked")
    public <T>T create(final Class<T> service){
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, (Object proxy, Method method, Object[] args) -> {

            if (method.isAnnotationPresent(FullUri.class)){
                StringBuilder uriBuilder = new StringBuilder();

                FullUri fullUri = method.getAnnotation(FullUri.class);
                String uri = fullUri.value();
                uriBuilder.append(uri);
                Annotation[][] annotations =method.getParameterAnnotations();
                HashMap<String,Object> serializedParams = new HashMap<>();

                int position = 0;

                for (int j = 0; j< annotations.length;j++) {

                    Annotation[] annotationParams = annotations[j];

                    for (int i= 0 ;i<annotationParams.length;i++) {

                        Annotation annotation = annotationParams[i];

                        if (annotation instanceof UriParam){

                            uriBuilder.append(position == 0?"?":"&");
                            position++;
                            uriBuilder.append(((UriParam) annotation).value())
                            .append("=").append(args[j]);

                        }else if (annotation instanceof IntentParam){

                            serializedParams.put(((IntentParam) annotation).value(),args[j]);
                        }
                    }
                }

                //执行跳转操作
                performJump(uriBuilder.toString(),serializedParams);

            }

            return null;
        });
    }

    private void performJump(String s, HashMap<String, Object> serializedParams) {
        System.out.println("跳转到:"+s+"--参数:"+serializedParams);
        //TODO:处理跳转动作

    }

}
