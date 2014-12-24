package qfpay.wxshop.ui.main.fragment;

import java.lang.ref.SoftReference;

import android.content.Context;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.CommonWebFragment_;

public enum HuoYuanFragmentsWrapper {
	OFFICIAL("click_official"),  FANS("click_fans"),  MINE("click_mine");

	String umenEventName = "";
	SoftReference<BaseFragment> fragmentRef;
	boolean isNeedRefresh;

	HuoYuanFragmentsWrapper(String umenEventName) {
		this.umenEventName = umenEventName;
	}

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
		case 2:
			return MINE.getFragment(context);
		case 1:
			return FANS.getFragment(context);
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
		case 2:
            MINE.onFragmentSelect();
            break;
		case 1:
			FANS.onFragmentSelect();
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
		case FANS:
                fragmentRef = new SoftReference<BaseFragment>(
                        new CommonWebFragment_().init(WDConfig.getInstance().getFansLoadUrl(context), true));
                break;
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
            if (FANS.fragmentRef != null) {
                FANS.fragmentRef.clear();
            }
            if (MINE.fragmentRef != null) {
                MINE.fragmentRef.clear();
		}
	}

}