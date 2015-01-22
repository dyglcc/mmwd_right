package qfpay.wxshop.ui.BusinessCommunity;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.view.XScrollView;

/**
 * 显示商户圈中“发现”页面
 * @author zhangzhichao
 */
@EFragment(R.layout.business_community_discovery_parent)
public class DiscoveryFragment extends BaseFragment {
    @ViewById
    XScrollView scrollview;

    @AfterViews
    void init(){
        scrollview.setAutoLoadEnable(false);
        scrollview.setPullLoadEnable(false);
        scrollview.setPullRefreshEnable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ViewGroup view = (ViewGroup)layoutInflater.inflate(R.layout.business_community_discovery_child,null);
        scrollview.setContentView(view);
    }
}
