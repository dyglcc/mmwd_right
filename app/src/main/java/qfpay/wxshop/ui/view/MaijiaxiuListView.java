package qfpay.wxshop.ui.view;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.ManagePreViewActivity;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment_;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MaijiaxiuListView extends ListView {

	private View mFooterMOREView;

	private View mEmptyFotterView;
	private LayoutInflater mInflater;
	private Context mContext;
	private MaijiaxiuFragment fragment;
	public boolean isaddedEmptyFooter = false;

	public void setFragment(MaijiaxiuFragment fragment) {
		this.fragment = fragment;
	}

	public MaijiaxiuListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public MaijiaxiuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MaijiaxiuListView(Context context) {
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
		if (MaijiaxiuFragment_.data.isEmpty() && MaijiaxiuFragment_.nodata) {
			removeFooterView();
			addEmptyFooterView();
			return;
		}
		// 空列表 有数据
		if (MaijiaxiuFragment_.data.isEmpty() && !MaijiaxiuFragment_.nodata) {
			if (handler != null) {
				handler.sendEmptyMessage(MaijiaxiuFragment_.ACTION_GET_DATA);
			}
			return;
		}
		if (!MaijiaxiuFragment_.data.isEmpty()) {
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
		mEmptyFotterView = mInflater.inflate(R.layout.main_maijiaxiu_empty,
				null);
		mEmptyFotterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		this.addFooterView(mEmptyFotterView);
		isaddedEmptyFooter = true;

		Button btnSee = (Button) mEmptyFotterView
				.findViewById(R.id.btn_empty_see);
		btnSee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (fragment != null) {
					((MainActivity) fragment.getActivity()).onAddBuyersShow();
				}
				mEmptyFotterView.setVisibility(View.INVISIBLE);
			}
		});
		mEmptyFotterView.findViewById(R.id.tv_empty_link).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(fragment.getActivity(),
								ManagePreViewActivity.class);
						intent.putExtra(ConstValue.URL, "http://"
								+ WxShopApplication.app.getDomainMMWDUrl()
								+ "/h5/show.html?shopid=605");
						intent.putExtra(ConstValue.TITLE, "查看她人买家秀");
						fragment.startActivity(intent);
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
