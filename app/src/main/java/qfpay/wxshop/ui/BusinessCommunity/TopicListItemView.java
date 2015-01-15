package qfpay.wxshop.ui.BusinessCommunity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyDynamicItemReplyBean;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.utils.Util;

/**
 * 话题列表item
 */
@EViewGroup(R.layout.mytopics_list_item)
public class TopicListItemView extends LinearLayout{

    @ViewById com.makeramen.RoundedImageView g_avatar;
    @ViewById TextView g_name,member_num;
    MyTopicBean myTopicBean;
    private Context context;

	public TopicListItemView(Context context) {
        super(context);
        this.context = context;
    }




    /**
     * 为列表项设置数据
     * @param myTopicBean
     * @return
     */
	public TopicListItemView setData(MyTopicBean myTopicBean) {
		this.myTopicBean = myTopicBean;
        Picasso.with(getContext()).load(myTopicBean.getG_avatar()).fit().centerCrop().placeholder(R.drawable.list_item_default).into(g_avatar);
        g_name.setText(myTopicBean.getG_name());
        member_num.setText(myTopicBean.getMember_num());
        return this;
	}

    /**
     * 整个item点击事件监听
     */
//    @Click
//    void mytopic_item_ll(){
//        MyTopicDetailActivity_.intent(context).myTopicBean(myTopicBean).start();
//    }

}
