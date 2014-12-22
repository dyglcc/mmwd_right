package qfpay.wxshop.wheel.deflaut;

import java.util.ArrayList;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.CityBean;
import qfpay.wxshop.data.beans.ProvinceBean;
import qfpay.wxshop.utils.RegisterJsonTool;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityPickerWheel extends LinearLayout {
	private static final int ITEMINDEX_DEFLAUT = 0;
	private static final int ITEM_FONTSIZE_SP = 17;
	
	ImageButton btn_close;
	WheelView wv_province, wv_city;
	TextView outsideView;
	ArrayList<ProvinceBean> mProvinceBeans;
	ArrayList<CityBean> cities;
	ArrayList<String> citiesStr;
	int provinceIndex = ITEMINDEX_DEFLAUT;
	int cityIndex = ITEMINDEX_DEFLAUT;
	
	public CityPickerWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.timepicker, this);
		wv_province = (WheelView) findViewById(R.id.day);
		wv_city = (WheelView) findViewById(R.id.min);
		wv_city.setVisibility(View.VISIBLE);
		btn_close = (ImageButton) findViewById(R.id.btn_close);
		RegisterJsonTool.getInstance().init(context);
		mProvinceBeans = RegisterJsonTool.getInstance().parseProvinceBeans();
		cities = RegisterJsonTool.getInstance().parseCityBeans();
		init();
	}
	
	public void init() {
		ArrayList<String> provinceStr = RegisterJsonTool.getInstance().getStrProvince(mProvinceBeans);
		ArrayWheelAdapter<String> provinceAdapter = new ArrayWheelAdapter<String>(getContext(), provinceStr.toArray(new String[0]));
		provinceAdapter.setTextSize(ITEM_FONTSIZE_SP);
		wv_province.setViewAdapter(provinceAdapter);
		wv_province.setCurrentItem(ITEMINDEX_DEFLAUT);
		wv_province.setVisibleItems(5);
		wv_province.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				wv_city.setViewAdapter(getCitiesStr(provinceIndex));
				setOutsideText(provinceIndex, ITEMINDEX_DEFLAUT);
			}
		});
		wv_province.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				provinceIndex = newValue;
			}
		});
		
		wv_city.setViewAdapter(getCitiesStr(ITEMINDEX_DEFLAUT));
		wv_city.setCurrentItem(ITEMINDEX_DEFLAUT);
		wv_city.setVisibleItems(7);
		wv_city.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				setOutsideText(provinceIndex, cityIndex);
			}
		});
		wv_city.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				cityIndex = newValue;
			}
		});
	}
	
	public ArrayWheelAdapter<String> getCitiesStr(int index) {
		ArrayList<CityBean> citiesWithProvinceid = RegisterJsonTool.getInstance().getCityBeanByProvinceId(mProvinceBeans.get(index).getId(), cities);
		citiesStr = RegisterJsonTool.getInstance().getStrCity(citiesWithProvinceid);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(getContext(), citiesStr.toArray(new String[0]));
		adapter.setTextSize(ITEM_FONTSIZE_SP);
		wv_city.setCurrentItem(ITEMINDEX_DEFLAUT);
		return adapter;
	}
	
	public void setOutsideText(int provinceIndex, int cityIndex) {
		if (outsideView == null) {
			return;
		}
		String provinceStr = RegisterJsonTool.getInstance().getStrProvince(mProvinceBeans).get(this.provinceIndex);
		if (!(this.cityIndex > citiesStr.size())) {
			String cityStr = citiesStr.get(this.cityIndex);
			outsideView.setText(provinceStr + " " + cityStr);
		}
	}
	
	public void setCityTextView(TextView outsideView) {
		this.outsideView = outsideView;
	}
}
