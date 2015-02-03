package qfpay.wxshop.data.repository.api.retrofit.setting;

import retrofit.RequestInterceptor;

/**
 * 公共的请求拦截者
 *
 * Created by LiFZhe on 2015/1/3.
 */
public class MMInterceptor implements RequestInterceptor {
    private String sessionId;
    private String userAgent;

    public MMInterceptor(String sessionId, String userAgent) {
        this.sessionId = sessionId;
        this.userAgent = userAgent;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Cookie", String.format("sessionid=%s", sessionId));
        request.addHeader("User-Agent", userAgent);
    }
}