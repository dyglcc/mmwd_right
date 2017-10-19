package qfpay.wxshop.ui.main.fragment;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import m.framework.utils.UIHandler;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.GeneralWebViewActivity;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.MsgsWrapper;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.MaijiaxiuGetNetImpl;
import qfpay.wxshop.dialogs.ISimpleDialogListener;
import qfpay.wxshop.listener.MaijiaxiuUploadListener;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.buyersshow.*;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseNetProcesser;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.view.CustomProgressDialog;
import qfpay.wxshop.ui.view.MaijiaxiuItem;
import qfpay.wxshop.ui.view.MaijiaxiuListView;
import qfpay.wxshop.ui.view.TopCloseAnimation;
import qfpay.wxshop.ui.view.TopExpandAnimation;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.framework.utils.UIHandler;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.squareup.okhttp.internal.Platform;

/**
 * 买家秀fragment
 * */
@SuppressLint("HandlerLeak")
@EFragment(R.layout.main_maijiaxiu_list)
public class MaijiaxiuFragment extends BaseFragment implements
		OnScrollListener, ISimpleDialogListener,
		Callback, OnShareLinstener, MaijiaxiuUploadListener {
	private static final long serialVersionUID = 1L;
	public static final String SP_NAME_MANAGE = "config";
	public static final String SP_ITEN_ISNEW = "copy_isnew";
	public static final String SP_HEADER_ISNEW = "header_header_img_isnew";

	@Bean
	BuyersShowReleaseNetProcesser processer;
	@ViewById
	Button btn_share;

	@ViewById
	Button btn_empty_see;
	@ViewById
	TextView tv_link;

	private LayoutInflater mInflater;
	@ViewById(R.id.list_manage_shops)
	MaijiaxiuListView listView;
	@ViewById(R.id.iv_share_image_onload)
	ImageView shareImg;

	@ViewById(R.id.layer_iv_share_result)
	ImageView ivImagelayer2;

	// 已经没有数据了吗？
	public static boolean nodata;

	public static ArrayList<BuyerShowBean> data = new ArrayList<BuyerShowBean>();

	@ViewById(R.id.layout_anima_tion)
	View animaView;

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
	@ViewById(R.id.iv_jiaocheng)
	View iv_jiaocheng;

	private boolean isloadding;
	private int pageIndex = 1;

	public static int clickedPos;
	private static final int ACTION_PROMOTION = 11;
	private static final int ACTION_PUBLISH_CREATE = 12;
	public static final int ACTION_EDIT_MAIJIAXIU = 13;
	public static final int ACTION_ADD_MAIJIAXIU = ACTION_EDIT_MAIJIAXIU + 1;
	private static final int ACTION_EMPTY_DATA = ACTION_ADD_MAIJIAXIU + 1;
	
	// 碎碎念
	public static final int ACTION_EDIT_SSN = ACTION_EMPTY_DATA + 1;
	public static final int ACTION_ADD_SSN = ACTION_EDIT_SSN + 1;
	// 货源
	public static final int ACTION_HUOYUAN_ADD = ACTION_ADD_SSN + 1;
    //商户圈我的动态
    public static final int ACTION_MYDYNAMIC_EDIT_NOTE = ACTION_HUOYUAN_ADD + 1;
    public static final int ACTION_PUBLISH_NOTE = ACTION_MYDYNAMIC_EDIT_NOTE + 1;

	TopExpandAnimation expand_animation;
	TopCloseAnimation closeAnima;
	Animation leftAnima, leftanima2;

	@SuppressLint("UseSparseArrays")
	public static Map<Integer, String> dateStrs = new HashMap<Integer, String>();

	@Override
	public void onUpload(int pos) {
		T.d("maijiaxiu upload");
		progressBar1.setProgress(pos);
	}

	@Click
	void iv_jiaocheng() {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(),
				"click_maijiaxiu_demo");

		Intent intent = new Intent(getActivity(), GeneralWebViewActivity.class);
		intent.putExtra(ConstValue.TITLE, "买家秀教程");
		intent.putExtra(ConstValue.URL, WDConfig.getInstance()
				.getMaijiaxiuUrl());
		startActivity(intent);

	}

	@Override
	@UiThread
	public void onSuccess(boolean add, int pos, BuyerShowBean editbean) {
		T.d("maijiaxiu success");
		// 加入一条动画实现
		view11.startAnimation(leftAnima);
		// 动画结束会handler会处理成功消息。
		view12.startAnimation(leftanima2);

		if (displayImage != null) {

			if (displayImage.startsWith("http://")) {

				aq.id(ivImagelayer2).image(
						Utils.getThumblePic(displayImage, 100),

						true, true);
			} else {

				File file = new File(displayImage);

				BitmapAjaxCallback call = new BitmapAjaxCallback();

				call.anchor(1.0f / 1.0f);

				call.ratio(1.0f);

				aq.id(ivImagelayer2).image(file, false, 200, call);

			}

		}

		// aq.id(ivImagelayer2).image(Utils.getThumblePic(getImageUrl(bsb),
		// 100),
		// true, true);
		String datestr = format.format(new Date());
		editbean.setUpdate_time(datestr);

		editbean.setChineseDate(MaijiaxiuGetNetImpl.getChineseDateStr(editbean
				.getUpdate_time()));
		// handler.sendEmptyMessageDelayed(upload_success, 400);
		shareActivity();

		editPos = pos;

		MobAgentTools.OnEventMobOnDiffUser(getActivity(),
				"maijiaxiu_pub_success");
	}

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	@UiThread
	public void onUploadFaild() {
		T.d("maijiaxiu faild");
		// Toaster.l(getActivity(), "上传失败");
		// viewLoading.setVisibility(View.GONE);
		// viewFaild.setVisibility(View.VISIBLE);
		handler.sendEmptyMessageDelayed(upload_faild, 400);
		// Toaster.l(getActivity(), "upload faild");
	}

	@Click
	void btn_retry() {
		processer.retryRelease();
		handler.sendEmptyMessage(upload_retry);
		// Toaster.l(getActivity(), "重试");
		animaView.startAnimation(expand_animation);
	}

	@Click
	void ib_close_2() {
		ib_close();
	}

	@Override
	public void onInitProgress(int count, BuyerShowBean bean, boolean isWb,
			boolean isTwb, boolean isQzone, boolean isEdit) {
		
		if(listView.getEmptyFooterView()!=null){
			listView.setVisibility(View.VISIBLE);
		}
		T.d("maijiaxiu init");
		
		progressBar1.setMax(100);

		bsb = bean;

		isAdd = !isEdit;

		bsb.setChineseDate(MaijiaxiuGetNetImpl.getChineseDateStr(bsb
				.getUpdate_time()));

		this.isWb = isWb;
		this.isTwb = isTwb;
		this.isQzone = isQzone;

		leftAnima = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_left_out);
		leftanima2 = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_right_in);

		leftanima2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				animaView.setVisibility(View.VISIBLE);
				view11.setVisibility(View.VISIBLE);
				view12.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view11.setVisibility(View.INVISIBLE);
				handler.sendEmptyMessage(upload_success);
			}
		});
		expand_animation = new TopExpandAnimation(animaView, 600);
		expand_animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				view12.setVisibility(View.INVISIBLE);
				view11.setVisibility(View.VISIBLE);

				viewLoading.setVisibility(View.VISIBLE);
				viewFaild.setVisibility(View.GONE);
				animaView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				File file = new File(displayImage);
				BitmapAjaxCallback call = new BitmapAjaxCallback();
				call.anchor(1.0f / 1.0f);
				call.ratio(1.0f);
				aq.id(shareImg).image(file, false, 200, call);
			}
		});
	}

	private AQuery aq = new AQuery(getActivity());

	@AfterViews
	void init() {

		if(initSuccess){
			notifyData1();
			return;
		}
		
		dateStrs.clear();
		mInflater = LayoutInflater.from(getActivity());

		data = new ArrayList<BuyerShowBean>();

		initHeader();

		initListView();
		// loadData
		getData();

		if (!initShare) {
//			ShareSDK.initSDK(getActivity());
			initShare = true;
		}

//		weibo = ShareSDK.getPlatform(getActivity(), SinaWeibo.NAME);
//		qzone = ShareSDK.getPlatform(getActivity(), QZone.NAME);
//		tecentWeibo = ShareSDK.getPlatform(getActivity(), TencentWeibo.NAME);

		WxShopApplication.app.maijiaxiuListener = this;

		expand_animation = new TopExpandAnimation(animaView, 600);

		((MainActivity) getActivity()).onBuyersShowFragmentInit(this);

		aq = new AQuery(getActivity());
		
		initSuccess = true;
	}


	private boolean initSuccess;

	@Override
	public void onFragmentRefresh() {
		if (data != null) {
			if (data.isEmpty() && initSuccess) {
				getData();
			}
		}
		super.onFragmentRefresh();
	}

	@Click
	void layout_moment() {
		friendShop(bsb);
	}

	@Click
	void layout_friend() {
		momentsShop(bsb);
	}

	@Click
	void ib_close() {
		closeAnima = new TopCloseAnimation(animaView, 500);
		closeAnima.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				animaView.setVisibility(View.GONE);
				view11.setVisibility(View.INVISIBLE);
				view12.setVisibility(View.INVISIBLE);
			}
		});
		animaView.startAnimation(closeAnima);
	}

	View headerViewInfo;

	@SuppressLint("InflateParams")
	private void initHeader() {
		headerViewInfo = mInflater.inflate(R.layout.list_header_empty, null);
	}

	private MyAdatpter adapter;
	private AQuery query;

	@Click
	void btn_share() {
		popUpDialog();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
		super.onActivityResult(requestCode, resultCode, dataIntent);
		if(dataIntent == null){
			return;
		}
		int code = dataIntent.getIntExtra("result", -1);
		switch (code) {
		case ACTION_ADD_MAIJIAXIU:
			String justfinish  = dataIntent.getStringExtra("onlyfinish");
			if(justfinish!=null && !justfinish.equals("")){
				if(listView!=null ){
					View view = listView.getEmptyFooterView();
					if(view!=null){
						if(data!=null && data.isEmpty()){
							view.setVisibility(View.VISIBLE);
						}
					}
				}
				return;
			}
			String data = dataIntent.getStringExtra("data");
			displayImage = data;
			animaView.startAnimation(expand_animation);
			break;
		case ACTION_EDIT_MAIJIAXIU:

			String dir = dataIntent.getStringExtra("data");
			displayImage = dir;
			if (dir != null && !dir.equals("")) {
				if (dir.startsWith("http:")) {
					aq.id(shareImg).image(Utils.getThumblePic(dir, 100),
							true, true);
					aq.id(ivImagelayer2).image(Utils.getThumblePic(dir, 100),
							true, true);
				} else {
					File file1 = new File(dir);
					BitmapAjaxCallback call1 = new BitmapAjaxCallback();
					call1.anchor(1.0f / 1.0f);
					call1.ratio(1.0f);
					aq.id(shareImg).image(file1, false, 200, call1);
					aq.id(ivImagelayer2).image(file1, false, 200, call1);
				}
			}

			animaView.startAnimation(expand_animation);
		
			break;

		default:
			break;
		}
	}

	@OnActivityResult(ACTION_ADD_MAIJIAXIU)
	void onAdd(Intent intent, int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// intent) {
	// // TODO Auto-generated method stub
	// if (resultCode == Activity.RESULT_OK) {
	// String data = intent.getStringExtra("data");
	// if (data != null && !data.equals("")) {
	// if (data.startsWith("http:")) {
	// aq.id(ivImagelayer2).image(Utils.getThumblePic(data, 100),
	// true, true);
	// } else {
	// File file = new File(data);
	// // BitmapAjaxCallback call = new BitmapAjaxCallback();
	// // call.anchor(1.0f / 1.0f);
	// // call.ratio(1.0f);
	// aq.id(shareImg).image(file, 200);
	// }
	// }
	//
	// animaView.startAnimation(expand_animation);
	// }
	// super.onActivityResult(requestCode, resultCode, intent);
	// }

	String displayImage;

	@OnActivityResult(ACTION_EDIT_MAIJIAXIU)
	void onEdit(Intent intent, int resultCode) {
		T.d("ACTION_EDIT_GOOD");
		if (resultCode == Activity.RESULT_OK) {}
	}

	@OnActivityResult(ACTION_EMPTY_DATA)
	void onNodata(Intent intent, int resultCode) {

		if (resultCode == Activity.RESULT_OK) {
			String extra = intent.getStringExtra("finish");
			if (extra == null) {
				processer.setMaijiaxiuLinstener(this, 0);
				BuyersShowReleaseActivity_.intent(getActivity())
						.startForResult(ACTION_ADD_MAIJIAXIU);
			}
		}
	}

	private void initListView() {
		listView.setFragment(this);
		query = new AQuery(getActivity());
		listView.setHandler(handler);
		if(listView.getHeaderViewsCount() == 0){
			listView.addHeaderView(headerViewInfo, null, false);
		}
		adapter = new MyAdatpter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View childView,
					int position, long arg3) {
			}
		});
		listView.setOnScrollListener(this);
		View view = listView.getmFooterView();
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getData();
			}
		});
	}

	String cacheKey = null;

	void getData() {
		if (isloadding == true) {
			return;
		}
		if (nodata) {
			// Toaster.l(getActivity(), "亲，发布来一个买家秀吧");
			setMoreButtonText(getResources().getString(R.string.haveNoData),
					false);
			return;
		}
		isloadding = true;

		// layout_progress_load.setVisibility(View.VISIBLE);
		setMoreButtonText("加载中...", true);
		AbstractNet net = new MaijiaxiuGetNetImpl(getActivity());
		Bundle bun = new Bundle();
		if (pageIndex == -1) {
			bun.putString("page", "1");
		} else {
			bun.putString("page", pageIndex + "");
		}
		net.request(bun, new MainHandler(getActivity(), handler) {

			@Override
			public void onSuccess(Bundle bundle) {

				if (bundle != null
						&& bundle.getString(ConstValue.CACHE_KEY) != null) {
					cacheKey = bundle.getString(ConstValue.CACHE_KEY);
					HashMap<String, Object> list = CacheData.getInstance()
							.getData(cacheKey).get(0);
					MsgsWrapper tradeData = (MsgsWrapper) list.get("orderList");
					List<BuyerShowBean> datas = tradeData.getMsgs();
					if (datas.size() < ConstValue.PAGE_SIZE_MANAGE) {
						nodata = true;
					}
					if (data == null) {
						data = new ArrayList<BuyerShowBean>();
					}
					if (pageIndex == -1) {
						data.clear();
						pageIndex = 1;
					}
					data.addAll(datas);
					// 展开状态
					notifyData();
					// CacheData.getInstance().destroy();
					isLoadingData = false;
					stopOldProgress();
					pageIndex++;
				}
			}

			@Override
			public void onFailed(Bundle bundle) {
				isLoadingData = false;
				loadFail = true;
				stopOldProgress();
				// T.d("网络有问题哦~可以检查一下网络哦~");

				MobAgentTools.OnEventMobOnDiffUser(getActivity(),
						"maijiaxiu_pub_faild");
			}
		});
	}

	private boolean loadFail = false;

	private void setMoreButtonText(String str, boolean loading) {

		View view = listView.getmFooterView();
		if (view == null) {
			return;
		}
		View progress = view.findViewById(R.id.pb_loading);
		if (loading) {
			progress.setVisibility(View.VISIBLE);
		} else {
			progress.setVisibility(View.GONE);
		}
		TextView textview = (TextView) view.findViewById(R.id.tv_more);
		if (textview == null) {
			return;
		}
		textview.setText(str);
	}

	protected void notifyData() {
		initDateStrs();
		listView.checkFooterView();
		adapter.notifyDataSetChanged();
	}
	
	protected void notifyData1() {
		initListView();
		listView.checkFooterView();
		if (nodata) {
			
			setMoreButtonText(getResources().getString(R.string.haveNoData),
					false);
		}else if(data.size() == 0){
			getData();
			
		}else{
			setMoreButtonText(getResources().getString(R.string.show_more),
					false);
		}
		animaView.setVisibility(View.GONE);
		initDateStrs();
		adapter.notifyDataSetChanged();
	}

	private void initDateStrs() {
		if(dateStrs == null){
			dateStrs = new HashMap<Integer, String>();
		}
		dateStrs.clear();
		for(int i=0;i<data.size();i++){
			BuyerShowBean buyerShowBean = data.get(i);
			if(!dateStrs.containsValue(buyerShowBean.getChineseDate())){
				dateStrs.put(i, buyerShowBean.getChineseDate());
			}else{
				dateStrs.put(i, "");
			}
		}
	}

	private class MyAdatpter extends BaseAdapter implements Serializable {
		private static final long serialVersionUID = 1L;

		@SuppressLint("UseSparseArrays")
		private void setVisiableTextDarkArea(final View convertView,
				int position) {
		}

		public MyAdatpter() {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public BuyerShowBean getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int pos, View convertview, ViewGroup arg2) {

			BuyerShowBean gb = data.get(pos);
			if (convertview == null) {
				convertview = new MaijiaxiuItem(getActivity(),
						MaijiaxiuFragment.this, gb, query, handler, pos,
						processer);
			} else {
				((MaijiaxiuItem) convertview).setValues(data.get(pos), pos);
			}
			return convertview;
		}

	}

	public final static int POPUP_GOODITEM = ConstValue.MSG_ERROR_FROM_MAINHANDLER + 1;
	public final static int MAIJIAXIU_DEL = POPUP_GOODITEM + 1;
	public final static int ACTION_GET_DATA = MAIJIAXIU_DEL + 1;
	public final static int upload_faild = ACTION_GET_DATA + 1;
	public final static int upload_retry = upload_faild + 1;
	public final static int CHANGE_DATA = upload_retry + 1;
	public final static int NOTIFY_DATA = CHANGE_DATA + 1;
	private static final int upload_success = NOTIFY_DATA + 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			if(!attached){
				return;
			}
			if (getActivity() == null) {
				return;
			}
			switch (msg.what) {
			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
				isloadding = false;
				loadFail = true;
				// layout_progress_load.setVisibility(View.INVISIBLE);
				if (nodata) {
					setMoreButtonText(
							getResources().getString(R.string.haveNoData),
							false);
				} else {
					setMoreButtonText(getResources().getString(R.string.show_more), false);
				}
				break;
			case MAIJIAXIU_DEL:
				Bundle closeBun = msg.getData();
				BuyerShowBean showbean = (BuyerShowBean) closeBun
						.getSerializable("buyshowbean");
				data.remove(showbean);
				dateStrs.clear();
				notifyData();
				break;

			case 1:
				Toaster.l(getActivity(), "启动分享...");
				break;
			case ACTION_GET_DATA:
				getData();
				break;
			case 102:
//				progressBar1.setProgress(j++);
				break;
			case upload_success:
				if (isAdd) {
					dateStrs.clear();
					listView.setSelection(0);
					data.add(0, bsb);
				} else {
					changeEditPos(editPos, bsb);
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
			default:
				break;
			}

		};
	};
	private int editPos;
	private boolean isAdd = true;
	private int j = 1;

	protected void changeEditPos(int editPos2, BuyerShowBean ediBean2) {
		BuyerShowBean buyerShowBean = data.get(editPos2);
		buyerShowBean.setChineseDate(ediBean2.getChineseDate());
		buyerShowBean.setContent(ediBean2.getContent());
		buyerShowBean.setCreate_time(ediBean2.getCreate_time());
		buyerShowBean.setGood_id(ediBean2.getGood_id());
		buyerShowBean.setHm_images(ediBean2.getHm_images());
		buyerShowBean.setMid(ediBean2.getMid());
		buyerShowBean.setUpdate_time(ediBean2.getUpdate_time());
		buyerShowBean.setMsg_type(ediBean2.getMsg_type());
		buyerShowBean.setWeixinid(ediBean2.getWeixinid());
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (data != null) {
			data.clear();
		}
		dateStrs.clear();
		nodata = false;
		data = null;
		if (initShare) {
//			ShareSDK.stopSDK(getActivity());
		}
		pageIndex = -1;
		initSuccess = false;
		isloadding = false;
	}

	private boolean initShare;

	@OnActivityResult(ACTION_PROMOTION)
	void onResult(Intent intent, int resultCode) {

	}

	@OnActivityResult(ACTION_PUBLISH_CREATE)
	void onResultPublish(Intent intent, int resultCode) {

	}


	@UiThread
	public void refreshListView() {

		pageIndex = -1;
		getData();
	}

	boolean isLoadingData = false;

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 1) {
//			Intent intent = new Intent(getActivity(),
//					ManagePreViewActivity.class);
//			intent.putExtra(ConstValue.TITLE, "店铺预览");
//			intent.putExtra(ConstValue.URL, WDConfig.getInstance().getShopUrl()
//					+ WxShopApplication.dataEngine.getShopId());
//			startActivity(intent);
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
		if (getActivity()!=null && !getActivity().isFinishing()) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog4BBS(getActivity());
				progressDialog.setMessage("加载中...");
			}
			progressDialog.show();
		}
	}

	@UiThread
	void stopOldProgress() {
		if (getActivity()!=null && !getActivity().isFinishing()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
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

	private BuyerShowBean bsb;

	private static final int SHARE_MOMENT = 0;
	private static final int SHARE_FRIENT = 1;
	private static final int ONE_KEY_SHARE = SHARE_FRIENT + 1;
	private static final int COPY = ONE_KEY_SHARE + 1;

	private void popUpDialog() {
		String[] items = getResources().getStringArray(
				R.array.share_friends_maijiaxiu);
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_manage");
		if (data.isEmpty() && loadFail) {
			Toaster.l(getActivity(), "加载失败，请重新打开");
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
									"click_maijiaxiu_share_moments");

							BuyerShowBean bsb = data.get(0);
							momentsShop(bsb);

							break;
						case SHARE_FRIENT:
							// 显示启动分享文字
							MobAgentTools.OnEventMobOnDiffUser(getActivity(),
									"click_maijiaxiu_share_friend");

							handler.sendEmptyMessage(1);
							BuyerShowBean bsbFriend = data.get(0);
							friendShop(bsbFriend);
							break;
						case ONE_KEY_SHARE:

							MobAgentTools.OnEventMobOnDiffUser(getActivity(),
									"click_maijiaxiu_onkey");
							BuyerShowBean shareBsb = data.get(0);
							WxShopApplication.shareBean = getShareBean(shareBsb);
							startActivity(new Intent(getActivity(),
									ShareActivity.class));
							break;
						case COPY:

							MobAgentTools.OnEventMobOnDiffUser(getActivity(),
									"click_maijiaxiu_share_copy");
							String copyStr = "又有新的买家秀啦！店主再夸也白搭，买家说好，才是真的好~ "
									+ getMaijiaxiuUrl();
							Toaster.l(getActivity(), "复制链接成功");
							Utils.saveClipBoard(getActivity(), copyStr);

							break;
						default:
							break;
						}
					}

				});
	}

	private void momentsShop(BuyerShowBean gb) {
		if (data.size() == 0) {
			Toaster.s(getActivity(), "亲，还没有发布买家秀，先发布买家秀吧");
			return;
		}

		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.title = WxShopApplication.dataEngine.getShopName()
				+ "又有新的买家秀啦！买家说好，才是真的好~";
		String desc = "";
		if (gb != null) {
			desc = gb.getContent();
			if (desc.length() > 100) {
				desc = desc.substring(0, 100) + "...";
			}
		}
		wdb.description = desc;
		wdb.scope = ConstValue.circle_share;
		wdb.url = getMaijiaxiuUrl();
		wdb.imgUrl = getImageUrl(gb);

		UtilsWeixinShare.shareWeb(wdb, null, getActivity());
	}

	private String getMaijiaxiuUrl() {
		return "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/h5/show.html?shopid="
				+ WxShopApplication.dataEngine.getShopId();
	}

	private void friendShop(BuyerShowBean gb) {
		if (gb == null) {
			Toaster.s(getActivity(), "亲，还没有发布买家秀，先发布买家秀吧");
			return;
		}
		List<ImageBean> list = gb.getHm_images();
		if (list == null) {
			return;
		}
		ImageBean ib = list.get(0);
		WeiXinDataBean wdb = new WeiXinDataBean();
		if (ib != null && !ib.getUrl().equals("")) {
			wdb.imgUrl = Utils.getThumblePic(ib.getUrl(), 120);
		} else {
			Toaster.l(getActivity(), "分享图片丢失");
		}

		wdb.description = gb.getContent();
		wdb.title = WxShopApplication.dataEngine.getShopName()
				+ "又有新的买家秀啦！买家说好，才是真的好~";
		wdb.url = getMaijiaxiuUrl();
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb, null, getActivity());
	}

	protected ShareBean getShareBean(BuyerShowBean gb) {

		if (gb == null) {
			return null;
		}
		List<ImageBean> list = gb.getHm_images();
		if (list == null) {
			return null;
		}
		ImageBean ib = list.get(0);
		ShareBean sb = new ShareBean();
		String content = "#" + WxShopApplication.dataEngine.getShopName()
				+ "的买家秀#" + gb.getContent();

		if (!getLink(gb).equals("")) {
			content += "  人气口碑商品在此：" + getLink(gb) + " ";
		} else {
			content += "  更多买家好评点此查看哦~" + getMaijiaxiuUrl() + " ";
		}
		sb.imgUrl = ib.getUrl();
		sb.link = getMaijiaxiuUrl();
		sb.title = content;

		sb.desc = gb.getContent();
		sb.from = "maijiaxiu";

		// qqZone
		String qqText = gb.getContent();

		if (!getLink(gb).equals("")) {
			qqText += "  人气口碑商品在此：" + getLink(gb) + " ";
		} else {
			qqText += "  更多买家好评点此查看哦~" + getMaijiaxiuUrl() + " ";
		}
		sb.qq_imageUrl = ib.getUrl();
		sb.qqText = qqText;
		sb.qqTitle = "#" + WxShopApplication.dataEngine.getShopName() + "的买家秀#";
		String linkurl = getLink(gb);
		if (linkurl.equals("")) {
			linkurl = getMaijiaxiuUrl();
		}
		sb.qqTitle_url = linkurl;

		return sb;

	}

	public boolean isWb, isTwb, isQzone;

	private Platform weibo;
	private Platform qzone;
	private Platform tecentWeibo;

	@Background(id = ConstValue.THREAD_CANCELABLE)
	void shareActivity() {
//		try {
//			Thread.sleep(800);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		if (isWb) {
//			SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
//			// sp.text = WxShopApplication.shareBean.title
//			// + WxShopApplication.shareBean.link;
//
//			// 人气口碑商品在此：（商品link） ，（店铺名）的更多买家好评点此查看哦~：link （分享自 @喵喵微店）
//
//			String content = "#" + WxShopApplication.dataEngine.getShopName()
//					+ "的买家秀#" + bsb.getContent();
//
//			if (!getLink(bsb).equals("")) {
//				content += "  人气口碑商品在此：" + getLink(bsb) + " ,";
//			} else {
//				content += "  更多买家好评点此查看哦~" + getMaijiaxiuUrl() + " ,";
//			}
//			content += "（分享自 @喵喵微店）";
//			sp.text = content;
//			sp.imageUrl = getImageUrl(bsb);
//			weibo.setPlatformActionListener(this); // 设置分享事件回调
//			// 执行图文分享
//
//			weibo.share(sp);
//		}
//		if (isTwb) {
//			TencentWeibo.ShareParams sp = new TencentWeibo.ShareParams();
//			// sp.text = WxShopApplication.shareBean.title
//			// + WxShopApplication.shareBean.link;
//			String content = "#" + WxShopApplication.dataEngine.getShopName()
//					+ "的买家秀#" + bsb.getContent();
//
//			if (!getLink(bsb).equals("")) {
//				content += "  人气口碑商品在此：" + getLink(bsb);
//			} else {
//				content += "  更多买家好评点此查看哦~" + getMaijiaxiuUrl();
//			}
//			sp.text = content;
//			sp.imageUrl = getImageUrl(bsb);
//			tecentWeibo.setPlatformActionListener(this); // 设置分享事件回调
//			// 执行图文分享
//			tecentWeibo.share(sp);
//		}
//		if (isQzone) {
//			QZone.ShareParams sp = new QZone.ShareParams();
//			String linkurl = getLink(bsb);
//			String shareText = "#" + WxShopApplication.dataEngine.getShopName()
//					+ "的买家秀#";
//			if (linkurl.equals("")) {
//				linkurl = getMaijiaxiuUrl();
//			}
//			sp.title = shareText;
//
//			sp.titleUrl = linkurl; // 标题的超链接
//			// + WxShopApplication.shareBean.link;
//			sp.text = bsb.getContent() + " 更多买家好评点此查看哦~";
//			sp.imageUrl = getImageUrl(bsb);
//			// sp.comment = "我对此分享内容的评论";
//			sp.site = "发布分享的网站名称";
//			sp.siteUrl = getMaijiaxiuUrl();
//			qzone.setPlatformActionListener(this); // 设置分享事件回调
//			// 执行图文分享
//			qzone.share(sp);
//		}
	}

	private String getLink(BuyerShowBean bsb2) {
		if (bsb2.getGood_id() == null || bsb2.getGood_id().equals("")
				|| bsb2.getGood_id().equals("0")) {
			return "";
		}

		return "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + bsb2.getGood_id();
	}

	private String getImageUrl(BuyerShowBean bsb2) {
		List<ImageBean> hm_images = bsb2.getHm_images();
		if (hm_images == null) {
			return "";
		}
		if (hm_images.size() != 0) {
			ImageBean ib = hm_images.get(0);
			return ib.getUrl() == null ? "" : Utils.getThumblePic(ib.getUrl(),
					120);
		}
		return "";
	}

	@Override
	public boolean handleMessage(Message msg) {
//		// TODO Auto-generated method stub
//		String text = Utils.actionToString(msg.arg2);
//		switch (msg.arg1) {
//		case 1: {
//			// 成功
//			Platform plat = (Platform) msg.obj;
//			text = plat.getName() + "分享成功";
//		}
//			break;
//		case 2: {
//			// 失败
//			if ("WechatClientNotExistException".equals(msg.obj.getClass()
//					.getSimpleName())) {
//				text = getActivity().getString(
//						R.string.wechat_client_inavailable);
//			} else if ("WechatTimelineNotSupportedException".equals(msg.obj
//					.getClass().getSimpleName())) {
//				text = getActivity().getString(
//						R.string.wechat_client_inavailable);
//			} else {
//				text = getString(R.string.fail_share2);
//			}
//		}
//			break;
//		case 3: {
//			// 取消
//			Platform plat = (Platform) msg.obj;
//			text = plat.getName() + "取消分享";
//		}
//			break;
//		}
//
//		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
//		return false;
		return false;
	}

