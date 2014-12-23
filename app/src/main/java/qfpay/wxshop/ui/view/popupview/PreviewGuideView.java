package qfpay.wxshop.ui.view.popupview;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.utils.QFCommonUtils;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

@EViewGroup(R.layout.userguide_preview_layout)
public class PreviewGuideView extends RelativeLayout {
	private static PopupWindowCompat win = null;
	
	@SuppressWarnings("deprecation") public static void showAddGuide(MainActivity activity, View view) {
        if (activity.isFinishing()) {
            return;
        }
        if (win != null && win.isShowing()) {
            return;
        }
		win = new PopupWindowCompat(activity);
		win.setHeight(LayoutParams.FILL_PARENT);
		win.setWidth(LayoutParams.FILL_PARENT);
		win.setFocusable(true);
		win.setOutsideTouchable(true);
		win.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		win.setContentView(PreviewGuideView_.build(activity).setData(activity));
		win.showAsDropDown(view, 0, - QFCommonUtils.getScreenHeight(activity));
	}
	
	@ViewById ImageView iv_preview, star1, star2, star3;
	@Pref AppStateSharePreferences_ statePref;
	
	private MainActivity activity;

	public PreviewGuideView(Context context) {
		super(context);
		
	}

	public PreviewGuideView setData(MainActivity activity) {
		this.activity = activity;
		
		setBlingAni(star1);
		setBlingAni(star2);
		setBlingAni(star3);
		
		return this;
	}
	
	private void setBlingAni(ImageView view) {
		final AnimatorSet aniX = new AnimatorSet();
		aniX.play(ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f)).before(ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1f));
		aniX.setDuration(800);
		aniX.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator arg0) {
				if (win != null && win.isShowing()) {
					aniX.start();
				}
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		aniX.start();
		
		final AnimatorSet aniY = new AnimatorSet();
		aniY.play(ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f)).before(ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1f));
		aniY.setDuration(800);
		aniY.addListener(new AnimatorListener() {
			@Override public void onAnimationStart(Animator arg0) {}
			@Override public void onAnimationRepeat(Animator arg0) {}
			@Override public void onAnimationCancel(Animator arg0) {}
			@Override public void onAnimationEnd(Animator arg0) {
				if (win != null && win.isShowing()) {
					aniY.start();
				}
			}
		});
		aniY.start();
	}
	
	@Click void iv_preview() {
		statePref.guidePointer().put(MainActivity.GUIDE_SHARE);
		statePref.isShowGuide().put(true);
		activity.onPreviewGuide();
		if (win != null && win.isShowing()) {
			win.dismiss();
		}
	}
}
