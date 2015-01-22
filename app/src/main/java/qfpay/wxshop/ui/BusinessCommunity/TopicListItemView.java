package qfpay.wxshop.ui.BusinessCommunity;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyTopicBean;
/**
 * 话题列表item
 */
@EViewGroup(R.layout.mytopics_list_item)
public class TopicListItemView extends LinearLayout{

    @ViewById com.makeramen.RoundedImageView g_avatar;
    @ViewById TextView g_name,topic_num;
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
        topic_num.setText(myTopicBean.getTopic_num());
        return this;
	}
}
