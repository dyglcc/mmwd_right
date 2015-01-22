package qfpay.wxshop.ui.BusinessCommunity;


import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;

/**
 * 显示商户圈中“我加入的话题”列表页面
 *
 * @author zhangzhichao
 */
@EFragment(R.layout.mydynamic_notes_list)
public class MyTopicListFragment extends BaseFragment implements
        XListView.IXListViewListener{
    private MyTopicsListAdapter myTopicsListAdapter;
    @ViewById
    XListView listView;
    @ViewById
    FrameLayout fl_indictor, input_reply_ll;
    @ViewById
    ImageView iv_indictor;
    @ViewById
    FrameLayout publish_note_fl;
    @DrawableRes
    Drawable commodity_list_refresh;
    @Bean
    BusinessCommunityDataController businessCommunityDataController;
    DataEngine dataEngine;
    BusinessCommunityService.TopicsListDataWrapper topicsListDataWrapper;

    @AfterViews
    void init() {
        dataEngine = new DataEngine(getActivity());
        initListView();
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setPullRefreshEnable(true);
        listView.setAutoLoadEnable(false);
        listView.setXListViewListener(this);
        listView.setPullLoadEnable(false);
        myTopicsListAdapter = new MyTopicsListAdapter();
        listView.setAdapter(myTopicsListAdapter);
        fl_indictor.setVisibility(View.VISIBLE);
        iv_indictor.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();
        getMyTopicList();
    }

    /**
     * 我的话题列表数据适配器
     */
    class MyTopicsListAdapter extends BaseAdapter {
        List<MyTopicBean> wrapperList = new ArrayList<MyTopicBean>();
        @Override
        public int getCount() {
            return wrapperList.size();
        }

        @Override
        public Object getItem(int position) {
            return wrapperList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TopicListItemView item = (TopicListItemView) convertView;
            if (item == null) {
                item = TopicListItemView_.build(getActivity());
            }
            item.setData(wrapperList.get(position));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_merchant_topic_details");
                    MyTopicDetailActivity_.intent(getActivity()).myTopicBean(wrapperList.get(position)).start();
                }
            });
            if (item != null) {
                return item;
            } else {
                return null;
            }
        }

        @Override
        public void notifyDataSetChanged() {
            processData();
            super.notifyDataSetChanged();
        }

        void processData(){
            wrapperList.clear();
            wrapperList.addAll(topicsListDataWrapper.data.items);
        }
    }

    //XListView的回调事件
    @Override
    public void onRefresh() {
        getMyTopicList();
    }

    @Override
    public void onLoadMore() {
    }

    @Background
    void getMyTopicList(){
        topicsListDataWrapper =  businessCommunityDataController.getMyTopicList(dataEngine.getUserId());
        showMyTopicList();
    }
    @UiThread
    void showMyTopicList(){
        if(topicsListDataWrapper!=null){
            if(topicsListDataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                listView.stopRefresh();
                fl_indictor.setVisibility(View.GONE);
                if (myTopicsListAdapter != null) {
                    listView.setVisibility(View.VISIBLE);
                    myTopicsListAdapter.notifyDataSetChanged();
                }
            }else{
                Toaster.l(getActivity(), topicsListDataWrapper.getResperr());
                fl_indictor.setVisibility(View.GONE);
                listView.stopRefresh();
            }
        }else{
            Toaster.s(getActivity(), "加载失败，请稍后重试！");
            fl_indictor.setVisibility(View.GONE);
            listView.stopRefresh();
        }
    }
}
