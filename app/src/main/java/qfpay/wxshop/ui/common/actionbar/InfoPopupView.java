package qfpay.wxshop.ui.common.actionbar;

import java.util.Arrays;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.RegBankAccountActivity_;
import qfpay.wxshop.activity.ShowBankInfoActivity;
import qfpay.wxshop.activity.WeixinQmmActivity;
import qfpay.wxshop.activity.menu.MyInComeActivity;
import qfpay.wxshop.activity.menu.WeiXinCollectMoney;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.main.MoreActivity_;
import qfpay.wxshop.ui.presonalinfo.ShopInfoActivity_;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.actionbarsherlock.view.MenuItem;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.common_popupwin_info)
public class InfoPopupView extends FrameLayout {
	private static PopupWindowCompat popupwin;
	
	public static PopupWindowCompat showInfoPopupwin(MenuItem item, View anchor) {
		popupwin = new PopupWindowCompat(anchor.getContext());
		popupwin.setOutsideTouchable(true);
		popupwin.setFocusable(true);
		popupwin.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		popupwin.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		popupwin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupwin.setAnimationStyle(R.style.PopupAnimation_Null);
		final InfoPopupView view = InfoPopupView_.build(anchor.getContext()).setData(popupwin);
		popupwin.setContentView(view);
		
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int hdp = QFCommonUtils.dip2px(anchor.getContext(),5);
		int swidth = dm.widthPixels;
		Rect frame = new Rect();
		anchor.getWindowVisibleDisplayFrame(frame);
		popupwin.showAtLocation(anchor, Gravity.NO_GRAVITY, swidth, frame.top + hdp);
		return popupwin;
	}
	
	@ViewById LinearLayout contentList;
	@ViewById FrameLayout  fl_list;
	@ViewById TextView     tv_title, tv_wx, tv_shopsite;
	@ViewById ImageView    iv_shopbg;
	
	public InfoPopupView(Context context) {
		super(context);
	}
	
	@SuppressLint("NewApi") public InfoPopupView setData(final PopupWindowCompat popupwin) {
		tv_title.setText("" + WxShopApplication.dataEngine.getShopName());
		tv_wx.setText("微信号 : " + WxShopApplication.dataEngine.getContract());
		tv_shopsite.setText(WxShopApplication.app.getDomainMMWDUrl()+"/shop/" + WxShopApplication.dataEngine.getShopId());
		
		addView("微信好友直接收款", InfoClickEnum.WXRECEIVE);
		addView("店铺装修", InfoClickEnum.SHOPSTATISTICS);
		addView("我的收入", InfoClickEnum.MYINCOME);
		addView("收款银行卡", InfoClickEnum.RECEIVEDSCARD);
		addView("微信客服", InfoClickEnum.WXSERVICE);
		addView("喵喵购", InfoClickEnum.MM_BUY);
		addView("更多~", InfoClickEnum.MORE);
		
		YoYo.with(Techniques.SlideInDown).duration(200).playOn(fl_list);
		Picasso.with(getContext()).load(WxShopApplication.dataEngine.getAvatar()).fit().centerCrop().into(iv_shopbg);
		startImageAni();
		return this;
	}
	
	@Click void iv_shopbg() {
		popupwin.dismiss();
	}
	
	@UiThread(delay = 50) void startImageAni() {
		ObjectAnimator sxAni = ObjectAnimator.ofFloat(iv_shopbg, "scaleX", 1, 2.2f);
		ObjectAnimator syAni = ObjectAnimator.ofFloat(iv_shopbg, "scaleY", 1, 2.2f);
		float x = (float) (iv_shopbg.getX() - iv_shopbg.getWidth() * 1.2 / 2);
		float y = (float) (iv_shopbg.getY() + iv_shopbg.getHeight() * 1.2 / 2);
		ObjectAnimator xAni = ObjectAnimator.ofFloat(iv_shopbg, "x", iv_shopbg.getX(), x);
		ObjectAnimator yAni = ObjectAnimator.ofFloat(iv_shopbg, "y", iv_shopbg.getY(), y);
		AnimatorSet imageAni = new AnimatorSet();
		imageAni.playTogether(sxAni, syAni, xAni, yAni);
		imageAni.setDuration(200);
		imageAni.start();
	}
	
	private void addView(String title, final InfoClickEnum clickEnum) {
		TitleIconView titleview = new TitleIconView(getContext());
		titleview.setData(title);
		titleview.setTextColor(Color.WHITE);
		titleview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				switch (clickEnum) {
				case WXRECEIVE:
					intent.setClass(getContext(), WeiXinCollectMoney.class);
					getContext().startActivity(intent);
					break;
				case SHOPSTATISTICS:
					ShopInfoActivity_.intent(getContext()).start();
					break;
				case MYINCOME:
					intent.setClass(getContext(), MyInComeActivity.class);
					getContext().startActivity(intent);
					break;
				case RECEIVEDSCARD:
					if (!WxShopApplication.dataEngine.getApplyCardbind()) {
						intent.setClass(getContext(), RegBankAccountActivity_.class);
						intent.putExtra("class", "mainactivity");
					} else {
						intent.setClass(getContext(), ShowBankInfoActivity.class);
					}
					getContext().startActivity(intent);
					break;
				case WXSERVICE:
					intent.setClass(getContext(), WeixinQmmActivity.class);
					getContext().startActivity(intent);
					break;
				case MM_BUY:
					MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_mmg");
					String url = "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/h5/mmg.html?ga_medium=" + ConstValue.android_mmwdapp_home_;
					CommonWebActivity_.intent(getContext())
							.title("喵喵微店")
							.url(url)
							.platFroms(Arrays.asList(SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS, SharedPlatfrom.COPY))
							.shareTitle("喵喵购")
							.shareName("喵喵购")
							.pointName("click_mmg_share")
							.start();
					break;
				case MORE:
					MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_Individual center_gengduo");
					MoreActivity_.intent(getContext()).start();
					break;
				}
				if (popupwin != null && popupwin.isShowing()) {
					popupwin.dismiss();
				}
			}
		});
		
		contentList.addView(titleview);
	}
	
	private enum InfoClickEnum {
		WXRECEIVE, SHOPSTATISTICS, MYINCOME, RECEIVEDSCARD, WXSERVICE, MM_BUY, MORE
	}
}
