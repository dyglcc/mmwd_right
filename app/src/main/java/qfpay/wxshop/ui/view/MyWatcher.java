package qfpay.wxshop.ui.view;

import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.MobAgentTools;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MyWatcher implements TextWatcher {
	public MyWatcher(EditText edittext){
		this.focuseEditTextView = edittext;
	}
	private EditText focuseEditTextView;
	private int len;
	private int beforeLen;
	private int afterLen;
	private int selectionPositon;

	private boolean endIsFengefu;
	private boolean change = true;
	public abstract void nextDo(Object...obj);
	public abstract String tiaoShuju(String str,String fengefu);

	public void onTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		beforeLen = arg0.length();
		selectionPositon = focuseEditTextView.getSelectionStart();
	}


	
	public void afterTextChanged(Editable afterStr) {

		if (change) {
			change = false;
			return;
		}
		if (afterStr == null) {
			return;
		}
		if (afterStr.equals("")) {
			return;
		}
		afterLen = afterStr.length();
		len = afterLen - beforeLen;
		change = true;
		String srcString = afterStr.toString();
		endIsFengefu = srcString.endsWith(ConstValue.fengefu) ? true : false;
		String tString = srcString.replaceAll(ConstValue.fengefu, "");
		String resultString = tiaoShuju(tString, ConstValue.fengefu);
		focuseEditTextView.setText(resultString);
		if (len >= 0) {

			if (resultString.length() > srcString.length()) {
				focuseEditTextView.setSelection(selectionPositon + 1);
			} else {
				focuseEditTextView.setSelection(selectionPositon);
			}
		} else {
			int selection = selectionPositon;
			if (endIsFengefu) {
				int selection1 = selection - ConstValue.fengefu.length();
				focuseEditTextView.setSelection(selection1 < 0 ? 0
						: selection1);
			} else {
				int selection2 = selectionPositon;
				if (selection2 <= 0) {
					selection2 = 0;
				} else if (selection2 > srcString.length()) {
					selection2 = srcString.length();
				}
				focuseEditTextView.setSelection(selection2);
			}
		}
		nextDo(tString);
	}
}
