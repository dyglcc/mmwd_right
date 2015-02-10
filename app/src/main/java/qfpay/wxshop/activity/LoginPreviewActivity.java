package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 注册登录选择界面
 */
public class LoginPreviewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login_preview);
		initView();
	}

	TextView tvRegister;
	TextView tvLogin;
	TextView tvOldLogin;
	View tv_other_people;
    ImageView shouFaImageView;

	private void initView() {

		getSupportActionBar().hide();
		// tv_other_people = findViewById(R.id.tv_other_people);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		tvOldLogin = (TextView) findViewById(R.id.tv_old_login);
        shouFaImageView = (ImageView)findViewById(R.id.imageView1);
        if (QFCommonUtils.isFirstLaunch(this)) {
            shouFaImageView.setVisibility(View.VISIBLE);
        } else {
            shouFaImageView.setVisibility(View.INVISIBLE);
        }
		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(LoginPreviewActivity.this,
						RegStep1Activity_.class));
			}
		});
		tvLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(LoginPreviewActivity.this,
						LoginActivity.class));
				// finish();

				MobAgentTools.OnEventMobOnDiffUser(LoginPreviewActivity.this, "User login");
			}
		});
		tvOldLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(LoginPreviewActivity.this,
						InputShopNameActivity.class));
			}
		});
		// tv_other_people.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// startActivity(new Intent(LoginPreviewActivity.this,
		// OtherShopMainActivity.class));
		// }
		// });

		findViewById(R.id.iv_show).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginPreviewActivity.this,
						OtherShopViewActivity.class);
				intent.putExtra("url", WDConfig.getInstance().URL_DEMO);

				startActivity(intent);

			}
		});
		findViewById(R.id.tv_show).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginPreviewActivity.this,
						OtherShopViewActivity.class);
				intent.putExtra("url", WDConfig.getInstance().URL_DEMO);

				startActivity(intent);
			}
		});
		
//		ImageView iv =  (ImageView) findViewById(R.id.iv_bg);
//		iv.setImageResource(R.drawable.az01);

	}
}
