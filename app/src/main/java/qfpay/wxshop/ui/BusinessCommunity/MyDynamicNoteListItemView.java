package qfpay.wxshop.ui.BusinessCommunity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Set;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Util;
import qfpay.wxshop.utils.Utils;

/**
 * 我的动态列表item
 */
@EViewGroup(R.layout.mydynamic_notes_list_item)
public class MyDynamicNoteListItemView extends LinearLayout {

    @ViewById
    ImageView image, link_data_iv;
    @ViewById
    ImageButton publish_reply_bt;
    @ViewById
    com.makeramen.RoundedImageView u_avatar, u_avatar_reply2, u_avatar_reply;
    @ViewById
    TextView u_name, g_name, content, read_num, reply_num, like_data, content_reply, content_reply2,content_reply_name_tv1,content_reply_name_tv2;
    @ViewById
    LinearLayout parent_ll, reply_content_ll, reply_content_child1, reply_content_child2, reply_ll, link_ll,bottom_ll;
    @ViewById
    View imageview_below_line, reply_below_line;
    @ViewById
    FrameLayout root_fl;

    @ViewById
    EditText input_reply_et;
    MyDynamicItemBean0 data;
    private int screenWidth;
    private Context context;
    private int currentShowReplyIndex = 1;//当前显示评论的索引
    private float scale;//屏幕密度
    private int position;
    BusinessCommunityDataController businessCommunityDataController;
    private HashMap<String, MyDynamicItemReplyBean> myDynamicItemReplyBeans = new HashMap<String, MyDynamicItemReplyBean>();//待显示的评论列表数据

    private MyDynamicListFragment myDynamicListFragment;

    public MyDynamicNoteListItemView(Context context) {
        super(context);
        this.context = context;
        screenWidth = Util.getScreenWidth((Activity) context);
        scale = context.getResources().getDisplayMetrics().density;
    }

    public MyDynamicNoteListItemView(Context context, BusinessCommunityDataController businessCommunityDataController) {
        this(context);
        this.businessCommunityDataController = businessCommunityDataController;
    }


    /**
     * 为列表项设置数据
     *
     * @param data
     * @return
     */
    public MyDynamicNoteListItemView setData(MyDynamicItemBean0 data, int position) {
        this.data = data;
        this.position = position;
        android.view.ViewGroup.LayoutParams layoutParams = image
                .getLayoutParams();
        int picWidth = (int) (screenWidth - (36 * scale + 0.5f));
        layoutParams.height = picWidth;
        image.setLayoutParams(layoutParams);

        if (data.getItem_type().equals("0")) {
            u_name.setText(data.getU_name());
            if(context instanceof MyTopicDetailActivity){//如果是某一小组内的帖子，不显示来自组别
                g_name.setVisibility(View.GONE);
            }else{
                g_name.setVisibility(View.VISIBLE);
            }
            g_name.setText("来自" + data.getG_name());
            content.setText(data.getContent());
            read_num.setText(data.getRead_num());
            reply_num.setText(data.getReply_num());
            like_data.setText(data.getLike_data().getLike_count());
            if (data.getLike_data().getIs_liked().equals("0")) {
                link_data_iv.setBackgroundResource(R.drawable.mydynamic_note_link);
            } else {
                link_data_iv.setBackgroundResource(R.drawable.mydynamic_note_link2);
            }

            if (data.getImage() != null && !data.getImage().equals("")) {
                image.setVisibility(View.VISIBLE);
                imageview_below_line.setVisibility(View.INVISIBLE);
                String picUrl = data.getImage()+"?imageView2/1/w/"+picWidth+"/h/"+picWidth;
                Picasso.with(getContext()).load(picUrl).fit().centerCrop().placeholder(R.drawable.list_item_default).into(image);
            } else {
                imageview_below_line.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
            }
            Picasso.with(getContext()).load(data.getU_avatar()).fit().centerInside().placeholder(R.drawable.list_item_default).
                    error(R.drawable.list_item_default).into(u_avatar);

            MyDynamicItemBean0.ReplyWrapper replyWrapper = data.getReply();
            if (replyWrapper != null) {
                if (replyWrapper.getItems().size() > 0) {
                    initReplyList(replyWrapper);
                    currentShowReplyIndex = replyWrapper.getItems().size();
                    reply_content_ll.setVisibility(View.VISIBLE);
                    reply_below_line.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(getCurrentReply(myDynamicItemReplyBeans).getU_avatar()).fit().centerInside().placeholder(R.drawable.list_item_default).
                            error(R.drawable.list_item_default).into(u_avatar_reply);
                    animationRecovery();
                    content_reply.setText(getCurrentReply(myDynamicItemReplyBeans).getContent());
                    content_reply_name_tv1.setText(getCurrentReply(myDynamicItemReplyBeans).getU_name()+": ");
                } else {
                    reply_content_ll.setVisibility(View.GONE);
                    reply_below_line.setVisibility(View.INVISIBLE);
                }
            } else {
                reply_content_ll.setVisibility(View.GONE);
                reply_below_line.setVisibility(View.INVISIBLE);
            }
            setOnClickListener();
            return this;
        }
        return null;
    }

