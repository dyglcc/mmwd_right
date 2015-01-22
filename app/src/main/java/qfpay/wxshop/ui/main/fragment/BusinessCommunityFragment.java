package qfpay.wxshop.ui.main.fragment;

import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController;
import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController.WrapperType;
import qfpay.wxshop.ui.view.BadgeView;
import qfpay.wxshop.ui.view.BadgeView2;
import qfpay.wxshop.ui.web.CommonWebFragment;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.MobAgentTools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indicator.TabPageIndicator;

import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;

@EFragment(R.layout.businesscommunity_layout)
public class BusinessCommunityFragment extends BaseFragment {
    private static final int[] titles = {R.string.community_pagertitle_mydynamic, R.string.community_pagertitle_discovery, R.string.community_pagertitle_ranginglist, R.string.community_pagertitle_mytopic};

    private Map<String, String> header = new HashMap<String, String>();
    @ViewById
    TabPageIndicator indicator;
    @ViewById
    ViewPager pager;
    BadgeView2 badgeView;
    FragmentPagerAdapter adapter;

    @AfterViews
    void init() {
        adapter = new BusinessCommunityPagerAdapter(getChildFragmentManager());
        refreshView();
    }

    @Override
    public void onFragmentRefresh() {
        refreshView();
        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "clicle_Merchant circle");
        super.onFragmentRefresh();
    }

    @IgnoredWhenDetached
    void refreshView() {
        if (getActivity().isFinishing()) {
            return;
        }
        if (adapter != null) {
            pager.setAdapter(adapter);
            indicator.setViewPager(pager);
            indicator.setOnPageChangeListener((OnPageChangeListener) adapter);
            indicator.notifyDataSetChanged();

            TextView textView = ((TextView) indicator.getTabView(0));
            badgeView = new BadgeView2(getActivity(), textView);
            badgeView.setPager(pager);
            badgeView.setBackgroundResource(R.drawable.icon_reddot2);
            badgeView.setWidth(Utils.dip2px(getActivity(), 10));
            badgeView.setHeight(Utils.dip2px(getActivity(), 10));
            badgeView.setGravity(Gravity.CENTER);
            badgeView.setTextSize(6);
            //初始化动态消息通知角标
            BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper dataWrapper = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
            if (dataWrapper.data.tag.equals("1") && dataWrapper.data.items.size() > 0) {
                showCommunityNotification("1", dataWrapper.data.items.size() + "");
            }
            if (dataWrapper.data.tag.equals("0") && dataWrapper.data.has_new.equals("1")) {
                showCommunityNotification("0", "0");
            }
            if (dataWrapper.data.tag.equals("0") && dataWrapper.data.has_new.equals("0")) {
                hideCommunityNotification();
            }
        }
    }

    class BusinessCommunityPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
        public BusinessCommunityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragmentController.get(WrapperType.BUSINESS_COMMUNITY).get(position).setParent(BusinessCommunityFragment.this);
//			CommonWebFragment fragment = new CommonWebFragment_();
//			switch (position) {
//			case 0:
//				fragment.init(WDConfig.FAXIANGEMIAO, true, getString(R.string.share_faxiangemiao_title), ConstValue.SHARE_NAME_FINDMIAO,
//						SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS);
//				break;
//			case 1:
//                fragment.init(WDConfig.getInstance().getCommoditySource(), true);
//				T.d("paihangbang + " + WDConfig.getInstance().getPaihangbang());
//				break;
//			case 2:
//                fragment.init(WDConfig.getInstance().getPaihangbang(), true);
//				T.d("paihangbang + " + WDConfig.getInstance().getCommoditySource());
//				break;
//			}
//			fragment.onFragmentRefresh();
//			return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(titles[position]);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            String pointName = "";
            switch (arg0) {
                case 0:
                    pointName = "click_merchant_dynamic";
                    break;
                case 2:
                    pointName = "click_merchant_ranklist";
                    break;
                case 3:
                    pointName = "click_merchant_mine";
            }
            MobAgentTools.OnEventMobOnDiffUser(getActivity(), pointName);
        }
    }

    /**
     * 动态 根据tag值显示角标 0 显示红点 1 显示数字加红点
     *
     * @param tag
     * @param count
     */
    @UiThread
    public void showCommunityNotification(String tag, String count) {
        if (tag.equals("0")) {
            badgeView.show();
        }
        if (tag.equals("1")) {
            badgeView.setText(count);
            badgeView.show();
        }
    }

    /**
     * 动态 隐藏角标
     */
    public void hideCommunityNotification() {
        badgeView.setText("");
        badgeView.hide();
    }
}
