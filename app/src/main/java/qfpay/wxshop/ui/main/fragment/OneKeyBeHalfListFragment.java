package qfpay.wxshop.ui.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.SSNPublishActivity;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.beans.OneKeybehalfListResponseWrapper;
import qfpay.wxshop.data.beans.OneKeybehalfListResponseWrapper.MsgsWrapper;
import qfpay.wxshop.data.beans.OnekeybehalfItemBean;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.OneKeybehalfListNetImpl;
import qfpay.wxshop.dialogs.ISimpleDialogListener;
import qfpay.wxshop.getui.ImageUtils;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.view.CustomProgressDialog;
import qfpay.wxshop.ui.view.OnekeyBehalfListView;
import qfpay.wxshop.ui.view.OnekeybeHalfItem;
import qfpay.wxshop.ui.view.OnekeybeHalfItem_;
import qfpay.wxshop.ui.view.TopCloseAnimation;
import qfpay.wxshop.ui.view.TopExpandAnimation;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

@SuppressLint("HandlerLeak")
@EFragment(R.layout.main_onkeybehalf_list)
public class OneKeyBeHalfListFragment extends BaseFragment implements
        ISimpleDialogListener, XListView.IXListViewListener, PlatformActionListener, Callback {
    public static final String SP_NAME_MANAGE = "config";
    public static final String SP_ITEN_ISNEW = "copy_isnew";
    public static final String SP_HEADER_ISNEW = "header_header_img_isnew";

    @ViewById
    Button btn_share;
    @ViewById
    Button btn_add;

    @ViewById
    Button btn_empty_see;
    @ViewById
    TextView tv_link;

    public static final float BILI = 1f;

    private LayoutInflater mInflater;
    @ViewById(R.id.listView)
    OnekeyBehalfListView listView;

    public OnekeybehalfItemBean sharebean;

    // 已经没有数据了吗？
    public static boolean nodata;

    public static ArrayList<OnekeybehalfItemBean> data = new ArrayList<OnekeybehalfItemBean>();
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, String> dateStrs = new HashMap<Integer, String>();
    @ViewById(R.id.layout_1)
    View view11;

    @ViewById(R.id.layout_2)
    View view12;
    @ViewById(R.id.layout_3)
    View viewLoading;

    @ViewById(R.id.layout_4)
    View viewFaild;

    @ViewById(R.id.progressBar1)
    ProgressBar progressBar1;

    @ViewById(R.id.ib_close)
    Button ibClose;
    @ViewById
    Button ib_close_2;

    @ViewById(R.id.layout_friend)
    View friendGoon;

    @ViewById(R.id.layout_moment)
    View momentGoon;
    @ViewById(R.id.btn_retry)
    View btn_retry;

    private boolean isloadding;
    private int pageIndex = 1;

    @ViewById
    FrameLayout fl_indictor;
    @ViewById
    ImageView iv_indictor;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private int widthPixels;

    @AfterViews
    void init() {

        if (initSuccess) {
            notifyData1();
            return;
        }

        dateStrs.clear();
        mInflater = LayoutInflater.from(getActivity());

        data = new ArrayList<OnekeybehalfItemBean>();

        initHeader();

        initListView();

        getData(false);


        if (!initShare) {
            ShareSDK.initSDK(getActivity());
            initShare = true;
        }

        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);

        widthPixels = metric.widthPixels;

        initSuccess = true;

    }


    private OnekeybehalfItemBean demobean;


    @ViewById
    TextView tv_day, tv_month;
    private boolean initSuccess;

    @Override
    public void onFragmentRefresh() {

        Toaster.l(getActivity(),"refresh : boolean - "+ WxShopApplication.IS_NEED_REFRESH_ONE_KEY_BEFALLF);
        if(WxShopApplication.IS_NEED_REFRESH_ONE_KEY_BEFALLF){
            Toaster.l(getActivity(),"refresh onekey");
            pageIndex = -1;
            getData(true);
            WxShopApplication.IS_NEED_REFRESH_ONE_KEY_BEFALLF = false;
            return;
        }

        if (data.isEmpty() && initSuccess ) {
            getData(true);
            return;
        }else{
            notifyData();
        }
        super.onFragmentRefresh();


    }

    View headerViewInfo;

    @SuppressLint("InflateParams")
    private void initHeader() {
        headerViewInfo = mInflater
                .inflate(R.layout.list_header_empty_ssn, null);
    }

    private MyAdatpter adapter;

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        int code = data.getIntExtra("result", -1);
        switch (code) {
            case MaijiaxiuFragment.ACTION_ADD_SSN:

                OnekeybehalfItemBean item = (OnekeybehalfItemBean) data.getSerializableExtra("bean");
                if (item != null) {
                    bsb = item;
                    isAdd = true;
                    handler.sendEmptyMessage(upload_success);
                } else {
                    Toaster.l(getActivity(), "数据异常！");
                }

                break;
            case MaijiaxiuFragment.ACTION_EDIT_SSN:
                OnekeybehalfItemBean itemEdit = (OnekeybehalfItemBean) data
                        .getSerializableExtra("bean");
                int pos = data.getIntExtra("editpos", -1);
                if (itemEdit != null) {
                    if (pos != -1) {
                        editPos = pos;
                    }
                    bsb = itemEdit;
                    isAdd = false;
                    handler.sendEmptyMessage(upload_success);
                } else {
                    Toaster.l(getActivity(), "数据异常！");
                }
                break;

            default:
                break;
        }

    }

    ;

    @ItemClick
    void listView(int position) {
        int pos = position - 1;
        if (adapter.getItem(pos).isMenuOpened) {
            adapter.closeMenu(adapter.getItem(pos));
        } else {
            adapter.openMenu(adapter.getItem(pos));
            if (position == listView.getLastVisiblePosition()) {
                listView.setSelection(pos - 1);
            }
        }
    }

    @OnActivityResult(MaijiaxiuFragment.ACTION_EDIT_SSN)
    void onEdit(Intent intent, int resultCode) {
        T.d("ACTION_EDIT_GOOD");
        if (resultCode == Activity.RESULT_OK) {

        }
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        listView.setAutoLoadEnable(false);
        listView.setXListViewListener(this);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setFragment(OneKeyBeHalfListFragment.this);
        adapter = new MyAdatpter();
        listView.setAdapter(adapter);
    }


    String cacheKey = null;

    void getData(final boolean refresh) {
        if (isloadding == true) {
            return;
        }

        isloadding = true;

        AbstractNet net = new OneKeybehalfListNetImpl(getActivity());
        Bundle bun = new Bundle();
        if (pageIndex == -1) {
            bun.putInt("page", 1);
        } else {
            bun.putInt("page", pageIndex);
        }
        net.request(bun, new MainHandler(getActivity(), handler) {

            @Override
            public void onSuccess(Bundle bundle) {

                if (bundle != null
                        && bundle.getString(ConstValue.CACHE_KEY) != null) {
                    cacheKey = bundle.getString(ConstValue.CACHE_KEY);
                    HashMap<String, Object> list = CacheData.getInstance()
                            .getData(cacheKey).get(0);
                    OneKeybehalfListResponseWrapper.MsgsWrapper tradeData = (MsgsWrapper) list.get("orderList");
                    List<OnekeybehalfItemBean> datas = tradeData.getItems();
                    if (datas.size() < ConstValue.PAGE_SIZE_MANAGE) {
                        nodata = true;
                    }
                    if (data == null) {
                        data = new ArrayList<OnekeybehalfItemBean>();
                    }
                    if (pageIndex == -1) {
                        data.clear();
                        pageIndex = 1;
                    }
                    if (refresh) {
                        data.clear();
                    }
                    data.addAll(datas);
                    notifyData();
                    isLoadingData = false;
                    pageIndex++;
                }
            }


            @Override
            public void onFailed(Bundle bundle) {
                isLoadingData = false;
                loadFail = true;
                MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                        "maijiaxiu_pub_faild");
            }
        });
    }

    private boolean loadFail = false;


    protected void notifyData() {
        checkEmptyfooter();
        adapter.notifyDataSetChanged();
    }

    private void checkEmptyfooter() {
        checkFooterView();
    }

    


    protected void notifyData1() {

        initListView();
        notifyData();
    }


    private int mHideGroupPos = -1;


    @Override
    public void onRefresh() {

        pageIndex = -1;
        getData(true);

    }

    @Override
    public void onLoadMore() {

        getData(false);
    }

    private class MyAdatpter extends BaseAdapter implements Serializable {
        private static final long serialVersionUID = 1L;

        @SuppressLint("UseSparseArrays")
        private void setVisiableTextDarkArea(final View convertView,
                                             int position) {
        }

        public void setFloatViewData(int npos) {

        }

        public MyAdatpter() {

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data == null ? 0 : data.size();
        }

        @Override
        public OnekeybehalfItemBean getItem(int arg0) {
            if(arg0 == -1){
                return null;
            }
            return data.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int pos, View convertview, ViewGroup arg2) {

            OnekeybeHalfItem item = (OnekeybeHalfItem) convertview;
            if (item == null) {
                item = OnekeybeHalfItem_.build(getActivity());
            }
            item.setData(data.get(pos), handler, getActivity());
            return item;
        }

        public void openMenu(OnekeybehalfItemBean commodity) {
            commodity.isMenuOpened = true;
            commodity.isAni = true;
            for (OnekeybehalfItemBean commodityWrapper : data) {
                if (commodityWrapper.isMenuOpened && !commodity.equals(commodityWrapper)) {
                    commodityWrapper.isMenuOpened = false;
                    commodityWrapper.isAni = true;
                }
            }
            notifyDataSetChanged();
        }

        public void closeMenu(OnekeybehalfItemBean commodity) {
            commodity.isMenuOpened = false;
            commodity.isAni = true;
            notifyDataSetChanged();
        }

    }

    public final static int POPUP_GOODITEM = ConstValue.MSG_ERROR_FROM_MAINHANDLER + 1;
    public final static int SSN_DEL = POPUP_GOODITEM + 1;
    public final static int ACTION_GET_DATA = SSN_DEL + 1;
    public final static int upload_faild = ACTION_GET_DATA + 1;
    public final static int upload_retry = upload_faild + 1;
    public final static int CHANGE_DATA = upload_retry + 1;
    public final static int NOTIFY_DATA = CHANGE_DATA + 1;
    private static final int upload_success = NOTIFY_DATA + 1;
    public static final int SSN_SHARE = upload_success + 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (getActivity() == null) {
                return;
            }
            if (!attached) {
                return;
            }
            switch (msg.what) {
                case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
                    isloadding = false;
                    loadFail = true;

                    break;
                case SSN_DEL:
                    Bundle closeBun = msg.getData();
                    OnekeybehalfItemBean showbean = (OnekeybehalfItemBean) closeBun
                            .getSerializable(SSNPublishActivity.SSN_DEL_BEAN);
                    dateStrs.clear();
                    data.remove(showbean);
                    notifyData();
                    break;

                case 1:
                    Toaster.l(getActivity(), "启动分享...");
                    break;
                case ACTION_GET_DATA:
                    getData(false);
                    break;
                case 102:
                    progressBar1.setProgress(j++);
                    break;
                case upload_success:
                    if (isAdd) {
                        dateStrs.clear();
                        listView.setSelection(0);
                        data.add(0, bsb);
                    }
                    handler.sendEmptyMessageDelayed(NOTIFY_DATA, 500);
                    // notifyData();
                    break;
                case upload_faild:
                    viewLoading.setVisibility(View.GONE);
                    viewFaild.setVisibility(View.VISIBLE);
                    break;
                case upload_retry:
                    viewLoading.setVisibility(View.VISIBLE);
                    viewFaild.setVisibility(View.GONE);
                    break;
                case NOTIFY_DATA:
                    notifyData();
                    break;
                case SSN_SHARE:
                    Bundle sharebun = msg.getData();
                    OnekeybehalfItemBean bean = (OnekeybehalfItemBean) sharebun
                            .getSerializable("share");
                    sharebean = bean;
                    popUpDialog();
                    break;
                default:
                    break;
            }

        }

        ;
    };
    private int editPos;
    private boolean isAdd = true;
    private int j = 1;
    @ViewById(R.id.btn_back)
    ImageButton btnBack;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (data != null) {
            data.clear();
        }
        nodata = false;
        data = null;
        if (initShare) {
            ShareSDK.stopSDK(getActivity());
        }
        pageIndex = -1;
        initSuccess = false;
        isloadding = false;
    }

    private boolean initShare;

    @UiThread
    public void refreshListView() {
        pageIndex = -1;
        getData(false);
    }

    boolean isLoadingData = false;

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == 1) {
            CommonWebActivity_.intent(getActivity()).url(WDConfig.getInstance().getShopUrl()
                    + WxShopApplication.dataEngine.getShopId()).title("店铺预览").start();
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
    }

    public boolean isLoadingData() {
        return isLoadingData;
    }

    private CustomProgressDialog progressDialog;

    @UiThread
    void startOldProgress() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog
                        .createDialog4BBS(getActivity());
                progressDialog.setMessage("加载中...");
            }
            progressDialog.show();
        }
    }


    public void removeItem(int position) {
        data.remove(position);
        adapter.notifyDataSetChanged();
    }

    @UiThread
    void showErrorMsg(String msg) {
        Toaster.s(getActivity(), msg);
    }

    private OnekeybehalfItemBean bsb;

    private static final int SHARE_MOMENT = 0;
    private static final int SHARE_FRIENT = 1;
    private static final int ONE_KEY_SHARE = SHARE_FRIENT + 1;
    private static final int COPY = ONE_KEY_SHARE + 1;

    private void popUpDialog() {
        String[] items = getResources().getStringArray(
                R.array.share_friends_ssn);
        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_manage");
        if (sharebean == null) {
            Toaster.l(getActivity(), "失败，分享数据空");
            return;
        }
        QMMAlert.showAlert(getActivity(), getString(R.string.share2), items,
                null, new QMMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case SHARE_MOMENT:
                                // 显示启动分享文字
                                handler.sendEmptyMessage(1);
                                MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                                        "click_onkeybehalf_share_moments");
                                momentsGoodItem(sharebean);
                                break;
                            case SHARE_FRIENT:
                                // 显示启动分享文字
                                handler.sendEmptyMessage(1);
                                friendGoodItem(sharebean);
                                MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                                        "click_onkeybehalf_share_friend");
                                break;
                            case ONE_KEY_SHARE:
                                // 显示启动分享文字
                                MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                                        "click_onkeybehalf_share_onkeyshare");
                                WxShopApplication.shareBean = getShareBean(sharebean);
                                Intent intent = new Intent(getActivity(),
                                        ShareActivity.class);

                                intent.putExtra(ConstValue.gaSrcfrom, "android_mmwdapp_hmsgshare_");
                                intent.putExtra("share_content_type", ShareActivity.SHARE_CONTENT_GOOD_ITEM);
                                startActivity(intent);
                                break;
                            case COPY:

                                Toaster.l(getActivity(), "已复制商品链接");
                                Utils.saveClipBoard(getActivity(), getOnkeydaifa(sharebean));

                                break;
                        }
                    }

                });
    }


    private String getOnkeydaifa(OnekeybehalfItemBean model) {
        return WDConfig.getInstance().WD_URL_HUO_YUAN + "item/" + model.getId() + "?fx_refer=qfuid_" + WxShopApplication.dataEngine.getUserId() + "&ga_medium=android_mmwdapp_preview_&ga_source=entrance";
    }

    private void momentsGoodItem(OnekeybehalfItemBean model) {
        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "weixin_share_moment_begin");
        WeiXinDataBean wdb = new WeiXinDataBean();
        wdb.url = getOnkeydaifa(model);
        wdb.imgUrl = QFCommonUtils.generateQiniuUrl(model.getImg(), ImageUtils.ImageSizeForUrl.MIN);
        String desc = model.getDescr();
        if (desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        wdb.description = desc;
        wdb.title = "【新品推荐】" + model.getTitle() + "仅需" + model.getPrice() + "元，欢迎进店选购下单哟！";
        wdb.scope = ConstValue.circle_share;
        UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wctimeline, getActivity());
    }

    private void friendGoodItem(OnekeybehalfItemBean model) {
        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "weixin_share_friend_begin");
        WeiXinDataBean wdb = new WeiXinDataBean();
        wdb.url = getOnkeydaifa(model);
        wdb.imgUrl = QFCommonUtils.generateQiniuUrl(model.getImg(), ImageUtils.ImageSizeForUrl.MIN);
        String desc = model.getDescr();
        if (desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        wdb.description = desc;
        wdb.title = "【新品推荐】" + model.getTitle()+ "仅需" + model.getPrice() + "元，欢迎进店选购下单哟！";
        wdb.scope = ConstValue.friend_share;
        UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wcfriend, getActivity());
    }

    protected ShareBean getShareBean(OnekeybehalfItemBean model) {
        ShareBean shareBean = new ShareBean();
        shareBean.imgUrl = Utils.getThumblePic(model.getImg(), ConstValue.shareSmallPic);
        shareBean.link = WDConfig.getInstance().WD_URL_HUO_YUAN+"item/" + model.getId();

            String descString = model.getDescr();
            if (descString.length() > 100) {
                descString = descString.substring(0, 98) + "...";
            }
            shareBean.title = "亲,我的店铺又有新宝贝了哦!" + model.getTitle() + " "
                    + descString + " 仅需" + model.getPrice() + "元,点击宝贝链接"
                    + "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + model.getId()
                    + " 直接下单购买哦";
            shareBean.from = "manageshop";

        String desc = model.getDescr();
        if (desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        shareBean.desc = desc;
        shareBean.qq_imageUrl = model.getImg();
        shareBean.qqTitle = "亲,我的店铺又有新宝贝了哦! ";
        shareBean.qqTitle_url = shareBean.link;
        shareBean.qqText = shareBean.title;
        return shareBean;
    }


    private static final int SIZE = 80;

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
                    text = getActivity().getString(
                            R.string.wechat_client_inavailable);
                } else if ("WechatTimelineNotSupportedException".equals(msg.obj
                        .getClass().getSimpleName())) {
                    text = getActivity().getString(
                            R.string.wechat_client_inavailable);
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

        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "sina_share_success_sharesdk");
        } else if (plat.getName().equals(QZone.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "qzone_share_success_sharesdk");
        } else if (plat.getName().equals(TencentWeibo.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "qqweibo_share_success_sharesdk");
        }

    }

    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public void onError(Platform plat, int action, Throwable t) {
        t.printStackTrace();
        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
        if (plat.getName().equals(SinaWeibo.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "sina_share_faill_sharesdk");
        } else if (plat.getName().equals(QZone.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "qzone_share_fail_sharesdk");
        } else if (plat.getName().equals(TencentWeibo.NAME)) {
            MobAgentTools.OnEventMobOnDiffUser(getActivity(),
                    "qqweibo_share_fail_sharesdk");
        }
    }


    private void checkFooterView() {
        listView.stopRefresh();
        listView.stopLoadMore();


        if (OneKeyBeHalfListFragment_.data.isEmpty() && OneKeyBeHalfListFragment_.nodata) {
            fl_indictor.setVisibility(View.VISIBLE);
            return;
        }else{
            invisiableEmptyView();
        }
        // 空列表 有数据
        if (OneKeyBeHalfListFragment_.data.isEmpty()){
            if (handler != null) {
                handler.sendEmptyMessage(OneKeyBeHalfListFragment.ACTION_GET_DATA);
            }
            return;
        }
    }
    private void invisiableEmptyView() {
        fl_indictor.setVisibility(View.INVISIBLE);
    }



}