    /**
     * 得到当前显示的评论数据
     *
     * @param myDynamicItemReplyBeans
     * @return
     */
    private MyDynamicItemReplyBean getCurrentReply(HashMap<String, MyDynamicItemReplyBean> myDynamicItemReplyBeans) {
        MyDynamicItemReplyBean replyBean = myDynamicItemReplyBeans.get(currentShowReplyIndex + "");
        return replyBean;
    }

    /**
     * 得到下一条要显示的评论数据
     *
     * @param myDynamicItemReplyBeans
     * @return
     */
    private MyDynamicItemReplyBean getNextReply(HashMap<String, MyDynamicItemReplyBean> myDynamicItemReplyBeans) {
        if (currentShowReplyIndex == 1) {
            currentShowReplyIndex = myDynamicItemReplyBeans.size();
            return myDynamicItemReplyBeans.get(currentShowReplyIndex + "");
        } else {
            currentShowReplyIndex = currentShowReplyIndex - 1;
            return myDynamicItemReplyBeans.get(currentShowReplyIndex + "");
        }
    }

    /**
     * 显示评论内容
     *
     * @param
     */
    @UiThread
    public void showReplyContent() {
        if (myDynamicItemReplyBeans != null && myDynamicItemReplyBeans.size() > 0) {
            //当前评论
            MyDynamicItemReplyBean currentReply = getCurrentReply(myDynamicItemReplyBeans);
            Picasso.with(getContext()).load(currentReply.getU_avatar()).fit().centerCrop().placeholder(R.drawable.list_item_default).
                    error(R.drawable.list_item_default).into(u_avatar_reply);
            content_reply.setText(currentReply.getContent());
            content_reply_name_tv1.setText(currentReply.getU_name()+": ");
            //下一条要显示的评论
            MyDynamicItemReplyBean nextReply = getNextReply(myDynamicItemReplyBeans);
            Picasso.with(getContext()).load(nextReply.getU_avatar()).fit().centerCrop().placeholder(R.drawable.list_item_default).
                    error(R.drawable.list_item_default).into(u_avatar_reply2);
            content_reply2.setText(nextReply.getContent());
            content_reply_name_tv2.setText(nextReply.getU_name()+": ");
            //开始动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(reply_content_child1, "translationY", 0, -Utils.dip2px(context, 42)),
                    ObjectAnimator.ofFloat(reply_content_child1, "alpha", 1, 0),
                    ObjectAnimator.ofFloat(reply_content_child2, "translationY", 0, -Utils.dip2px(context, 42)),
                    ObjectAnimator.ofFloat(reply_content_child2, "alpha", 0, 1)
            );

            if(animatorSet.isRunning()){
                animatorSet.cancel();
            }
            animatorSet.setDuration(1 * 1000);
            animatorSet.start();
        }
    }

    /**
     * 评论动画恢复原位
     */
    void animationRecovery(){
//开始动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(reply_content_child1, "translationY", 0),
                ObjectAnimator.ofFloat(reply_content_child1, "alpha", 0, 1),
                ObjectAnimator.ofFloat(reply_content_child2, "translationY", 0)
        );

        animatorSet.setDuration(1);
        animatorSet.start();
    }
    /**
     * 初始化回复列表数据
     *
     * @param replyWrapper
     */
    private void initReplyList(MyDynamicItemBean0.ReplyWrapper replyWrapper) {
        if (replyWrapper != null) {
            myDynamicItemReplyBeans.clear();
            for (MyDynamicItemReplyBean bean : replyWrapper.getItems()) {
                if (bean != null) {
                    myDynamicItemReplyBeans.put(bean.getFloor(), bean);
                }
            }

        }
    }



    /**
     * 设置点击事件
     */
    private void setOnClickListener() {
        parent_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                        .position(position).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
            }
        });
    }

    /**
     * 评论点击响应
     */
    @Click
    void reply_ll() {
        MobAgentTools.OnEventMobOnDiffUser(context, "click_merchant_dynamic_comment");
        MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                .position(position).isPublishReply(true).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
        //popupWindow的方式 不能指定出现位置 键盘出现后上顶不完全
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View view = layoutInflater.inflate(R.layout.mydynamic_reply_input, null);
//        final EditText editText = (EditText)view.findViewById(R.id.input_reply_et);
//        ImageButton imageButton = (ImageButton)view.findViewById(R.id.publish_reply_bt);
//        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.showAtLocation(root_fl, Gravity.TOP,0,0);
//
//        imageButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = editText.getText().toString();
//                if(content!=null&&!"".equals(content)){
//                    businessCommunityDataController.publishReply(data.getId(),content);
//                    InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//                    editText.setText("");
//                }
//            }
//        });
//        InputMethodManager inputMethodManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.showSoftInputFromInputMethod(editText.getWindowToken(),InputMethodManager.SHOW_FORCED);

//        bottom_ll.setVisibility(View.VISIBLE);
//        input_reply_et.requestFocus();
//        input_reply_et.setFocusable(true);
//        input_reply_et.setFocusableInTouchMode(true);
//        InputMethodManager inputMethodManager = (InputMethodManager)input_reply_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(input_reply_et,InputMethodManager.SHOW_FORCED,null);
    }

    @Click
    void publish_reply_bt(){
        bottom_ll.setVisibility(View.GONE);
        InputMethodManager inputMethodManager = (InputMethodManager)input_reply_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(input_reply_et.getWindowToken(), 0);
    }
}
