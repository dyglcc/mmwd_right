package qfpay.wxshop.activity;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.BankBean;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.view.BankPickerItem;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * 添加银行卡，选择银行
 *
 * */
@EActivity(R.layout.activity_bankpicker)
public class BankPickerActivity extends BaseActivity {
	public static final String DATANAME_BANKNAME = "bankname";
	
	@ViewById
	Button tv_back, tv_complete;
	@ViewById
	TextView tv_title, tv_tips;
	@Extra
	String str;
	@ViewById
	LinearLayout ll_banklist;
	
	@AfterViews
	void initView() {
		tv_back.setText("返回");
		tv_title.setText("选择开户银行");
		tv_complete.setVisibility(View.GONE);
		processView();
		
		String tips = tv_tips.getText().toString();
		SpannableString span = new SpannableString(tips);
		span.setSpan(new MyClickSpan(), tips.length() - 6, tips.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_tips.setText(span);
		tv_tips.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public void onBankPicker(BankBean bean) {
		Intent data = new Intent();
		data.putExtra(DATANAME_BANKNAME, bean.getbName());
		setResult(0, data);
		finish();
	}
	
	void processView() {
		List<BankBean> beans = getBankBean();
		for (BankBean bankBean : beans) {
			ll_banklist.addView(new BankPickerItem(this, bankBean));
		}
	}
	
	List<BankBean> getBankBean() {
		String[] banks = getResources().getStringArray(R.array.bankarray);
		List<BankBean> beans = new ArrayList<BankBean>();
		for (String string : banks) {
			if (string != null && !string.equals("")) {
				try {
					String name = string.split("#")[0];
					String bankno = string.split("#")[1];
					BankBean bean = new BankBean();
					bean.setbName(name);
					bean.setBnum(bankno);
					bean.setIconFileName("bankicon/" + bankno + ".png");
					beans.add(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return beans;
	}
	
	@Click
	void tv_back() {
		finish();
	}
	
	class MyClickSpan extends ClickableSpan implements OnClickListener {
		@Override
		public void updateDrawState(TextPaint ds) {
		    ds.setColor(getResources().getColor(R.color.title_bg_color));
		    ds.setUnderlineText(false); //去掉下划线
		}
		
		@Override
		public void onClick(View v) {
			startActivity(new Intent(BankPickerActivity.this, WeixinQmmActivity.class));
		}
	}
}
