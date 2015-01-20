package qfpay.wxshop.ui.BusinessCommunity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Set;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
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
    TextView u_name, g_name, content, read_num, reply_num, like_data, content_reply, content_reply2, content_reply_name_tv1, content_reply_name_tv2;
    @ViewById
    LinearLayout parent_ll, reply_content_ll, reply_content_child1, reply_content_child2, reply_ll, link_ll, bottom_ll;
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
    private LoopperRunnable loopper;

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

        if (loopper == null) {
            loopper = new LoopperRunnable();
            loopper.start();
        } else {
            loopper.reset();
        }

        android.view.ViewGroup.LayoutParams layoutParams = image
                .getLayoutParams();
        int picWidth = (int) (screenWidth - (36 * scale + 0.5f));
        layoutParams.height = picWidth;
        image.setLayoutParams(layoutParams);

        if (data.getItem_type().equals("0")) {
            u_name.setText(data.getU_name());
            if (context instanceof MyTopicDetailActivity) {//如果是某一小组内的帖子，不显示来自组别,并且昵称居中
                g_name.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.addRule(RelativeLayout.RIGHT_OF, u_avatar.getId());
                params.setMargins(Utils.dip2px(context, 8), 0, 0, 0);
                u_name.setLayoutParams(params);
            } else {
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
                String picUrl = data.getImage() + "?imageView2/1/w/" + picWidth + "/h/" + picWidth;
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
                    content_reply_name_tv1.setText(getCurrentReply(myDynamicItemReplyBeans).getU_name() + ": ");
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
            content_reply_name_tv1.setText(currentReply.getU_name() + ": ");
            //下一条要显示的评论
            MyDynamicItemReplyBean nextReply = getNextReply(myDynamicItemReplyBeans);
            Picasso.with(getContext()).load(nextReply.getU_avatar()).fit().centerCrop().placeholder(R.drawable.list_item_default).
                    error(R.drawable.list_item_default).into(u_avatar_reply2);
            content_reply2.setText(nextReply.getContent());
            content_reply_name_tv2.setText(nextReply.getU_name() + ": ");
            //开始动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(reply_content_child1, "translationY", 0, -Utils.dip2px(context, 42)),
                    ObjectAnimator.ofFloat(reply_content_child1, "alpha", 1, 0),
                    ObjectAnimator.ofFloat(reply_content_child2, "translationY", 0, -Utils.dip2px(context, 42)),
                    ObjectAnimator.ofFloat(reply_content_child2, "alpha", 0, 1)
            );

            if (animatorSet.isRunning()) {
                animatorSet.cancel();
            }
            animatorSet.setDuration(1 * 1000);
            animatorSet.start();
        }
    }

    /**
     * 评论动画恢复原位
     */
    void animationRecovery() {
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
                if (context instanceof MyTopicDetailActivity) {
                    MobAgentTools.OnEventMobOnDiffUser(context, "click_merchant_topic_post");
                    MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                            .position(position).isFromTopicDetail(true).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
                } else {
                    MobAgentTools.OnEventMobOnDiffUser(context, "click_merchant_dynamic_post");
                    MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                            .position(position).isFromTopicDetail(false).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
                }

            }
        });
    }

    /**
     * 评论点击响应
     */
    @Click
    void reply_ll() {
        MobAgentTools.OnEventMobOnDiffUser(context, "click_merchant_dynamic_comment");
        if (context instanceof MyTopicDetailActivity) {
            MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                    .position(position).isPublishReply(true).isFromTopicDetail(true).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
        }else{
            MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(data)
                    .position(position).isPublishReply(true).isFromTopicDetail(false).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
        }

    }

    @Click
    void publish_reply_bt() {
        bottom_ll.setVisibility(View.GONE);
        InputMethodManager inputMethodManager = (InputMethodManager) input_reply_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(input_reply_et.getWindowToken(), 0);
    }

    /**
     * 点击用户头像
     */
    @Click
    void u_avatar() {
        MobAgentTools.OnEventMobOnDiffUser(context, "click_merchant_avatars");
        getShopIdByUserId(data.getU_id());
    }

    @Background
    void getShopIdByUserId(String userId) {
        String shopId = "";
        try {
            BusinessCommunityService.ShopIdDataWrapper dataWrapper = businessCommunityDataController.getShopIdByUserId(userId);
            if (dataWrapper != null && dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
                shopId = dataWrapper.data.shop_id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jumpToUserShop(shopId);
    }

    @UiThread
    void jumpToUserShop(String shopId) {
        if (shopId != null && !shopId.equals("")) {
            String shopUrl = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/shop/" + shopId;
            ShopDetailActivity_.intent(context).shopUrl(shopUrl).start();
        } else {
            Toaster.s(context, "请求失败，请稍后重试！");
        }
    }

    class LoopperRunnable implements Runnable {
        Thread thread;
        boolean isLoop;

        public void start() {
            isLoop = true;
            thread = new Thread(this);
            thread.start();
        }

        public void cancel() {
            isLoop = false;
            thread.interrupt();
        }

        public void reset() {
            thread.interrupt();
        }

        @Override
        public void run() {
            while (isLoop) {
                try {
                    Thread.sleep(5000);
                    showReplyContent();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
