package qfpay.wxshop.ui.BusinessCommunity;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.view.XScrollView;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;

/**
 * 显示商户圈中“发现”页面
 * @author zhangzhichao
 */
@EFragment(R.layout.business_community_discovery_parent)
public class DiscoveryFragment extends BaseFragment {
    @ViewById
    XScrollView scrollview;
    @ViewById
    ViewPager banner_view_pager;
    @ViewById
    LinearLayout pager_point;
    @ViewById
    ExpandableListView expandable_list_view;
    @Bean
    BusinessCommunityDataController businessCommunityDataController;
    BusinessCommunityService.DiscoveryDataWrapper dataWrapper;
    List<View> views;

    @AfterViews
    void init(){
        scrollview.setAutoLoadEnable(false);
        scrollview.setPullLoadEnable(false);
        scrollview.setPullRefreshEnable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ViewGroup view = (ViewGroup)layoutInflater.inflate(R.layout.business_community_discovery_child,null);
        scrollview.setContentView(view);
//        getDiscoveryData();
//        initPagerView();
    }

    void initPagerView(){
        views = new ArrayList<View>();
//        views.add();
    }

    class MyPagerAdapter extends PagerAdapter{
        private List<View> myViewList;
        public MyPagerAdapter(List<View> viewList){
            this.myViewList = viewList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(myViewList.get(position),0);
            return myViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(myViewList.get(position));
        }

        @Override
        public int getCount() {
            return myViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Background
    void getDiscoveryData(){
        try{
            dataWrapper = businessCommunityDataController.getDiscoveryData();
        }catch (Exception e){
            if(e.getMessage()!=null){
                T.i(e.getMessage());
            }
        }
        showDiscoveryData();
    }

    @UiThread
    void showDiscoveryData(){
        if(dataWrapper!=null){
            if(dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                System.out.println("获取发现数据返回--banner数据-->"+dataWrapper.data.getBanners().toString());

            }else{
                Toaster.l(getActivity(),dataWrapper.getResperr());
            }
        }else{
            Toaster.s(getActivity(),"数据加载失败，请检查网络！");
        }
    }
}
