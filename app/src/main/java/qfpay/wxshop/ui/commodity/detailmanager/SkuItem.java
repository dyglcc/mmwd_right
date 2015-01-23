package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Context;
import android.view.ViewGroup;
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

    private SkuViewModel mViewModel;

    public SkuItem(Context context) {
        super(context);
    }

    public SkuItem setData(SkuViewModel viewModel) {
        this.mViewModel = viewModel;
        tv_name.setText(mViewModel.getName());
        tv_count.setText(mViewModel.getAmount());
        tv_price.setText(mViewModel.getPrice());
        return this;
    }

    public SkuItem setParentView(ItemDetailManagerActivity activity) {
        this.mActivity = activity;
        return this;
    }

    @Click void iv_edit() {
        mActivity.onSkuEditClick(mViewModel);
    }
}
