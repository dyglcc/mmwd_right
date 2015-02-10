package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 公告引导介绍页面
 */
public class FunctionNoticeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_function_notice);

		WxShopApplication.dataEngine.isSetEdvertiseMent(false);
		
		ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
		iv_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finishAct();
			}
		});

	}
	
	private void finishAct() {
		finish();
		overridePendingTransition(0, R.anim.anima_activity_out);
	}
}
