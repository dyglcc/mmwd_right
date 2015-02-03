package qfpay.wxshop.ui.customergallery;

import java.lang.ref.SoftReference;

import android.support.v4.app.FragmentTransaction;
import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseFragment;

public enum ImagePage {
	GRID, DETAIL;

	SoftReference<BaseFragment> ref;

	void initFragment() {
		if (ref == null || ref.get() == null) {
			if (this == GRID) {
				ref = new SoftReference<BaseFragment>(new CustomerGalleryGridFragment_());
			}
			if (this == DETAIL) {
				ref = new SoftReference<BaseFragment>(new CustomerGalleryDetailsFragment_());
			}
		}
	}
	
	public void refresh() {
		initFragment();
		try {
			ref.get().onFragmentRefresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showFragment(FragmentTransaction ft) {
		initFragment();
		int fadein = android.R.anim.fade_in;
		int fadeout = android.R.anim.fade_out;
		ft.setCustomAnimations(fadein, fadeout, fadein, fadeout);
		if (!ref.get().isAdded()) {
			ft.add(R.id.ll_root, ref.get());
		}
		ft.show(ref.get());
		hideOthers(ft, this);
		if (this == DETAIL) {
			ft.addToBackStack(null);
		}
		ft.commitAllowingStateLoss();
	}

	public static void hideOthers(FragmentTransaction ft, ImagePage imagePage) {
		if (imagePage == GRID) {
			DETAIL.hideFragment(ft);
		} else {
			GRID.hideFragment(ft);
		}
	}
	
	public void hideFragment(FragmentTransaction ft) {
		if (ref != null && ref.get() != null) {
			ft.hide(ref.get());
		}
	}
	
	public static void clear() {
		if (GRID.ref != null) {
			GRID.ref.clear();
		}
		GRID.ref = null;
		if (DETAIL.ref != null) {
			DETAIL.ref.clear();
		}
		DETAIL.ref = null;
	}
}
