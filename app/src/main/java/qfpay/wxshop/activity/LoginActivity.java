package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.event.LogoutEvent;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.LoginImpl;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.*;
import qfpay.wxshop.ui.view.MyWatcher;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login);
		initView();

		MobAgentTools.OnEventMobOnDiffUser(LoginActivity.this, "login");

	}

	TextView tvCancel;
	EditText etPwd;
	private View btnLogin;
	private EditText etMobile;

	private void initView() {

		findViewById(R.id.layout_et_changePwd).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						MobAgentTools.OnEventMobOnDiffUser(LoginActivity.this,
								"forget_password");

						Intent intent = new Intent(LoginActivity.this,
								ChangePwdActivity.class);
						intent.putExtra(ConstValue.URL, WDConfig.CHANGE_PWD_URL);
						intent.putExtra(ConstValue.TITLE,
								getString(R.string.changePwdtitle));
						startActivity(intent);
					}
				});

		tvCancel = (TextView) findViewById(R.id.tv_back);

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etMobile = (EditText) findViewById(R.id.et_mobile);
		etMobile.addTextChangedListener(new MyWatcher(etMobile) {

			@Override
			public String tiaoShuju(String str, String fengefu) {
				return Utils.tiaoShujuPhone(str, fengefu);
			}

			@Override
			public void nextDo(Object... obj) {

			}
		});

		etMobile.setText(Utils.tiaoShujuPhone(
				WxShopApplication.dataEngine.getMobile(), ConstValue.fengefu));

		etPwd = (EditText) findViewById(R.id.et_pwd);

		btnLogin = findViewById(R.id.tv_complete);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				loginNow();
			}
		});

		EventBus.getDefault().post(new LogoutEvent());
	}

	protected void showDialogInputError(String content) {
		Utils.showDialog(LoginActivity.this, null,
				getResources().getString(R.string.miaomiaotishi), content,
				"知道了", null, false, false);
	}

	protected void loginNow() {

		String username = etMobile.getText().toString().trim();
		username = username.replaceAll(ConstValue.fengefu, "");
		if (username.length() != 11) {
			showDialogInputError(getString(R.string.phonenumber_wrong));
			etMobile.requestFocus();
			return;
		}
		if (!Utils.isRightPatternPhone(username)) {
			showDialogInputError(getString(R.string.phonenumber_wrong));
			etMobile.requestFocus();
			return;
		}
		String password = etPwd.getText().toString().trim();
		if (password.equals("")) {
			showDialogInputError(getString(R.string.passwd_input));
			etPwd.requestFocus();
			return;
		}
		if (password.length() < 6) {
			showDialogInputError(getString(R.string.passwd_at_lease_6_2));
			etPwd.requestFocus();
			return;
		}
		AbstractNet net = new LoginImpl(LoginActivity.this);
		Bundle bun = new Bundle();
		bun.putString("username", username);
		bun.putString("password", password);

		net.request(bun, new MainHandler(LoginActivity.this) {

			@Override
			public void onSuccess(Bundle bundle) {

				MobAgentTools.OnEventMobOnDiffUser(LoginActivity.this,
						"login_succeed");

				WxShopApplication.app.closeAllActivity();
				if (NewIntroductionActivity.INSTANCE != null) {
					NewIntroductionActivity.INSTANCE.finish();
				}
				if(WxShopApplication.dataEngine.getShopName().equals("")){
					startActivity(new Intent(LoginActivity.this, InputShopNameActivity.class));
					finish();
				}else{
					MainActivity_.intent(LoginActivity.this).start();
				}
			}

			@Override
			public void onFailed(Bundle bundle) {
			}
		});

	}

}
