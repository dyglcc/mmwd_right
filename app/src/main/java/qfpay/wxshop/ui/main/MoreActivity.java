package qfpay.wxshop.ui.main;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

import de.greenrobot.event.EventBus;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.LoginActivity;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.config.update.UpdateManager;
import qfpay.wxshop.data.event.LogoutEvent;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.QFCommonUtils;

@EActivity(R.layout.more_layout)
public class MoreActivity extends BaseActivity implements Callback {
	public static final int SHOW_UPDATE_DIALOG = 1;
	public static final int SHOW_UPDATE_DIALOG_AlEADY_NEWSEST = 2;
	
	@ViewById TextView tv_version;
	
	@AfterViews void onInit() {
		getSupportActionBar().setTitle(getString(R.string.more_title));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setVersion(QFCommonUtils.getAppVersionString(this));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Click void ll_about() {
		CommonWebActivity_.intent(this).url(WDConfig.ABOUT_US).title(getString(R.string.more_item_about)).start();
	}
	
	@Click void ll_recommend() {
		CommonWebActivity_.intent(this).url(WDConfig.MORE_APP).title(getString(R.string.more_item_recommend)).start();
	}
	
	@Click void ll_officialshop() {
		String url = "http://mmwd.me/shop/2?ga_medium=" + ConstValue.android_mmwdapp_home_;
		CommonWebActivity_.intent(this)
			.title("喵喵微店")
			.url(url)
			.platFroms(Arrays.asList(new SharedPlatfrom[]{SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS, SharedPlatfrom.COPY}))
			.shareTitle("官方店")
			.shareName("官方店")
			.start();
	}
	
	@Click void ll_logout() {
		WxShopApplication.dataEngine.setLoginStatus(false);
		WxShopApplication.dataEngine.setcid("");
		WxShopApplication.dataEngine.setNoticeText("");
		WxShopApplication.app.closeAllActivity();
		WxShopApplication.dataEngine.setShopBg("");
		EventBus.getDefault().post(new LogoutEvent());
		startActivity(new Intent(MoreActivity.this, LoginActivity.class));
	}
	
	@Click void tv_checkupdate() {
		Handler handler = new Handler(this);
		WxShopApplication.app.checkUpdate(this, handler);
	}
	
	private void setVersion(String version) {
		tv_version.setText(String.format(getResources().getString(R.string.more_img_version), version));
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == SHOW_UPDATE_DIALOG) {
			Bundle data = msg.getData();
			String updateUrl = data.getString("updateUrl");
			String updateComment = data.getString("updateComment");
			String force = data.getString("needForce");
			UpdateManager mUpdateManager = new UpdateManager(this, updateUrl, updateComment);
			mUpdateManager.checkUpdateInfo(force);
		} else if (msg.what == SHOW_UPDATE_DIALOG_AlEADY_NEWSEST) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(getResources().getString(R.string.check_update));
			builder.setMessage(getResources().getString(R.string.aleady_newest));
			builder.setNegativeButton(getString(R.string.OK),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
		}
		return false;
	}
}
