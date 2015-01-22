package qfpay.wxshop.activity.share;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.androidquery.AQuery;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

public class ShareActivity extends BaseActivity implements
        PlatformActionListener, Callback {
    private boolean initShare;

    private Button btn_back;
    private Button btn_share;

    private EditText tv_content;
    private ImageView iv_pic;
    private TextView tv_count;

    private CheckBox iv_sina;
    private CheckBox iv_qzone;
    private CheckBox iv_tencent;
    private Platform weibo;
    private Platform qzone;
    private Platform tecentWeibo;
    AQuery aq;
    private String gaSrcfrom;

    private String reg1 = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/shop/\\d+";
    private String reg2 = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/item_detail/\\d+";
    private String reg3 = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/h5/show.html[?]shopid=\\d+";
    private String reg4 = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/item/\\d+";
    private String reg5 = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/hmsg/\\d+";
    Pattern pattern1 = Pattern.compile(reg1);
    Pattern pattern2 = Pattern.compile(reg2);
    Pattern pattern3 = Pattern.compile(reg3);
    Pattern pattern4 = Pattern.compile(reg4);
    Pattern pattern5 = Pattern.compile(reg5);

    public static int SHARE_CONTENT_SHOP = 1;
    public static int SHARE_CONTENT_GOOD_ITEM = SHARE_CONTENT_SHOP + 1;
    public static int SHARE_CONTENT_PUBLISH_SUCCESS = SHARE_CONTENT_GOOD_ITEM + 1;
    public static int SHARE_CONTENT_MANPRO_SUCCESS = SHARE_CONTENT_PUBLISH_SUCCESS + 1;

    private int content_type;

    private boolean shareQQzoneSuccess = false;
    private int shareQQzoneTimes = 0;


    private boolean isSinaSharing;
    private boolean isTencentSharing;


    // Tencent
    Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_share);
        Intent intent = getIntent();
        gaSrcfrom = intent.getStringExtra("gaSrcfrom");
        content_type = intent.getIntExtra("share_content_type", 0);

        // qq 互联 初始化设置
        mTencent = Tencent.createInstance(ConstValue.QQ_ZONE_ID, this.getApplicationContext());
        aq = new AQuery(this);
        // 初始化ShareSDK
        if (!initShare) {
            ShareSDK.initSDK(this);
            initShare = true;
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.common_menuitem_shareactivity);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        View view = actionBar.getCustomView();
        btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();

            }
        });
        btn_share = (Button) view.findViewById(R.id.btn_save);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_content = (EditText) findViewById(R.id.textContent);
        iv_pic = (ImageView) findViewById(R.id.imageViewPic);

        iv_sina = (CheckBox) findViewById(R.id.ck_sina);
        iv_qzone = (CheckBox) findViewById(R.id.ck_zone);
        iv_tencent = (CheckBox) findViewById(R.id.ck_qqweibo);

        weibo = ShareSDK.getPlatform(ShareActivity.this, SinaWeibo.NAME);
        qzone = ShareSDK.getPlatform(ShareActivity.this, QZone.NAME);
        tecentWeibo = ShareSDK.getPlatform(ShareActivity.this,
                TencentWeibo.NAME);

        if (!weibo.isValid()) {
            iv_sina.setChecked(false);
        }
        iv_qzone.setChecked(true);
        if (!tecentWeibo.isValid()) {
            iv_tencent.setChecked(false);
        }

        iv_sina.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton view, boolean checked) {
                if (checked) {
                    // 如果微博未绑定
                    if (!weibo.isValid()) {
                        Toaster.l(ShareActivity.this, "开始授权");
                        weibo.setPlatformActionListener(authorListener);
                        weibo.authorize();
                    }
                }

            }
        });

        iv_qzone.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {

                    // 如果未绑定绑定
//                    if (!qzone.isValid()) {
//                        Toaster.l(ShareActivity.this, "开始授权");
//                        qzone.setPlatformActionListener(authorListener);
//                        qzone.authorize();
//                    }
                }
            }
        });

        tv_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                tv_count.setText(arg0.length() + "/120");
                if (arg0.length() == 120) {
                    tv_count.setTextColor(getResources().getColor(R.color.title_bg_color));
                } else {
                    tv_count.setTextColor(getResources().getColor(R.color.grey));
                }
            }
        });

        iv_tencent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {

                    if (!tecentWeibo.isValid()) {
                        Toaster.l(ShareActivity.this, "开始授权");
                        tecentWeibo.setPlatformActionListener(authorListener);
                        tecentWeibo.authorize();
                    }
                }
            }
        });

        if (WxShopApplication.shareBean != null) {
            if (WxShopApplication.shareBean.from != null) {
                tv_content.setText(WxShopApplication.shareBean.title);
            } else {
                tv_content.setText(WxShopApplication.shareBean.title
                        + WxShopApplication.shareBean.link + " ");
            }


            if (WxShopApplication.shareBean.imgUrl.indexOf("imgstore01") != -1) {
                aq.id(iv_pic)
                        .progress(R.id.progress_share)
                        .image(WxShopApplication.shareBean.imgUrl, true, true, 120,
                                R.drawable.icon);
            } else {
                aq.id(iv_pic).progress(R.id.progress_share)
                        .image(WxShopApplication.shareBean.imgUrl);
            }
        }


        // 回退操作
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
                // overridePendingTransition(R.anim.in_from_down, R.anim.quit);
            }
        });

        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!iv_tencent.isChecked() && !iv_sina.isChecked()
                        && !iv_qzone.isChecked()) {
                    handler.sendEmptyMessage(12);
                    return;
                }

                if (WxShopApplication.shareBean == null) {
                    Toaster.l(ShareActivity.this,
                            getString(R.string.fail_share2));
                    return;
                }
