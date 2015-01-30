package qfpay.wxshop.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.fragment.OneKeyBeHalfListFragment;
import qfpay.wxshop.ui.main.fragment.OneKeyBeHalfListFragment_;

public class OnekeyBehalfListView extends XListView{


    private View mEmptyFotterView;
    private LayoutInflater mInflater;
    private Context mContext;
    private OneKeyBeHalfListFragment fragment;
    public boolean isaddedEmptyFooter = false;

    public void setFragment(OneKeyBeHalfListFragment fragment) {
        this.fragment = fragment;
    }

    public OnekeyBehalfListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public OnekeyBehalfListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public OnekeyBehalfListView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void checkFooterView() {
		if (OneKeyBeHalfListFragment_.data.isEmpty() && OneKeyBeHalfListFragment_.nodata) {
			removeFooterView();
			addEmptyFooterView();
			return;
		}
        // 空列表 有数据
        if (OneKeyBeHalfListFragment_.data.isEmpty()){
            if (handler != null) {
                handler.sendEmptyMessage(OneKeyBeHalfListFragment.ACTION_GET_DATA);
            }
            return;
        }

    }
    public void removeFooterView() {
        removeFooterView(mEmptyFotterView);
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

        Button btnSee = (Button) mEmptyFotterView
                .findViewById(R.id.btn_empty_see);
        btnSee.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (fragment != null) {
                    ((MainActivity) fragment.getActivity()).onAddSsuinian();
                }
                mEmptyFotterView.setVisibility(View.INVISIBLE);
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

