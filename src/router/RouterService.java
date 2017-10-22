package router;

import router.entity.User;

public interface RouterService {
    /**
     * 跳转到第一个页面
     * @param intentParam
     *
     * @param uriParam
     * */
    @FullUri("iqiyi://router/firstActivity")
    void startFirstActivity(@UriParam("island") String uriParam,@IntentParam("user") User intentParam);
    /**
     * 跳转到第二个页面
     * @param intentParam
     *
     * @param uriParam
     * */
    @FullUri("iqiyi://router/SecondActivity")
    void startSecondActivity(@UriParam("TaiWan") String uriParam,@IntentParam("user") int intentParam);
    /**
     * 跳转到第三个页面
     *@param intentParam
     *
     * @param uriParam
     * */
    @FullUri("iqiyi://router/ThirdActivity")
    void startThirdActivity(@UriParam("HongKong") String uriParam,@IntentParam("user") String intentParam);
}
