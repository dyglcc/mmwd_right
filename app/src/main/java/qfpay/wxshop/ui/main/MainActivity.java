package qfpay.wxshop.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import cn.sharesdk.framework.ShareSDK;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.nineoldandroids.animation.ObjectAnimator;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.sharedpreferences.Pref;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.InputShopNameActivity;
import qfpay.wxshop.activity.SSNPublishActivity_;
import qfpay.wxshop.config.update.UpdateManager;
import qfpay.wxshop.data.beans.LabelBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.data.netImpl.LabelGetNetImpl;
import qfpay.wxshop.data.netImpl.NoticeUnReadNetImpl;
import qfpay.wxshop.getui.RestartReceiver;
import qfpay.wxshop.getui.StartAlarmService;
import qfpay.wxshop.listener.MaijiaxiuUploadListener;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.BusinessCommunity.AllTopicListActivity_;
import qfpay.wxshop.ui.BusinessCommunity.BusinessCommunityDataController;
import qfpay.wxshop.ui.BusinessCommunity.MyDynamicListFragment;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseActivity_;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseNetProcesser;
import qfpay.wxshop.ui.common.actionbar.InfoMenuProvider;
import qfpay.wxshop.ui.common.actionbar.NoticeActionProvider;
import qfpay.wxshop.ui.common.actionbar.ShareActionProvider;
import qfpay.wxshop.ui.main.fragment.*;
import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController;
import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController.WrapperType;
import qfpay.wxshop.ui.main.fragmentcontroller.StartupProcessor;
import qfpay.wxshop.ui.view.BadgeView;
import qfpay.wxshop.ui.view.popupview.*;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import com.networkbench.agent.impl.NBSAppAgent;
import java.util.HashMap;
import java.util.List;

@EActivity(R.layout.main_myshop)
@SuppressLint({ "InflateParams", "HandlerLeak" })
public class MainActivity extends BaseActivity {
	public static final String SP_NAME_MANAGE = "config";
	public static final String SP_SHOP_NEW = "shop_new";

	public static final String GUIDE_RELEASE = "release";
	public static final String GUIDE_PREVIEW = "preview";
	public static final String GUIDE_SHARE = "share";
	public static final String GUIDE_COMPLETE = "complete";

	private static final int GO2ACTIVITY = 100;
	public static final int REQUEST_GUIDE = 109;
	public static final int Rx = REQUEST_GUIDE + 1;
	public static final int RY = Rx + 1;
	public static boolean running = false;
	@ViewById
	View frame_content;
	@ViewById
	ImageView iv_shop, iv_order, iv_promotion, iv_community, iv_add,
			iv_indicater;
    @ViewById
    public RelativeLayout main_bottom_rl,layout_parent;
	@Bean
	MainAddAniUtils aniUtils;
	@Bean
	BuyersShowReleaseNetProcesser buyersShowProcesser;
	@Bean
	InfoMenuProvider mInfoProvider;
	@Bean
	StartupProcessor mStartupProcessor;

	NoticeActionProvider noticeProvider;
	@Pref
	AppStateSharePreferences_ statePref;

	private OnShareLinstener onShareClickLinstener;
	private boolean isShowSharepopup = false;
	private boolean isShowShareButton = true;
	private Handler handler;
	private boolean initShare;
    private BadgeView badgeView;//商户圈选项右上角有更新提示点
    @Bean
    BusinessCommunityDataController businessCommunityDataController;
	private void changeTab(MainTab tab, View view) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		tab.showFragment(ft, this);
		ft.commitAllowingStateLoss();

		if (tab.getFragment() instanceof OnShareLinstener) {
			onShareClickLinstener = (OnShareLinstener) tab.getFragment();
			// 内层的Fragment有可能会有ViewPager,需要通过ViewPager里面的子页面来控制
			if (onShareClickLinstener.getShareFromName() == null
					|| "".equals(onShareClickLinstener.getShareFromName())) {
				isShowShareButton = false;
			} else {
				isShowShareButton = true;
			}
		} else {
			isShowShareButton = false;
		}
		supportInvalidateOptionsMenu();

