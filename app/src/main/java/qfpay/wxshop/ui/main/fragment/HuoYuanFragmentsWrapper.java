package qfpay.wxshop.ui.main.fragment;

import java.lang.ref.SoftReference;

import android.content.Context;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.MobAgentTools;

public enum HuoYuanFragmentsWrapper {
    OFFICIAL("click_official"), FANS("click_fans"), MINE("click_mine"),CommoditySource("click_source");

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
                return  CommoditySource.getFragment(context);
            case 1:
                return FANS.getFragment(context);
            case 3:
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
            case 2:
                CommoditySource.onFragmentSelect();
                break;
            case 1:
                FANS.onFragmentSelect();
                break;
            case 3:
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
            case FANS:
                MobAgentTools.OnEventMobOnDiffUser(context, "click_Supply_of_goods_Fans_of_supply");
                fragmentRef = new SoftReference<BaseFragment>(
                        new CommonWebFragment_().init(WDConfig.getInstance().getFansLoadUrl(context), true,"粉丝货源"));
                break;
            case MINE:
                fragmentRef = new SoftReference<BaseFragment>(
                        new MineBuysListFragment_());
                break;
            case CommoditySource:
                fragmentRef = new SoftReference<BaseFragment>(
                        new CommonWebFragment_().init(WDConfig.getInstance().getCommoditySource(), true,"货源推荐"));
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
        if (CommoditySource.fragmentRef != null) {
            CommoditySource.fragmentRef.clear();
        }
    }

}