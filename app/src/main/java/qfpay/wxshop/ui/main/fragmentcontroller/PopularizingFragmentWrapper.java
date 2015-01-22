package qfpay.wxshop.ui.main.fragmentcontroller;

import qfpay.wxshop.ui.lovelycard.LovelyCardFragment_;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment_;
import qfpay.wxshop.ui.main.fragment.SSNListFragment_;

public class PopularizingFragmentWrapper extends BasePagerFragmentWrapper {

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
		switch (position) {
		case 0:
			return new SSNListFragment_();
		case 1:
			return new MaijiaxiuFragment_();
		case 2:
			return new LovelyCardFragment_();
		}
		return new BaseFragment();
	}
}