//	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
//		Message msg = new Message();
//		msg.arg1 = 1;
//		msg.arg2 = action;
//		msg.obj = plat;
//		UIHandler.sendMessage(msg, this);
//
//		if (plat.getName().equals(SinaWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"sina_share_success_sharesdk");
//		} else if (plat.getName().equals(QZone.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"qzone_share_success_sharesdk");
//		} else if (plat.getName().equals(TencentWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"qqweibo_share_success_sharesdk");
//		}

	}

	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onError(Platform plat, int action, Throwable t) {
//		t.printStackTrace();
//		Message msg = new Message();
//		msg.arg1 = 2;
//		msg.arg2 = action;
//		msg.obj = t;
//		UIHandler.sendMessage(msg, this);
//		if (plat.getName().equals(SinaWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"sina_share_faill_sharesdk");
//		} else if (plat.getName().equals(QZone.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"qzone_share_fail_sharesdk");
//		} else if (plat.getName().equals(TencentWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
//					"qqweibo_share_fail_sharesdk");
//		}
	}

	@Override
	public void onShare(SharedPlatfrom which) {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_share_maijiaxiu");
		// 好有，朋友圈。一键，复制连接
		switch (which) {
		case WXFRIEND:
			// 好友
			// 显示启动分享文字
			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
					"click_maijiaxiu_share_friend");

			if (data.size() == 0) {
				Toaster.s(getActivity(), "亲，还没有发布买家秀，先发布买家秀吧");
				break;
			}
			handler.sendEmptyMessage(1);
			BuyerShowBean bsbFriend = data.get(0);
			friendShop(bsbFriend);
			break;
		case WXMOMENTS:
			// 朋友圈
			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
					"click_maijiaxiu_share_moments");
			if (data.size() == 0) {
				Toaster.s(getActivity(), "亲，还没有发布买家秀，先发布买家秀吧");
				break;
			}
			handler.sendEmptyMessage(1);
			BuyerShowBean bsb = data.get(0);
			momentsShop(bsb);
			break;
		case ONEKEY:
			// 一键分享社交平台
			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
					"click_maijiaxiu_onkey");
			if (data.size() == 0) {
				Toaster.s(getActivity(), "亲，还没有发布买家秀，先发布买家秀吧");
				break;
			}
			BuyerShowBean shareBsb = data.get(0);
			WxShopApplication.shareBean = getShareBean(shareBsb);
			startActivity(new Intent(getActivity(), ShareActivity.class));
			break;
		case COPY:
			// 复制连接
			MobAgentTools.OnEventMobOnDiffUser(getActivity(),
					"click_maijiaxiu_share_copy");
			String copyStr = "又有新的买家秀啦！店主再夸也白搭，买家说好，才是真的好~ "
					+ getMaijiaxiuUrl();
			Toaster.l(getActivity(), "复制成功");
			Utils.saveClipBoard(getActivity(), copyStr);
			break;

		default:
			break;
		}
	}
	
	@Override
	public String getShareFromName() {
		return "买家秀";
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
