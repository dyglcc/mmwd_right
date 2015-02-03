package qfpay.wxshop.data.exception;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import qfpay.wxshop.data.repository.api.netbean.NetDataContainer;
import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * 用于包装Http的错误消息
 *
 * Created by LiFZhe on 2015/1/3.
 */
public class HttpRequestException extends MessageException {
    private RetrofitError mError;

    public HttpRequestException(RetrofitError retrofitError) {
        this.mError = retrofitError;
    }

    public RetrofitError.Kind getErrorKind() {
        return mError.getKind();
    }

    @Override
    public String getMsgForToast() {
        String message = "";
        switch (getErrorKind()) {
            case HTTP:
                message = "服务器错误";
                break;
            case NETWORK:
                message = "网络错误,请稍后重试";
                break;
            case CONVERSION:
                message = "服务器数据回馈异常";
                break;
            case UNEXPECTED:
                message = "未知异常,请尝试联系喵喵客服";
                break;
        }
        return message;
    }
}