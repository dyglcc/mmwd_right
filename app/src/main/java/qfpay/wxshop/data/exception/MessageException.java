package qfpay.wxshop.data.exception;

/**
 * 可以提供消息的Exception
 *
 * Created by LiFZhe on 1/19/15.
 */
public abstract class MessageException extends Exception {
    public abstract String getMsgForToast();
}
