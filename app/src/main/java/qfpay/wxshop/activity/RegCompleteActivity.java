package qfpay.wxshop.activity;

import java.io.File;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.*;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

public class RegCompleteActivity extends BaseActivity implements Callback {

	private AQuery aQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.main_reg_complete);
		aQuery = new AQuery(this);
		initView();
		
		MobAgentTools.OnEventMobOnDiffUser(RegCompleteActivity.this, "creat_shop");

	}

	private Button tvBack;
	private Button btnSave;
	private View IvLovelyCat;
	private View ivBallonSrc_ballon;
	private View ivBallonSrc_Cheken;

	private Animation animationUP, animationDown;

	private void initView() {

		tvBack = (Button) findViewById(R.id.tv_back);
		btnSave = (Button) findViewById(R.id.btn_save);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// todo 进入mainActivity
				finish();
				startActivity(new Intent(RegCompleteActivity.this,
						MainActivity_.class));
				
				MobAgentTools.OnEventMobOnDiffUser(RegCompleteActivity.this, "enter_directly");
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setAvator();

		setShopName();

		findViewById(R.id.btn_inputRegBankActivity).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
//						startActivity(new Intent(RegCompleteActivity.this,
//								RegBankAccountActivity_.class));
						RegBankAccountActivity_.intent(RegCompleteActivity.this).from("regSteip1").start();
						finish();

						MobAgentTools.OnEventMobOnDiffUser(RegCompleteActivity.this, "fill_bankinfo");
						
					}
				});

		ivBallonSrc_ballon = findViewById(R.id.iv_ball);
		ivBallonSrc_Cheken = findViewById(R.id.iv_chick);
		IvLovelyCat = findViewById(R.id.iv_lovely_cat);

		ivBallonSrc_ballon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startAnimation();
			}
		});
		ivBallonSrc_Cheken.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startAnimation();
			}
		});

		animationUP = AnimationUtils.loadAnimation(RegCompleteActivity.this,
				R.anim.anim_ballon_up);

		animationDown = AnimationUtils.loadAnimation(RegCompleteActivity.this,
				R.anim.anim_checken_down);
		
		animationDown.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				 ivBallonSrc_ballon.setVisibility(View.GONE);
			     ivBallonSrc_Cheken.setVisibility(View.GONE);
			}
		});
		
	}

	protected void startAnimation() {
		ivBallonSrc_ballon.startAnimation(animationUP);
		ivBallonSrc_Cheken.startAnimation(animationDown);
	}

	private void setShopName() {
		String contract = WxShopApplication.dataEngine.getShopName();
		TextView tv = (TextView) findViewById(R.id.tv_shop_name);
		tv.setText(contract);
	}

	private void setAvator() {
		String selectedPicDIR = getIntent().getStringExtra("selectedPicDIR");
		if (selectedPicDIR != null && !"".equals(selectedPicDIR)) {
//			ImageOptions options = new ImageOptions();
//			options.memCache = false;
//			options.fileCache = false;
//			options.round = 100;
			findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
			File file = new File(selectedPicDIR);
			aQuery.id(R.id.iv_photo).progress(R.id.progressBar1).image(file, false, 300, new BitmapAjaxCallback(){
				@Override
				protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
					iv.setImageBitmap(Utils.toOvalBitmap(bm));
				}
			});
		} else {
			Bitmap defaultMap = BitmapFactory.decodeResource(getResources(), R.drawable.defaul_tavator);
			Bitmap map =  Utils.toOvalBitmap(defaultMap);
			defaultMap.recycle();
			defaultMap = null;
			aQuery.id(R.id.iv_photo).image(map);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case 1:

			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			AnimationDrawable drawable = (AnimationDrawable) IvLovelyCat
					.getBackground();
			drawable.start();

			AnimationDrawable drawable2 = (AnimationDrawable) ivBallonSrc_Cheken
					.getBackground();
			drawable2.start();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (animationDown != null) {
			animationDown = null;
		}
		if (animationUP != null) {
			animationUP = null;
		}
		if (aQuery != null) {
			aQuery = null;
		}
		super.onDestroy();
	}
}
