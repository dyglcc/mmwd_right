package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class ShowBankInfoActivity extends BaseActivity implements Callback {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_show_bank_info);
		
		initUi();
		
		overridePendingTransition(R.anim.push_up_in, R.anim.quit);

		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		handler = new Handler(this);

		getUmengPara();
		
		getUmengParaChangeBankInfo();
		
		
		getSupportActionBar().hide();
		
	}

	private TextView tvAccount;
	private TextView tvBankCard;
	private TextView tvBanBranchName;
	private View tvContrachMiaoMiao;
	
	private Handler handler;
	private void getUmengPara() {
		getUmenBsStaticUrl();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					T.e(e);
				}
				while (true) {
					if (rateString != null) {

						handler.sendEmptyMessage(1);
						break;
					}
				}
			}
		}).start();
	}
	private void getUmengParaChangeBankInfo() {
		getUmenBankInfoUrl();

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					T.e(e);
//				}
//				while (true) {
//					if (changeBankInfoString != null) {
//
////						handler.sendEmptyMessage(2);
//						break;
//					}
//				}
//			}
//		}).start();
	}
	
	String rateString = null;

	public void getUmenBsStaticUrl() {

		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					if (rateString == null) {
						rateString = MobclickAgent.getConfigParams(
								ShowBankInfoActivity.this,
								ConstValue.ONLINE_RATE);
					}
				}
			}).start();
		}

	}
	private String changeBankInfoString ;
	public void getUmenBankInfoUrl() {

		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					if (changeBankInfoString == null) {
						changeBankInfoString = MobclickAgent.getConfigParams(
								ShowBankInfoActivity.this,
								ConstValue.ONLINE_BANK_INFO_CHANGE);
					}
				}
			}).start();
		}

	}
	
	private void initUi() {
		tvAccount = (TextView) findViewById(R.id.tv_bankAccount);
		tvAccount.setText(WxShopApplication.dataEngine.getBankUser());
		tvBankCard = (TextView) findViewById(R.id.tv_card);
		tvBankCard.setText(WxShopApplication.dataEngine.getBankAccount());
		tvBanBranchName = (TextView) findViewById(R.id.tv_bank_branch);
		tvBanBranchName.setText(WxShopApplication.dataEngine.getBrankBranchName());
		tvContrachMiaoMiao = findViewById(R.id.tv_contract_miaomiao);
		tvContrachMiaoMiao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(ShowBankInfoActivity.this,GonglueActivity.class));
				Intent intent = new Intent(ShowBankInfoActivity.this,
						ChangeBankCardInfoActivity.class);
				if(changeBankInfoString==null || changeBankInfoString.equals("")){
					changeBankInfoString = WDConfig.CHANGE_BANK_ACCOUNT;
				}
				intent.putExtra(ConstValue.URL, changeBankInfoString);
				intent.putExtra(ConstValue.TITLE, getString(R.string.changeBankAccout));
				startActivity(intent);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// LogUtil.i(this, "keyCode=" keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == 1){
			if(rateString!=null && !rateString.equals("")){
				TextView tvRate = (TextView) findViewById(R.id.tv_show_rate);
				tvRate.setText(rateString);
			}
		}else if(msg.what == 2){
//			if(changeBankInfoString!=null && !changeBankInfoString.equals("")){
//				
//				
//			}
		}
		return false;
	}
}