//				handler.sendEmptyMessage(11);
                // ga统计 需要将文本框内的链接加入ga_medium 和ga source

                // 首先
                if (iv_sina.isChecked()) {

                    if (isSinaSharing) {
                        Toaster.l(ShareActivity.this, "新浪微博正在分享中，稍等一下吧");
                    } else {
                        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
                        String text = tv_content.getText().toString();
                        String repleaceText = getRepleaceText(text, "sinaweibo");
                        if (content_type > 0) {
                            repleaceText += "(分享自 @喵喵微店 http://www.mmweidian.com )";
                        }
                        sp.text = repleaceText;
                        sp.imageUrl = WxShopApplication.shareBean.imgUrl;
                        weibo.setPlatformActionListener(ShareActivity.this); // 设置分享事件回调
                        // 执行图文分享
                        weibo.share(sp);
                        isSinaSharing = true;
                    }


                }
                if (iv_tencent.isChecked()) {
                    if (isTencentSharing) {
                        Toaster.l(ShareActivity.this, "腾讯微博正在分享中，稍等一下吧");
                    } else {
                        TencentWeibo.ShareParams sp = new TencentWeibo.ShareParams();
                        // sp.text = WxShopApplication.shareBean.title
                        // + WxShopApplication.shareBean.link;
                        String textTcentent = tv_content.getText().toString();
                        sp.text = getRepleaceText(textTcentent, "tencentweibo");
                        sp.imageUrl = WxShopApplication.shareBean.imgUrl;
                        tecentWeibo.setPlatformActionListener(ShareActivity.this); // 设置分享事件回调
                        // 执行图文分享
                        tecentWeibo.share(sp);

                        isTencentSharing = true;
                    }

                }

                if (iv_qzone.isChecked()) {
                    String shareUrl = getGaUrl(WxShopApplication.shareBean.qqTitle_url, "qzone");
//                        sp.titleUrl = shareUrl.replace(" ", ""); // 标题的超链接
                    // sp.text = WxShopApplication.shareBean.title
                    // + WxShopApplication.shareBean.link;
                    String contextStr = tv_content.getText().toString();
                    if (contextStr.startsWith(WxShopApplication.shareBean.qqTitle)) {
                        contextStr = contextStr.substring(WxShopApplication.shareBean.qqTitle.length());
                    }

                    // 去掉空间中间的链接
                    contextStr = contextStr.replaceAll(reg1, "");
                    contextStr = contextStr.replaceAll(reg2, "");
                    contextStr = contextStr.replaceAll(reg3, "");
                    contextStr = contextStr.replaceAll(reg4, "");
                    contextStr = contextStr.replaceAll(reg5, "");
                    contextStr = contextStr.replaceAll("店铺链接：", "");
//                        sp.text = contextStr;
//                        sp.imageUrl = WxShopApplication.shareBean.qq_imageUrl;
////						sp.comment = "我对此分享内容的评论";
//                        sp.site = "发布分享的网站名称";
//                        // 去掉空格
//                        sp.siteUrl = shareUrl.replace(" ", "");
//                        qzone.setPlatformActionListener(ShareActivity.this); // 设置分享事件回调
                    // 执行图文分享
//                        qzone.share(sp);
                    Bundle params = new Bundle();
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(WxShopApplication.shareBean.qq_imageUrl);
                    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, WxShopApplication.shareBean.qqTitle);//必填
                    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, contextStr);//选填
                    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl.replace(" ", ""));//必填
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
                    BaseUIListener listener = new BaseUIListener(ShareActivity.this);
                    mTencent.shareToQzone(ShareActivity.this, params, listener);
                }


                Toaster.l(ShareActivity.this, getString(R.string.start_share));
            }
        });
    }

