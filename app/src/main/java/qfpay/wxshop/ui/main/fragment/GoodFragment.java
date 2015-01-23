package qfpay.wxshop.ui.main.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.indicator.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.commodity.CommodityListFragment;
import qfpay.wxshop.ui.commodity.CommodityListFragment_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;

/**
 * Created by dongyuangui
 */

@EFragment(R.layout.main_good_list)
public class GoodFragment extends BaseFragment implements TabPageIndicator.TabClickListener, OnShareLinstener {

    @ViewById
    View layout_own, layout_onkey_behalf;

    @ViewById
    FrameLayout layout_content;

    CommodityListFragment_ commodityListFragment;
    OneKeyBeHalfFragment_ onkeyListFragment;

    @AfterViews
    void init() {

        refreshView();

    }

    @Click
    void layout_own() {
        changePager(0);
    }

    @Click
    void layout_onkey_behalf() {

        changePager(1);
    }

    @IgnoredWhenDetached
    void refreshView() {
        if (getActivity().isFinishing()) {
            return;
        }
        GoodsPageAdapter adapter = new GoodsPageAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
    }

    @Override
    public void onFragmentRefresh() {
        super.onFragmentRefresh();
        if (onkeyListFragment.isAdded()) {
            onkeyListFragment.onFragmentRefresh();
        }
        if (commodityListFragment.isAdded()) {
            commodityListFragment.onFragmentRefresh();
        }
    }

    @ViewById
    ViewPager pager;

    public void changePager(int index) {
        if (pager != null) {
            pager.setCurrentItem(index, true);
        }
    }

    class GoodsPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        public GoodsPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public BaseFragment getItem(int position) {
            if (position == 0) {
                if (commodityListFragment == null) {
                    return new CommodityListFragment_();
                } else {
                    return commodityListFragment;
                }
            } else {
                if (onkeyListFragment == null) {
                    return new OneKeyBeHalfFragment_();
                }else{
                    return onkeyListFragment;
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void onPageSelected(int position) {
            Toaster.l(getActivity(), " select pos");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTabClick(int newSelected) {
        if (getActivity() == null) {
            return;
        }
        if (newSelected == 0) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(), "Click_zijishangpin");
        } else {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(), "Click_yijiandaifashangpin");
        }
    }

    @Override
    public void onShare(SharedPlatfrom which) {
    }

    @Override
    public String getShareFromName() {
        return "";
    }

}