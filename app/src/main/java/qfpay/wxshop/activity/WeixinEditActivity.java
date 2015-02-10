package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.WeixinUpdateImpl;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.presonalinfo.ShopInfoActivity;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 微信号编辑页面
 * */
@EActivity(R.layout.main_edit_weixin)
public class WeixinEditActivity extends BaseActivity {

	@ViewById ImageButton btn_back;
	@ViewById TextView    tv_title, tv_count, tv_go2shopview;
	@ViewById Button      btn_share;
	@ViewById EditText    et_text;
	@ViewById View        layout_progress_load;
	@ViewById ImageView   iv_progress_load;

	@AfterViews
	void init() {
		tv_title.setText("编辑微信号");
		btn_share.setText("完成");
		btn_share.setVisibility(View.VISIBLE);
		et_text.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override public void afterTextChanged(Editable arg0) {
				tv_count.setText(arg0.length() + "/20");
				if (arg0.length() == 20) {
					tv_count.setTextColor(getResources().getColor(R.color.title_bg_color));
				} else {
					tv_count.setTextColor(getResources().getColor(R.color.grey));
				}
			}
		});
		String str = WxShopApplication.dataEngine.getContract();
		if (str != null && !str.equals("")) {
			et_text.setText(str);
			try {
				et_text.setSelection(str.length());
			} catch (Exception e) {
				T.e(e);
			}
		}
	}

	@Click void btn_back() {
		finish();
	}

	@Click void btn_share() {
		saveSever();
	}

	@Click void tv_go2shopview() {
		Intent intent = new Intent(WeixinEditActivity.this, FunctionNoticeActivity.class);
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
			Toaster.l(WeixinEditActivity.this, "请输入微信号");
			return;
		}
		btn_share.setVisibility(View.GONE);
		layout_progress_load.setVisibility(View.VISIBLE);
		WeixinUpdateImpl net = new WeixinUpdateImpl(WeixinEditActivity.this);
		Bundle bun = new Bundle();
		bun.putString("intro", content);
		net.request(bun, new MainHandler(WeixinEditActivity.this, handler) {

			@Override
			public void onSuccess(Bundle bundle) {
				// finish();
				setResultOK();
			}

			@Override
			public void onFailed(Bundle bundle) {

			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			AnimationDrawable animation = (AnimationDrawable) iv_progress_load.getBackground();
			animation.start();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	protected void setResultOK() {
		Intent intent = new Intent(WeixinEditActivity.this, ShopInfoActivity.class);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
