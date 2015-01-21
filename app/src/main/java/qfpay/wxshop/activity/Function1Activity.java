package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.ui.commodity.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class Function1Activity extends Activity {

	private View imageView;
	private Animation animationLight, animationBg;

	// private View viewBg;
	private View viewTitle;
	private View viewBottom;
	private View viewOther1;
	private View viewOther2;
	private View viewMargin;
	private View viewLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_function_show);

		WxShopApplication.dataEngine.setGuidePublish(true);

		imageView = findViewById(R.id.view_light);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				
				EditItemActivity_.intent(Function1Activity.this).start();
				finish();
			}
		});

		viewTitle = findViewById(R.id.tv_title);
		viewMargin = findViewById(R.id.view_line);
		viewBottom = findViewById(R.id.layout_stat);

		viewTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toaster.l(Function1Activity.this, "sdfs");
//				finish();
				finishAct();
			}
		});
		viewBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toaster.l(Function1Activity.this, "sdfs");
				finishAct();
			}
		});

		viewOther1 = findViewById(R.id.layout_cloumn2);
		viewOther1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishAct();

			}
		});

		
		viewLeft = findViewById(R.id.view_left);
		viewOther2 = findViewById(R.id.btn_more);

		viewOther2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishAct();
			}
		});
		viewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishAct();
			}
		});

		animationLight = AnimationUtils.loadAnimation(this, R.anim.alpha_guide);
		animationLight.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				imageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				findViewById(R.id.view_guid).setVisibility(View.VISIBLE);
			}
		});

		animationBg = AnimationUtils.loadAnimation(this, R.anim.alpha_bg);
		animationBg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				findViewById(R.id.view_guid).setVisibility(View.VISIBLE);
			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			viewBottom.setAnimation(animationBg);
			viewOther1.setAnimation(animationBg);
			viewOther2.setAnimation(animationBg);
			viewLeft.setAnimation(animationBg);
			viewTitle.setAnimation(animationBg);
			viewMargin.setAnimation(animationBg);
			imageView.setAnimation(animationLight);

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageView = null;
		animationLight = null;
		animationBg = null;
		viewTitle = null;
		viewBottom = null;
		viewOther1 = null;
		viewTitle = null;
		viewMargin = null;

	}
	private  void finishAct(){
		finish();
		overridePendingTransition(0, R.anim.anima_activity_out);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishAct();
	}
}
