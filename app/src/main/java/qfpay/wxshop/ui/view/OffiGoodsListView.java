package qfpay.wxshop.ui.view;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.fragment.OfficalListFragment;
import qfpay.wxshop.ui.main.fragment.OfficalListFragment_;
import qfpay.wxshop.ui.main.fragment.SSNListFragment_;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class OffiGoodsListView extends ListView {

	private View mFooterMOREView;

	private View mEmptyFotterView;
	private LayoutInflater mInflater;
	private Context mContext;
	private OfficalListFragment fragment;
	public boolean isaddedEmptyFooter = false;

	public void setFragment(OfficalListFragment fragment) {
		this.fragment = fragment;
	}

	public OffiGoodsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public OffiGoodsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public OffiGoodsListView(Context context) {
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
//		if (OfficalListFragment_.data.isEmpty() && OfficalListFragment_.nodata) {
//			removeFooterView();
//			addEmptyFooterView();
//			return;
//		}
		// 空列表 有数据
		if ((OfficalListFragment_.data.isEmpty() && !OfficalListFragment_.nodata)) {
			if (handler != null) {
				handler.sendEmptyMessage(OfficalListFragment_.ACTION_GET_DATA);
			}
			return;
		}
		if (!OfficalListFragment_.data.isEmpty()) {
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
		mEmptyFotterView = mInflater.inflate(R.layout.main_ssn_empty, null);
		mEmptyFotterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		this.addFooterView(mEmptyFotterView);
		isaddedEmptyFooter = true;

//		Button btnSee = (Button) mEmptyFotterView
//				.findViewById(R.id.btn_empty_see);
//		btnSee.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (fragment != null) {
//					((MainActivity) fragment.getActivity()).onAddSsuinian();
//				}
//				mEmptyFotterView.setVisibility(View.INVISIBLE);
//			}
//		});
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
