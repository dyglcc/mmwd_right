package qfpay.wxshop.data.repository.api.retrofit.setting;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * 简单的Retrofit创建封装
 *
 * Created by LiFZhe on 1/19/15.
 */
public class MMRetrofitCreator {
    private RestAdapter mRestAdapter;

    public MMRetrofitCreator(String endPoint, RequestInterceptor interceptor, ErrorHandler errorHandler, boolean isTesting) {
        RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.HEADERS_AND_ARGS;
        if (isTesting) {
            logLevel = RestAdapter.LogLevel.NONE;
        }

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .setRequestInterceptor(interceptor)
                .setErrorHandler(errorHandler)
                .setLogLevel(logLevel)
                .build();
    }

    public <T> T getService(Class<T> serviceClazz) {
        return mRestAdapter.create(serviceClazz);
    }
}
