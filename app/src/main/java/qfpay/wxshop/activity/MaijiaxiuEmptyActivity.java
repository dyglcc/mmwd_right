package qfpay.wxshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.event.AddBuyersShowEvent;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.app.BaseActivity;

@EActivity(R.layout.main_maijiaxiu_empty)
public class MaijiaxiuEmptyActivity extends BaseActivity {

	@ViewById Button   btn_empty_see;
//	@ViewById Button   btn_share;
	@ViewById TextView tv_link;

	@AfterViews
	void init() {
//		btn_share.setVisibility(View.INVISIBLE);
	}

	private void finishDoNothing() {
		Intent intent = new Intent();
		intent.putExtra("finish", "en");
		setResult(Activity.RESULT_OK, intent);
		finish();
	}


	@Click
	void btn_empty_see() {
		EventBus.getDefault().post(new AddBuyersShowEvent());
		finish();
	}


	@Click
	void tv_link() {
//		Intent intent = new Intent(MaijiaxiuEmptyActivity.this, ManagePreViewActivity.class);
//		intent.putExtra(ConstValue.URL, "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/h5/show.html?shopid=605");
//		intent.putExtra(ConstValue.TITLE, "查看她人买家秀");
//		startActivity(intent);

        CommonWebActivity_.intent(MaijiaxiuEmptyActivity.this).url("http://"+WxShopApplication.app.getDomainMMWDUrl()+"/h5/show.html?shopid=605").title("查看她人买家秀").start();
	}

	@Click
	void btn_share() {

	}

	@Override
	public void onBackPressed() {
		finishDoNothing();
	}
}
