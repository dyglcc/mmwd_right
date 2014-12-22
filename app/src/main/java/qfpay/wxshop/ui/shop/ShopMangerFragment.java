package qfpay.wxshop.ui.shop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import android.support.v4.view.ViewPager;

import com.indicator.TabPageIndicator;

@EFragment(R.layout.shop_manager)
public class ShopMangerFragment extends BaseFragment {
	@ViewById TabPageIndicator indicator;
	@ViewById ViewPager        pager;
	
	@AfterViews void initialize() {
		
	}
}
