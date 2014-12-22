package qfpay.wxshop.ui.view;

import qfpay.wxshop.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialog extends Dialog {
	ProgressBar progressbar;
	TextView tv_progress;
	
	int max = 1, current = 0;
	
	public ProgressDialog(Context context) {
		super(context);
	}
	
	public ProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	
	void init() {
		progressbar = (ProgressBar) findViewById(R.id.progress_imgupload);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(false);
	}

	public static ProgressDialog createDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context,R.style.CustomProgressDialog);
		dialog.setContentView(R.layout.progressdialog_layout);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return dialog;
	}
	
	public ProgressDialog setProgress(int max, int current) {
		this.max = max;
		this.current = current;
		if (progressbar == null) {
			return this;
		}
		progressbar.setMax(max);
		progressbar.setProgress(current);
		int progress = current * 100 / max;
		tv_progress.setText(progress + " %");
		return this;
	}
	
	@Override
	public void show() {
		if (this.isShowing()) {
			return;
		}
		super.show();
		init();
		setProgress(max, current);
	}
}
