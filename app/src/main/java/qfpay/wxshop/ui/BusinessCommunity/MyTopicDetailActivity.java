package qfpay.wxshop.ui.BusinessCommunity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.makeramen.RoundedImageView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyDynamicItemLinkDataBean;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.MainActivity_;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

/**
 * 我的话题详情页
 */
@EActivity(R.layout.mydynamic_notes_list)
public class MyTopicDetailActivity extends BaseActivity implements XListView.IXListViewListener,BusinessCommunityDataController.BusinessCommunityCallback{
    private OneTopicNotesListAdapter oneTopicNotesListAdapter;
    @Extra
    MyTopicBean myTopicBean;
    @ViewById XListView   listView;
    @ViewById
    FrameLayout fl_indictor,input_reply_ll,publish_note_fl;
    @ViewById
    ImageView iv_indictor;
    @ViewById
    RelativeLayout lyt_publish ,tool_bar;
    @ViewById EditText input_reply_et;
    @DrawableRes
    Drawable commodity_list_refresh;
    @Bean BusinessCommunityDataController businessCommunityDataController;
    @ViewById TextView tv_title;
    @ViewById ImageView publish_note;
    @ViewById View publish_note_line;
    final AnimatorSet animatorSet = new AnimatorSet();
    @AfterViews
    void init(){
        ActionBar bar = getSupportActionBar();
        bar.hide();//隐藏默认actionbars
        tool_bar.setVisibility(View.VISIBLE);
        businessCommunityDataController.getMyTopicBeanList().clear();
        businessCommunityDataController.setTopic_last_fid("");
        refreshListView(RefreshFrom.LOADING);
        businessCommunityDataController.setCallback(this);
        initListView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setPullRefreshEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setPullLoadEnable(true);
        tv_title.setText(myTopicBean.getG_name());
        businessCommunityDataController.getNotesListOfTopicFromServer(myTopicBean.getId());
        oneTopicNotesListAdapter = new OneTopicNotesListAdapter();
        listView.setAdapter(oneTopicNotesListAdapter);
//        startItemReplyAnimation();
    }
//    @Background
//    void startItemReplyAnimation(){
//        try {
//            Thread.currentThread().sleep(4*1000);
//            if(listItemViews!=null&&listItemViews.size()>0){
//                for(MyDynamicNoteListItemView itemView:listItemViews){
//                    itemView.showReplyContent();
//                }
//            }
//            Thread.currentThread().sleep(4*1000);
//            startItemReplyAnimation();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 某一小组帖子列表数据适配器
     */
    class OneTopicNotesListAdapter extends BaseAdapter {
        List<MyDynamicItemBean0> wrapperList = new ArrayList<MyDynamicItemBean0>();

        public OneTopicNotesListAdapter(){processData();}
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
            final int jumpToPostion = position+1;
            MyDynamicNoteListItemView item = (MyDynamicNoteListItemView)convertView;
            if(item==null){
                item = MyDynamicNoteListItemView_.build(MyTopicDetailActivity.this,businessCommunityDataController);
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
                    DataEngine dataEngine = new DataEngine(MyTopicDetailActivity.this);
                    MobAgentTools.OnEventMobOnDiffUser(MyTopicDetailActivity.this,"click_merchant_dynamic_like");
                    if(isLiked.equals("0")){//点赞
                        isLiked="1";
                        myDynamicItemBean0.getLike_data().setIs_liked("1");
                        myDynamicItemBean0.getLike_data().setLike_count((linkCount+1)+"");
                        linkedUser.add(dataEngine.getUserId());
                        myDynamicItemBean0.getLike_data().setLiked_user(linkedUser);
                    }else{//取消点赞
                        isLiked="0";
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
            publish_note_fl.setVisibility(View.VISIBLE);
            startAnimation();
            super.notifyDataSetChanged();
        }
        public void processData(){
            wrapperList.clear();
            wrapperList.addAll(0,businessCommunityDataController.getNotesListOfOneTopic());
        }
    }
    //自定义的网络请求操作的回调事件
    @Override  @UiThread
    public void onSuccess() {
        // 没有加判断是因为现在几乎所有的情况都需要刷新列表来完成
        listView.stopRefresh();
        listView.stopLoadMore();
        refreshListView(RefreshFrom.REFRESH);
    }

    @Override  @UiThread
    public void onNetError() {
        listView.stopRefresh();
        listView.stopLoadMore();
        Toaster.s(this, "加载失败，请稍后重试！");
    }

    @Override  @UiThread
    public void onServerError( String msg) {
        Toaster.s(this,msg);
        listView.stopRefresh();
        listView.stopLoadMore();
    }

    @Override  @UiThread
    public void refresh() {
    }
    //XListView的回调事件
    @Override
    public void onRefresh() {
        businessCommunityDataController.setTopic_last_fid("");
        businessCommunityDataController.setCallback(this);
        businessCommunityDataController.getNotesListOfTopicFromServer(myTopicBean.getId());
    }

    @Override
    public void onLoadMore() {
        businessCommunityDataController.getNotesListOfTopicFromServer(myTopicBean.getId());
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
        if (oneTopicNotesListAdapter != null) {
            oneTopicNotesListAdapter.notifyDataSetChanged();
        }
    }

    void setListState(ListState state) {
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
        if(resultCode== Activity.RESULT_OK){
            int code = data.getIntExtra("result", -1);
            switch (code) {
                case MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE://查看帖子返回
                    int position = data.getIntExtra("position",-1);
                    MyDynamicItemBean0 myDynamicItemBean0 = (MyDynamicItemBean0)data.getSerializableExtra("myDynamicItemBean0");
                    if(position!=-1&&myDynamicItemBean0!=null){
                        businessCommunityDataController.getNotesListOfOneTopic().set(position,myDynamicItemBean0);
                        onSuccess();
                    }
                    break;
                case MaijiaxiuFragment.ACTION_PUBLISH_NOTE://发帖返回
                    listView.autoRefresh();
                    break;
            }
        }
    }
    /**
     * 返回按钮点击事件
     */
    @Click
    void btn_back(){
        this.finish();
    }

    /**
     * 发帖按钮点击事件
     */
    @Click
    void publish_note(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_topic_release");
        PublishNoteActivity_.intent(this).myTopicBean(myTopicBean).startForResult(MaijiaxiuFragment.ACTION_PUBLISH_NOTE);
    }

    /**
     * 发帖按钮的动画
     */
    void startAnimation(){
        ViewWrapper viewWrapper = new ViewWrapper(publish_note_line);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(publish_note, "translationX", 0, Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0),
                ObjectAnimator.ofInt(viewWrapper, "width", 0, Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0)
        );
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.setDuration(2 * 1000).setStartDelay(2*1000);
                animatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if(!animatorSet.isRunning()){
            animatorSet.setDuration(2 * 1000).setStartDelay(2*1000);
            animatorSet.start();
        }
    }

    private static class ViewWrapper{
        private View mTarget;
        public ViewWrapper(View target){
            mTarget = target;
        }

        public int getWidth(){
            return mTarget.getLayoutParams().width;
        }
        public void setWidth(int width){
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
    }
}
