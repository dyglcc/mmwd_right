package qfpay.wxshop.ui.customergallery;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import qfpay.wxshop.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionProvider;

@EBean @SuppressLint("InflateParams")
public class CompleteBtnActionProvider extends ActionProvider {
	@RootContext   CustomerGalleryActivity mActivity;
	RelativeLayout rl;
	TextView       tv;
	
	public CompleteBtnActionProvider(Context context) {
		super(context);
		rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.customergallery_title_btn, null);
		rl.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				mActivity.onComplete();
			}
		});
		tv = (TextView) rl.findViewById(R.id.tv_complete);
	}
	
	void setChoicedCount(int choicedCount, int maxCount) {
		tv.setText(String.format(mActivity.getString(R.string.gallery_title_btn), choicedCount + " / " + maxCount));
	}
	
	@Override
	public View onCreateActionView() {
		setChoicedCount(mActivity.choicedCount, mActivity.maxCount);
		return rl;
	}
}