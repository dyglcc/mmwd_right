package qfpay.wxshop.ui.BusinessCommunity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import java.io.IOException;
import java.util.List;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyDynamicItemLinkDataBean;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.dialogs.SimpleDialogFragment;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.web.CommonWebActivity;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.ui.web.CommonWebFragment;
import qfpay.wxshop.ui.web.CommonWebFragment_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Util;
import qfpay.wxshop.utils.Utils;

/**
 * 帖子详情页面
 * @author zhangzhichao
 */
@EActivity(R.layout.mydynamic_one_note_detail)
public class MyDynamicOneNoteDetailActivity extends BaseActivity implements BusinessCommunityDataController.BusinessCommunityCallback{
    @DrawableRes
    Drawable commodity_list_refresh;
    @ViewById
    FrameLayout fl_indictor;
    @ViewById
    ImageView iv_indictor,liked_iv;
    @ViewById LinearLayout bottom_ll;
    @ViewById ImageButton publish_reply_ib;
    @ViewById
    ScrollView scrollview;
    @Extra
    MyDynamicItemBean0 myDynamicItemBean0;
    @Extra
    int position;
    @Extra
    boolean isPublishReply;
    @Extra
    boolean isFromTopicDetail;
    @Bean
    BusinessCommunityDataController businessCommunityDataController;
    @ViewById
    TextView tv_title,u_name,content,g_name,liked_num_tv,reply_num;
    @ViewById
    ImageView image;
    @ViewById com.makeramen.RoundedImageView u_avatar,g_avatar;
    @ViewById
    LinearLayout liked_u_avatar,reply_u_avatar,reply_ll;
    @ViewById
    EditText input_reply_et;
    private String replyContent;
    DataEngine dataEngine ;
    @ViewById
    RelativeLayout rootview,note_from_rl;
    private boolean isActivityRunning = false;//当前Activity是否在运行
    private boolean hasPublishReply = false;//是否已经发布过评论
    BusinessCommunityService.ReplyAndLikeOfOneNoteDataWrapper dataWrapper;
    boolean canReport = true;

