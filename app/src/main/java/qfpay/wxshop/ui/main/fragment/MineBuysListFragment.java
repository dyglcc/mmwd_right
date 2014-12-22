package qfpay.wxshop.ui.main.fragment;import java.io.Serializable;import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;import org.androidannotations.annotations.AfterViews;import org.androidannotations.annotations.Bean;import org.androidannotations.annotations.EFragment;import org.androidannotations.annotations.UiThread;import org.androidannotations.annotations.ViewById;import qfpay.wxshop.R;import qfpay.wxshop.WxShopApplication;import qfpay.wxshop.activity.ManagePreViewActivity;import qfpay.wxshop.activity.SSNPublishActivity;import qfpay.wxshop.config.WDConfig;import qfpay.wxshop.data.beans.MyHuoyuanItemBean;import qfpay.wxshop.data.beans.MyHuoyuanResponseWrapper.MsgsWrapper;import qfpay.wxshop.data.beans.SsnContentBean;import qfpay.wxshop.data.handler.MainHandler;import qfpay.wxshop.data.net.AbstractNet;import qfpay.wxshop.data.net.CacheData;import qfpay.wxshop.data.net.ConstValue;import qfpay.wxshop.data.netImpl.MineHuoyuanGetNetImpl;import qfpay.wxshop.dialogs.ISimpleDialogListener;import qfpay.wxshop.ui.commodity.CommodityDataController;import qfpay.wxshop.ui.view.CustomProgressDialog;import qfpay.wxshop.ui.view.EditorView;import qfpay.wxshop.ui.view.MineBuyItem;import qfpay.wxshop.ui.view.MineBuyItem_;import qfpay.wxshop.ui.view.MineBuyListView;import qfpay.wxshop.ui.view.TopCloseAnimation;import qfpay.wxshop.ui.view.TopExpandAnimation;import qfpay.wxshop.ui.web.huoyuan.CommonWebActivityHuoyuan_;import qfpay.wxshop.utils.MobAgentTools;import qfpay.wxshop.utils.Toaster;import qfpay.wxshop.utils.Utils;import android.annotation.SuppressLint;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Handler.Callback;import android.os.Message;import android.util.DisplayMetrics;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.view.animation.Animation;import android.widget.AbsListView;import android.widget.AbsListView.OnScrollListener;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.BaseAdapter;import android.widget.Button;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.ProgressBar;import android.widget.TextView;import android.widget.Toast;import cn.sharesdk.framework.Platform;import cn.sharesdk.framework.PlatformActionListener;import cn.sharesdk.framework.ShareSDK;import cn.sharesdk.framework.utils.UIHandler;import cn.sharesdk.sina.weibo.SinaWeibo;import cn.sharesdk.tencent.qzone.QZone;import cn.sharesdk.tencent.weibo.TencentWeibo;@SuppressLint("HandlerLeak")@EFragment(R.layout.main_myhuoyuan_list)public class MineBuysListFragment extends BaseFragment implements		OnScrollListener, ISimpleDialogListener, PlatformActionListener,		Callback {	public static final String SP_NAME_MANAGE = "config";	public static final String SP_ITEN_ISNEW = "copy_isnew";	public static final String SP_HEADER_ISNEW = "header_header_img_isnew";	@ViewById	Button btn_share;	@ViewById	Button btn_add;	@ViewById	Button btn_empty_see;	@ViewById	TextView tv_link;	public static final float BILI = 1f;	private LayoutInflater mInflater;	@ViewById(R.id.list_manage_ssn)	MineBuyListView listView;	@ViewById(R.id.iv_share_image_onload)	ImageView shareImg;	@ViewById	ImageView iv_nodata;	@ViewById(R.id.layer_iv_share_result)	ImageView ivImagelayer2;	public MyHuoyuanItemBean sharebean;		@Bean	CommodityDataController cdc;	// 已经没有数据了吗？	public static boolean nodata;	public static ArrayList<MyHuoyuanItemBean> data = new ArrayList<MyHuoyuanItemBean>();	@SuppressLint("UseSparseArrays")	public static Map<Integer, String> dateStrs = new HashMap<Integer, String>();	@ViewById(R.id.layout_1)	View view11;	@ViewById(R.id.layout_2)	View view12;	@ViewById(R.id.layout_3)	View viewLoading;	@ViewById(R.id.layout_4)	View viewFaild;	@ViewById(R.id.progressBar1)	ProgressBar progressBar1;	@ViewById(R.id.ib_close)	Button ibClose;	@ViewById	Button ib_close_2;	@ViewById(R.id.layout_friend)	View friendGoon;	@ViewById(R.id.layout_moment)	View momentGoon;	@ViewById(R.id.btn_retry)	View btn_retry;	private boolean isloadding;	private int pageIndex = 1;	public static int clickedPos;	TopExpandAnimation expand_animation;	TopCloseAnimation closeAnima;	Animation leftAnima, leftanima2;	@SuppressLint("SimpleDateFormat")	private SimpleDateFormat format = new SimpleDateFormat(			"yyyy-MM-dd HH:mm:ss");	private int widthPixels;	@AfterViews	void init() {		if (initSuccess) {			notifyData1();			return;		}		dateStrs.clear();		mInflater = LayoutInflater.from(getActivity());		data = new ArrayList<MyHuoyuanItemBean>();		initHeader();		initListView();		getData();		if (!initShare) {			ShareSDK.initSDK(getActivity());			initShare = true;		}		DisplayMetrics metric = new DisplayMetrics();		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);		widthPixels = metric.widthPixels;		initSuccess = true;	}	private MyHuoyuanItemBean demobean;	private int indicatorGroupId = -1;	private int indicatorGroupHeight;	@ViewById	TextView tv_day, tv_month;	private boolean initSuccess;	public void onScroll(AbsListView view, int firstVisibleItem,			int visibleItemCount, int totalItemCount) {	}	@Override	public void onFragmentRefresh() {		if (data != null) {			if (data.isEmpty() && initSuccess) {				getData();			}		}		if(WxShopApplication.IS_NEED_REFRESH_MINE_HUOYUAN){			getData();		}		super.onFragmentRefresh();	}	View headerViewInfo;	@SuppressLint("InflateParams")	private void initHeader() {		headerViewInfo = mInflater				.inflate(R.layout.list_header_empty_ssn, null);	}	private MyAdatpter adapter;	@Override	public void onStop() {		super.onStop();	}	public void onActivityResult(int requestCode, int resultCode, Intent data) {		int code = data.getIntExtra("result", -1);		switch (code) {		case MaijiaxiuFragment.ACTION_HUOYUAN_ADD://			MyHuoyuanItemBean item = (MyHuoyuanItemBean) data.getSerializableExtra("bean");//			if (item != null) {//				bsb = item;//				isAdd = true;//				handler.sendEmptyMessage(upload_success);//			} else {//				Toaster.l(getActivity(), "数据异常！");//			}			MineBuysListFragment.data.clear();			pageIndex = 1;			nodata = false;			getData();			if(getActivity()!=null){				Toaster.l(getActivity(), "商品已经复制到你的店铺");			}			cdc.reloadData();			break;		default:			break;		}	};	String displayImage;	private void initListView() {		listView.setFragment(this);		listView.setHandler(handler);		if (listView.getHeaderViewsCount() == 0) {			listView.addHeaderView(headerViewInfo, null, false);		}		adapter = new MyAdatpter();		listView.setAdapter(adapter);		listView.setOnScrollListener(this);		View view = listView.getmFooterView();//		listView.setOnItemClickListener(new OnItemClickListener() {////			@Override//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,//					long arg3) {}//		});		view.setOnClickListener(new OnClickListener() {			@Override			public void onClick(View arg0) {				getData();			}		});	}	String cacheKey = null;	void getData() {//		Toaster.l(getActivity(), "sdfsdfs");		if (isloadding == true) {			return;		}		if (nodata) {			setMoreButtonText(getResources().getString(R.string.haveNoData),					false);			return;		}		isloadding = true;		setMoreButtonText("加载中...", true);		AbstractNet net = new MineHuoyuanGetNetImpl(getActivity());		Bundle bun = new Bundle();		if (pageIndex == -1) {			bun.putInt("page", 1);		} else {			bun.putInt("page", pageIndex);		}		net.request(bun, new MainHandler(getActivity(), handler) {			@Override			public void onSuccess(Bundle bundle) {				if (bundle != null						&& bundle.getString(ConstValue.CACHE_KEY) != null) {					cacheKey = bundle.getString(ConstValue.CACHE_KEY);					HashMap<String, Object> list = CacheData.getInstance().getData(cacheKey).get(0);					MsgsWrapper tradeData = (MsgsWrapper) list.get("orderList");					List<MyHuoyuanItemBean> datas = tradeData.getOrders();					if (datas.size() < ConstValue.PAGE_SIZE_MANAGE) {						nodata = true;					}					if (data == null) {						data = new ArrayList<MyHuoyuanItemBean>();					}					data.addAll(datas);					if (pageIndex == -1) {						data.clear();						pageIndex = 1;					}					notifyData();					isLoadingData = false;					stopOldProgress();					pageIndex++;				}			}			@Override			public void onFailed(Bundle bundle) {				isLoadingData = false;				loadFail = true;				stopOldProgress();				MobAgentTools.OnEventMobOnDiffUser(getActivity(),						"maijiaxiu_pub_faild");			}		});	}	private boolean loadFail = false;	private void setMoreButtonText(String str, boolean loading) {		if(listView == null){			return;		}		View view = listView.getmFooterView();		if (view == null) {			return;		}		View progress = view.findViewById(R.id.pb_loading);		if (loading) {			progress.setVisibility(View.VISIBLE);		} else {			progress.setVisibility(View.GONE);		}		TextView textview = (TextView) view.findViewById(R.id.tv_more);		if (textview == null) {			return;		}		textview.setText(str);	}	protected void notifyData() {		listView.checkFooterView();		setFooterText();		initDateStrs();		adapter.notifyDataSetChanged();	}	private void initDateStrs() {		if (dateStrs == null) {			dateStrs = new HashMap<Integer, String>();		}		dateStrs.clear();		for (int i = 0; i < data.size(); i++) {			MyHuoyuanItemBean MyHuoyuanItemBean = data.get(i);			String substring = MyHuoyuanItemBean.getUpdate_time().substring(0, 10);			if (!dateStrs.containsValue(substring)) {				dateStrs.put(i, substring);			} else {				dateStrs.put(i, "");			}		}	}	protected void notifyData1() {		initListView();		listView.checkFooterView();		setFooterText();		adapter.notifyDataSetChanged();	}	@UiThread(delay = 300)	void setFooterText() {		if (getActivity() == null) {			return;		}		if (nodata) {			setMoreButtonText(getResources().getString(R.string.haveNoData),					false);		} else if (data.size() == 0) {			getData();		} else {			setMoreButtonText(getResources().getString(R.string.show_more),					false);		}	}	private int mHideGroupPos = -1;	private class MyAdatpter extends BaseAdapter implements Serializable {		private static final long serialVersionUID = 1L;		@SuppressLint("UseSparseArrays")		private void setVisiableTextDarkArea(final View convertView,				int position) {		}		public MyAdatpter() {		}		@Override		public int getCount() {			// TODO Auto-generated method stub			return data == null ? 0 : data.size();		}		@Override		public MyHuoyuanItemBean getItem(int arg0) {			return data.get(arg0);		}		@Override		public long getItemId(int arg0) {			return 0;		}		@Override		public View getView(int pos, View convertview, ViewGroup arg2) {			if (convertview == null) {				convertview = MineBuyItem_.build(getActivity());			}			((MineBuyItem) convertview).setValues(data.get(pos),getActivity());			return convertview;		}	}	public final static int POPUP_GOODITEM = ConstValue.MSG_ERROR_FROM_MAINHANDLER + 1;	public final static int SSN_DEL = POPUP_GOODITEM + 1;	public final static int ACTION_GET_DATA = SSN_DEL + 1;	public final static int upload_faild = ACTION_GET_DATA + 1;	public final static int upload_retry = upload_faild + 1;	public final static int CHANGE_DATA = upload_retry + 1;	public final static int NOTIFY_DATA = CHANGE_DATA + 1;	private static final int upload_success = NOTIFY_DATA + 1;	public static final int SSN_SHARE = upload_success + 1;	private Handler handler = new Handler() {		@Override		public void handleMessage(android.os.Message msg) {			if (getActivity() == null) {				return;			}			if (!attached) {				return;			}			switch (msg.what) {			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:				isloadding = false;				loadFail = true;				// layout_progress_load.setVisibility(View.INVISIBLE);				if (nodata) {					setMoreButtonText(							getResources().getString(R.string.haveNoData),							false);				} else {					setMoreButtonText("显示更多", false);				}				break;			case SSN_DEL:				Bundle closeBun = msg.getData();				MyHuoyuanItemBean showbean = (MyHuoyuanItemBean) closeBun						.getSerializable(SSNPublishActivity.SSN_DEL_BEAN);				dateStrs.clear();				data.remove(showbean);				notifyData();				break;			case 1:				Toaster.l(getActivity(), "启动分享...");				break;			case ACTION_GET_DATA:				getData();				break;			case 102:				progressBar1.setProgress(j++);				break;//			case upload_success://				if (isAdd) {//					dateStrs.clear();//					listView.setSelection(0);//					data.add(0, bsb);//				} else {//					changeEditPos(editPos, bsb);//				}//				handler.sendEmptyMessageDelayed(NOTIFY_DATA, 500);//				// notifyData();//				break;			case upload_faild:				viewLoading.setVisibility(View.GONE);				viewFaild.setVisibility(View.VISIBLE);				break;			case upload_retry:				viewLoading.setVisibility(View.VISIBLE);				viewFaild.setVisibility(View.GONE);				break;			case NOTIFY_DATA:				notifyData();				break;			case SSN_SHARE:				popUpDialog();				break;			default:				break;			}		};	};	private int editPos;	private boolean isAdd = true;	private int j = 1;	@ViewById(R.id.btn_back)	ImageButton btnBack;	@Override	public void onScrollStateChanged(AbsListView view, int scrollState) {	}	@Override	public void onDestroy() {		super.onDestroy();		if (data != null) {			data.clear();		}		nodata = false;		data = null;		if (initShare) {			ShareSDK.stopSDK(getActivity());		}		pageIndex = 1;		initSuccess = false;		isloadding = false;	}	private boolean initShare;	@UiThread	public void refreshListView() {		pageIndex = 1;		getData();	}	boolean isLoadingData = false;	@Override	public void onPositiveButtonClicked(int requestCode) {		if (requestCode == 1) {			Intent intent = new Intent(getActivity(),					ManagePreViewActivity.class);			intent.putExtra(ConstValue.TITLE, "店铺预览");			intent.putExtra(ConstValue.URL, WDConfig.getInstance().getShopUrl()					+ WxShopApplication.dataEngine.getShopId());			startActivity(intent);		}	}	@Override	public void onNegativeButtonClicked(int requestCode) {	}	public boolean isLoadingData() {		return isLoadingData;	}	private CustomProgressDialog progressDialog;	@UiThread	void startOldProgress() {		if (getActivity() != null && !getActivity().isFinishing()) {			if (progressDialog == null) {				progressDialog = CustomProgressDialog						.createDialog4BBS(getActivity());				progressDialog.setMessage("加载中...");			}			progressDialog.show();		}	}	@UiThread	void stopOldProgress() {		if (getActivity() != null && !getActivity().isFinishing()) {			if (progressDialog != null && progressDialog.isShowing()) {				progressDialog.dismiss();				progressDialog = null;			}		}	}	public void removeItem(int position) {		data.remove(position);		adapter.notifyDataSetChanged();	}	@UiThread	void showErrorMsg(String msg) {		Toaster.s(getActivity(), msg);	}	private MyHuoyuanItemBean bsb;	private static final int SHARE_MOMENT = 0;	private static final int SHARE_FRIENT = 1;	private static final int ONE_KEY_SHARE = SHARE_FRIENT + 1;	private static final int COPY = ONE_KEY_SHARE + 1;	private void popUpDialog() {		String[] items = getResources().getStringArray(				R.array.share_friends_ssn);		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_manage");		if (sharebean == null) {			Toaster.l(getActivity(), "失败，分享数据空");			return;		}//		QMMAlert.showAlert(getActivity(), getString(R.string.share2), items,//				null, new QMMAlert.OnAlertSelectId() {////					@Override//					public void onClick(int whichButton) {//						switch (whichButton) {//						case SHARE_FRIENT://							// 显示启动分享文字//							handler.sendEmptyMessage(1);//							MobAgentTools.OnEventMobOnDiffUser(getActivity(),//									"click_ssn_share_moments");//							momentsShop(sharebean);//							break;//						case SHARE_MOMENT://							// 显示启动分享文字//							MobAgentTools.OnEventMobOnDiffUser(getActivity(),//									"click_ssn_share_friend");////							handler.sendEmptyMessage(1);//							friendShop(sharebean);//							break;//						}//					}////				});	}//	private void momentsShop(MyHuoyuanItemBean gb) {//		String image_url = gb.getImg_url();//		WeiXinDataBean wdb = new WeiXinDataBean();//		if (image_url != null && !image_url.equals("")) {//			wdb.imgUrl = Utils.getThumblePic(image_url, 120);//		} else {//			Toaster.l(getActivity(), "分享图片丢失,分享失败");//			return;//		}//		wdb.title = gb.getTitle();//		String desc = getContentStr(gb.getContent());//		if (desc != null) {//			desc = desc.replace(" ", "");//			desc = desc.replaceAll("\\n", "");//		}//		// if (desc.length() > MAX_SHARE_LEN) {//		// desc = desc.substring(0, MAX_SHARE_LEN) + "...";//		// }//		wdb.description = desc;//		wdb.scope = ConstValue.circle_share;//		wdb.url = Utils.getSSNurl(gb);//		wdb.imgUrl = gb.getImg_url();////		UtilsWeixinShare.shareWeb(wdb, null, getActivity());//	}	// private static final int MAX_SHARE_LEN = 34;//	private void friendShop(MyHuoyuanItemBean gb) {//		String image_url = gb.getImg_url();//		WeiXinDataBean wdb = new WeiXinDataBean();//		if (image_url != null && !image_url.equals("")) {//			wdb.imgUrl = Utils.getThumblePic(image_url, 120);//		} else {//			Toaster.l(getActivity(), "分享图片丢失,分享失败");//			return;//		}//		String desc = getContentStr(gb.getContent());//		if (desc != null) {//			desc = desc.replace(" ", "");//			desc = desc.replaceAll("\\n", "");//		}//		// if (desc.length() > MAX_SHARE_LEN) {//		// desc = desc.substring(0, MAX_SHARE_LEN) + "...";//		// }//		wdb.description = desc;//		wdb.title = gb.getTitle();//		wdb.url = Utils.getSSNurl(gb);//		wdb.scope = ConstValue.friend_share;//		UtilsWeixinShare.shareWeb(wdb, null, getActivity());//	}	private String getContentStr(List<SsnContentBean> content) {		StringBuilder sb = new StringBuilder();		for (int i = 0; i < content.size(); i++) {			SsnContentBean sdb = content.get(i);			if (sdb.getType().equals(EditorView.FLAG_EDIT)) {				sb.append(sdb.getContent());				sb.append("\n");			}		}		return sb.toString();	}	@Override	public boolean handleMessage(Message msg) {		// TODO Auto-generated method stub		String text = Utils.actionToString(msg.arg2);		switch (msg.arg1) {		case 1: {			// 成功			Platform plat = (Platform) msg.obj;			text = plat.getName() + "分享成功";		}			break;		case 2: {			// 失败			if ("WechatClientNotExistException".equals(msg.obj.getClass()					.getSimpleName())) {				text = getActivity().getString(						R.string.wechat_client_inavailable);			} else if ("WechatTimelineNotSupportedException".equals(msg.obj					.getClass().getSimpleName())) {				text = getActivity().getString(						R.string.wechat_client_inavailable);			} else {				text = getString(R.string.fail_share2);			}		}			break;		case 3: {			// 取消			Platform plat = (Platform) msg.obj;			text = plat.getName() + "取消分享";		}			break;		}		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();		return false;	}	@Override	public void onComplete(Platform plat, int action,			HashMap<String, Object> res) {		Message msg = new Message();		msg.arg1 = 1;		msg.arg2 = action;		msg.obj = plat;		UIHandler.sendMessage(msg, this);		if (plat.getName().equals(SinaWeibo.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"sina_share_success_sharesdk");		} else if (plat.getName().equals(QZone.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"qzone_share_success_sharesdk");		} else if (plat.getName().equals(TencentWeibo.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"qqweibo_share_success_sharesdk");		}	}	public void onCancel(Platform plat, int action) {		Message msg = new Message();		msg.arg1 = 3;		msg.arg2 = action;		msg.obj = plat;		UIHandler.sendMessage(msg, this);	}	public void onError(Platform plat, int action, Throwable t) {		t.printStackTrace();		Message msg = new Message();		msg.arg1 = 2;		msg.arg2 = action;		msg.obj = t;		UIHandler.sendMessage(msg, this);		if (plat.getName().equals(SinaWeibo.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"sina_share_faill_sharesdk");		} else if (plat.getName().equals(QZone.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"qzone_share_fail_sharesdk");		} else if (plat.getName().equals(TencentWeibo.NAME)) {			MobAgentTools.OnEventMobOnDiffUser(getActivity(),					"qqweibo_share_fail_sharesdk");		}	}	// @Override	// public void onShare(SharedPlatfrom which) {	// MobAgentTools.OnEventMobOnDiffUser(getActivity(),	// "click_share_maijiaxiu");	// // 好有，朋友圈。一键，复制连接	// switch (which) {	// case WXFRIEND:	// // 好友	// // 显示启动分享文字	// MobAgentTools.OnEventMobOnDiffUser(getActivity(),	// "click_maijiaxiu_share_friend");	//	// if (data.size() == 0) {	// Toaster.s(getActivity(), "亲，还没有发布碎碎念，先发布碎碎念吧");	// break;	// }	// handler.sendEmptyMessage(1);	// MyHuoyuanItemBean bsbFriend = data.get(0);	// friendShop(bsbFriend);	// break;	// case WXMOMENTS:	// // 朋友圈	// MobAgentTools.OnEventMobOnDiffUser(getActivity(),	// "click_maijiaxiu_share_moments");	// if (data.size() == 0) {	// Toaster.s(getActivity(), "亲，还没有发布碎碎念，先发布碎碎念吧");	// break;	// }	// handler.sendEmptyMessage(1);	// MyHuoyuanItemBean bsb = data.get(0);	// momentsShop(bsb);	// break;	//	// default:	// break;	// }	// }	//	// @Override	// public String getShareFromName() {	// return "碎碎念";	// }	private String getSSNUrl(MyHuoyuanItemBean gb) {		return "http://" + WxShopApplication.app.getDomainMMWDUrl()				+ "/h5/show.html?shopid=" + gb.getId();	}}