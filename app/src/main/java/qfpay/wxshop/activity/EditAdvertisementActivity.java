package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.NoticeUpdateImpl;
import qfpay.wxshop.dialogs.SimpleDialogFragment;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.presonalinfo.ShopInfoActivity;
import qfpay.wxshop.utils.Toaster;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@EActivity(R.layout.main_edit_advert)
public class EditAdvertisementActivity extends BaseActivity {

	@ViewById
	ImageButton btn_back;
	@ViewById
	Button btn_share;
	@ViewById
	TextView tv_title;
	@ViewById
	TextView tv_count;

	@ViewById
	EditText et_text;

	@ViewById
	View layout_progress_load;
	@ViewById
	TextView tv_go2shopview;

	@ViewById(R.id.iv_progress_load)
	ImageView ivProgress;

	// @ViewById(R.id.iv_close)
	// ImageView iv_close;

	@AfterViews
	void init() {
		tv_title.setText("编辑店铺公告");
		btn_share.setText("完成");
		btn_share.setVisibility(View.VISIBLE);
		et_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				tv_count.setText(arg0.length() + "/60");
				if(arg0.length() ==60){
					tv_count.setTextColor(getResources().getColor(R.color.title_bg_color));
				}else{
					tv_count.setTextColor(getResources().getColor(R.color.grey));
				}
			}
		});
		et_text.setText(WxShopApplication.dataEngine.getNoticeText());
		// if (WxShopApplication.dataEngine.isFirstinEdvertiseMent()) {
		// tv_go2shopview.setVisibility(View.GONE);
		// iv_close.setVisibility(View.VISIBLE);
		// } else {
		tv_go2shopview.setVisibility(View.VISIBLE);
		// iv_close.setVisibility(View.GONE);
		// }
		// WxShopApplication.dataEngine.isSetEdvertiseMent(false);
		if (WxShopApplication.dataEngine.isFirstinEdvertiseMent()) {
			Intent intent = new Intent(EditAdvertisementActivity.this,
					FunctionNoticeActivity.class);
			startActivity(intent);
		}

	}

	@Click
	void btn_back() {

		goback();

	}

	private void goback() {
		// TODO Auto-generated method stub
		if (et_text.getText().toString().equals("")) {
			finish();
			return;
		}
		showDialogConfirm();
	}

	@Click
	void btn_share() {
		saveSever();
	}

	// @Click
	// void iv_close() {
	// tv_go2shopview.setVisibility(View.VISIBLE);
	// iv_close.setVisibility(View.GONE);
	// }

	@Click
	void tv_go2shopview() {

		Intent intent = new Intent(EditAdvertisementActivity.this,
				FunctionNoticeActivity.class);
		startActivity(intent);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
				layout_progress_load.setVisibility(View.INVISIBLE);
				btn_share.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}

		};
	};

	private void saveSever() {
		final String content = et_text.getText().toString().trim();
		if (content.equals("")) {
			Toaster.l(EditAdvertisementActivity.this, "请输入公告消息");
			return;
		}
		btn_share.setVisibility(View.GONE);
		layout_progress_load.setVisibility(View.VISIBLE);
		NoticeUpdateImpl net = new NoticeUpdateImpl(
				EditAdvertisementActivity.this);
		Bundle bun = new Bundle();
		bun.putString("intro", content);
		net.request(bun, new MainHandler(EditAdvertisementActivity.this,
				handler) {

			@Override
			public void onSuccess(Bundle bundle) {
				WxShopApplication.dataEngine.setNoticeText(content);
				Intent intent = new Intent(EditAdvertisementActivity.this,ShopInfoActivity.class);
				setResult(-1, intent);
				finish();
			}

			@Override
			public void onFailed(Bundle bundle) {

			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			AnimationDrawable animation = (AnimationDrawable) ivProgress
					.getBackground();
			animation.start();
		}
	}

	protected void showDialogConfirm() {

		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(getString(R.string.mm_hint))
				.setMessage("亲，公告还没编辑完成，真的要放弃嘛~").setNegativeButtonText("继续编辑")
				.setPositiveButtonText("确定放弃").setCancelable(true)
				.setRequestCode(-1).setPositiveClick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}
	@Override
	public void onBackPressed() {
		goback();
	}
}
