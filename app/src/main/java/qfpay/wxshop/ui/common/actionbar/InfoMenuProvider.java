package qfpay.wxshop.ui.common.actionbar;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.utils.MobAgentTools;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.MenuItem;
import com.makeramen.RoundedImageView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Picasso;

@EBean
public class InfoMenuProvider extends ActionProvider implements OnClickListener {
	@Pref   AppStateSharePreferences_ pref;
	private Context                   context;
	private MenuItem                  item;
	private RoundedImageView          imageView;
	private ObjectAnimator            rotationAni;
	private float                     y;
	private boolean                   isAni = true;
	private boolean                   isAnimationing = false;

	public InfoMenuProvider(Context context) {
		super(context);
		this.context = context;
	}
	
	public void setItem(MenuItem item) {
		this.item = item;
	}

	@SuppressLint("InflateParams") @Override
	public View onCreateActionView() {
		View rootView = LayoutInflater.from(context).inflate(R.layout.common_menuitem_info, null);
		rootView.setOnClickListener(this);
		
		imageView = (RoundedImageView) rootView.findViewById(R.id.iv);
		Picasso.with(context).load(WxShopApplication.dataEngine.getAvatar()).fit().centerCrop().into(imageView);
		this.y = imageView.getY();
		if (pref.isShowInfoHeaderbtnPrompt().get()) {
			if (!isAnimationing) {
				isAnimationing = true;
				startAni();
			}
		}
		return rootView;
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE, serial = "infomenu_ani")
	void startAni() {
		while (isAni) {
			showAni();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@UiThread void showAni() {
		if (rotationAni == null) {
			rotationAni = ObjectAnimator.ofFloat(imageView, "rotation", 0, 20, -20, 20, -20, 20, -20, 20, -20, 20, 0);
		}
		rotationAni.setDuration(800);
		rotationAni.start();
	}

	public void stopAni() {
		if (rotationAni != null && rotationAni.isRunning()) {
			rotationAni.cancel();
		}
		imageView.setY(y);
		imageView.setRotation(0);
		isAni = false;
	}
	
	@Override
	public void onClick(View v) {
		MobAgentTools.OnEventMobOnDiffUser(context, "click_Individual center");
		pref.isShowInfoHeaderbtnPrompt().put(false);
		stopAni();
		hideImage();
		InfoPopupView.showInfoPopupwin(item, item.getActionView()).setOnDismissListener(new OnDismissListener() {
			@Override public void onDismiss() {
				imageView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@UiThread(delay = 70) void hideImage() {
		imageView.setVisibility(View.INVISIBLE);
	}
}
