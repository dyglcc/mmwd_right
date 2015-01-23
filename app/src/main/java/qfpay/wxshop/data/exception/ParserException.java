package qfpay.wxshop.data.exception;

/**
 * 解析时候的异常
 *
 * Created by LiFZhe on 1/19/15.
 */
public class ParserException extends MessageException {
    String msg;

    public ParserException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsgForToast() {
        return msg;
    }
}