    @AfterViews
    void init() {
        isActivityRunning = true;
        getLatestReply(myDynamicItemBean0.getId());
        rootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootview.getWindowVisibleDisplayFrame(rect);
                int screenHeight = rootview.getRootView().getHeight();
                int keyBoardHeight = screenHeight - (rect.bottom - rect.top);
                int[] location = new int[2];
                reply_num.getLocationOnScreen(location);
                if(keyBoardHeight>38){
                    int smoothLenght = location[1]-(screenHeight-keyBoardHeight-Utils.dip2px(MyDynamicOneNoteDetailActivity.this,60));
                    if(smoothLenght>0){
                        scrollview.smoothScrollTo(0,smoothLenght);
                    }
                }
            }
        });
        ActionBar bar = getSupportActionBar();
        bar.hide();//隐藏默认actionbar
        dataEngine = new DataEngine(getBaseContext());
        tv_title.setText(myDynamicItemBean0.getG_name());
        businessCommunityDataController.setCallback(this);
        if(myDynamicItemBean0!=null&&myDynamicItemBean0.getId()!=null){
            setViewContent();
        }
        /**
         * 发布评论点击事件
         */
        publish_reply_ib.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    replyContent = input_reply_et.getText().toString().trim();
                    if(replyContent!=null&&!replyContent.equals("")){
                        String t_id = myDynamicItemBean0.getId();
                        businessCommunityDataController.publishReply(t_id,replyContent);
                        if(isPublishReply==true){
                            MobAgentTools.OnEventMobOnDiffUser(MyDynamicOneNoteDetailActivity.this, "click_merchant_dynamic_send");
                        }else{
                            MobAgentTools.OnEventMobOnDiffUser(MyDynamicOneNoteDetailActivity.this, "click_merchant_post_comment");
                        }
                    }else{
                        Toaster.s(MyDynamicOneNoteDetailActivity.this,"评论内容不能为空！");
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(Utils.isShouldHideInput(v,ev)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        if(getWindow().superDispatchTouchEvent(ev)){
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 设置控件内容
     */
    public void setViewContent(){
        Picasso.with(getBaseContext()).load(myDynamicItemBean0.getU_avatar()).fit().centerInside().placeholder(R.drawable.list_item_default).
                error(R.drawable.list_item_default).into(u_avatar);
        u_name.setText(myDynamicItemBean0.getU_name());
        content.setText(myDynamicItemBean0.getContent());
        int screenSize = Util.getScreenWidth(this);
        int size = screenSize - Utils.dip2px(this,16);
        if(myDynamicItemBean0.getImage()==null||myDynamicItemBean0.getImage().equals("")){
            image.setVisibility(View.GONE);
        }else{
            image.setVisibility(View.VISIBLE);
            Picasso.with(getBaseContext()).load(myDynamicItemBean0.getImage()).resize(size,size).centerInside().placeholder(R.drawable.list_item_default).
                    error(R.drawable.list_item_default).into(image);
        }
        Picasso.with(getBaseContext()).load(myDynamicItemBean0.getG_avatar()).fit().centerInside().placeholder(R.drawable.list_item_default).
                error(R.drawable.list_item_default).into(g_avatar);
        g_name.setText(myDynamicItemBean0.getG_name());
        showLikeData();
        showReplyContent();
        if(isPublishReply==true){
            input_reply_et.setFocusable(true);
            input_reply_et.setFocusableInTouchMode(true);
            input_reply_et.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager)input_reply_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(input_reply_et,InputMethodManager.SHOW_FORCED,null);
        }
    }
    /**
     * 点赞事件
     */
    @Click
    void liked_ll(){
        MyDynamicItemLinkDataBean myDynamicItemLinkDataBean = myDynamicItemBean0.getLike_data();
        int likeCount = Integer.parseInt(myDynamicItemLinkDataBean.getLike_count());
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_post_like");
        if(myDynamicItemLinkDataBean.getIs_liked().equals("0")){//没有点过赞
            liked_iv.setBackgroundResource(R.drawable.mydynamic_note_link2);
            myDynamicItemLinkDataBean.setIs_liked("1");
            myDynamicItemLinkDataBean.setLike_count((likeCount+1)+"");
            myDynamicItemLinkDataBean.getLiked_user().add(0,dataEngine.getUserId());
            businessCommunityDataController.setPriaseState(myDynamicItemBean0.getId(),"1");
        }else{//已经点过赞
            liked_iv.setBackgroundResource(R.drawable.mydynamic_note_link);
            myDynamicItemLinkDataBean.setIs_liked("0");
            myDynamicItemLinkDataBean.setLike_count((likeCount-1)+"");
            myDynamicItemLinkDataBean.getLiked_user().remove(dataEngine.getUserId());
            businessCommunityDataController.setPriaseState(myDynamicItemBean0.getId(),"0");
        }
        showLikeData();
    }

    /**
     * 返回点击事件
     */
    @Click
    void btn_back() {
        Intent intent = new Intent();
        intent.putExtra("myDynamicItemBean0", myDynamicItemBean0);
        intent.putExtra("position", position);
        intent.putExtra("result", MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            btn_back();
        }
        return false;
    }
    /**
     * 显示点赞数据
     */
    @UiThread
    void showLikeData(){
            liked_u_avatar.removeAllViews();
            MyDynamicItemLinkDataBean myDynamicItemLinkDataBean = myDynamicItemBean0.getLike_data();
            liked_num_tv.setText(myDynamicItemLinkDataBean.getLike_count());
            if(myDynamicItemBean0.getLike_data().getIs_liked().equals("0")){
                liked_iv.setBackgroundResource(R.drawable.mydynamic_note_link);
            }else{
                liked_iv.setBackgroundResource(R.drawable.mydynamic_note_link2);
            }
            if(myDynamicItemLinkDataBean.getLiked_user().size()>0){
                liked_u_avatar.setVisibility(View.VISIBLE);
                for(String oneLikeUser:myDynamicItemLinkDataBean.getLiked_user()){
                    RoundedImageView roundedImageView = new RoundedImageView(this);
                    String url = "http://b-avatar.qiniudn.com/"+oneLikeUser+".png";
                    roundedImageView.setLayoutParams(new LinearLayout.LayoutParams(Utils.dip2px(this,35),Utils.dip2px(this,35)));
                    roundedImageView.setPadding(0, 0, Utils.dip2px(this, 5), 0);
                    roundedImageView.setCornerRadius(50f);
                    Picasso.with(getBaseContext()).load(url).fit().centerInside().placeholder(R.drawable.list_item_default).
                            error(R.drawable.list_item_default).into(roundedImageView);
                    liked_u_avatar.addView(roundedImageView);
                }
            }else{
                liked_u_avatar.setVisibility(View.GONE);
            }
        }

    /**
     * 显示评论数据
     */
    @UiThread
    void  showReplyContent(){
            reply_u_avatar.removeAllViews();
            List<MyDynamicItemReplyBean> myDynamicItemReplyBeanList = myDynamicItemBean0.getReply().getItems();
            reply_num.setText(myDynamicItemBean0.getReply_num());
            if(myDynamicItemReplyBeanList!=null&&myDynamicItemReplyBeanList.size()>0){
                reply_u_avatar.setVisibility(View.VISIBLE);
                LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
                for(final MyDynamicItemReplyBean myDynamicItemReplyBean:myDynamicItemReplyBeanList){
                    View view = layoutInflater.inflate(R.layout.mydynamic_note_reply_item,null);
                    TextView u_name =  (TextView)view.findViewById(R.id.u_name);
                    u_name.setText(myDynamicItemReplyBean.getU_name());
                    RoundedImageView u_avatar = (RoundedImageView)view.findViewById(R.id.u_avatar);
                    final String u_id = myDynamicItemReplyBean.getU_id();
                    u_avatar.setOnClickListener(new View.OnClickListener() {//点击评论用户头像进入到
                        @Override
                        public void onClick(View view) {
                            getShopIdByUserId(u_id);
                        }
                    });
                    Picasso.with(getBaseContext()).load(myDynamicItemReplyBean.getU_avatar()).fit().centerCrop().placeholder(R.drawable.list_item_default).
                            error(R.drawable.list_item_default).into(u_avatar);
                    TextView content = (TextView)view.findViewById(R.id.content);
                    content.setText(myDynamicItemReplyBean.getContent());
                    TextView floor = (TextView)view.findViewById(R.id.floor);
                    floor.setText(myDynamicItemReplyBean.getFloor()+"楼");
                    reply_u_avatar.addView(view);
                }
            }else{
                reply_u_avatar.setVisibility(View.GONE);
            }
        }

    @Override @UiThread
    public void onSuccess() {
            hasPublishReply = true;
            input_reply_et.setText("");
            View view = getWindow().peekDecorView();
            if(view!=null){
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            getLatestReply(myDynamicItemBean0.getId());//评论成功后再次请求最新评论数据
            if(isPublishReply==true){//直接点击评论进来
                MobAgentTools.OnEventMobOnDiffUser(this, "Succ_merchant_dynamic_comment");
            }else{//点击帖子详情进来
                MobAgentTools.OnEventMobOnDiffUser(this, "Succ_merchant_post_comment");
            }
    }

    @Override @UiThread
    public void onNetError() {
        fl_indictor.setVisibility(View.INVISIBLE);
        Toaster.s(this,"评论失败,请检查网络！");
    }

    @Override @UiThread
    public void onServerError(String msg) {
        fl_indictor.setVisibility(View.INVISIBLE);
        Toaster.l(this,msg);
        if(isPublishReply==true){
            MobAgentTools.OnEventMobOnDiffUser(this, "Fail_merchant_dynamic_comment");
        }else{
            MobAgentTools.OnEventMobOnDiffUser(this, "Fail_merchant_post_comment");
        }
    }

    @Override @UiThread
    public void refresh() {
        fl_indictor.setVisibility(View.VISIBLE);
        iv_indictor.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();
    }

    /**
     * 点击图片放大
     */
    @Click
    void image(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_post_preview");
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
                new String[] {myDynamicItemBean0.getImage()});
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
        startActivity(intent);
    }

    /**
     * 点击话题头像进入到对应话题详情
     */
    @Click
    void note_from_rl(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_dynamic_topic");
        if(isFromTopicDetail){//如果是话题动态列表进入到帖子详情的，再次点击话题小组头像后，不再跳转，直接关闭当前页面
            this.finish();
        }else{
            MyTopicBean myTopicBean = new MyTopicBean();
            myTopicBean.setG_name(myDynamicItemBean0.getG_name());
            myTopicBean.setId(myDynamicItemBean0.getG_id());
            MyTopicDetailActivity_.intent(MyDynamicOneNoteDetailActivity.this).myTopicBean(myTopicBean).start();
        }
    }

    /**
     * 点击用户头像进入到用户店铺主页
     */
    @Click
    void u_avatar(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_avatars");
        fl_indictor.setVisibility(View.VISIBLE);
        iv_indictor.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();
        getShopIdByUserId(myDynamicItemBean0.getU_id());
    }

    @Background
    void getShopIdByUserId(String userId){
        String shopId = "";
        try{
            BusinessCommunityService.ShopIdDataWrapper dataWrapper =  businessCommunityDataController.getShopIdByUserId(userId);
            if(dataWrapper!= null&&dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                shopId = dataWrapper.data.shop_id;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        jumpToUserShop(shopId);
    }

    @UiThread
    void jumpToUserShop(String shopId){
        fl_indictor.setVisibility(View.INVISIBLE);
        if(shopId!=null&&!shopId.equals("")){
            String shopUrl = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/shop/"+shopId;
            ShopDetailActivity_.intent(this).shopUrl(shopUrl).start();
        }else{
            Toaster.s(this,"请求失败，请稍后重试！");
        }
    }

    /**
     * 进入到帖子详情后，请求最新的帖子回复和点赞数据
     * @param t_id
     */
    @Background
    void getLatestReply(String t_id){
        dataWrapper = businessCommunityDataController.getReplyAndLikeData(t_id);
        showLatestReply();
    }

    @UiThread
    void showLatestReply(){
        if(dataWrapper!=null){
            if(dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                if(dataWrapper.data.items==null){
                    return;
                }
                MyDynamicItemBean0.ReplyWrapper replyWrapper = new MyDynamicItemBean0.ReplyWrapper();
                replyWrapper.setItems(dataWrapper.data.items);
                myDynamicItemBean0.setReply(replyWrapper);
                myDynamicItemBean0.setLike_data(dataWrapper.data.like_data);
                myDynamicItemBean0.setReply_num(replyWrapper.getItems().size()+"");
                if(isActivityRunning) {
                    showLikeData();
                    showReplyContent();
                    fl_indictor.setVisibility(View.INVISIBLE);
                }
                if(isPublishReply&&hasPublishReply){
                    btn_back();
                }
            }else{
                Toaster.l(this,dataWrapper.getResperr());
            }
        }else{
            Toaster.s(this,"数据加载失败，请刷新重试！");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;
    }

    /**
     * 举报
     */
    @Click
    void note_report(){
        if(canReport){
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(getString(R.string.mm_hint)).setMessage("喵喵收到举报后，会尽快核实处理！")
                .setNegativeButtonText("取消").setPositiveButtonText("举报")
                .setPositiveClick(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        canReport = false;
                        fl_indictor.setVisibility(View.VISIBLE);
                        iv_indictor.setImageDrawable(commodity_list_refresh);
                        reportThisNote();
                    }
                }).show();
        }else{
            Toaster.s(this,"正在发送举报请求，请稍后！");
        }
    }

    @Background
    void reportThisNote(){
        RetrofitWrapper.CommonJsonBean reportReturn = businessCommunityDataController.noteReport(myDynamicItemBean0.getId());
        dealReportReturn(reportReturn);
    }

    @UiThread
    void dealReportReturn(RetrofitWrapper.CommonJsonBean data){
        canReport = true;
        if(data!=null){
            if(data.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)){
                if(isActivityRunning) {
                    fl_indictor.setVisibility(View.INVISIBLE);
                    Toaster.l(this,"喵喵已经收到举报，会尽快核实处理！");
                }
            }else{
                Toaster.l(this,data.getResperr());
            }
        }else{
            Toaster.s(this,"请求失败，请检查网络！");
        }
    }

}
