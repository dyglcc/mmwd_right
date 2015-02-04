package qfpay.wxshop.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.GoodMSBean;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.CreatePanicImpl;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.commodity.CommodityDataController;
import qfpay.wxshop.ui.view.MoneyEditTextView;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import qfpay.wxshop.wheel.timepicker.TimePickerWheel;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

@EActivity(R.layout.main_man_promo)
public class ManPromoActivity extends BaseActivity {

	@ViewById
	ImageView iv_good_pic;
	@ViewById
	MoneyEditTextView etProPrice;
	@ViewById
	TextView etEndTime, tv_price, etStartTime, tv_title;
	@ViewById
	ImageButton btn_back, btn_close;
	@ViewById
	Button btn_save;
	@ViewById(R.id.iv_progress_load)
	ImageView ivProgress;
	@ViewById
	View timepickerview, layout_progress_load, tv_endtime_front;
	@ViewById(R.id.tv_province_front)
	View tvProvinceFront;

	AQuery aq;
	Calendar calendar;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format = new SimpleDateFormat("MM月dd日 E HH时");
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat formatInit = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日 E");
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd ");
	@Bean CommodityDataController commodityDataController;

	public static String startTime;
	public static String endTime;

	private Animation loadAnimation;
	private Animation closeAnimation;

	public static final int START_TIME_SELECTION = 0;
	public static final int END_TIME_SELECTION = START_TIME_SELECTION + 1;
	@Extra
	GoodsBean gb;
	@Extra
	int pos;
	@Extra
	String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void init() {
		if (gb == null) {
			Toaster.s(ManPromoActivity.this, "空数据");
			return;
		}
		if (gb.getPriceStr() == null) {
			Toaster.s(ManPromoActivity.this, "出错了，商品怎么没有价格呢");
			return;
		}
		calendar = Calendar.getInstance();
		aq = new AQuery(ManPromoActivity.this);
		aq.id(iv_good_pic).image(gb.getImageUrl());
		tv_price.setText(gb.getPriceStr());
		Date date = calendar.getTime();
		etStartTime.setText(format.format(date));
		startTime = formatInit.format(date);

		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date dateTommorw = calendar.getTime();
		etEndTime.setText(format.format(dateTommorw));
		endTime = formatInit.format(dateTommorw);

		tv_title.setText("设置秒杀活动");

		initPickerViews();

		etProPrice.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					if (timepickerview.getVisibility() == View.VISIBLE) {
						timepickerview.setVisibility(View.GONE);
					}
				}else{
					// 如果将金钱规整
					
				}

			}
		});
		etProPrice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (timepickerview.getVisibility() == View.VISIBLE) {
					timepickerview.setVisibility(View.GONE);
				}
			}
		});
	}

	@Click
	void btn_save() {
		// btn_save.setText("设置中...");
		final String priceStr = etProPrice.getText().toString();
		
		Pattern pattern = Pattern.compile("^0+$");

		if (priceStr.equals("") || priceStr.equals("0.00")
				|| priceStr.equals("0") || priceStr.equals("0.0")
				|| priceStr.equals("0.") || priceStr.endsWith(".0") || priceStr.equals(".00") ||priceStr.equals(".")) {
			Toaster.s(ManPromoActivity.this, "请输入秒杀价格");
			return;
		}
		Matcher matcher= pattern.matcher(priceStr);
		if(matcher.find()){
			Toaster.s(ManPromoActivity.this, "秒杀价格有误");
			return;
		}
		float priceF = Float.parseFloat(priceStr);
		float oldPrice = Float.parseFloat(gb.getPriceStr());

		if (priceF >= oldPrice) {
			Toaster.s(ManPromoActivity.this, "秒杀价格要小于原价");
			return;
		}
		if (etStartTime.getText().toString().equals("")) {
			Toaster.s(ManPromoActivity.this, "请设置秒杀开始时间");
			return;
		}
		if (etEndTime.getText().toString().equals("")) {
			Toaster.s(ManPromoActivity.this, "请设置秒杀结束时间");
			return;
		}
		if (MaxDate(endTime, startTime)) {
			Toaster.s(ManPromoActivity.this, "结束时间有误");
			return;
		}

		layout_progress_load.setVisibility(View.VISIBLE);

		AbstractNet net = new CreatePanicImpl(ManPromoActivity.this);
		Bundle bun = new Bundle();

		bun.putString("goodid", gb.getGoodsId());
		bun.putString("promoPrice", priceStr);
		bun.putString("startTime", startTime);
		bun.putString("endTime", endTime);
		net.request(bun, new MainHandler(ManPromoActivity.this, handler) {

			@Override
			public void onSuccess(Bundle bundle) {

				Toaster.s(ManPromoActivity.this, "设置成功");

				GoodMSBean gms = new GoodMSBean();
				gms.setId(gb.getGoodsId());
				gms.setNewprice(priceStr);
				
				SalesPromotionModel salesPromotionModel = new SalesPromotionModel();
				salesPromotionModel.setCommodityID(Integer.parseInt(gb.getGoodsId(), 10));
				salesPromotionModel.setPromotionPrice(Float.parseFloat(priceStr));
				commodityDataController.setPromotion(salesPromotionModel);
				
				finish();
				ManPromoSuccessActivity_.intent(ManPromoActivity.this).gb(gb)
						.gms(gms).from(from).pos(pos).start();
			}

			@Override
			public void onFailed(Bundle bundle) {

			}
		});
	}

	@Click
	void btn_back() {
		finish();
	}

	@Click
	void tv_endtime_front() {
		Utils.hideSoftKeyboard(ManPromoActivity.this);
		if (timepickerview.getVisibility() != View.VISIBLE) {
			timepickerview.startAnimation(loadAnimation);
		}
		etEndTime.requestFocus();
		((TimePickerWheel) timepickerview).setCityTextView(etEndTime);
		((TimePickerWheel) timepickerview).init(30, format2, format3,
				END_TIME_SELECTION);
	}

	@Click
	void btn_close() {
		if (timepickerview.getVisibility() == View.VISIBLE) {
			timepickerview.startAnimation(closeAnimation);
		}
	}

	@Click(R.id.tv_province_front)
	void startTimeClick() {
		Utils.hideSoftKeyboard(ManPromoActivity.this);
		if (timepickerview.getVisibility() != View.VISIBLE) {
			timepickerview.startAnimation(loadAnimation);
		}
		etStartTime.requestFocus();
		((TimePickerWheel) timepickerview).setCityTextView(etStartTime);
		((TimePickerWheel) timepickerview).init(10, format2, format3,
				START_TIME_SELECTION);
	}

	private void initPickerViews() {

		loadAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_timepicker);
		closeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_timepicker_close);

		closeAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				timepickerview.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// etTime.requestFocus();
				timepickerview.setVisibility(View.GONE);
			}
		});

		loadAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				timepickerview.setVisibility(View.VISIBLE);
				// initSelectTime();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		// 可以调整到后台线程去做
		// TimePickerWheel wheel = (TimePickerWheel) timepickerview;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (timepickerview.getVisibility() == View.VISIBLE) {
			timepickerview.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	private boolean MaxDate(String str, String str1) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		Date date1 = null;
		try {
			date = format.parse(str);
			date1 = format.parse(str1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date.getTime() <= date1.getTime() ? true : false;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
				layout_progress_load.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}

		};
	};

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
}
