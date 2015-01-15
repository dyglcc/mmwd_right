package qfpay.wxshop.ui.BusinessCommunity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.MainActivity_;
import qfpay.wxshop.ui.main.MainTab;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.main.fragment.BusinessCommunityFragment;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

/**
 * 显示商户圈中“我的动态”列表页面
 */
@EFragment(R.layout.mydynamic_notes_list)
public class MyDynamicListFragment extends BaseFragment implements
        XListView.IXListViewListener, BusinessCommunityDataController.BusinessCommunityCallback {
    private MyDynamicNotesListAdapter myDynamicNotesListAdapter;
    @ViewById XListView   listView;
    @ViewById
    FrameLayout fl_indictor,input_reply_ll;
    @ViewById
    ImageView iv_indictor;
    @ViewById
    RelativeLayout lyt_publish;
    @ViewById EditText input_reply_et;
    @DrawableRes
    Drawable commodity_list_refresh;
    @Bean BusinessCommunityDataController businessCommunityDataController;
    TextView textView = null;
    @ViewById FrameLayout publish_note_fl;

    List<MyDynamicNoteListItemView> listItemViews;
    @AfterViews
    void init(){
        publish_note_fl.setVisibility(View.GONE);
        if(WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData().data.tag.equals("1")) {
            addNewMyNotificationLayout(WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData().data.items.size()+"");
        }
        refreshListView(RefreshFrom.LOADING);
        businessCommunityDataController.setCallback(this);
        initListView();
        listItemViews = new ArrayList<MyDynamicNoteListItemView>();
    }
    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setPullRefreshEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setPullLoadEnable(true);
        myDynamicNotesListAdapter = new MyDynamicNotesListAdapter();
        listView.setAdapter(myDynamicNotesListAdapter);
        listView.autoRefresh();
        startItemReplyAnimation();
    }

    /**
     * 我的动态帖子列表数据适配器
     */
    class MyDynamicNotesListAdapter extends BaseAdapter{
        List<MyDynamicItemBean0> wrapperList = new ArrayList<MyDynamicItemBean0>();

        public MyDynamicNotesListAdapter(){processData();}
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
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final int jumpToPostion = position+1;
            MyDynamicNoteListItemView item = (MyDynamicNoteListItemView)convertView;
            if(item==null){
                item = MyDynamicNoteListItemView_.build(getActivity(),businessCommunityDataController);
                if(!listItemViews.contains(item)){
                    listItemViews.add(item);
                }
            }
                final MyDynamicItemBean0 myDynamicItemBean0 = wrapperList.get(position);
                item.setData(myDynamicItemBean0,position);
            //点赞事件
            final MyDynamicNoteListItemView finalItem = item;
            item.link_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = myDynamicItemBean0.getId();
                    String isLiked = myDynamicItemBean0.getLike_data().getIs_liked();
                    int linkCount = Integer.parseInt(myDynamicItemBean0.getLike_data().getLike_count());
                    List<String> linkedUser = myDynamicItemBean0.getLike_data().getLiked_user();
                    JSONArray userIds = new JSONArray(linkedUser);
                    DataEngine dataEngine = new DataEngine(getActivity());
                    if(isLiked.equals("0")){//点赞
                        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_merchant_dynamic_like");
                        finalItem.link_data_iv.setBackgroundResource(R.drawable.mydynamic_note_link2);
                        isLiked="1";
                        myDynamicItemBean0.getLike_data().setIs_liked("1");
                        myDynamicItemBean0.getLike_data().setLike_count((linkCount+1)+"");
                        linkedUser.add(0,dataEngine.getUserId());
                        myDynamicItemBean0.getLike_data().setLiked_user(linkedUser);
                    }else{//取消点赞
                        isLiked="0";
                        finalItem.link_data_iv.setBackgroundResource(R.drawable.mydynamic_note_link);
                        myDynamicItemBean0.getLike_data().setIs_liked("0");
                        myDynamicItemBean0.getLike_data().setLike_count((linkCount - 1) + "");
                        linkedUser.remove(dataEngine.getUserId());
                        myDynamicItemBean0.getLike_data().setLiked_user(linkedUser);
                    }
                    wrapperList.set(position,myDynamicItemBean0);//更新列表数据
                    onSuccess();
                    businessCommunityDataController.setPriaseState(id,isLiked);//发送点赞请求
                }
            });
            if(item!=null){
                return item;
            }else {
                return null;
            }
        }
        @Override
        public void notifyDataSetChanged() {
            processData();
            super.notifyDataSetChanged();
        }
        public void processData(){
            wrapperList.clear();
            wrapperList.addAll(0,businessCommunityDataController.getCurrentList());
        }
    }

    @Background
    void startItemReplyAnimation(){
        try {
            Thread.currentThread().sleep(4*1000);
        if(listItemViews!=null&&listItemViews.size()>0){
            for(MyDynamicNoteListItemView itemView:listItemViews){
                itemView.showReplyContent();
            }
        }
            Thread.currentThread().sleep(4*1000);
            startItemReplyAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//自定义的网络请求操作的回调事件
    @Override  @UiThread
    @IgnoredWhenDetached
    public void onSuccess() {
        // 没有加判断是因为现在几乎所有的情况都需要刷新列表来完成
        System.out.println("--------------onSuccess");
        listView.stopRefresh();
        listView.stopLoadMore();
        refreshListView(RefreshFrom.REFRESH);
    }

    @Override  @UiThread @IgnoredWhenDetached
    public void onNetError() {
        listView.stopRefresh();
        listView.stopLoadMore();
        Toaster.s(getActivity(),"加载失败，请稍后重试！");
        System.out.println("--------------onNetError");
    }

    @Override  @UiThread @IgnoredWhenDetached
    public void onServerError( String msg) {
        Toaster.s(getActivity(),msg);
        listView.stopRefresh();
        listView.stopLoadMore();
        System.out.println("--------------onServerError");
    }

    @Override  @UiThread @IgnoredWhenDetached
    public void refresh() {
        System.out.println("--------------refresh");
    }
//XListView的回调事件
    @Override
    public void onRefresh() {
        System.out.println("listview onRefresh-------------");
        businessCommunityDataController.setCallback(this);
        businessCommunityDataController.setLast_fid("");
        businessCommunityDataController.reloadData();
        removeNotificationImage();//清除商户圈TAB角标通知
    }

    @Override
    public void onLoadMore() {
        businessCommunityDataController.setCallback(this);
        businessCommunityDataController.reloadData();
    }

    /**
     * 所有对列表的刷新都需要经过这个方法
     * 这个方法会对整体列表的状态进行fit
     */
    void refreshListView(RefreshFrom from) {
        switch (from) {
            case REFRESH:
                if (businessCommunityDataController.getCurrentList().isEmpty()) {
                    setListState(ListState.NULL);
                } else {
                    setListState(ListState.NORMAL);
                }
                break;
            case NETERROR:
                setListState(ListState.ERROR);
                break;
            case SERVERERROR:
                setListState(ListState.ERROR);
                break;
            case LOADING:
                setListState(ListState.LOADING);
                break;
        }
        listView.setPullLoadEnable(businessCommunityDataController.isHasNext());
        if (myDynamicNotesListAdapter != null) {
            myDynamicNotesListAdapter.notifyDataSetChanged();
        }
    }

    @IgnoredWhenDetached
    void setListState(ListState state) {
        System.out.println("设置列表状态-------"+state);
        if (state == ListState.NULL) {
            listView.setVisibility(View.INVISIBLE);
            fl_indictor.setVisibility(View.VISIBLE);
            iv_indictor.setImageResource(R.drawable.commodity_list_nodata);
        }

        if (state == ListState.LOADING) {
            listView.setVisibility(View.INVISIBLE);
            fl_indictor.setVisibility(View.VISIBLE);
            iv_indictor.setImageDrawable(commodity_list_refresh);
            ((AnimationDrawable) (commodity_list_refresh)).start();
        }

        if (state == ListState.NORMAL) {
            listView.setVisibility(View.VISIBLE);
            fl_indictor.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 表示刷新的来源
     */
    public enum RefreshFrom {
        NETERROR, SERVERERROR, REFRESH, LOADING
    }

    /**
     * 表示列表的状态
     */
    public enum ListState {
        NULL, LOADING, NORMAL, ERROR
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int code = data.getIntExtra("result", -1);
        switch (code) {
            case MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE://查看帖子返回
                int position = data.getIntExtra("position",-1);
                MyDynamicItemBean0 myDynamicItemBean0 = (MyDynamicItemBean0)data.getSerializableExtra("myDynamicItemBean0");
                if(position!=-1&&myDynamicItemBean0!=null){
                    businessCommunityDataController.getCurrentList().set(position,myDynamicItemBean0);
                    onSuccess();
                }
                break;
            case MaijiaxiuFragment.ACTION_PUBLISH_NOTE://发帖返回
                listView.autoRefresh();
                break;
        }
    }

    /**
     * 清除角标通知
     */
    void removeNotificationImage(){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper notificationData = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
        if(notificationData.data.tag.equals("0")&&notificationData.data.has_new.equals("1")){
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.hideCommunityNotifycation();
            BusinessCommunityFragment businessCommunityFragment = (BusinessCommunityFragment) MainTab.BUSINESS_COMMUNITY.getFragment();
            if(businessCommunityFragment!=null){
                businessCommunityFragment.hideCommunityNotification();
            }
        }

    }

    /**
     * 在列表头部显示新消息通知
     * @param num
     */
    @UiThread
    public void addNewMyNotificationLayout(String num){
        if(listView!=null){
        if(listView.getHeaderViewsCount()==1) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.businesscommunication_mydynamic_newmsg, null);
            textView = (TextView) view.findViewById(R.id.newMsg_tv);
            textView.setText("有" + num + "条新消息");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击消息后，列表头部隐藏，商户圈角标隐藏，动态角标隐藏
                    v.setVisibility(View.GONE);
                    BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper dataWrapper = new BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper();
                    dataWrapper = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
                    WxShopApplication.initBusinessCommunityAboutMyNotification();
                    MyNotificationListActivity_.intent(getActivity()).notificationDataWrapper(dataWrapper).start();
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.hideCommunityNotifycation();
                    BusinessCommunityFragment businessCommunityFragment = (BusinessCommunityFragment) MainTab.BUSINESS_COMMUNITY.getFragment();
                    if(businessCommunityFragment!=null){
                        businessCommunityFragment.hideCommunityNotification();
                    }
                }
            });
            listView.addHeaderView(view);
        }else{
            if(textView!=null){
                textView.setVisibility(View.VISIBLE);
                textView.setText("有" + num + "条新消息");
            }
        }

        }
    }

}