//	protected String getSrcImageUrl(String imgUrl) {
//		if(imgUrl == null || imgUrl.equals("")){
//			return "";
//		}
//		if(imgUrl.indexOf("?imageView")!=-1){
//			imgUrl = imgUrl.substring(0,imgUrl.indexOf("?imageView"));
//		}
//		return imgUrl;
//	}

    protected String getRepleaceText(String text, String platform) {

        if (gaSrcfrom == null) {
            return text;
        }
        Matcher matcher1 = pattern1.matcher(text);
        if (matcher1.find()) {
            String content1 = matcher1.group();
            text = text.replaceAll(reg1, getGaUrl(content1, platform));
        }
        Matcher matcher2 = pattern2.matcher(text);
        if (matcher2.find()) {
            String content2 = matcher2.group();
            text = text.replaceAll(reg2, getGaUrl(content2, platform));
        }
        Matcher matcher3 = pattern3.matcher(text);
        if (matcher3.find()) {
            String content3 = matcher3.group();
            text = text.replaceAll(reg3, getGaUrl(content3, platform));
        }
        Matcher matcher4 = pattern4.matcher(text);
        if (matcher4.find()) {
            String content4 = matcher4.group();
            text = text.replaceAll(reg4, getGaUrl(content4, platform));
        }
        Matcher matcher5 = pattern5.matcher(text);
        if (matcher5.find()) {
            String content5 = matcher5.group();
            text = text.replaceAll(reg5, getGaUrl(content5, platform));
        }

        return text;
    }

    protected String getGaUrl(String qqTitle_url, String platform) {
        String str = qqTitle_url;
        if (gaSrcfrom != null) {
            str = str + "?ga_medium=" + gaSrcfrom + platform + "&ga_source=entrance";
        }
        return str;
    }

    private static final int author_cancle = 1;
    private static final int author_complete = author_cancle + 1;
    private static final int author_error = author_complete + 1;
    private PlatformActionListener authorListener = new PlatformActionListener() {

        @Override
        public void onCancel(Platform arg0, int arg1) {
            handler.sendEmptyMessage(author_cancle);
            checkAuth();
        }

        @Override
        public void onComplete(Platform platform, int arg1,
                               HashMap<String, Object> arg2) {
            handler.sendEmptyMessage(author_complete);
            checkAuth();
            if (platform.getName().equals(SinaWeibo.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "sina_auth_success");
            } else if (platform.getName().equals(QZone.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "qzone_auth_success");
            } else if (platform.getName().equals(TencentWeibo.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "qqweibo_auth_success");
            }
        }

        @Override
        public void onError(Platform platform, int arg1, Throwable arg2) {
            handler.sendEmptyMessage(author_error);
            checkAuth();
            if (platform.getName().equals(SinaWeibo.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "sina_auth_fail");
            } else if (platform.getName().equals(QZone.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "qzone_auth_fail");
            } else if (platform.getName().equals(TencentWeibo.NAME)) {
                MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                        "qqweibo_auth_fail");
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == author_cancle) {
                Toaster.l(ShareActivity.this, "授权取消");
            } else if (msg.what == author_complete) {
                Toaster.l(ShareActivity.this, "授权完成");

            } else if (msg.what == 12) {
                Toaster.l(ShareActivity.this, "请选择分享的平台");
            } else if (msg.what == author_error) {
                Toaster.l(ShareActivity.this, "授权错误");
            }

        }

        ;
    };

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        String text = Utils.actionToString(msg.arg2);
        switch (msg.arg1) {
            case 1: {
                // 成功
                Platform plat = (Platform) msg.obj;
                text = plat.getName() + "分享成功";
            }
            break;
            case 2: {
                // 失败
                if ("WechatClientNotExistException".equals(msg.obj.getClass()
                        .getSimpleName())) {
                    text = ShareActivity.this
                            .getString(R.string.wechat_client_inavailable);
                } else if ("WechatTimelineNotSupportedException".equals(msg.obj
                        .getClass().getSimpleName())) {
                    text = ShareActivity.this
                            .getString(R.string.wechat_client_inavailable);
                } else {
                    text = getString(R.string.fail_share2);
                }
            }
            break;
            case 3: {
                // 取消
                Platform plat = (Platform) msg.obj;
                text = plat.getName() + "取消分享";
            }
            break;
        }

        Toast.makeText(ShareActivity.this, text, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
        if (plat.getName().equals(SinaWeibo.NAME)) {
            isSinaSharing = false;
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "sina_share_success_sharesdk");
        } else if (plat.getName().equals(QZone.NAME)) {
            shareQQzoneSuccess = true;
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "qzone_share_success_sharesdk");
        } else if (plat.getName().equals(TencentWeibo.NAME)) {
            isTencentSharing = false;
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "qqweibo_share_success_sharesdk");
        }

    }

    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
        if (plat.getName().equals(SinaWeibo.NAME)) {
            isSinaSharing = false;
        } else if (plat.getName().equals(QZone.NAME)) {
        } else if (plat.getName().equals(TencentWeibo.NAME)) {
            isTencentSharing = false;
        }

    }

    public void onError(Platform plat, int action, Throwable t) {
        t.printStackTrace();
        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
        if (plat.getName().equals(SinaWeibo.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "sina_share_faill_sharesdk");
            isSinaSharing = false;
        } else if (plat.getName().equals(QZone.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "qzone_share_fail_sharesdk");
//            Toaster.l(this, getResources().getString(R.string.qqshareFail));
        } else if (plat.getName().equals(TencentWeibo.NAME)) {
            isTencentSharing = false;
            MobAgentTools.OnEventMobOnDiffUser(ShareActivity.this,
                    "qqweibo_share_fail_sharesdk");
        }
    }

    private void checkAuth() {
        if (!weibo.isValid()) {
            iv_sina.setChecked(false);
        }
        if (!tecentWeibo.isValid()) {
            iv_tencent.setChecked(false);
        }
        if (!qzone.isValid()) {
            iv_qzone.setChecked(false);
        }
    }

    // sp.site = "http://www.mmweidian.com";

    private void shareToQzone() {

        //分享类型

    }

}
