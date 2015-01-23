package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.GetMobileCode;
import qfpay.wxshop.data.netImpl.RegImpl;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.view.MyWatcher;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.QMMAlert.OnAlertSelectId;
import qfpay.wxshop.utils.RegisterJsonTool;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@EActivity(R.layout.main_reg_step1)
public class RegStep1Activity extends BaseActivity implements Callback {

	private Handler handler;
	private int getCodeTimes;
	@ViewById
	TextView tv_haiwaitips;
	
	@Pref AppStateSharePreferences_ pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();
	}
	@Click
	void tv_haiwaitips(){
		startActivity(new Intent(RegStep1Activity.this,
				WeixinQmmActivity.class));
	}

	private Button tvBack;
	private Button tvComplete, btnSave;
	private Button btnGetCode;
	private EditText etMobile, etCode, etPwd;

	@AfterViews void initView() {

		handler = new Handler(this);

		MobAgentTools.OnEventMobOnDiffUser(RegStep1Activity.this,
				"click_register");
		
		tvBack = (Button) findViewById(R.id.tv_back);
		btnGetCode = (Button) findViewById(R.id.btn_get_code);
		tvComplete = (Button) findViewById(R.id.tv_complete);
		btnSave = (Button) findViewById(R.id.btn_save);
		etPwd = (EditText) findViewById(R.id.et_pwd);

		// mobile
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
		etCode = (EditText) findViewById(R.id.et_code);

		tvComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isAllright()) {

					save2Server();

				}
				// startActivity(new Intent(RegStep1Activity.this,
				// InputShopNameActivity.class));
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isAllright()) {
					MobAgentTools.OnEventMobOnDiffUser(RegStep1Activity.this,
							"Mobile phone registered");

					save2Server();

				}
				// startActivity(new Intent(RegStep1Activity.this,
				// InputShopNameActivity.class));
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnGetCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (getCodeTimes >= 2) {

					showTipLIst();
					return;
				} else {
					getCodeFromServer();
				}
				MobAgentTools.OnEventMobOnDiffUser(RegStep1Activity.this,
						"verification code");
			}
		});

		findViewById(R.id.tv_protocol).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						RegisterJsonTool.getInstance().init(
								RegStep1Activity.this);
						QMMAlert.showAlertTextView(RegStep1Activity.this,
								getString(R.string.mm_protocol),
								RegisterJsonTool.getInstance().getStringMulLine("user_protocol.txt"),
								getString(R.string.know_i), null, null);
					}
				});
	}

	String mobile;

	protected void save2Server() {

		mobile = etMobile.getText().toString();
		mobile = mobile.replaceAll(ConstValue.fengefu, "");
		String password = etPwd.getText().toString();
		String verify_code = etCode.getText().toString();

		AbstractNet reg = new RegImpl(RegStep1Activity.this);
		Bundle bun = new Bundle();
		bun.putString("mobile", mobile);
		bun.putString("password", password);
		bun.putString("verify_code", verify_code);

		reg.request(bun, new MainHandler(RegStep1Activity.this) {

			@Override
			public void onSuccess(Bundle bundle) {

				WxShopApplication.dataEngine.setMobile(mobile);
				WxShopApplication.dataEngine.setLoginStatus(true);
				WxShopApplication.app.closeAllActivity();
				if (NewIntroductionActivity.INSTANCE != null) {
					NewIntroductionActivity.INSTANCE.finish();
				}

				startActivity(new Intent(RegStep1Activity.this, InputShopNameActivity.class));

				pref.isNewUser().put(true);
				pref.guidePointer().put(MainActivity.GUIDE_RELEASE);
				pref.isShowGuide().put(true);
			}

			@Override
			public void onFailed(Bundle bundle) {

			}
		});

	}

	private void showTipLIst() {
		String[] items = new String[] { getString(R.string.resume),
				getString(R.string.contact_customer_service) };
		QMMAlert.showAlertCenterMenu(RegStep1Activity.this,
				getString(R.string.fail_get_security_code), items, null,
				new OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {

						switch (whichButton) {
						case 0:
							getCodeFromServer();
							break;

						case 1:
							startActivity(new Intent(RegStep1Activity.this,
									WeixinQmmActivity.class));
							break;

						default:
							break;
						}

					}
				});
	}

	protected void showDialogInputError(String content) {

		Utils.showDialog(RegStep1Activity.this, null,
				getString(R.string.mm_hint), content, getString(R.string.know),
				null, false, false);
	}

	protected boolean isAllright() {
		boolean success = false;
		if (isRightPhone()) {
			success = true;
		} else {
			showDialogInputError(getString(R.string.phonenumber_wrong));
			return false;
		}

		if (isCodeRight()) {
			success = true;
		} else {
			showDialogInputError(getString(R.string.wrong_security_code_2));
			return false;
		}
		if (isRightPwd()) {
			success = true;
		} else {
			showDialogInputError(getString(R.string.passwd_at_lease_6_3));
			return false;
		}

		return success;
	}

	private boolean isCodeRight() {
		String codeStr = etCode.getText().toString();
		if (codeStr.length() < 4) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isRightPwd() {
		String txt = etPwd.getText().toString().trim();
		if (txt.length() > 5) {
			return true;
		}
		return false;
	}

	private void sendRequest2ServerGetCode() {

		String mobile = etMobile.getText().toString();
		mobile = mobile.replaceAll(ConstValue.fengefu, "");
		AbstractNet getCodeNet = new GetMobileCode(RegStep1Activity.this);
		Bundle bun = new Bundle();
		bun.putString("mobile", mobile);
		bun.putString("count", getCodeTimes + "");
		getCodeNet.request(bun, new getCodeHandler(RegStep1Activity.this));
	}

	private class getCodeHandler extends MainHandler {

		public getCodeHandler(Context context) {
			super(context);
		}

		@Override
		public void onSuccess(Bundle bundle) {
			if (bundle != null) {
				String str = bundle.getString(ConstValue.ERROR_MSG);
				if (ConstValue.SUCCESS.equals(str)) {
					getCodeTimes++;
					getCodeChangeUis();
					Toaster.l(RegStep1Activity.this,
							getString(R.string.send_OK));
				}
			}
		}

		@Override
		public void onFailed(Bundle bundle) {
			currentNum = 0;
		}

	}

	protected boolean isRightPhone() {
		String mobile = etMobile.getText().toString();
		mobile = mobile.replaceAll(ConstValue.fengefu, "");
		if (Utils.isRightPatternPhone(mobile)) {
			return true;
		}
		return false;
	}

	private static final int COUNTING = 1;
	private static final int SECONDS = 50;
	private int currentNum = SECONDS;

	protected void getCodeFromServer() {
		currentNum = SECONDS;
		// change ui
		// access server for code
		if (isRightPhone()) {
			sendRequest2ServerGetCode();
		} else {
			showDialogInputError(getString(R.string.phonenumber_wrong));
			etMobile.requestFocus();
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case COUNTING:
			if (currentNum > 1) {
				btnGetCode.setEnabled(false);
				btnGetCode.setText(getString(R.string.sending) + "("
						+ currentNum-- + ")");
			} else {
				btnGetCode.setEnabled(true);
				btnGetCode.setText(getString(R.string.resume));
			}
			break;

		default:
			break;
		}
		return false;
	}

	/**
	 * 开始计数
	 * 
	 * */

	private void getCodeChangeUis() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				currentNum = SECONDS;
				int count = currentNum;
				while (count-- >= 0) {
					handler.sendEmptyMessage(COUNTING);
					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
