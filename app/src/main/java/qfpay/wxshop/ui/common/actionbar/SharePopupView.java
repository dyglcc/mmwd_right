package qfpay.wxshop.ui.common.actionbar;

import java.util.Arrays;
import java.util.List;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.utils.QFCommonUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

@EViewGroup(R.layout.common_popupwin_share)
public class SharePopupView extends FrameLayout {
	private static PopupWindowCompat popupwin;
	private static SharedPlatfrom[]  mAllPlatfroms = {SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS
		                                             ,SharedPlatfrom.ONEKEY,   SharedPlatfrom.COPY};
	
	public static void showSharePopupwin(View anchor, OnShareLinstener linstener) {
		showSharePopupwin(anchor, linstener, Arrays.asList(mAllPlatfroms));
	}
	
	public static void showSharePopupwin(View anchor, OnShareLinstener linstener, List<SharedPlatfrom> platfroms) {
		popupwin = new PopupWindowCompat(anchor.getContext());
		popupwin.setOutsideTouchable(true);
		popupwin.setFocusable(true);
		popupwin.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		popupwin.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		popupwin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		final SharePopupView view = SharePopupView_.build(anchor.getContext()).setData(linstener, platfroms);
		popupwin.setContentView(view);
		popupwin.showAsDropDown(anchor, 0, - QFCommonUtils.dip2px(anchor.getContext(), 12));
		YoYo.with(Techniques.SlideInDown).duration(200).playOn(view);
	}
	
	@ViewById LinearLayout     contentList;
	@ViewById TextView         tv_title;
	private   OnShareLinstener linstener;
	
	public SharePopupView(Context context) {
		super(context);
		
	}
	
	public SharePopupView setData(OnShareLinstener linstener, List<SharedPlatfrom> platfroms) {
		this.linstener = linstener;
		for (SharedPlatfrom sharedPlatfrom : platfroms) {
			switch (sharedPlatfrom) {
			case WXFRIEND:
				addView(getResources().getString(R.string.actionbar_share_wxfirend),  SharedPlatfrom.WXFRIEND);
				break;
			case WXMOMENTS:
				addView(getResources().getString(R.string.actionbar_share_wxmoments), SharedPlatfrom.WXMOMENTS);
				break;
			case ONEKEY:
				addView(getResources().getString(R.string.actionbar_share_onekey),    SharedPlatfrom.ONEKEY);
				break;
			case COPY:
				addView(getResources().getString(R.string.actionbar_share_copy),      SharedPlatfrom.COPY);
				break;
			default:
				break;
			}
		}
		return this;
	}
	
	private void addView(String title, final SharedPlatfrom which) {
		TitleIconView titleview = new TitleIconView(getContext());
		titleview.setData(title);
		titleview.setTextColor(Color.WHITE);
		switch (which) {
		case WXFRIEND:
			titleview.setIcon(R.drawable.actionbar_share_wxfriend);
			break;
		case WXMOMENTS:
			titleview.setIcon(R.drawable.actionbar_share_wxmoments);
			break;
		case ONEKEY:
			titleview.setIcon(R.drawable.actionbar_share_onkey);
			break;
		case COPY:
			titleview.setIcon(R.drawable.actionbar_share_copy);
			break;

		default:
			break;
		}
		tv_title.setText(String.format(getContext().getString(R.string.share_popup_title), linstener.getShareFromName()));
		titleview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linstener.onShare(which);
				if (popupwin != null && popupwin.isShowing()) {
					popupwin.dismiss();
				}
			}
		});
		contentList.addView(titleview);
	}
}
