package qfpay.wxshop.activity;

import android.os.Bundle;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import qfpay.wxshop.ui.BaseActivity;

/**
 * Created by dongyuangui on 14-12-25.
 */
public class TestActivity extends BaseActivity{

    private Platform weibo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weibo = ShareSDK.getPlatform(TestActivity.this, SinaWeibo.NAME);
    }
}
