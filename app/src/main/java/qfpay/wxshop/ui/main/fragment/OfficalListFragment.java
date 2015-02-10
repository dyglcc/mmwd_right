package qfpay.wxshop.ui.main.fragment;import java.io.Serializable;import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import org.androidannotations.annotations.AfterViews;import org.androidannotations.annotations.Click;import org.androidannotations.annotations.EFragment;import org.androidannotations.annotations.OnActivityResult;import org.androidannotations.annotations.UiThread;import org.androidannotations.annotations.ViewById;import org.androidannotations.annotations.res.StringArrayRes;import qfpay.wxshop.R;import qfpay.wxshop.WxShopApplication;import qfpay.wxshop.activity.HuoyuanQmmActivity_;import qfpay.wxshop.activity.OfficialGoodIsWhatActivity_;import qfpay.wxshop.activity.OnekeybehalfWhatisActivty;import qfpay.wxshop.activity.OnekeybehalfWhatisActivty_;import qfpay.wxshop.activity.SSNPublishActivity;import qfpay.wxshop.adapter.GridAdapterDeal;import qfpay.wxshop.adapter.GridAdapterDeal.OnGridItemClickListener;import qfpay.wxshop.adapter.GridAdapterDeal.OnLazeLoadListener;import qfpay.wxshop.app.BaseFragment;import qfpay.wxshop.config.WDConfig;import qfpay.wxshop.data.beans.OfficialGoodItemBean;import qfpay.wxshop.data.beans.SsnContentBean;import qfpay.wxshop.data.handler.MainHandler;import qfpay.wxshop.data.net.AbstractNet;import qfpay.wxshop.data.net.CacheData;import qfpay.wxshop.data.net.ConstValue;import qfpay.wxshop.data.netImpl.OfficialGoodGetNetImpl;import qfpay.wxshop.dialogs.ISimpleDialogListener;import qfpay.wxshop.ui.view.CustomProgressDialog;import qfpay.wxshop.ui.view.EditorView;import qfpay.wxshop.ui.view.OffiGoodsListView;import qfpay.wxshop.ui.view.OfficialGoodsItem;import qfpay.wxshop.ui.view.OfficialGoodsItem_;import qfpay.wxshop.ui.web.CommonWebActivity_;import qfpay.wxshop.ui.web.huoyuan.CommonWebActivityHuoyuan_;import qfpay.wxshop.utils.MobAgentTools;import qfpay.wxshop.utils.QMMAlert;import qfpay.wxshop.utils.QMMAlert.OnAlertSelectId;import qfpay.wxshop.utils.T;import qfpay.wxshop.utils.Toaster;import qfpay.wxshop.utils.Utils;import android.annotation.SuppressLint;import android.app.Activity;import android.content.Intent;import android.graphics.drawable.AnimationDrawable;import android.os.Bundle;import android.os.Handler;import android.os.Handler.Callback;import android.os.Message;import android.util.DisplayMetrics;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.widget.ArrayAdapter;import android.widget.BaseAdapter;import android.widget.Button;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.ProgressBar;import android.widget.TextView;import android.widget.Toast;import cn.sharesdk.framework.Platform;import cn.sharesdk.framework.PlatformActionListener;import cn.sharesdk.framework.ShareSDK;import cn.sharesdk.framework.utils.UIHandler;import cn.sharesdk.sina.weibo.SinaWeibo;import cn.sharesdk.tencent.qzone.QZone;import cn.sharesdk.tencent.weibo.TencentWeibo;/** * 官方货源列表页面 * * */@SuppressLint("HandlerLeak")@EFragment(R.layout.main_official_good_list)public class OfficalListFragment extends BaseFragment implements        ISimpleDialogListener, PlatformActionListener, Callback {    @ViewById    Button btn_share;    @ViewById    Button btn_add;    @StringArrayRes(R.array.official_category)    String categorys[];    public static final int page_size = 10;    @ViewById    Button btn_empty_see;    @ViewById    TextView tv_link;    public static final float BILI = 1f;    private LayoutInflater mInflater;    @ViewById(R.id.list_manage_official)    OffiGoodsListView listView;    @ViewById(R.id.iv_share_image_onload)    ImageView shareImg;    @ViewById    ImageView iv_nodata;    @ViewById(R.id.layer_iv_share_result)    ImageView ivImagelayer2;    @ViewById    ImageView iv_loading;    @ViewById    ImageView iv_service;    public OfficialGoodsItem sharebean;    // 已经没有数据了吗？    public static boolean nodata;    public static ArrayList<OfficialGoodItemBean> data = new ArrayList<OfficialGoodItemBean>();    @SuppressLint("UseSparseArrays")//	public static Map<Integer, String> dateStrs = new HashMap<Integer, String>();    @ViewById(R.id.layout_1)    View view11;    @ViewById(R.id.layout_2)    View view12;    @ViewById(R.id.layout_3)    View viewLoading;    @ViewById(R.id.layout_4)    View viewFaild;    @ViewById(R.id.progressBar1)    ProgressBar progressBar1;    @ViewById(R.id.ib_close)    Button ibClose;    @ViewById    Button ib_close_2;    @ViewById(R.id.layout_friend)    View friendGoon;    @ViewById(R.id.layout_moment)    View momentGoon;    @ViewById(R.id.btn_retry)    View btn_retry;    private boolean isloadding;    private int pageIndex;//    全部商品 0//    女装 1//    美食6//    护肤4//    彩妆5//    配饰3//    鞋包2//    家居日用8//    男装9    private static HashMap<String, Integer> map = new HashMap<String, Integer>() {        {            this.put("全部商品", 0);            this.put("女装", 1);            this.put("美食", 6);            this.put("护肤", 4);            this.put("彩妆", 5);            this.put("配饰", 3);            this.put("鞋包", 2);            this.put("家居日用", 8);            this.put("男装", 9);        }    };    @SuppressLint("SimpleDateFormat")    private SimpleDateFormat format = new SimpleDateFormat(            "yyyy-MM-dd HH:mm:ss");    private int widthPixels;    View layout_order, layout_category;    TextView tv_category, tv_order;    @AfterViews    void init() {        if (initSuccess) {            notifyData1();            return;        }//		dateStrs.clear();        mInflater = LayoutInflater.from(getActivity());        data = new ArrayList<OfficialGoodItemBean>();        setClickListenter();        initHeader();        initListView();        getData();        if (!initShare) {            ShareSDK.initSDK(getActivity());            initShare = true;        }        DisplayMetrics metric = new DisplayMetrics();        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);        widthPixels = metric.widthPixels;        initSuccess = true;        if (!WxShopApplication.dataEngine.isFirstinOfficalActivity()) {            handler.postDelayed(new Runnable() {                @Override                public void run() {                    OfficialGoodIsWhatActivity_.intent(getActivity()).start();                }            }, 2000);        }    }    void layout_order() {        if (getActivity() == null) {            return;        }        final String[] stringArray = getActivity().getResources().getStringArray(                R.array.official_order);        QMMAlert.showAlertWithListView(getActivity(),                stringArray[pos_order], R.drawable.icon_up_arrow,                stringArray, pos_order, new OnAlertSelectId() {                    @Override                    public void onClick(int whichButton) {                        if (pos_order != whichButton) {                            pos_order = whichButton;                            String text = stringArray[whichButton];                            tv_order.setText(text);                            // 显示指导价或者显示利润                            adapter.notifyDataSetChanged();                            // umeng 统计                            HashMap<String, String> values = new HashMap<String, String>();                            values.put("show_type", pos_order == 0 ? "选择指导价" : "选择显示进价");                            MobAgentTools.OnEventMobOnDiffUser(getActivity(), "PROFIT_GUIDE_PRICE_SHOW_CHOOSE", values);                        }                    }                }, layout_order);    }    void layout_category() {        if (getActivity() == null) {            return;        }        final String[] stringArray = getActivity().getResources().getStringArray(                R.array.official_category);        QMMAlert.showAlertWithListView(getActivity(),                stringArray[pos_category], R.drawable.icon_up_arrow,                stringArray, pos_category, new OnAlertSelectId() {                    @Override                    public void onClick(int whichButton) {                        if (pos_category != whichButton) {                            pos_category = whichButton;                            // umeng 统计                            HashMap<String, String> values = new HashMap<String, String>();                            values.put("category", categorys[pos_category]);                            MobAgentTools.OnEventMobOnDiffUser(getActivity(), "CATEGORY_OFFICIAL_GOODS_CHOOSE", values);                            String text = stringArray[whichButton];                            tv_category.setText(text);                            data.clear();                            adapter.notifyDataSetChanged();                            pageIndex = 0;                            nodata = false;                            getData();                        }                    }                }, layout_category);    }    @ViewById    TextView tv_day, tv_month;    @ViewById    View layout_float;    @ViewById    View ib_search_sub;    private boolean initSuccess;    @Override    public void onFragmentRefresh() {        if (data != null) {            if (data.isEmpty() && initSuccess) {                getData();            }        }        super.onFragmentRefresh();    }    @Click    void layout_moment() {        friendShop(bsb);    }    @Click    void layout_friend() {        // momentsShop(bsb);    }    @Click    void ib_search_sub() {    }    View headerViewInfo;    public static final int PROFIT = 0;    public static final int GUIDE_PRICE = 1;    public static int pos_order = GUIDE_PRICE;    private int pos_category;    View headerBanner;    private void initHeader() {        if (headerViewInfo == null) {            headerViewInfo = mInflater.inflate(                    R.layout.list_header_empty_official_goods, null);        }        if (headerBanner == null) {            headerBanner = mInflater.inflate(                    R.layout.list_header_banner_official_goods, null);        }        headerBanner.findViewById(R.id.iv_off_banner).setOnClickListener(                new OnClickListener() {                    @Override                    public void onClick(View view) {//                        Toaster.l(getActivity(), "跳转一键代发网页");                        OnekeybehalfWhatisActivty_.intent(getActivity()).start();                    }                }        );        headerViewInfo.findViewById(R.id.ib_official).setOnClickListener(                new OnClickListener() {                    @Override                    public void onClick(View arg0) {                        if (getActivity() != null) {                            MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_Supply_of_goods_Guidepage");                        }                        OfficialGoodIsWhatActivity_.intent(getActivity())                                .start();                    }                });        if (listView.getHeaderViewsCount() == 0) {            listView.addHeaderView(headerBanner, null, false);        }        if (listView.getHeaderViewsCount() == 1) {            listView.addHeaderView(headerViewInfo, null, false);        }        layout_category = headerViewInfo.findViewById(R.id.layout_category);        layout_order = headerViewInfo.findViewById(R.id.layout_order);        tv_category = (TextView) headerViewInfo.findViewById(R.id.tv_category);        tv_order = (TextView) headerViewInfo.findViewById(R.id.tv_order);        layout_category.setOnClickListener(new OnClickListener() {            @Override            public void onClick(View view) {                layout_category();            }        });        layout_order.setOnClickListener(new OnClickListener() {            @Override            public void onClick(View view) {//                layout_order();            }        });        String[] stringArrayCategory = getActivity().getResources().getStringArray(                R.array.official_category);        tv_category.setText(stringArrayCategory[pos_category]);        // 设置选项        String[] stringArrayOrder = getActivity().getResources().getStringArray(                R.array.official_order);        tv_order.setText(stringArrayOrder[pos_order]);    }    private ArrayAdapter<String> adapter_spnner;    private MyAdatpter adapter;    private void setClickListenter() {        iv_service.setOnClickListener(new OnClickListener() {            @Override            public void onClick(View v) {                MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_Supply_of_goods_Contactus");                HuoyuanQmmActivity_.intent(getActivity()).start();            }        });    }    @Override    public void onStop() {        super.onStop();    }    public void onActivityResult(int requestCode, int resultCode, Intent data) {        int code = data.getIntExtra("result", -1);        switch (code) {            case MaijiaxiuFragment.ACTION_ADD_SSN:                OfficialGoodsItem item = (OfficialGoodsItem) data                        .getSerializableExtra("bean");                if (item != null) {                    bsb = item;                    isAdd = true;                    handler.sendEmptyMessage(upload_success);                } else {                    Toaster.l(getActivity(), "数据异常！");                }                break;            case MaijiaxiuFragment.ACTION_EDIT_SSN:                OfficialGoodsItem itemEdit = (OfficialGoodsItem) data                        .getSerializableExtra("bean");                int pos = data.getIntExtra("editpos", -1);                if (itemEdit != null) {                    if (pos != -1) {                        editPos = pos;                    }                    bsb = itemEdit;                    isAdd = false;                    handler.sendEmptyMessage(upload_success);                } else {                    Toaster.l(getActivity(), "数据异常！");                }                break;            default:                break;        }    }    ;    String displayImage;    @OnActivityResult(MaijiaxiuFragment.ACTION_EDIT_SSN)    void onEdit(Intent intent, int resultCode) {        T.d("ACTION_EDIT_GOOD");        if (resultCode == Activity.RESULT_OK) {        }    }    public int getDefaultItemWH(int screenWidth) {        if (screenWidth == 0) {            screenWidth = 480;        }        int paddingleft = (int) getActivity().getResources().getDimension(                R.dimen.hy_margin_2_eage);        int paddingRight = (int) getActivity().getResources().getDimension(                R.dimen.hy_margin_2_eage);        // Toaster.l(mContext, "margin :" + paddingleft);        int itemWidth = (screenWidth - paddingleft - paddingRight - paddingRight) / 2;        return itemWidth;    }    private int itemWidth;    private GridAdapterDeal<MyAdatpter> mAdapter;    private void initListView() {        if (WxShopApplication.app.HUOYUAN_KEFU_DISPLAY != null && WxShopApplication.app.HUOYUAN_KEFU_DISPLAY.equals("no")) {            iv_service.setVisibility(View.INVISIBLE);        } else {            iv_service.setVisibility(View.VISIBLE);        }        adapter = new MyAdatpter();        DisplayMetrics metric = new DisplayMetrics();        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);        int screenWidth = metric.widthPixels;        int itemWidth = getDefaultItemWH(screenWidth);        this.itemWidth = itemWidth;        mAdapter = new GridAdapterDeal<MyAdatpter>(                getActivity(), adapter, screenWidth, itemWidth);        mAdapter.setNumColumns(2);        mAdapter.setLazeLoadListener(new OnLazeLoadListener() {            @Override            public void onLazeLoad() {                getData();            }        });        mAdapter.setOnItemClickListener(new OnGridItemClickListener() {            @Override            public void onItemClick(int pos, int realPos) {                // 这里的Listener需要自己扩充，可以callback你需要的内容。                if (data.size() == 0) {                    return;                }                if (getActivity() == null) {                    return;                }                MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_every_goods");                OfficialGoodItemBean officialGoodItemBean = data.get(realPos);                if (officialGoodItemBean == null                        || officialGoodItemBean.getId() != null                        && !officialGoodItemBean.getId().equals("")) {                    CommonWebActivityHuoyuan_                            .intent(getActivity())                            .url(WDConfig.getInstance().WD_URL_HUO_YUAN                                    + "h5/b-item.html?itemid="                                    + officialGoodItemBean.getId() + "&app_ver=" + Utils.getAppVersionString(getActivity()))                            .title(officialGoodItemBean.getTitle())                            .startForResult(                                    MaijiaxiuFragment.ACTION_HUOYUAN_ADD);                }            }        });        listView.setAdapter(mAdapter);        View getmFooterView = listView.getmFooterView();        getmFooterView.setOnClickListener(new OnClickListener() {            @Override            public void onClick(View arg0) {                getData();            }        });    }    String cacheKey = null;    void getData() {        if (isloadding == true) {            return;        }        if (nodata) {            setMoreButtonText(getResources().getString(R.string.haveNoData),                    false);            return;        }        isloadding = true;        setMoreButtonText("加载中...", true);        AbstractNet net = new OfficialGoodGetNetImpl(getActivity());        Bundle bun = new Bundle();        if (pageIndex == -1) {            bun.putInt("page", 0);        } else {            bun.putInt("page", pageIndex);        }        bun.putInt("category", map.get(categorys[pos_category]));        net.request(bun, new MainHandler(getActivity(), handler) {            @Override            public void onSuccess(Bundle bundle) {                if (bundle != null                        && bundle.getString(ConstValue.CACHE_KEY) != null) {                    cacheKey = bundle.getString(ConstValue.CACHE_KEY);                    HashMap<String, Object> list = CacheData.getInstance()                            .getData(cacheKey).get(0);                    qfpay.wxshop.data.beans.OfficialGoodsResponseWrapper.MsgsWrapper tradeData = (qfpay.wxshop.data.beans.OfficialGoodsResponseWrapper.MsgsWrapper) list                            .get("orderList");                    List<OfficialGoodItemBean> datas = tradeData.getItems();                    if (data == null) {                        data = new ArrayList<OfficialGoodItemBean>();                    }                    if (pageIndex == -1) {                        data.clear();                        pageIndex = 0;                    }                    data.addAll(datas);                    if (datas.size() < page_size) {                        nodata = true;                    }//                    if (data.size() == tradeData.getCount()) {//                        nodata = true;//                    }                    notifyData();                    isLoadingData = false;                    stopOldProgress();                    pageIndex++;                }            }            @Override            public void onFailed(Bundle bundle) {                isLoadingData = false;                loadFail = true;                stopOldProgress();                MobAgentTools.OnEventMobOnDiffUser(getActivity(),                        "maijiaxiu_pub_faild");            }        });    }    private boolean loadFail = false;    private void setMoreButtonText(String str, boolean loading) {        View view = listView.getmFooterView();        if (view == null) {            return;        }        View progress = view.findViewById(R.id.pb_loading);        if (loading) {            progress.setVisibility(View.VISIBLE);        } else {            progress.setVisibility(View.GONE);        }        TextView textview = (TextView) view.findViewById(R.id.tv_more);        if (textview == null) {            return;        }        textview.setText(str);        displayImage();    }    protected void notifyData() {        setClickListenter();        listView.checkFooterView();        setFooterText();        adapter.notifyDataSetChanged();    }    protected void notifyData1() {        setClickListenter();        initHeader();        initListView();        listView.checkFooterView();        setFooterText();        adapter.notifyDataSetChanged();    }    @UiThread(delay = 300)    void setFooterText() {        if (getActivity() == null) {            return;        }        if (nodata) {            setMoreButtonText(getResources().getString(R.string.haveNoData),                    false);        } else if (data.size() == 0) {            getData();        } else {            setMoreButtonText(getResources().getString(R.string.show_more),                    false);        }    }    private class MyAdatpter extends BaseAdapter implements Serializable {        private static final long serialVersionUID = 1L;        @SuppressLint("UseSparseArrays")        private void setVisiableTextDarkArea(final View convertView,                                             int position) {        }        @Override        public int getCount() {            // TODO Auto-generated method stub            return data == null ? 0 : data.size();        }        @Override        public OfficialGoodItemBean getItem(int arg0) {            return data.get(arg0);        }        @Override        public long getItemId(int arg0) {            return 0;        }        @Override        public View getView(int pos, View convertview, ViewGroup arg2) {            if (convertview == null) {                // convertview = OfficialGoodsItem_.build(getActivity());                convertview = OfficialGoodsItem_.build(getActivity());            }            ((OfficialGoodsItem_) convertview).setValues(data.get(pos), pos,                    itemWidth);            return convertview;        }    }    public final static int POPUP_GOODITEM = ConstValue.MSG_ERROR_FROM_MAINHANDLER + 1;    public final static int SSN_DEL = POPUP_GOODITEM + 1;    public final static int ACTION_GET_DATA = SSN_DEL + 1;    public final static int upload_faild = ACTION_GET_DATA + 1;    public final static int upload_retry = upload_faild + 1;    public final static int CHANGE_DATA = upload_retry + 1;    public final static int NOTIFY_DATA = CHANGE_DATA + 1;    private static final int upload_success = NOTIFY_DATA + 1;    public static final int SSN_SHARE = upload_success + 1;    private Handler handler = new Handler() {        @Override        public void handleMessage(android.os.Message msg) {            if (getActivity() == null) {                return;            }            if (!attached) {                return;            }            switch (msg.what) {                case ConstValue.MSG_ERROR_FROM_MAINHANDLER:                    isloadding = false;                    loadFail = true;                    // layout_progress_load.setVisibility(View.INVISIBLE);                    if (nodata) {                        setMoreButtonText(                                getResources().getString(R.string.haveNoData),                                false);                    } else {                        setMoreButtonText("显示更多", false);                    }                    break;                case SSN_DEL:                    Bundle closeBun = msg.getData();                    OfficialGoodsItem showbean = (OfficialGoodsItem) closeBun                            .getSerializable(SSNPublishActivity.SSN_DEL_BEAN);//				dateStrs.clear();                    data.remove(showbean);                    notifyData();                    break;                case 1:                    Toaster.l(getActivity(), "启动分享...");                    break;                case ACTION_GET_DATA:                    getData();                    break;                case 102:                    progressBar1.setProgress(j++);                    break;                case upload_success:                    break;                case upload_faild:                    viewLoading.setVisibility(View.GONE);                    viewFaild.setVisibility(View.VISIBLE);                    break;                case upload_retry:                    viewLoading.setVisibility(View.VISIBLE);                    viewFaild.setVisibility(View.GONE);                    break;                case NOTIFY_DATA:                    notifyData();                    break;                case SSN_SHARE:                    popUpDialog();                    break;                default:                    break;            }        }        ;    };    private int editPos;    private boolean isAdd = true;    private int j = 1;    @ViewById(R.id.btn_back)    ImageButton btnBack;    // protected void changeEditPos(int editPos2, OfficialGoodsItem des) {    // OfficialGoodsItem src = data.get(editPos2);    //    // src.setAlluv(des.getAlluv());    // src.setContent(des.getContent());    // src.setContent_type(des.getContent_type());    // src.setCreate_time(des.getCreate_time());    // src.setGood_id(des.getGood_id());    // src.setId(des.getId());    // src.setImg_url(des.getImg_url());    // src.setLikes(des.getLikes());    // src.setLink(des.getLink());    // src.setMid(des.getMid());    // src.setMsg_type(des.getMsg_type());    // src.setQf_uid(des.getQf_uid());    // src.setShop_id(des.getShop_id());    // src.setSys_state(des.getSys_state());    // src.setTitle(des.getTitle());    // src.setUpdate_time(des.getUpdate_time());    // src.setUser_state(des.getUser_state());    // src.setWeixinid(des.getWeixinid());    //    // }    @Override    public void onDestroy() {        super.onDestroy();        if (data != null) {            data.clear();        }        nodata = false;        data = null;        if (initShare) {            ShareSDK.stopSDK(getActivity());        }        pageIndex = 0;        initSuccess = false;        isloadding = false;    }    private boolean initShare;    @UiThread    public void refreshListView() {        pageIndex = 0;        getData();    }    boolean isLoadingData = false;    @Override    public void onPositiveButtonClicked(int requestCode) {        if (requestCode == 1) {            CommonWebActivity_.intent(getActivity()).url(WDConfig.getInstance().getShopUrl()                    + WxShopApplication.dataEngine.getShopId()).title("店铺预览").start();        }    }    @Override    public void onNegativeButtonClicked(int requestCode) {    }    public boolean isLoadingData() {        return isLoadingData;    }    private CustomProgressDialog progressDialog;    @UiThread    void startOldProgress() {        if (getActivity() != null && !getActivity().isFinishing()) {            if (progressDialog == null) {                progressDialog = CustomProgressDialog                        .createDialog4BBS(getActivity());                progressDialog.setMessage("加载中...");            }            progressDialog.show();        }    }    @UiThread    void stopOldProgress() {        if (getActivity() != null && !getActivity().isFinishing()) {            if (progressDialog != null && progressDialog.isShowing()) {                progressDialog.dismiss();                progressDialog = null;            }        }    }    public void removeItem(int position) {        data.remove(position);        adapter.notifyDataSetChanged();    }    @UiThread    void showErrorMsg(String msg) {        Toaster.s(getActivity(), msg);    }    private OfficialGoodsItem bsb;    private static final int SHARE_MOMENT = 0;    private static final int SHARE_FRIENT = 1;    private static final int ONE_KEY_SHARE = SHARE_FRIENT + 1;    private static final int COPY = ONE_KEY_SHARE + 1;    private void popUpDialog() {        String[] items = getResources().getStringArray(                R.array.share_friends_ssn);        MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_manage");        if (sharebean == null) {            Toaster.l(getActivity(), "失败，分享数据空");            return;        }        QMMAlert.showAlert(getActivity(), getString(R.string.share2), items,                null, new QMMAlert.OnAlertSelectId() {                    @Override                    public void onClick(int whichButton) {                        switch (whichButton) {                            case SHARE_FRIENT:                                // 显示启动分享文字                                handler.sendEmptyMessage(1);                                MobAgentTools.OnEventMobOnDiffUser(getActivity(),                                        "click_ssn_share_moments");                                // momentsShop(sharebean);                                break;                            case SHARE_MOMENT:                                // 显示启动分享文字                                MobAgentTools.OnEventMobOnDiffUser(getActivity(),                                        "click_ssn_share_friend");                                handler.sendEmptyMessage(1);                                friendShop(sharebean);                                break;                        }                    }                });    }    private void momentsShop(OfficialGoodItemBean gb) {        // String image_url = gb.getImg();        // WeiXinDataBean wdb = new WeiXinDataBean();        // if (image_url != null && !image_url.equals("")) {        // wdb.imgUrl = Utils.getThumblePic(image_url, 120);        // } else {        // Toaster.l(getActivity(), "分享图片丢失,分享失败");        // return;        // }        // wdb.title = gb.getTitle();        // String desc = getContentStr(gb.getContent());        // if (desc != null) {        // desc = desc.replace(" ", "");        // desc = desc.replaceAll("\\n", "");        // }        // wdb.description = desc;        // wdb.scope = ConstValue.circle_share;        // wdb.url = Utils.getSSNurl(gb);        // wdb.imgUrl = gb.getImg_url();        //        // UtilsWeixinShare.shareWeb(wdb, null, getActivity());    }    private void friendShop(OfficialGoodsItem gb) {        // String image_url = gb.getImg_url();        // WeiXinDataBean wdb = new WeiXinDataBean();        // if (image_url != null && !image_url.equals("")) {        // wdb.imgUrl = Utils.getThumblePic(image_url, 120);        // } else {        // Toaster.l(getActivity(), "分享图片丢失,分享失败");        // return;        // }        // String desc = getContentStr(gb.getContent());        // if (desc != null) {        // desc = desc.replace(" ", "");        // desc = desc.replaceAll("\\n", "");        // }        // wdb.description = desc;        // wdb.title = gb.getTitle();        // wdb.url = Utils.getSSNurl(gb);        // wdb.scope = ConstValue.friend_share;        // UtilsWeixinShare.shareWeb(wdb, null, getActivity());    }    private String getContentStr(List<SsnContentBean> content) {        StringBuilder sb = new StringBuilder();        for (int i = 0; i < content.size(); i++) {            SsnContentBean sdb = content.get(i);            if (sdb.getType().equals(EditorView.FLAG_EDIT)) {                sb.append(sdb.getContent());                sb.append("\n");            }        }        return sb.toString();    }    @Override    public boolean handleMessage(Message msg) {        // TODO Auto-generated method stub        String text = Utils.actionToString(msg.arg2);        switch (msg.arg1) {            case 1: {                // 成功                Platform plat = (Platform) msg.obj;                text = plat.getName() + "分享成功";            }            break;            case 2: {                // 失败                if ("WechatClientNotExistException".equals(msg.obj.getClass()                        .getSimpleName())) {                    text = getActivity().getString(                            R.string.wechat_client_inavailable);                } else if ("WechatTimelineNotSupportedException".equals(msg.obj                        .getClass().getSimpleName())) {                    text = getActivity().getString(                            R.string.wechat_client_inavailable);                } else {                    text = getString(R.string.fail_share2);                }            }            break;            case 3: {                // 取消                Platform plat = (Platform) msg.obj;                text = plat.getName() + "取消分享";            }            break;        }        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();        return false;    }    @Override    public void onComplete(Platform plat, int action,                           HashMap<String, Object> res) {        Message msg = new Message();        msg.arg1 = 1;        msg.arg2 = action;        msg.obj = plat;        UIHandler.sendMessage(msg, this);        if (plat.getName().equals(SinaWeibo.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "sina_share_success_sharesdk");        } else if (plat.getName().equals(QZone.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "qzone_share_success_sharesdk");        } else if (plat.getName().equals(TencentWeibo.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "qqweibo_share_success_sharesdk");        }    }    public void onCancel(Platform plat, int action) {        Message msg = new Message();        msg.arg1 = 3;        msg.arg2 = action;        msg.obj = plat;        UIHandler.sendMessage(msg, this);    }    public void onError(Platform plat, int action, Throwable t) {        t.printStackTrace();        Message msg = new Message();        msg.arg1 = 2;        msg.arg2 = action;        msg.obj = t;        UIHandler.sendMessage(msg, this);        if (plat.getName().equals(SinaWeibo.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "sina_share_faill_sharesdk");        } else if (plat.getName().equals(QZone.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "qzone_share_fail_sharesdk");        } else if (plat.getName().equals(TencentWeibo.NAME)) {            MobAgentTools.OnEventMobOnDiffUser(getActivity(),                    "qqweibo_share_fail_sharesdk");        }    }    private String getSSNUrl(OfficialGoodsItem gb) {        return "http://" + WxShopApplication.app.getDomainMMWDUrl()                + "/h5/show.html?shopid=" + gb.getId();    }    @UiThread(delay = 300)    void displayImage() {        if (data.size() == 0 && isloadding) {            iv_loading.setVisibility(View.VISIBLE);            AnimationDrawable drawable = (AnimationDrawable) iv_loading.getDrawable();            if (drawable != null) {                drawable.start();            }        } else {            iv_loading.setVisibility(View.INVISIBLE);        }    }}