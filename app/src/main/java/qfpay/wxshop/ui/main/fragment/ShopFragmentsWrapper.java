package qfpay.wxshop.ui.main.fragment;

import java.lang.ref.SoftReference;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import android.content.Context;

public enum ShopFragmentsWrapper {
	COMMODITY("click_shopmanagement"), PREVIEW("Preview"), STATISTICS(
			"shop statistic"),ORDER("click_order");

	String umenEventName = "";
	SoftReference<BaseFragment> fragmentRef;
	boolean isNeedRefresh;

	ShopFragmentsWrapper(String umenEventName) {
		this.umenEventName = umenEventName;
	}

	public static String getUmenEventName(int position) {
		switch (position) {
		case 0:
			return COMMODITY.umenEventName;
		case 1:
			return PREVIEW.umenEventName;
		case 2:
			return STATISTICS.umenEventName;
		case 3:
			return ORDER.umenEventName;
		}
		return "";
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
			return COMMODITY.getFragment(context);
		case 1:
			return PREVIEW.getFragment(context);
		case 2:
			return STATISTICS.getFragment(context);
		case 3:
			return ORDER.getFragment(context);
		}
		return null;
	}

	public void refresh() {
		isNeedRefresh = true;
	}

	public static void onFragmentSelect(int position, Context context) {
		switch (position) {
		case 0:
			COMMODITY.onFragmentSelect();
			break;
		case 1:
			PREVIEW.onFragmentSelect();
			break;
		case 2:
			STATISTICS.onFragmentSelect();
			break;
		case 3:
			ORDER.onFragmentSelect();
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
		case COMMODITY:
			fragmentRef = new SoftReference<BaseFragment>(
					new GoodFragment_());
			break;
		case PREVIEW:
			fragmentRef = new SoftReference<BaseFragment>(
					new CommonWebFragment_().init(WDConfig.getInstance()
							.getPreviewShopAddress()
							+ WxShopApplication.dataEngine.getShopId()
                            + "?ga_medium=android_mmwdapp_shoppreview_&ga_source=entrance", true,""));
			break;
		case STATISTICS:
			fragmentRef = new SoftReference<BaseFragment>(
					new StatFragment_());
			break;
		case ORDER:
			fragmentRef = new SoftReference<BaseFragment>(
					new OrderFragment_());
			break;
		}
	}

	public static void clear() {
		if (COMMODITY.fragmentRef != null) {
			COMMODITY.fragmentRef.clear();
		}
		if (PREVIEW.fragmentRef != null) {
			PREVIEW.fragmentRef.clear();
		}
		if (STATISTICS.fragmentRef != null) {
			STATISTICS.fragmentRef.clear();
		}
		if (ORDER.fragmentRef != null) {
			ORDER.fragmentRef.clear();
		}
	}
}