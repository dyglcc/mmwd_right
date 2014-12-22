package qfpay.wxshop.ui.view;

import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Utils;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class MoneyEditTextView extends EditText {

	/**
	 * 金额edittext,限制2位小数
	 * 
	 * */

	public MoneyEditTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addTextWatcher();
	}

	public MoneyEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		addTextWatcher();
	}

	public MoneyEditTextView(Context context) {
		super(context);
		addTextWatcher();
	}

	private void addTextWatcher() {
		// TODO Auto-generated method stub
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s == null || s.toString().equals("")) {
					return;
				}
				if (s.toString().indexOf(".") == -1) {
					return;
				}
				String endStr = s.toString().substring(
						s.toString().indexOf(".") + 1);
				if (endStr.length() < 3) {
					return;
				}
				String str = Utils.cut2moneyAmount(s.toString());
				MoneyEditTextView.this.setText(str);
				setSelection(str.length());
			}
		});
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null || s.toString().equals("")) {
					return;
				}
				if (s.toString().indexOf(".") == -1 && s.length() > 7) {
					String str = s.subSequence(0, 7).toString();
					MoneyEditTextView.this.setText(str);
					setSelection(str.length());
					return;
				}
				if(s.toString().indexOf(".") != -1 && s.toString().length() > 9){
					String str = s.toString();
					String qqStr = str.substring(0,str.indexOf("."));
					String extra = str.substring(str.indexOf("."));
					if(qqStr.length()>7){
						qqStr = qqStr.substring(0,7);
					}else{
						return;
					}
					MoneyEditTextView.this.setText(qqStr+extra);
					setSelection(qqStr.length());
				}
			}
		});
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null || s.toString().equals("")) {
					return;
				}
				if(s.toString().startsWith(".") && s.length() > 1){
					String str = "0" + s;
					MoneyEditTextView.this.setText(str);
					setSelection(str.length());
				}
			}
		});
		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null || s.toString().equals("")) {
					return;
				}
				if(s.toString().startsWith("0")&&s.toString().endsWith(".") && s.length() > 2){
					String str = s.subSequence(1, s.length()).toString();
					MoneyEditTextView.this.setText(str);
					setSelection(str.length());
				}
			}
		});
	}
}
