package qfpay.wxshop.ui.main.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.indicator.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.commodity.CommodityListFragment_;

/**
 * Created by dongyuangui
 */

@EFragment(R.layout.main_good_list)
public class GoodFragment extends BaseFragment implements ViewPager.OnPageChangeListener, OnShareLinstener {

    @ViewById
    View layout_own, layout_onkey_behalf;

    @ViewById
    FrameLayout layout_content;

    @ViewById
    TextView tv_self, tv_behalf;

    public CommodityListFragment_ commodityListFragment;
    public OneKeyBeHalfListFragment_ onkeyListFragment;

    private int selectedPos = 0;

    @AfterViews
    void init() {

        refreshView();

        if (selectedPos == 0) {
            viewPage1();
        } else {
            viewpage2();
        }

    }

    private void viewPage1() {
        tv_behalf.setTextColor(getResources().getColor(R.color.text_color_tab_unselected));
        tv_self.setTextColor(getResources().getColor(R.color.text_color_tab_selected));
        layout_onkey_behalf.setBackgroundResource(0);
        layout_own.setBackgroundResource(R.drawable.tab_bg_type2);
    }

    private void viewpage2() {
        tv_behalf.setTextColor(getResources().getColor(R.color.text_color_tab_selected));
        tv_self.setTextColor(getResources().getColor(R.color.text_color_tab_unselected));
        layout_onkey_behalf.setBackgroundResource(R.drawable.tab_bg_type2);
        layout_own.setBackgroundResource(0);
    }

    @Click
    void layout_own() {
        selectedPos = 0;
        changePager(0);
        viewPage1();
    }


    @Click
    void layout_onkey_behalf() {

        selectedPos = 1;
        changePager(1);
        viewpage2();
    }

    @IgnoredWhenDetached
    void refreshView() {
        if (getActivity().isFinishing()) {
            return;
        }
        GoodsPageAdapter adapter = new GoodsPageAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(adapter);
    }

    @Override
    public void onFragmentRefresh() {
//        Toaster.l(getActivity(),"good fragment onrefresh");
        super.onFragmentRefresh();
        if (onkeyListFragment != null && onkeyListFragment.isAdded()) {
            onkeyListFragment.onFragmentRefresh();
        }
        if (commodityListFragment != null && commodityListFragment.isAdded()) {
            commodityListFragment.onFragmentRefresh();
        }
    }

    @ViewById
    ViewPager pager;

    public void changePager(int index) {
        if (pager != null) {
            pager.setCurrentItem(index, true);
        }
        onFragmentRefresh();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            selectedPos = 0;
            viewPage1();
        } else {
            selectedPos = 1;
            viewpage2();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GoodsPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        public GoodsPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public BaseFragment getItem(int position) {
            if (position == 0) {
                if (commodityListFragment == null) {
                    commodityListFragment = new CommodityListFragment_();
                }
                return commodityListFragment;
            } else {
                if (onkeyListFragment == null) {
                    onkeyListFragment = new OneKeyBeHalfListFragment_();
                }
                return onkeyListFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                selectedPos = 0;
                viewPage1();
            } else {
                selectedPos = 1;
                viewpage2();
            }
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
    public void onShare(SharedPlatfrom which) {
    }

    @Override
    public String getShareFromName() {
        return "";
    }

}