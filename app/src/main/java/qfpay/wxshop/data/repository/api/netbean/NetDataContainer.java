package qfpay.wxshop.data.repository.api.netbean;

/**
 * 喵喵服务器返回的根部数据类型
 *
 * Created by LiFZhe on 1/19/15.
 */
public class NetDataContainer {
    public String resperr; // 错误提示, 可展示给用户
    public String respcd;  // 错误码
    public String respmsg; // 调试信息, 不可展示给用户

    public String getShownMessage() {
        return resperr;
    }
}
