package qfpay.wxshop.wheel;

import qfpay.wxshop.utils.MobAgentTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.CityBean;
import qfpay.wxshop.data.beans.ProvinceBean;
import qfpay.wxshop.utils.RegisterJsonTool;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class WheelMain {

	private View view;
	// private WheelView wv_year;
	// private WheelView wv_month;
	private WheelView wv_day;
	// private WheelView wv_hours;
	private WheelView wv_mins;
	public int screenheight;
	private boolean hasSelectTime;
	private TextView text;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public WheelMain(View view, TextView text) {
		super();
		this.view = view;
		hasSelectTime = false;
		this.text = text;
		setView(view);
	}

	public WheelMain(View view, boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}

	// public void initDateTimePicker(int day) {
	// this.initDateTimePicker(day, 0, 0);
	// }

	private int currentProvincePos;
	private int currentProvince;
	private int currentArea;

	private ArrayList<ProvinceBean> mProvinceBeans;
	private ArrayList<CityBean> mCitys;
	private ArrayList<String> strProvinces;
	private ArrayList<String> mCurrentAreas;

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker(Context context, int h, int m) {

		RegisterJsonTool.getInstance().init(context);

		currentProvince = h;
		currentArea = m;
		// 初始化日期

		mProvinceBeans = RegisterJsonTool.getInstance().parseProvinceBeans();
		strProvinces = RegisterJsonTool.getInstance().getStrProvince(
				mProvinceBeans);

		adapter = new ArrayWheelAdapter<String>(strProvinces,
				strProvinces.size());

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setAdapter(adapter);
		wv_day.setCyclic(false);
		currentProvincePos = initDatePos;
		// wv_day.setLabel("城市");// 添加文字
		wv_day.setCurrentItem(initDatePos);

		mCitys = RegisterJsonTool.getInstance().parseCityBeans();
//		strCities = RegisterJsonTool.getInstance().getStrCity(mCitys);

		mCurrentAreas = getAreasByProvince(h);
		adapterCity = new ArrayWheelAdapter<String>(mCurrentAreas, mCurrentAreas.size());
		// wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_mins = (WheelView) view.findViewById(R.id.min);

		// wv_hours.setVisibility(View.VISIBLE);
		wv_mins.setVisibility(View.VISIBLE);

		// wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		// wv_hours.setCyclic(true);// 可循环滚动
		// wv_hours.setLabel("省");// 添加文字
		// wv_hours.setCurrentItem(h);
		wv_mins.setAdapter(adapterCity);
		wv_mins.setCyclic(false);// 可循环滚动
		wv_mins.setCurrentItem(m);

		// 添加监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				currentProvincePos = newValue;
			}
		};
		// // 添加监听
		// OnWheelChangedListener wheelListener_hour = new
		// OnWheelChangedListener() {
		// public void onChanged(WheelView wheel, int oldValue, int newValue) {
		//
		// currentProvince = newValue;
		//
		// }
		// };
		// 添加监听
		OnWheelChangedListener wheelListener_min = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				currentArea = newValue;

			}
		};
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				
				mCurrentAreas = getAreasByProvince(currentProvincePos);
				adapterCity = new ArrayWheelAdapter<String>(mCurrentAreas, mCurrentAreas.size());
				
				wv_mins.setAdapter(adapterCity);
				wv_mins.setCyclic(false);// 可循环滚动
				wv_mins.setCurrentItem(0);
				text.setText(getSelectValue());
			}
		};
		
		OnWheelScrollListener scrollListenerArea = new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				
				text.setText(getSelectValue());
			}
		};
		wv_day.addChangingListener(wheelListener_day);
		// wv_hours.addChangingListener(wheelListener_hour);
		wv_mins.addChangingListener(wheelListener_min);

		wv_day.addScrollingListener(scrollListener);
		// wv_hours.addScrollingListener(scrollListener);
		wv_mins.addScrollingListener(scrollListenerArea);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		setTextSize();

	}
	private ArrayList<CityBean> currentCitybeans;
	private ArrayList<String> getAreasByProvince(int h) {

		ProvinceBean pb = mProvinceBeans.get(h);
		String no = pb.getId();
		
		currentCitybeans = RegisterJsonTool.getInstance().getCityBeanByProvinceId(no,mCitys);
		
		ArrayList<String> strsCurrentCitys = new ArrayList<String>();
		for(int i=0;i<currentCitybeans.size();i++){
			CityBean cb = currentCitybeans.get(i);
			strsCurrentCitys.add(cb.getcName());
			
		}
		return strsCurrentCitys;
	}

	protected String getSelectValue() {
		if (strProvinces != null && mCurrentAreas != null) {
			return strProvinces.get(currentProvincePos) + " "
					+ mCurrentAreas.get(currentArea);
		}
		return "";
	}

	private void setTextSize() {
		int textSize = 19;
//		if (hasSelectTime)
//			textSize = (screenheight / 100) * 3;
//		else
//			textSize = (screenheight / 100) * 3;
		wv_day.TEXT_SIZE = textSize;
		// wv_month.TEXT_SIZE = textSize;
		// wv_year.TEXT_SIZE = textSize;
		// wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;                                              
	}

	ArrayWheelAdapter<String> adapter;
	ArrayWheelAdapter<String> adapterCity;

	private int initDatePos = 0;

}
