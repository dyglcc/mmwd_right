package qfpay.wxshop.ui.main.fragmentcontroller;

import qfpay.wxshop.ui.BusinessCommunity.DiscoveryFragment_;
import qfpay.wxshop.ui.BusinessCommunity.MyDynamicListFragment_;
import qfpay.wxshop.ui.BusinessCommunity.MyTopicListFragment_;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.web.CommonWebFragment;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.T;

public class BusinessCommunityFragmentWrapper extends BasePagerFragmentWrapper {

    @Override
    public String getUmengEventName(int position) {
        switch (position) {
            case 0:
                return "click_suisuinian";
            case 1:
                return "click_maijiaxiu";
            case 2:
                return "promote_namecard";
        }
        return "";
    }

    @Override
    public BaseFragment newFragment(int position) {
        CommonWebFragment fragment = new CommonWebFragment_();
        switch (position) {
            case 0:
                return new MyTopicListFragment_();
            case 1:
                return new MyDynamicListFragment_();
            case 2:
                fragment.init(WDConfig.getInstance().getPaihangbang(), true);
                T.d("paihangbang + " + WDConfig.getInstance().getCommoditySource());
                break;
            case 3:
                return new DiscoveryFragment_();
        }
        fragment.onFragmentRefresh();
        return fragment;
    }
}
