package qfpay.wxshop.ui.BusinessCommunity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.BusinessCommunityMyNotificationBean;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;

/**
 * 话题列表item
 */
@EViewGroup(R.layout.businesscommunication_mynotify_item)
public class MyNotificationListItemView extends LinearLayout{
    BusinessCommunityMyNotificationBean notificationBean;
    @ViewById com.makeramen.RoundedImageView u_avatar;
    @ViewById TextView u_name,t_content_tv,reply_content_tv;
    @ViewById
    ImageView t_content_iv,reply_content_iv;
    private Context context;
    private int position;

	public MyNotificationListItemView(Context context) {
        super(context);
        this.context = context;
    }




    /**
     * 为列表项设置数据
     * @param notificationBean
     * @return
     */
	public MyNotificationListItemView setData(BusinessCommunityMyNotificationBean notificationBean,int position) {
		this.notificationBean = notificationBean;
        this.position = position;
        Picasso.with(context).load(notificationBean.getU_avatar()).fit().centerCrop().into(u_avatar);
        u_name.setText(notificationBean.getU_name());
        if(notificationBean.getItem_type().equals("0")){//0表示回复
            reply_content_iv.setVisibility(View.GONE);
            reply_content_tv.setVisibility(View.VISIBLE);
            reply_content_tv.setText(notificationBean.getContent());
        }else{//1表示点赞
            reply_content_iv.setVisibility(View.VISIBLE);
            reply_content_tv.setVisibility(View.GONE);
        }
        if(notificationBean.getTopic()!=null){
            if(notificationBean.getTopic().items.get(0).getImage()!=null&&!"".equals(notificationBean.getTopic().items.get(0).getImage())){
                t_content_iv.setVisibility(View.VISIBLE);
                t_content_tv.setVisibility(View.GONE);
                Picasso.with(context).load(notificationBean.getTopic().items.get(0).getImage()).fit().centerCrop().into(t_content_iv);
            }else{
                t_content_iv.setVisibility(View.GONE);
                t_content_tv.setVisibility(View.VISIBLE);
                if(notificationBean.getTopic().items.get(0).getContent()!=null){
                    t_content_tv.setText(notificationBean.getTopic().items.get(0).getContent());
                }
            }
        }
        return this;
	}

    /**
     * 整个item点击事件监听
     */
    @Click
    void mynotification_item_ll(){
        if(notificationBean.getTopic()!=null){
            MyDynamicOneNoteDetailActivity_.intent(context).myDynamicItemBean0(notificationBean.getTopic().items.get(0)).position(position).startForResult(MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE);
        }
    }

}
