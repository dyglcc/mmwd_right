package qfpay.wxshop.wheel.timepicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.ManPromoActivity;
import qfpay.wxshop.activity.*;
import qfpay.wxshop.wheel.deflaut.ArrayWheelAdapter;
import qfpay.wxshop.wheel.deflaut.OnWheelChangedListener;
import qfpay.wxshop.wheel.deflaut.OnWheelScrollListener;
import qfpay.wxshop.wheel.deflaut.WheelView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimePickerWheel extends LinearLayout {
	private static final int ITEMINDEX_DEFLAUT = 0;
	private static final int ITEM_FONTSIZE_SP = 17;

	ImageButton btn_close;
	WheelView wv_Day, wv_Hour;
	TextView outsideView;
	ArrayList<Date> dates;
	ArrayList<String> dateStrs;
	ArrayList<String> hours = new ArrayList<String>(24);
	// ArrayList<String> citiesStr;
	int provinceIndex = ITEMINDEX_DEFLAUT;
	int cityIndex = ITEMINDEX_DEFLAUT;
	Calendar calendar;
	SimpleDateFormat format;
	SimpleDateFormat format3;

	public TimePickerWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.timepicker, this);
		wv_Day = (WheelView) findViewById(R.id.day);
		wv_Hour = (WheelView) findViewById(R.id.min);
		wv_Hour.setVisibility(View.VISIBLE);
		btn_close = (ImageButton) findViewById(R.id.btn_close);
		// getDates(days);
		initHours();
	}

	private void initHours() {
		for(int i=0;i<24;i++){
			hours.add(i+"时");
		}
	}

	private void getDates(int days) {
		if (dates != null) {
			dates.clear();
		} else {
			dates = new ArrayList<Date>();
		}

		calendar = Calendar.getInstance();
		dates.add(calendar.getTime());
		for (int i = 0; i < days; i++) {
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			dates.add(calendar.getTime());
		}
	}

	private int currentSelection;

	public void init(int days, SimpleDateFormat format,
			SimpleDateFormat format3, int Selection) {
		this.format = format;
		this.format3 = format3;
		currentSelection = Selection;
		getDates(days);
		getDateStr();
		ArrayWheelAdapter<String> provinceAdapter = new ArrayWheelAdapter<String>(
				getContext(), dateStrs.toArray(new String[0]));
		provinceAdapter.setTextSize(ITEM_FONTSIZE_SP);
		wv_Day.setViewAdapter(provinceAdapter);
		wv_Day.setCurrentItem(ITEMINDEX_DEFLAUT);
		wv_Day.setVisibleItems(5);
		wv_Day.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// wv_city.setViewAdapter(getCitiesStr(provinceIndex));
				setOutsideText(provinceIndex, ITEMINDEX_DEFLAUT);
			}
		});
		wv_Day.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				provinceIndex = newValue;
				wv_Hour.setViewAdapter(getCitiesStr(ITEMINDEX_DEFLAUT));
				wv_Hour.setCurrentItem(ITEMINDEX_DEFLAUT);
				// todo
			}
		});

		wv_Hour.setViewAdapter(getCitiesStr(ITEMINDEX_DEFLAUT));
		wv_Hour.setCurrentItem(ITEMINDEX_DEFLAUT);
		wv_Hour.setVisibleItems(7);
		wv_Hour.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				setOutsideText(provinceIndex, cityIndex);
			}
		});
		wv_Hour.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				cityIndex = newValue;
			}
		});
	}

	private void getDateStr() {

		if (dateStrs == null) {
			dateStrs = new ArrayList<String>();
		} else {
			dateStrs.clear();
		}
		for (int i = 0; i < dates.size(); i++) {
			dateStrs.add(format.format(dates.get(i)));
		}
	}

	public ArrayWheelAdapter<String> getCitiesStr(int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
				getContext(), hours.toArray(new String[0]));
		adapter.setTextSize(ITEM_FONTSIZE_SP);
		wv_Hour.setCurrentItem(ITEMINDEX_DEFLAUT);
		return adapter;
	}

	public void setOutsideText(int provinceIndex, int cityIndex) {
		if (outsideView == null) {
			return;
		}
		String dateStr = dateStrs.get(this.provinceIndex);
		if (!(this.cityIndex > hours.size())) {
			outsideView.setText(dateStr + " " +hours.get(cityIndex));
			// 生成需要的str
			Date date = dates.get(provinceIndex);
			if (currentSelection == ManPromoActivity.START_TIME_SELECTION) {
				ManPromoActivity_.startTime = format3.format(date) + cityIndex
						+ ":00:00";
			} else if (currentSelection == ManPromoActivity.END_TIME_SELECTION) {
				ManPromoActivity_.endTime = format3.format(date) + cityIndex
						+ ":00:00";
			}
		}
	}

	public void setCityTextView(TextView outsideView) {
		this.outsideView = outsideView;
	}

}