		if (view != null) {
			changeBgAndAnimation(view);
		}
	}

	public void showShareButton(OnShareLinstener onShareLinstener) {
		this.onShareClickLinstener = onShareLinstener;
		isShowShareButton = true;
		supportInvalidateOptionsMenu();
	}

	public void hideShareButton() {
		isShowShareButton = false;
		supportInvalidateOptionsMenu();
	}

	private void changeBgAndAnimation(View iv) {
		startTabIndicaterMove(iv);
		setBg(iv);
	}

	public void startTabIndicaterMove(View iv) {
		int[] location = new int[2];
		iv_indicater.getLocationOnScreen(location);
		int oldx = location[0];
		iv.getLocationOnScreen(location);
		int newx = location[0] + iv.getWidth() / 2 - iv_indicater.getWidth()
				/ 2;
		ObjectAnimator.ofFloat(iv_indicater, "x", oldx, newx).setDuration(200)
				.start();
	}

	protected void setBg(View iv) {
		switch (iv.getId()) {
		case R.id.iv_shop:
			iv_shop.setImageResource(R.drawable.tab_icon_xd_selected);
			iv_order.setImageResource(R.drawable.tab_icon_hy);
			iv_promotion.setImageResource(R.drawable.tab_icon_tg);
			iv_community.setImageResource(R.drawable.tab_icon_shq);
			break;
		case R.id.iv_order:
			iv_shop.setImageResource(R.drawable.tab_icon_xd);
			iv_order.setImageResource(R.drawable.tab_icon_hy_selected);
			iv_promotion.setImageResource(R.drawable.tab_icon_tg);
			iv_community.setImageResource(R.drawable.tab_icon_shq);
			break;
		case R.id.iv_promotion:
			iv_shop.setImageResource(R.drawable.tab_icon_xd);
			iv_order.setImageResource(R.drawable.tab_icon_hy);
			iv_promotion.setImageResource(R.drawable.tab_icon_tg_selected);
			iv_community.setImageResource(R.drawable.tab_icon_shq);
			break;
		case R.id.iv_community:
			iv_shop.setImageResource(R.drawable.tab_icon_xd);
			iv_order.setImageResource(R.drawable.tab_icon_hy);
			iv_promotion.setImageResource(R.drawable.tab_icon_tg);
			iv_community.setImageResource(R.drawable.tab_icon_shq_selected);
			break;
		}
	}

	/**
	 * 页面底部中间加号的点击事件,如果未出现菜单则出现菜单,如果出现菜单则关闭菜单
	 */
	@Click
	public void iv_add_click() {
		OnDismissListener dismissListener = new OnDismissListener() {
			@Override
			public void onDismiss() {
				iv_add.setImageResource(R.drawable.ani_add_open_06);
				iv_add.setVisibility(View.VISIBLE);
				aniUtils.setImageView(iv_add);
				aniUtils.close();
			}
		};

		if (AddedPopupView.toggle(iv_add, dismissListener)) {
			aniUtils.open();
			iv_add.setVisibility(View.INVISIBLE);
		}

		MobAgentTools.OnEventMobOnDiffUser(this, "click_add");
	}

	public void onAddCommodity() {
		changeTab(MainTab.SHOP, iv_shop);
		// Toaster.l(MainActivity.this,"change commodity");
		((ShopFragment) MainTab.SHOP.getFragment()).changePager(0);
		AddedPopupView.close();
	}

	boolean isAddBuyers = false;

	public void onAddBuyersShow() {
		changeTab(MainTab.TUI_GUANG, iv_promotion);
		AddedPopupView.close();
		try {
			buyersShowProcesser.setMaijiaxiuLinstener(
					(MaijiaxiuUploadListener) MainFragmentController.get(
							WrapperType.POPULARIZING).get(1), 0);
			change2MaijiaxiuTab();
			BaseFragment fragment = MainFragmentController.get(
					WrapperType.POPULARIZING).get(1);
			BuyersShowReleaseActivity_.intent(fragment).startForResult(
					MaijiaxiuFragment.ACTION_ADD_MAIJIAXIU);
		} catch (NullPointerException e) {
			e.printStackTrace();
			isAddBuyers = true;
		}
	}

	@UiThread(delay = 300)
	void change2MaijiaxiuTab() {
		PopularizingFragment fragment = (PopularizingFragment) MainTab.TUI_GUANG
				.getFragment();
		fragment.changePager(1);
	}

	public void onBuyersShowFragmentInit(Fragment fragment) {
		// 这样做是因为直接使用从FragmentControlCenter获取到Fragment的引用会为null,所以在MaijiaxiuFragment完成加载以后再启动
		if (isAddBuyers) {
			BuyersShowReleaseActivity_.intent(fragment).startForResult(
					MaijiaxiuFragment.ACTION_ADD_MAIJIAXIU);
		}
		isAddBuyers = false;
	}

	@Click
	void iv_shop(View iv) {
		MobAgentTools.OnEventMobOnDiffUser(this, "click_xiaodian");
		changeTab(MainTab.SHOP, iv);
	}

	@Click
	void iv_order(View iv) {
		MobAgentTools.OnEventMobOnDiffUser(this, "click_Supply_of_goods");
		changeTab(MainTab.HUOYUAN, iv);
	}

	@Click
	void iv_promotion(View iv) {
		MobAgentTools.OnEventMobOnDiffUser(this, "Click_tuiguang");
		QFCommonUtils.collect("promote", this);
		changeTab(MainTab.TUI_GUANG, iv);
	}

	@Click
	void iv_community(View iv) {
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant");
        changeTab(MainTab.BUSINESS_COMMUNITY, iv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_default, menu);

		MenuItem shareItem = menu.findItem(R.id.menu_share);
		final ShareActionProvider shareActionProvider = new ShareActionProvider(
				this, shareItem, onShareClickLinstener);
		shareItem.setActionProvider(shareActionProvider);
		if (isShowSharepopup) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					shareActionProvider.showSharePopupWin();
				}
			}, 200);
			isShowSharepopup = false;
		}

		MenuItem infoItem = menu.findItem(R.id.menu_info);
		mInfoProvider.setItem(infoItem);
		infoItem.setActionProvider(mInfoProvider);

		if (!isShowShareButton) {
			menu.removeItem(R.id.menu_share);
		}
		// // 通知中心
		MenuItem actInfoItem = menu.findItem(R.id.menu_notice_info);
		noticeProvider = new NoticeActionProvider(this);
		noticeProvider.setHandler(handler);
		actInfoItem.setActionProvider(noticeProvider);

		return true;
	}

	@AfterViews
	void init() {
		if (WxShopApplication.dataEngine.getShopName() == null
				|| "".equals(WxShopApplication.dataEngine.getShopName())) {
			startActivity(new Intent(this, InputShopNameActivity.class));
			finish();
			return;
		}

		running = true;

		mStartupProcessor.processLovelyCard();

		handler = new Handler();
		if (WxShopApplication.dataEngine.isFirstInMainActivity()) {
			updateHandler.sendEmptyMessage(GO2ACTIVITY);
			WxShopApplication.dataEngine.setFirstInMainactivity();
		}

		MobAgentTools.OnEventMobOnDiffUser(MainActivity.this, "Index_page");

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (WxShopApplication.UPDATE_APK_URL == null) {
					WxShopApplication.app.checkUpdate(MainActivity.this,
							updateHandler);
				}
			}
		}, 4000);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getLabelFromServer();
			}
		}, 4000);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getNoticeUnreadFromServer();
			}
		}, 2000);

		if (WxShopApplication.MAIN_IS_RUNNING) {
			Intent intentRestart = new Intent(this, RestartReceiver.class);
			sendBroadcast(intentRestart);
			finish();
			return;
		}

		iv_shop.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						iv_shop(iv_shop);
						checkGuideShow();
						iv_shop.getViewTreeObserver().removeOnPreDrawListener(
								this);
						return true;
					}
				});

		aniUtils.setImageView(iv_add);
		aniUtils.start(true);

		Intent intentAlermServiceIntent = new Intent(this,
				StartAlarmService.class);
		startService(intentAlermServiceIntent);

		// 本地通知点击次数
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				statLocalNotifyClick();
			}
		}, 4400);
		WxShopApplication.MAIN_IS_RUNNING = true;

        //商户圈通知角标初始化
        badgeView = new BadgeView(this,iv_community);
        badgeView.setBackgroundResource(R.drawable.icon_reddot2);
        badgeView.setWidth(Utils.dip2px(this, 10));
        badgeView.setHeight(Utils.dip2px(this, 10));
        badgeView.setGravity(Gravity.CENTER);
        badgeView.setBadgeMargin(15,10);
        badgeView.setTextSize(6);
        initLastNoReadNotification();
        getBusinessCommunityAboutMyNotify();
        NBSAppAgent.setLicenseKey("26f23f2f3f8447b6a450174320f25969").withLocationServiceEnabled(true).start(this);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper dataWrapper = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
        if(dataWrapper!=null&&dataWrapper.data.items.size()>0){
            WxShopApplication.dataEngine.setBusinessCommunityAboutMyNotification(dataWrapper);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * 初始话上次未读商户圈消息
     */
    void initLastNoReadNotification(){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper dataWrapper = WxShopApplication.dataEngine.getBusinessCommunityAboutMyNotification();
        if(dataWrapper!=null&&dataWrapper.data.items.size()>0){
            WxShopApplication.dataEngine.setBusinessCommmunityMyNotificationData(dataWrapper);
        }
    }

    /**
     * 获取商户圈我的消息通知
     */
    @Background
    void getBusinessCommunityAboutMyNotify(){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationDataWrapper = businessCommunityDataController.getAboutMyNotification();
        if(businessCommmunityMyNotificationDataWrapper!=null){
            dealMyNotification(businessCommmunityMyNotificationDataWrapper);
        }
        try {
            Thread.currentThread().sleep(30*1000);
            if(!this.isFinishing()){
                getBusinessCommunityAboutMyNotify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *根据返回结果，并设置角标
     * @param newData
     */
    @UiThread
    void dealMyNotification(BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper newData){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper originData = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
        if(originData!=null){
            if(newData.data.tag.equals("1")&&originData.data.tag.equals("0")){
                originData.data.tag = "1";
            }
            if(newData.data.has_new!=null){
                originData.data.has_new = newData.data.has_new;
            }
            if(newData.data.items!=null){
                originData.data.items.addAll(0,newData.data.items);
            }
            BusinessCommunityFragment businessCommunityFragment = (BusinessCommunityFragment)MainTab.BUSINESS_COMMUNITY.getFragment();
            if(originData.data.tag.equals("1")){
                showCommunityNotifycation("1",originData.data.items.size()+"");
                MyDynamicListFragment myDynamicListFragment = (MyDynamicListFragment)MainFragmentController.get(WrapperType.BUSINESS_COMMUNITY).get(0);
                if(myDynamicListFragment!=null){//我的动态列表头部添加消息通知
                    myDynamicListFragment.addNewMyNotificationLayout(originData.data.items.size()+"");
                }
                if(businessCommunityFragment!=null){//我的动态tab添加角标通知
                    businessCommunityFragment.showCommunityNotification("1",originData.data.items.size()+"");
                }
            }else if(originData.data.tag.equals("0")&&originData.data.has_new.equals("1")){
                showCommunityNotifycation("0","0");
                if(businessCommunityFragment!=null){//我的动态tab添加角标通知
                    businessCommunityFragment.showCommunityNotification("0","0");
                }
            }else{
                hideCommunityNotifycation();
                if(businessCommunityFragment!=null){//我的动态tab隐藏角标通知
                    businessCommunityFragment.hideCommunityNotification();
                }
            }
        }
    }

    private void statLocalNotifyClick() {
		Intent intent = getIntent();
		String day = intent.getStringExtra("daysWhenClick");
		if (day != null && !day.equals("")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("daysWhenClick", day);
			MobAgentTools.OnEventMobOnDiffUser(MainActivity.this,
					"local_notify_days", map);
		}
	}

	@Override
	public void onRestart() {
		checkGuideShow();
		supportInvalidateOptionsMenu();
		super.onRestart();
	}

	public void checkGuideShow() {
		if (!statePref.isNewUser().get() || !statePref.isShowGuide().get()) {
			return;
		}
		String pointer = statePref.guidePointer().get();
		if (GUIDE_RELEASE.equals(pointer)) {
			AddGuideView.showAddGuide(MainActivity.this, iv_add);
			statePref.isShowGuide().put(false);
		} else if (GUIDE_PREVIEW.equals(pointer)) {
			PreviewGuideView.showAddGuide(MainActivity.this, iv_add);
			statePref.isShowGuide().put(true);
		} else if (GUIDE_SHARE.equals(pointer)) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ShareGuideView.showAddGuide(MainActivity.this, iv_add);
				}
			}, 5000);
			statePref.isShowGuide().put(false);
		}
	}

	public void onPreviewGuide() {
		((ShopFragment) MainTab.SHOP.getFragment()).changePager(1);
		checkGuideShow();
	}

	public void onShareGuide(View anchor) {
		isShowSharepopup = true;
		iv_shop(iv_shop);
		((ShopFragment) MainTab.SHOP.getFragment()).changePager(0);
	}

	@OnActivityResult(REQUEST_GUIDE)
	void onGuideResult() {
		AddedShareView.showDialog(this);
	}

	String cacheKey = null;

	private MainHandler mainHandler = new MainHandler(MainActivity.this) {
		@Override
		public void onSuccess(Bundle bundle) {
			if (bundle != null
					&& bundle.getString(ConstValue.CACHE_KEY) != null) {
				cacheKey = bundle.getString(ConstValue.CACHE_KEY);
				HashMap<String, Object> list = CacheData.getInstance()
						.getData(cacheKey).get(0);
				@SuppressWarnings("unchecked")
				List<LabelBean> tradeData = (List<LabelBean>) list
						.get("orderList");

				if (tradeData == null) {
					return;
				}
				Gson gson = new Gson();
				// 保存新的labels
				WxShopApplication.dataEngine.setLabels(gson.toJson(tradeData));
				// 设置默认的选择
				String his = WxShopApplication.dataEngine.getHistoryLabels();
				if (his.equals("") && tradeData.size() >= 5) {
					LabelBean lb0 = tradeData.get(0);
					LabelBean lb1 = tradeData.get(1);
					LabelBean lb2 = tradeData.get(2);
					LabelBean lb3 = tradeData.get(3);
					LabelBean lb4 = tradeData.get(4);
					String defaultHis = lb0.getId() + "_" + lb0.getName() + ","
							+ lb1.getId() + "_" + lb1.getName() + ","
							+ lb2.getId() + "_" + lb2.getName() + ","
							+ lb3.getId() + "_" + lb3.getName() + ","
							+ lb4.getId() + "_" + lb4.getName();
					WxShopApplication.dataEngine.setHistoryLabels(defaultHis);
				}
				tradeData.clear();
				tradeData = null;
			}
		}

		@Override
		public void onFailed(Bundle bundle) {

		}
	};
	private MainHandler mainGetNotice = new MainHandler(MainActivity.this) {
		@Override
		public void onSuccess(Bundle bundle) {
			if (bundle != null) {
				int unread = bundle.getInt("unread");
				// Test code
				noticeProvider.updateUnread(unread);
			}
		}

		@Override
		public void onFailed(Bundle bundle) {

		}
	};

	protected void getLabelFromServer() {
		AbstractNet net = new LabelGetNetImpl(MainActivity.this);
		net.request(null, mainHandler);
	}

	protected void getNoticeUnreadFromServer() {
		AbstractNet net = new NoticeUnReadNetImpl(MainActivity.this);
		net.request(null, mainGetNotice);
	}

	private UpdateManager mUpdateManager;

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MoreActivity.SHOW_UPDATE_DIALOG:
				Bundle data = msg.getData();
				String updateUrl = data.getString("updateUrl");
				String updateComment = data.getString("updateComment");
				String force = data.getString("needForce");
				mUpdateManager = new UpdateManager(MainActivity.this,
						updateUrl, updateComment);
				mUpdateManager.checkUpdateInfo(force);
				WxShopApplication.dataEngine.setShownUpdate(true);
				break;
			case MoreActivity.SHOW_UPDATE_DIALOG_AlEADY_NEWSEST:
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle(getResources()
						.getString(R.string.check_update));
				builder.setMessage(getResources().getString(
						R.string.aleady_newest));
				builder.setNegativeButton(getString(R.string.OK),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
				break;
			case Rx:
				Toaster.l(MainActivity.this, "Home key press");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (initShare) {
			ShareSDK.stopSDK(this);
		}
		WxShopApplication.MAIN_IS_RUNNING = false;
		MainTab.clear();
		if (UtilsWeixinShare.map != null) {
			UtilsWeixinShare.map = null;
		}
        //如果程序退出时还有未读的商户圈动态消息，则存储到本地
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper dataWrapper = WxShopApplication.dataEngine.getBusinessCommmunityMyNotificationData();
        if(dataWrapper!=null&&dataWrapper.data.items.size()>0){
            WxShopApplication.dataEngine.setBusinessCommunityAboutMyNotification(dataWrapper);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		int result = intent.getIntExtra("result", -1);
		if (result == MaijiaxiuFragment_.ACTION_ADD_SSN
				|| result == MaijiaxiuFragment_.ACTION_EDIT_SSN) {
			MainFragmentController.get(WrapperType.POPULARIZING).get(0)
					.onActivityResult(requestCode, resultCode, intent);
		} else if (result == MaijiaxiuFragment.ACTION_EDIT_MAIJIAXIU
				|| result == MaijiaxiuFragment.ACTION_ADD_MAIJIAXIU) {
			MainFragmentController.get(WrapperType.POPULARIZING).get(1)
					.onActivityResult(requestCode, resultCode, intent);
		} else if (result == MaijiaxiuFragment.ACTION_HUOYUAN_ADD) {
			HuoYuanFragmentsWrapper.getFragment(1, this).onActivityResult(
					requestCode, resultCode, intent);
		}else if (result == OrderFragment_.REFRESH) {
			ShopFragmentsWrapper.getFragment(3, this).onActivityResult(
					requestCode, resultCode, intent);
		}else if(result == MaijiaxiuFragment.ACTION_MYDYNAMIC_EDIT_NOTE){
            MainFragmentController.get(WrapperType.BUSINESS_COMMUNITY).get(0)
                    .onActivityResult(requestCode, resultCode, intent);
        }else if(result == MaijiaxiuFragment.ACTION_PUBLISH_NOTE){
            MainFragmentController.get(WrapperType.BUSINESS_COMMUNITY).get(0)
                    .onActivityResult(requestCode, resultCode, intent);
        }
	}

	public void onAddSsuinian() {
		changeTab(MainTab.TUI_GUANG, iv_promotion);
		AddedPopupView.close();
		startSuisuiNainPublish();
	}
	public void showOneKeybehalf() {
		changeTab(MainTab.SHOP, iv_shop);
        ShopFragment shopFragment = (ShopFragment) MainTab.SHOP.getFragment();
        if(shopFragment!=null){
            shopFragment.changePager(0);
        }
        GoodFragment goodFragment = (GoodFragment) ShopFragmentsWrapper.getFragment(0, MainActivity.this);
        if(goodFragment!=null){
            goodFragment.changePager(1);
        }
	}

    public void onAddNote(){
        MobAgentTools.OnEventMobOnDiffUser(this, "click_merchant_post");
        changeTab(MainTab.BUSINESS_COMMUNITY,iv_community);
        AddedPopupView.close();
        startPublishNote();
    }

    void startPublishNote(){
        AllTopicListActivity_.intent(MainActivity.this).startForResult(MaijiaxiuFragment.ACTION_PUBLISH_NOTE);
    }

	@UiThread(delay = 200)
	void startSuisuiNainPublish() {
		PopularizingFragment fragment = (PopularizingFragment) MainTab.TUI_GUANG
				.getFragment();
		fragment.changePager(0);
		SSNPublishActivity_.intent(
				MainFragmentController.get(WrapperType.POPULARIZING).get(0))
				.startForResult(MaijiaxiuFragment_.ACTION_ADD_SSN);
	}



    /**
     * 商户圈 根据tag值显示角标 0 显示红点 1 显示数字加红点
     * @param tag
     * @param count
     */
    @UiThread
    public void showCommunityNotifycation(String tag,String count){
        if(tag.equals("0")){
            badgeView.show();
        }
        if(tag.equals("1")){
            badgeView.setText(count);
            badgeView.show();
        }
    }

    /**
     * 商户圈 隐藏角标
     */
    public void hideCommunityNotifycation(){
        badgeView.setText("");
        badgeView.hide();
    }
}
