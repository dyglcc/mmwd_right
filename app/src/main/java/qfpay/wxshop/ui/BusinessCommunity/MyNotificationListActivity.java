package qfpay.wxshop.ui.BusinessCommunity;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.BusinessCommunityMyNotificationBean;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.XListView;

/**
 * 我的消息列表页
 */
@EActivity(R.layout.mydynamic_notes_list)
public class MyNotificationListActivity extends BaseActivity {
    @Extra
    BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper notificationDataWrapper;
    @ViewById
    FrameLayout publish_note_fl;
    @ViewById XListView   listView;
    @ViewById
    RelativeLayout tool_bar;
    MyNotoficationListAdapter adapter;
    @ViewById
    TextView tv_title;
    @AfterViews
    void init(){
        ActionBar bar = getSupportActionBar();
        bar.hide();//隐藏默认actionbar
        publish_note_fl.setVisibility(View.GONE);
        tool_bar.setVisibility(View.VISIBLE);
        tv_title.setText("动态");
        initListView();
    }
    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setPullRefreshEnable(false);
        listView.setAutoLoadEnable(false);
        listView.setPullLoadEnable(false);
        adapter = new MyNotoficationListAdapter();
        listView.setAdapter(adapter);

    }

    /**
     * 我的消息列表适配器
     */
    class MyNotoficationListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return notificationDataWrapper.data.items.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationDataWrapper.data.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyNotificationListItemView itemView = (MyNotificationListItemView)convertView;
            if(itemView==null){
                itemView = MyNotificationListItemView_.build(MyNotificationListActivity.this);
            }
            itemView.setData(notificationDataWrapper.data.items.get(position),position);
            return itemView;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int code = data.getIntExtra("result", -1);
            switch (code) {
                case MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE:
                    int position = data.getIntExtra("position",-1);
                    MyDynamicItemBean0 myDynamicItemBean0 = (MyDynamicItemBean0)data.getSerializableExtra("myDynamicItemBean0");
                    if(position!=-1&&myDynamicItemBean0!=null){
                        notificationDataWrapper.data.items.get(position).getTopic().items.set(0,myDynamicItemBean0);
                    }
                    break;
            }
        }
    }

    @Click
    void btn_back(){
        this.finish();
    }

}
