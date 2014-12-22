package qfpay.wxshop.ui.view.popupview;

import java.io.IOException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.main_popup_add_share)
public class AddedShareView extends RelativeLayout {
	private static Dialog dialog = null;
	
	public static void showDialog(final Context context) {
		dialog = new Dialog(context, R.style.MyDialog);
		dialog.setContentView(AddedShareView_.build(context));
		dialog.setCancelable(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				((MainActivity) context).checkGuideShow();
			}
		});
		dialog.show();
	}
	
	public AddedShareView(Context context) {
		super(context);
	}
	
	@Click void rl_share() {
		MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_Flash_share_yes");
		shareApp();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	private void shareApp() {
		Toaster.s(getContext(), "请稍等");
		final WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = "http://www.qianmiaomiao.com/more/";
		wdb.description = "";
		wdb.title = "我下载了喵喵微店，简直好用到没有朋友！赚钱利器么么哒！";
		wdb.scope = true;
		processImgAndShare(wdb);
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE)
	void processImgAndShare(WeiXinDataBean wdb) {
		try {
			wdb.thumbData = BitmapUtil.bmpToByteArray(Picasso.with(getContext()).load(R.drawable.icon).resize(100, 100).get(), true);
			UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wcfriend, getContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Click void rl_later() {
		MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_Flash_share_no");
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
