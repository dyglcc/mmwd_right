package qfpay.wxshop.ui.view.popupview;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.commodity.detailmanager.ItemDetailManagerActivity_;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.main.MainAddAniUtils;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

@EViewGroup(R.layout.main_popup_add)
public class AddedPopupView extends RelativeLayout {
	private static PopupWindowCompat win = null;
	
	/**
	 * 返回值代表当前(方法调用以后)的开启或者关闭状态
	 */
	public static boolean toggle(View anchor, OnDismissListener dismissLinstener) {
		if (win == null) {
			show(anchor, dismissLinstener);
			return true;
		}
		if (win.isShowing()) {
			close();
			return false;
		} else {
			show(anchor, dismissLinstener);
			return true;
		}
	}
	
	@SuppressWarnings("deprecation") public static PopupWindowCompat show(View anchor, OnDismissListener dismissLinstener) {
		if (win == null) {
			win = new PopupWindowCompat(anchor.getContext());
		}
		if (win.isShowing()) {
			return win;
		}
		win.setFocusable(true);
		win.setOutsideTouchable(true);
		win.setHeight(LayoutParams.FILL_PARENT);
		win.setWidth(LayoutParams.FILL_PARENT);
		win.setAnimationStyle(R.style.PopupAnimation_Null);
		win.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		win.setContentView(AddedPopupView_.build(anchor.getContext()).setData((MainActivity) anchor.getContext()));
		win.showAsDropDown(anchor, 0, -QFCommonUtils.getScreenHeight((Activity) anchor.getContext()));
		if (dismissLinstener != null) {
			win.setOnDismissListener(dismissLinstener);
		}
		return win;
	}
	
	public static PopupWindowCompat close() {
		if (win == null || !win.isShowing()) {
			return win;
		}
		win.dismiss();
		return win;
	}
	
	public static final int YOFFSET_DP = 150;

	@ViewById LinearLayout ll_content;
	@ViewById ImageView iv_add;
	@Bean MainAddAniUtils aniUtils;
	@Pref AppStateSharePreferences_ statePref;
	MainActivity activity;
	
	public AddedPopupView(Context context) {
		super(context);
	}
	
	public AddedPopupView setData(final MainActivity activity) {
		this.activity = activity;

		aniUtils.setImageView(iv_add);
		getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override public boolean onPreDraw() {
				ValueAnimator menuAni = ValueAnimator.ofFloat(0, QFCommonUtils.getScreenHeight(activity));
				menuAni.addUpdateListener(new AnimatorUpdateListener() {
					@Override public void onAnimationUpdate(ValueAnimator arg0) {
						changeMenuHeight((Float) arg0.getAnimatedValue());
					}
				});
				menuAni.setDuration(200);
				menuAni.start();
				YoYo.with(Techniques.Bounce).duration(200).playOn(iv_add);
				
				getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});
		return this;
	}
	
	private void changeMenuHeight(float height) {
		android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) ll_content.getLayoutParams();
		lp.height = (int) height;
		ll_content.requestLayout();
	}
	
	@Click void ll_content() {
		close();
	}
	
	@Click void iv_add() {
		MobAgentTools.OnEventMobOnDiffUser(getContext(), "release");
		close();
	}
	
	@Click void iv_addbuyersshow() {
		MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_add_maijiaxiu");
		activity.onAddBuyersShow();
	}
	
	@Click void iv_addcommodity() {
		ItemDetailManagerActivity_.intent(getContext()).start();
		activity.onAddCommodity();
	}
	
	@Click void iv_addsuisuinian() {
		activity.onAddSsuinian();
		MobAgentTools.OnEventMobOnDiffUser(getContext(), "Click_HybridText_Create");
	}
}
