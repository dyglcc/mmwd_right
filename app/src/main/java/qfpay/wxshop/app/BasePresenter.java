package qfpay.wxshop.app;

import android.content.Context;

import qfpay.wxshop.WxShopApplication;

/**
 * 为了依赖注入而使用的基类
 *
 * Created by LiFZhe on 1/21/15.
 */
public class BasePresenter {

    public BasePresenter(Context context) {
        WxShopApplication.get(context).inject(this);
    }
}
