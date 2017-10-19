package qfpay.wxshop.ui.main;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

//import cn.sharesdk.framework.ShareSDK;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.InputShopNameActivity;
import qfpay.wxshop.activity.LoginPreviewActivity;
import qfpay.wxshop.activity.NewIntroductionActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SplashParams;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.T;

/**
 *
 * 欢迎页
 */

@EActivity(R.layout.welcome_layout) @WindowFeature({ Window.FEATURE_NO_TITLE, WindowManager.LayoutParams.FLAG_FULLSCREEN })
public class WelcomeActivity extends BaseActivity {
	private static final int DEFAULT_SHOW_TIME = 5000;

	@ViewById ImageView iv_net, iv_firstlaunch;
    @ViewById TextView tv_version;
    @ViewById RelativeLayout rl_default, rl_root;


	@AfterViews
	void init() {
		initApp();
		initImg();
	}

	void initApp() {

	}

	void afterSplash() {
		if (this.isFinishing()) {
			finish();
			return;
		}
		
		// 检查服务器地址更改
		Intent intent = getIntent();
		if (intent != null) {
			String url = intent.getStringExtra("mUrl");
			int mServerType = intent.getIntExtra("mServerType", 0);
			if (url != null) {
				WDConfig.getInstance().init(url, mServerType);
				WxShopApplication.app.mTesterUrl = url;
				WxShopApplication.app.mServerType = mServerType;
			} else {
				if (WxShopApplication.app.mTesterUrl != null) {
					WDConfig.getInstance().init(
							WxShopApplication.app.mTesterUrl,
							WxShopApplication.app.mServerType);
				}
			}
		}

		// 检查是否显示引导界面
		boolean needShowIntroduce = WxShopApplication.dataEngine.getIntroduce();
		if (!needShowIntroduce) {
			Intent introduceIntent = new Intent(this,
					NewIntroductionActivity.class);
			startActivity(introduceIntent);
			finish();
			return;
		}
		if (!WxShopApplication.dataEngine.getLoginStatus()) {
			startActivity(new Intent(this, LoginPreviewActivity.class));
			finish();
			return;
		}
		if (WxShopApplication.dataEngine.getShopName().equals("")) {
			startActivity(new Intent(this, InputShopNameActivity.class));
			finish();
			return;
		}

        // share sdk 集成 目前使用share Sdk 的有新浪 腾讯微博
//		ShareSDK.initSDK(this);
		MainActivity_.intent(WelcomeActivity.this).start();
		finish();

        // 暂时没有发现在用open time
        WxShopApplication.dataEngine.setOpenTime(System.currentTimeMillis());
	}

	void initImg() {
//		if (QFCommonUtils.isFirstLaunch(this)) {
//			iv_firstlaunch.setVisibility(View.VISIBLE);
//		} else {
//			iv_firstlaunch.setVisibility(View.INVISIBLE);
//		}

		// 处理运营背景图的加载
		final SplashParams params = getParams();
		if (params != null && params.getUrl() != null && !params.getUrl().equals("")) {
			Picasso.with(WelcomeActivity.this).load(params.getUrl()).fit().centerCrop()
				.placeholder(R.color.common_background_red).skipMemoryCache().into(iv_net);
			rl_default.setVisibility(View.GONE);
			delayDisappear(params.getDisplay_time() * 1000);
		} else {
			delayDisappear(DEFAULT_SHOW_TIME);
		}
	}

	SplashParams getParams() {
		try {
			String json = MobclickAgent.getConfigParams(this,
					ConstValue.ONLINE_SPLASH_IMG);
			Gson gson = new Gson();
			return gson.fromJson(json, SplashParams.class);
		} catch (Exception e) {
			T.e(e);
			return null;
		}
	}

	void delayDisappear(long time) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				afterSplash();
			}
		}, time);
	}
}
