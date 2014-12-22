package qfpay.wxshop.ui.main.fragment;

import java.lang.ref.SoftReference;

import android.content.Context;

public enum HuoYuanFragmentsWrapper {
	OFFICIAL("click_official"),  MINE("click_mine");

	String umenEventName = "";
	SoftReference<BaseFragment> fragmentRef;
	boolean isNeedRefresh;

	HuoYuanFragmentsWrapper(String umenEventName) {
		this.umenEventName = umenEventName;
	}

	// public static String getUmenEventName(int position) {
	// switch (position) {
	// case 0:
	// return SSN.umenEventName;
	// case 1:
	// return MJX.umenEventName;
	// }
	// return "";
	// }

	public BaseFragment getFragment(Context context) {
		if (fragmentRef == null || fragmentRef.get() == null) {
			newFragment(context);
		}
		return fragmentRef.get();
	}

	public static BaseFragment getFragment(int position, Context context) {
		switch (position) {
		case 0:
			return OFFICIAL.getFragment(context);
//		case 1:
//			return FANS.getFragment(context);
		case 1:
			return MINE.getFragment(context);
		}
		return null;
	}

	public void refresh() {
		isNeedRefresh = true;
	}

	public static void onFragmentSelect(int position, Context context) {
		switch (position) {
		case 0:
			OFFICIAL.onFragmentSelect();
			break;
//		case 1:
//			FANS.onFragmentSelect();
//			break;
		case 1:
			MINE.onFragmentSelect();
			break;
		}
	}

	public void onFragmentSelect() {
		if (isNeedRefresh) {
			if (fragmentRef != null && fragmentRef.get() != null) {
				fragmentRef.get().onFragmentRefresh();
			}
			isNeedRefresh = false;
		}
	}

	public void newFragment(Context context) {
		switch (this) {
		case OFFICIAL:
			fragmentRef = new SoftReference<BaseFragment>(
					new OfficalListFragment_());
			break;
//		case FANS:
//			fragmentRef = new SoftReference<BaseFragment>(
//					new FansGoodsFragment_());
//			break;
		case MINE:
			fragmentRef = new SoftReference<BaseFragment>(
					new MineBuysListFragment_());
			break;
		}
	}

	public static void clear() {
		if (OFFICIAL.fragmentRef != null) {
			OFFICIAL.fragmentRef.clear();
		}
//		if (FANS.fragmentRef != null) {
//			FANS.fragmentRef.clear();
//		}
		if (MINE.fragmentRef != null) {
			MINE.fragmentRef.clear();
		}
	}

	public static void onDestory() {
		if (OFFICIAL.fragmentRef != null) {
			BaseFragment fragment = OFFICIAL.fragmentRef.get();
			if (fragment != null) {
				fragment = null;
			}
		}
//		if (FANS.fragmentRef != null) {
//			BaseFragment fragment = FANS.fragmentRef.get();
//			if (fragment != null) {
//				fragment = null;
//			}
//		}
		if (MINE.fragmentRef != null) {
			BaseFragment fragment = MINE.fragmentRef.get();
			if (fragment != null) {
				fragment = null;
			}
		}
	}
}