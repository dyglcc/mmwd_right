package qfpay.wxshop.ui.main.fragmentcontroller;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.BusinessCommunity.DiscoveryFragment_;
import qfpay.wxshop.ui.BusinessCommunity.MyDynamicListFragment_;
import qfpay.wxshop.ui.BusinessCommunity.MyTopicListFragment_;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.ui.web.CommonWebFragment;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.T;

public class BusinessCommunityFragmentWrapper extends BasePagerFragmentWrapper {

    @Override
    public String getUmengEventName(int position) {
        switch (position) {
            case 0:
                return "click_merchant_mine";
            case 1:
                return "click_merchant_dynamic";
            case 2:
                return "click_merchant_ranklist";
            case 3:
                return "click_merchant_fxgm";
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
                fragment.init(WDConfig.FAXIANGEMIAO, true, "", ConstValue.SHARE_NAME_FINDMIAO,
						SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS);
                break;
//                return new DiscoveryFragment_();
        }
        fragment.onFragmentRefresh();
        return fragment;
    }
}
