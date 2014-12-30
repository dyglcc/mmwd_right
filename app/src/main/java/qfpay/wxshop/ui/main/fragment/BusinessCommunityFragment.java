package qfpay.wxshop.ui.main.fragment;

import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.web.CommonWebFragment;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.MobAgentTools;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.indicator.TabPageIndicator;
import qfpay.wxshop.utils.T;

@EFragment(R.layout.businesscommunity_layout)
public class BusinessCommunityFragment extends BaseFragment {
	private static final int[] titles = {R.string.community_pagertitle_discover, R.string.community_pagertitle_ranginglist, R.string.community_pagertitle_commoditysource};

	private Map<String, String> header = new HashMap<String, String>();
	@ViewById TabPageIndicator indicator;
	@ViewById ViewPager        pager;
	
	FragmentPagerAdapter adapter;
	
	@AfterViews void init() {
		adapter = new BusinessCommunityPagerAdapter(getChildFragmentManager());
		refreshView();
	}
	
	@Override public void onFragmentRefresh() {
		refreshView();
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "clicle_Merchant circle");
		super.onFragmentRefresh();
	}
	
	@IgnoredWhenDetached void refreshView() {
		if (getActivity().isFinishing()) {
			return;
		}
		if (adapter != null) {
			pager.setAdapter(adapter);
			indicator.setViewPager(pager);
			indicator.setOnPageChangeListener((OnPageChangeListener) adapter);
			indicator.notifyDataSetChanged();
		}
	}
	
	class BusinessCommunityPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
		public BusinessCommunityPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override public Fragment getItem(int position) {
			CommonWebFragment fragment = new CommonWebFragment_();
			switch (position) {
			case 0:
				fragment.init(WDConfig.FAXIANGEMIAO, true, getString(R.string.share_faxiangemiao_title), ConstValue.SHARE_NAME_FINDMIAO, "",
						SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS);
				break;
			case 1:
				fragment.init(WDConfig.getInstance().getPaihangbang(), true,"");
				T.d("paihangbang + " + WDConfig.getInstance().getPaihangbang());
				break;
			case 2:
				fragment.init(WDConfig.getInstance().getCommoditySource(), true,"");
				T.d("paihangbang + " + WDConfig.getInstance().getCommoditySource());
				break;
			}
			fragment.onFragmentRefresh();
			return fragment;
		}

		@Override public int getCount() {
			return titles.length;
		}
		
		@Override public CharSequence getPageTitle(int position) {
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
				pointName = "faxiangemiao_webpage";
				break;
			case 2:
				pointName = "exchange_page";
				break;
			}
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), pointName);
		}
	}
}
