package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.UserPaymentImpl;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.*;
import qfpay.wxshop.ui.view.MyWatcher;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

@EActivity(R.layout.main_reg_bank_setting)
public class RegBankAccountActivity extends BaseActivity {
	@ViewById
	TextView tv_knowmore, et_bank;
	@ViewById
	EditText et_card, et_account;
	@ViewById
	Button tv_later, tv_confirm;
	
	String rateString = null;
	@Extra
	String from;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getUmenBsStaticUrl(); 
	}
	
	@AfterViews
	void initViews() {
		setKnowMore();
		et_card.addTextChangedListener(new MyWatcher(et_card) {
			@Override
			public void nextDo(Object... obj) {
				if (et_card.getText().toString().length() == 0) {
					et_card.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
				} else {
					et_card.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
				}
			}
			@Override
			public String tiaoShuju(String str, String fengefu) {
				return Utils.tiaoShujuCard(str, fengefu);
			}
		});
	}
	
	@OnActivityResult(value = 0)
	void onResult(Intent intent) {
		if (intent != null && intent.hasExtra(BankPickerActivity.DATANAME_BANKNAME)) {
			et_bank.setText(intent.getStringExtra(BankPickerActivity.DATANAME_BANKNAME));
		} else {
			Toaster.l(this, "未选择银行");
		}
	}
	
	@Click
	void et_bank() {
		BankPickerActivity_.intent(this).startForResult(0);
	}
	
	@Click(R.id.tv_confirm)
	void doneEdit() {
		if (isEditAll()) {
			showConfirmDialog();
		}
	}
	
	@Click(R.id.tv_later)
	void laterEdit() {
		finish();
		MobAgentTools.OnEventMobOnDiffUser(RegBankAccountActivity.this, "skip_bankinfo");
	}
	
	boolean isEditAll() {
		String bankName = et_bank.getText().toString();
		String card = et_card.getText().toString().replaceAll(ConstValue.fengefu, "");
		String account = et_account.getText().toString();
		if (bankName == null || "".equals(bankName)) {
			showDialogInputError(getString(R.string.needInputBranchName));
			return false;
		}
		if (card == null || "".equals(card) || !Utils.isRightBankCard(card)) {
			showDialogInputError(getString(R.string.card_number_wrong));
			et_card.setText("");
			et_card.requestFocus();
			return false;
		}
		if (account == null || "".equals(account)) {
			showDialogInputError(getString(R.string.input_account_name));
			et_account.requestFocus();
			return false;
		}
		return true;
	}
	
	public void getUmenBsStaticUrl() {
		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (rateString == null) {
						rateString = MobclickAgent.getConfigParams(RegBankAccountActivity.this, ConstValue.ONLINE_RATE);
					}
				}
			}).start();
		}
	}
	
	private static Dialog dialog;
	
	protected void showConfirmDialog() {
		String bankCode = et_card.getText().toString();
		String account = et_account.getText().toString();
		String bankName = et_bank.getText().toString();
		String msgStr = "1.银行卡号：" + bankCode + "\n\n" + "2.持卡人：" + account
				+ "\n\n" + "3.银行：" + bankName + "\n\n\n";
		if (rateString != null || !rateString.equals("")) {
			msgStr = msgStr + rateString;
		}
		dialog = Utils.showDialog(RegBankAccountActivity.this,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						MobAgentTools.OnEventMobOnDiffUser(RegBankAccountActivity.this, "confirm_bankinfo");
						
						save2Server();
					}
				}, "确认信息", msgStr, "继续修改", "提交", false, true);

	}
	
	protected void save2Server() {
		String bankCode = et_card.getText().toString().trim().replaceAll(ConstValue.fengefu, "");
		String account = et_account.getText().toString();
		String bankName = et_bank.getText().toString();
		AbstractNet net = new UserPaymentImpl(RegBankAccountActivity.this);
		Bundle bun = new Bundle();
		bun.putString("card_number", bankCode);
		bun.putString("card_holder_name", account);
		bun.putString("card_bank", bankName);
		net.request(bun, new MainHandler(RegBankAccountActivity.this) {
			@Override
			public void onSuccess(Bundle bundle) {
				
				MobAgentTools.OnEventMobOnDiffUser(RegBankAccountActivity.this, "binding_succeed");
				Toaster.l(RegBankAccountActivity.this, getResources().getString(R.string.bankcard_reg_success));
				finish();
				WxShopApplication.dataEngine.setLoginStatus(true);
				Intent intent = new Intent(RegBankAccountActivity.this, MainActivity_.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			@Override
			public void onFailed(Bundle bundle) {
			}
		});
	}
	
	protected void showDialogInputError(String content) {
		Utils.showDialog(RegBankAccountActivity.this, null, 
				getString(R.string.mm_hint), content, getString(R.string.know), null, false, false);
	}
	
	/**
	 * 设置"了解更多"可以点击
	 */
	void setKnowMore() {
//		String knowMore = "了解更多";
//		String tip = getString(R.string.money_receive_NBD).toString() + "      " + knowMore;
//		SpannableString span = new SpannableString(tip);
//		span.setSpan(new MyClickSpan(), tip.lastIndexOf(knowMore), tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		tv_knowmore.setText(span);
//		tv_knowmore.setMovementMethod(LinkMovementMethod.getInstance());
		tv_knowmore.setText(R.string.money_receive_NBD);
	}
	
	class MyClickSpan extends ClickableSpan implements OnClickListener {
		@Override
		public void updateDrawState(TextPaint ds) {
		    ds.setColor(getResources().getColor(R.color.title_bg_color));
		    ds.setUnderlineText(false); //去掉下划线
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RegBankAccountActivity.this, ChangeBankCardInfoActivity.class);
			intent.putExtra(ConstValue.URL, WDConfig.RateLearnMore);
			intent.putExtra(ConstValue.TITLE, getString(R.string.learn_more));
			startActivity(intent);
		}
	}
}
