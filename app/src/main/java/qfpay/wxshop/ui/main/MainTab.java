package qfpay.wxshop.ui.main;

import java.lang.ref.SoftReference;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.ui.main.fragment.BusinessCommunityFragment_;
import qfpay.wxshop.ui.main.fragment.HuoYuanFragment_;
import qfpay.wxshop.ui.main.fragment.PopularizingFragment_;
import qfpay.wxshop.ui.main.fragment.ShopFragment_;
import android.support.v4.app.FragmentTransaction;

public enum MainTab {
	SHOP, HUOYUAN, TUI_GUANG, BUSINESS_COMMUNITY;
	
	private SoftReference<BaseFragment> fragmentRef;
	
	public BaseFragment getFragment() {
		if (fragmentRef == null) {
			return null;
		}
		return fragmentRef.get();
	}
	
	public void showFragment(FragmentTransaction fragmentTransaction, MainActivity activity) {
		if (activity.isFinishing()) {
			return;
		}
		if (fragmentRef == null || fragmentRef.get() == null) {
			fragmentRef = new SoftReference<BaseFragment>(newFragment());
		}
		
		if (!fragmentRef.get().isAdded()) {
			fragmentTransaction.add(R.id.frame_content, fragmentRef.get());
		} else {
			fragmentRef.get().onFragmentRefresh();
		}
		
		int fadein = android.R.anim.fade_in;
		int fadeout = android.R.anim.fade_out;
		fragmentTransaction.setCustomAnimations(fadein, fadeout, fadein, fadeout);
		fragmentTransaction.show(fragmentRef.get());
		hideOtherFragment(fragmentTransaction);
	}
	
	public void hide(FragmentTransaction fragmentTransaction) {
		if (fragmentRef == null || fragmentRef.get() == null) {
			return;
		}
		fragmentTransaction.hide(fragmentRef.get());
	}
	
	private BaseFragment newFragment() {
		switch (this) {
		case SHOP:
			return new ShopFragment_();
		case HUOYUAN:
			return new HuoYuanFragment_();
		case TUI_GUANG:
			return new PopularizingFragment_();
		case BUSINESS_COMMUNITY:
			return new BusinessCommunityFragment_();
		}
		return null;
	}
	
	public void clearFragment() {
		fragmentRef = null;
	}
	
	private void hideOtherFragment(FragmentTransaction fragmentTransaction) {
		if (SHOP != this) {
			SHOP.hide(fragmentTransaction);
		}
		if (HUOYUAN != this) {
			HUOYUAN.hide(fragmentTransaction);
		}
		if (TUI_GUANG != this) {
			TUI_GUANG.hide(fragmentTransaction);
		}
		if (BUSINESS_COMMUNITY != this) {
			BUSINESS_COMMUNITY.hide(fragmentTransaction);
		}
	}
	
	public static void clear() {
		MainTab.BUSINESS_COMMUNITY.clearFragment();
		MainTab.TUI_GUANG.clearFragment();
		MainTab.HUOYUAN.clearFragment();
		MainTab.SHOP.clearFragment();
	}
}
