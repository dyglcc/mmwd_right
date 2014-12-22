package qfpay.wxshop.ui.view;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.ManagePreViewActivity;
import qfpay.wxshop.activity.SSNPublishActivity;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.MainTab;
import qfpay.wxshop.ui.main.fragment.HuoYuanFragment;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment_;
import qfpay.wxshop.ui.main.fragment.MineBuysListFragment;
import qfpay.wxshop.ui.main.fragment.MineBuysListFragment_;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MineBuyListView extends ListView {

	private View mFooterMOREView;

	private View mEmptyFotterView;
	private LayoutInflater mInflater;
	private Context mContext;
	private MineBuysListFragment fragment;
	public boolean isaddedEmptyFooter = false;

	public void setFragment(MineBuysListFragment fragment) {
		this.fragment = fragment;
	}

	public MineBuyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public MineBuyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MineBuyListView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		mInflater = LayoutInflater.from(mContext);
		mFooterMOREView = mInflater.inflate(R.layout.more_view_maijiaxiu, null);

	}

	public void checkFooterView() {
		
		if (MineBuysListFragment_.data.isEmpty() && MineBuysListFragment_.nodata) {
			removeFooterView();
			addEmptyFooterView();
			return;
		}
		// 空列表 有数据
		if (MineBuysListFragment_.data.isEmpty() && !MineBuysListFragment_.nodata) {
			if (handler != null) {
				handler.sendEmptyMessage(MineBuysListFragment_.ACTION_GET_DATA);
			}
			return;
		}
		if (!MineBuysListFragment_.data.isEmpty()) {
			addFooter();
			return;
		}

	}

	public View getmFooterView() {
		return mFooterMOREView;
	}

	public void removeFooterView() {
		if (getFooterViewsCount() > 0) {
			removeFooterView(mFooterMOREView);
		}
		removeFooterView(mEmptyFotterView);
	}

	private void addFooter() {

		if (mEmptyFotterView != null) {
			removeFooterView(mEmptyFotterView);
		}

		if (getFooterViewsCount() == 0) {
			addFooterView(mFooterMOREView);
		}
	}

	@SuppressLint("InflateParams")
	private void addEmptyFooterView() {
		if (mInflater == null) {
			mInflater = LayoutInflater.from(mContext);
		}
		mEmptyFotterView = mInflater.inflate(R.layout.main_minebuy_empty, null);
		mEmptyFotterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		this.addFooterView(mEmptyFotterView);
		isaddedEmptyFooter = true;

		ImageView btnSee = (ImageView) mEmptyFotterView
				.findViewById(R.id.btn_empty_see);
		btnSee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				HuoYuanFragment fragment = (HuoYuanFragment) MainTab.HUOYUAN
		                .getFragment();
		        fragment.changePager(0);
				
			}
		});
	}

	private Handler handler;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void removeEmptyfooterView() {
		this.removeFooterView(mEmptyFotterView);
	}

	public void setVisiAble() {
		if (mEmptyFotterView != null) {
			mEmptyFotterView.setVisibility(View.VISIBLE);
		}
	}

	public View getEmptyFooterView() {
		return mEmptyFotterView;
	}

}
