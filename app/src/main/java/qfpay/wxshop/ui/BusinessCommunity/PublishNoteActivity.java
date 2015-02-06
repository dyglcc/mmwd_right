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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.MyDynamicItemBean0;
import qfpay.wxshop.data.beans.MyTopicBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.processer.ImageType;
import qfpay.wxshop.takepicUtils.PictureBean;
import qfpay.wxshop.takepicUtils.TakePicUtils;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Util;
import qfpay.wxshop.utils.Utils;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 发帖页
 * @author 张志超
 */
@EActivity(R.layout.businesscommunity_publish_note)
public class PublishNoteActivity extends BaseActivity implements BusinessCommunityDataController.BusinessCommunityCallback{
    @Extra MyTopicBean myTopicBean;
    @ViewById TextView tv_title,add_pic_tv,tip_title;
    @ViewById ImageView note_iamge;
    @ViewById EditText note_content;
    @Bean QFImageUploader imageUploader;
    @ViewById
    LinearLayout tip_ll,tip_content_ll;
    @ViewById
    FrameLayout fl_indictor;
    @ViewById
    ImageView iv_indictor;
    @DrawableRes
    Drawable commodity_list_refresh;
    @Bean
    BusinessCommunityDataController businessCommunityDataController;
    String picUrl="";//上传的图片的服务器地址
    boolean hasPic = false;//帖子中是否有图片
    @AfterViews
    void init(){
        hasPic = false;
        ActionBar bar = getSupportActionBar();
        bar.hide();//隐藏默认actionbar
        tv_title.setText(myTopicBean.getG_name());
        businessCommunityDataController.setCallback(this);
        initPublishNoteRules();
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

    @Override @UiThread
    public void onSuccess() {
        fl_indictor.setVisibility(View.INVISIBLE);
        Intent intent =  new Intent();
        intent.putExtra("result", MaijiaxiuFragment.ACTION_PUBLISH_NOTE);
        setResult(Activity.RESULT_OK, intent);
        hasPic = false;
        finish();
    }

    @Override @UiThread
    public void onNetError() {
        fl_indictor.setVisibility(View.INVISIBLE);
        Toaster.s(this,"网络不太好！请稍后重试！");
    }

    @Override @UiThread
    public void onServerError(String msg) {
        fl_indictor.setVisibility(View.INVISIBLE);
        Toaster.l(this,msg);
    }

    @Override @UiThread
    public void refresh() {
        fl_indictor.setVisibility(View.VISIBLE);
        iv_indictor.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakePicUtils.TAKE_PIC_REQUEST_CODE) {

            if (resultCode != RESULT_OK) {
                Toaster.s(this,"选取照片失败！请重试！");
                return;
            }
            if(Utils.getFreeSizeOfSDCard()<100){
                Toaster.s(this,"存储卡空间不足啦！");
                return;
            }
            PictureBean fb = TakePicUtils.getInstance().receivePics(
                    requestCode, resultCode, data);
            if (fb != null) {
                add_pic_tv.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = note_iamge.getLayoutParams();
                int screenWidth = Util.getScreenWidth(this);
                params.width = screenWidth*3/7;
                params.height = screenWidth*3/7;
                note_iamge.setLayoutParams(params);
                File picFile = new File(fb.getFileStr());
                Picasso.with(PublishNoteActivity.this).load(picFile).resize(1000, 1000).centerCrop().into(note_iamge);
                hasPic = true;
                uploadThePicture(fb.getFileStr());
            }
        }
    }
    /**
     * 返回按钮点击事件
     */
    @Click
    void btn_back(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_topic_cancel");
        hasPic  = false;
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
     * 发帖按钮点击事件
     */
    @Click
    void publish_note(){
        String noteConentStr = note_content.getText().toString().trim();
        if(noteConentStr.equals("")&&hasPic==false){
            Toaster.s(this, "不写东西的裸奔帖会被警察蜀黍抓走的！");
        }else{
            MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_topic_send");
            if(hasPic==true){
                if(picUrl!=null&&!picUrl.equals("")){
                    businessCommunityDataController.publishOneNote(myTopicBean.getId()
                            ,noteConentStr,picUrl);
                }else{
                    Toaster.s(this,"正在上传图片...");
                }
            }else{
                businessCommunityDataController.publishOneNote(myTopicBean.getId()
                        ,noteConentStr,picUrl);
            }
        }
    }

    /**
     * 添加图片布局点击事件
     */
    @Click
    void add_note_pic_ll(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_topic_picture");
        TakePicUtils.getInstance().init(PublishNoteActivity.this);
        TakePicUtils.getInstance().takePic(
                TakePicUtils.TAKE_PIC_MODE_ONLY);
    }

    @Background
    void uploadThePicture(String path){
        picUrl = imageUploader.with(-1).path(path).imageType(ImageType.BIG).uploadSync();
        if(picUrl==null||"".equals(picUrl)){
            MobAgentTools.OnEventMobOnDiffUser(this, "fail_merchant_topic_upload");
        }else{
            MobAgentTools.OnEventMobOnDiffUser(this, "succ_merchant_topic_upload");
        }
    }

    /**
     * 初始化喵商圈规则
     */
    void initPublishNoteRules(){
        String json = MobclickAgent.getConfigParams(this,
                ConstValue.ONLINE_NOTE_TIP);
        if(json!=null){
            try {
                JSONObject tip = new JSONObject(json);
                tip_title.setText(tip.getString("title"));
                JSONArray tip_content = tip.getJSONArray("content");
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                for(int i=0;i<tip_content.length();i++){
                    View view  = layoutInflater.inflate(R.layout.business_community_tip,null);
                    TextView oneTip = (TextView)view.findViewById(R.id.tip_content_tv);
                    oneTip.setText(tip_content.getString(i));
                    tip_ll.addView(view);
                }
                tip_ll.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                tip_ll.setVisibility(View.GONE);
            }

        }else{
            tip_ll.setVisibility(View.GONE);
        }
    }
}
