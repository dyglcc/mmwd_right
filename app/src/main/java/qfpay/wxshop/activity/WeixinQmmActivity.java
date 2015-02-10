package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 微信介绍页面
 * */
public class WeixinQmmActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_weixin);
		MobAgentTools.OnEventMobOnDiffUser(WeixinQmmActivity.this, "MENU_RIGHT_WEIXIN");
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btn_tiaozhuan).setOnClickListener(
				new OnClickListener() {
					@Override public void onClick(View v) {
						openApp();
						MobAgentTools.OnEventMobOnDiffUser(WeixinQmmActivity.this, "WEIXIN_OPEN");
					}
				});
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
			Toaster.l(WeixinQmmActivity.this, getString(R.string.install_weichat_qin) + getString(R.string.focus_on_qmm_weichat));
		}
	}
}
