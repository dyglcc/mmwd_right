package qfpay.wxshop.ui.common.actionbar;

import org.androidannotations.annotations.EBean;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.*;
import qfpay.wxshop.ui.main.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionProvider;

@EBean
public class NoticeActionProvider extends ActionProvider implements
		OnClickListener {
	Context context;
	TextView tv_unread;
	private Handler handler;

	public NoticeActionProvider(Context context) {
		super(context);
		this.context = context;
	}

	private ImageView iv;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateActionView() {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.common_menuitem_notices, null);
		rootView.setOnClickListener(this);
		tv_unread = (TextView) rootView.findViewById(R.id.tv_unread);

		iv = (ImageView) rootView.findViewById(R.id.iv);
		
		return rootView;
	}

	AnimationDrawable animationStart, animationClose;

	@Override
	public void onClick(View v) {
		String string = tv_unread.getText().toString();
		if (string != null && !string.equals("0") && !string.equals("")) {
			// startAct();
			closeAnim();
		} else {
			startAct();
		}
	}

	private void startAct() {
        WxShopApplication.app.main = (MainActivity) (context);
		NoticeCenterActivity_.intent(context).start();
	}

	public void updateUnread(int unread) {
		// test code
		if (unread == 0) {
			tv_unread.setVisibility(View.INVISIBLE);
		} else {
			// 执行动画
			tv_unread.setVisibility(View.VISIBLE);
			tv_unread.setText("" + unread);
			iv.setImageResource(R.anim.anim_notice);
			animationStart = (AnimationDrawable) iv.getDrawable();
			animationStart.start();
			if(handler!=null){
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {

						iv.setImageResource(R.drawable.btn_notify_23);
					}
				}, 2300);
			}
		}
	}

	public void closeAnim() {

		iv.setImageDrawable(context.getResources().getDrawable(
				R.anim.anim_notice_close));
		animationClose = (AnimationDrawable) iv.getDrawable();
		animationClose.start();
		tv_unread.setVisibility(View.INVISIBLE);
		if(handler!=null){
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {

					startAct();
				}
			}, 1200);
		}
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}

}
