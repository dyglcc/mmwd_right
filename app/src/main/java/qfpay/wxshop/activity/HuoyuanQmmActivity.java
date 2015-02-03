package qfpay.wxshop.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

@EActivity(R.layout.main_weixin_huoyuan)
public class HuoyuanQmmActivity extends BaseActivity {

    @Click
    void btn_back(){
        finish();
    }
    @Click
    void iv_go2weixin(){
        openApp();
    }

	private void openApp() {
		Intent intent = new Intent();
		ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(cmp);
		try {
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			Toaster.l(HuoyuanQmmActivity.this, getString(R.string.install_weichat_qin) + getString(R.string.focus_on_qmm_weichat));
		}
	}
    @ViewById
    TextView tv_title;
    @AfterViews
    void init(){
        tv_title.setText("喵喵货源客服");
    }
    @Click
    void iv_copy(){
        Toaster.l(HuoyuanQmmActivity.this,"已复制微信号：miaoxiaomengcc");
        Utils.saveClipBoard(HuoyuanQmmActivity.this,"miaoxiaomengcc");
    }
}
