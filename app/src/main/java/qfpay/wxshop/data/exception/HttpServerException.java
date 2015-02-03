package qfpay.wxshop.data.exception;

/**
 * 服务器返回错误消息的Exception
 *
 * Created by LiFZhe on 1/19/15.
 */
public class HttpServerException extends MessageException {
    private String message;

    public HttpServerException(String message) {
        this.message = message;
    }

    @Override
    public String getMsgForToast() {
        return message;
    }
}
