package qfpay.wxshop.ui.main.fragmentGoods;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.commodity.CommodityListFragment;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.fragment.BaseFragment;

/**
 * Created by dongyuangui
 */

@EFragment(R.layout.main_good_list)
public class GoodFragment extends BaseFragment {


    @ViewById
    View layout_own, layout_onkey_behalf;

    @ViewById
    LinearLayout layout_content;

    CommodityListFragment commodityListFragment;
    OneKeyBeHalfFragment onkeyListFragment;

    @AfterViews
    void init() {

        layout_own();

    }

    @Click
    void layout_own() {
        MainActivity activity = (MainActivity) getActivity();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (commodityListFragment == null) {
            commodityListFragment = new CommodityListFragment();
        }
        ft.add(R.id.layout_content, commodityListFragment);
        ft.show(commodityListFragment);

        if (onkeyListFragment != null) {
            ft.hide(onkeyListFragment);
        }
    }


    @Click
    void setLayout_onkey_behalf() {
        MainActivity activity = (MainActivity) getActivity();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if (onkeyListFragment == null) {
            onkeyListFragment = new OneKeyBeHalfFragment();
        }
        ft.add(R.id.layout_content, onkeyListFragment);
        ft.show(onkeyListFragment);

        if (commodityListFragment != null) {
            ft.hide(commodityListFragment);
        }

    }

    @Override
    public void onFragmentRefresh() {
        super.onFragmentRefresh();
        if(onkeyListFragment.isAdded()){
            onkeyListFragment.onFragmentRefresh();
        }
        if(commodityListFragment.isAdded()){
            commodityListFragment.onFragmentRefresh();
        }
    }
}
