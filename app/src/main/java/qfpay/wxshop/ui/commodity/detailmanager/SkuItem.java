package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;

/**
 * 界面上Sku的展示条目
 *
 * Created by LiFZhe on 1/20/15.
 */
@EViewGroup(R.layout.itemdetail_skuitem)
public class SkuItem extends LinearLayout {
    private ItemDetailManagerActivity mActivity;

    @ViewById TextView tv_name, tv_price, tv_count;
    @ViewById View     v_line;

    private SkuViewModel mViewModel;

    public SkuItem(Context context) {
        super(context);
    }

    public SkuItem setData(SkuViewModel viewModel) {
        this.mViewModel = viewModel;
        if (mViewModel.getName() == null || mViewModel.getName().equals("")) {
            tv_name.setVisibility(View.GONE);
        } else {
            tv_name.setVisibility(View.VISIBLE);
        }
        tv_name.setText(mViewModel.getName());
        tv_count.setText(mViewModel.getAmount());
        tv_price.setText(mViewModel.getPrice());
        return this;
    }

    public SkuItem setParentView(ItemDetailManagerActivity activity) {
        this.mActivity = activity;
        return this;
    }

    public void hideLine() {
        v_line.setVisibility(View.GONE);
    }

    public void showLine() {
        v_line.setVisibility(View.VISIBLE);
    }

    @Click void iv_edit() {
        mActivity.onSkuEditClick(((LinearLayout) getParent()).indexOfChild(this), mViewModel);
    }
}
