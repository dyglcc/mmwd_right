package qfpay.wxshop.app;

import org.androidannotations.api.BackgroundExecutor;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import android.os.Bundle;
import cn.sharesdk.framework.ShareSDK;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WxShopApplication.app.addActivity(this);
		ShareSDK.initSDK(this);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("");
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		BackgroundExecutor.cancelAll(ConstValue.THREAD_CANCELABLE, true);
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
