package qfpay.wxshop.ui.main.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.main.MainActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.indicator.TabPageIndicator;
import com.indicator.TabPageIndicator.TabClickListener;
/**
 * 货源tab fragment
 * */
@SuppressLint("DefaultLocale") @EFragment(R.layout.huoyuan_manager)
public class HuoYuanFragment extends BaseFragment implements TabClickListener, OnShareLinstener {
	private static final String[] CONTENT = new String[] {"官方货源","粉丝货源","货源推荐","已购货源"};
	
	@ViewById ViewPager               pager;
	@ViewById TabPageIndicator        indicator;

	private   OnShareLinstener        mShareLinstener;
	
	@AfterViews
	void init() {
		refreshView(false);
	}
	
	@Override public void onFragmentRefresh() {
		refreshView(true);
	}
	
	@IgnoredWhenDetached void refreshView(boolean isRemovePage) {
		if (getActivity().isFinishing()) {
			return;
		}
		HuoyuanItemAdapter adapter = new HuoyuanItemAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		indicator.setmClickTabListener(this);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(adapter);
		indicator.notifyDataSetChanged();
	}
	
	public void changePager(int index) {
		if(pager!=null){
			pager.setCurrentItem(index, true);
		}
	}

	
	class HuoyuanItemAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
		public HuoyuanItemAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public BaseFragment getItem(int position) {
			return HuoYuanFragmentsWrapper.getFragment(position, getActivity());
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
		
		@Override
		public void onPageSelected(int position) {
			HuoYuanFragmentsWrapper.onFragmentSelect(position, getActivity());
			
			Fragment fragment = HuoYuanFragmentsWrapper.getFragment(position, getActivity());
			MainActivity activity = (MainActivity) getActivity();
			if (fragment instanceof OnShareLinstener) {
				mShareLinstener = (OnShareLinstener) fragment;
				activity.showShareButton(mShareLinstener);
			} else {
				mShareLinstener = null;
				activity.hideShareButton();
			}
		}
		@Override public void onPageScrollStateChanged(int arg0) { }
		@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onTabClick(int newSelected) {
		if(getActivity() == null){
			return;
		}
	}

	@Override
	public void onShare(SharedPlatfrom which) {
		if (mShareLinstener != null) {
			mShareLinstener.onShare(which);
		}
	}

	@Override
	public String getShareFromName() {
		if (mShareLinstener != null) {
			return mShareLinstener.getShareFromName();
		}
		return "";
	}
}
