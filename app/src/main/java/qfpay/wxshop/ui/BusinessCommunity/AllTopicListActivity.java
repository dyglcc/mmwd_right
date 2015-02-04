package qfpay.wxshop.ui.BusinessCommunity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.XListView;

/**
 * 所有话题列表页
 * @author zhangzhichao
 */
@EActivity(R.layout.mydynamic_notes_list)
public class AllTopicListActivity extends BaseActivity {
    @ViewById
    FrameLayout publish_note_fl;
    @ViewById XListView   listView;
    @ViewById
    RelativeLayout tool_bar;
    @ViewById
    TextView tv_title;
    @ViewById
    FrameLayout fl_indictor;
    @ViewById
    ImageView iv_indictor;
    @DrawableRes
    Drawable commodity_list_refresh;
    @Bean
    BusinessCommunityDataController dataController;
    BusinessCommunityService.TopicsListDataWrapper topicsListDataWrapper;
    AllTopicListAdapter allTopicListAdapter;
    DataEngine dataEngine;
    private Picasso picasso;
    private Cache cache = new LruCache(1024*512);

    @AfterViews
    void init(){
        dataEngine = new DataEngine(this);
        ActionBar bar = getSupportActionBar();
        bar.hide();//隐藏默认actionbar
        publish_note_fl.setVisibility(View.GONE);
        tool_bar.setVisibility(View.VISIBLE);
        tv_title.setText("选择想要发帖的话题");
        fl_indictor.setVisibility(View.VISIBLE);
        iv_indictor.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();
        getAllTopics();
        picasso = new Picasso.Builder(this).memoryCache(cache).build();
    }
    /**
     * 初始化列表
     */
    @UiThread
    void initListView() {
        fl_indictor.setVisibility(View.INVISIBLE);
        listView.setPullRefreshEnable(false);
        listView.setAutoLoadEnable(false);
        listView.setPullLoadEnable(false);
        allTopicListAdapter = new AllTopicListAdapter();
        listView.setAdapter(allTopicListAdapter);
    }

    /**
     * 所有话题列表适配器
     */
    class AllTopicListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return topicsListDataWrapper.data.items.size();
        }

        @Override
        public Object getItem(int position) {
            return topicsListDataWrapper.data.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TopicListItemView item = (TopicListItemView)convertView;
            if(item==null){
                item = TopicListItemView_.build(AllTopicListActivity.this,picasso);
            }
            final MyTopicBean myTopicBean = topicsListDataWrapper.data.items.get(position);
            item.setData(myTopicBean);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublishNoteActivity_.intent(AllTopicListActivity.this).myTopicBean(myTopicBean).startForResult(MaijiaxiuFragment.ACTION_PUBLISH_NOTE);
                }
            });
                return item;
        }
    }

    @Background
    void getAllTopics(){
       topicsListDataWrapper =  dataController.getMyTopicList2(dataEngine.getUserId());
        if(topicsListDataWrapper!=null){
            initListView();
        }else{
            getAllTopics();
        }
    }



    @Click
    void btn_back(){
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        int result = data.getIntExtra("result", -1);
        if(result==MaijiaxiuFragment.ACTION_PUBLISH_NOTE){
            data.putExtra("result", MaijiaxiuFragment.ACTION_PUBLISH_NOTE);
            setResult(Activity.RESULT_OK,data);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cache.clear();
        picasso = null;
    }
}
