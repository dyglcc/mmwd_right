package qfpay.wxshop.data.repository.api.retrofit.setting;

import qfpay.wxshop.data.exception.HttpRequestException;
import qfpay.wxshop.utils.T;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * 简单封装网络请求错误处理
 *
 * Created by LiFZhe on 1/19/15.
 */
public class MMErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        T.e(cause);
        return cause;
    }
}
