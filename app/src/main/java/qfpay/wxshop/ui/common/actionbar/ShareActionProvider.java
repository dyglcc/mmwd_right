package qfpay.wxshop.ui.common.actionbar;

import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.MenuItem;

public class ShareActionProvider extends ActionProvider implements OnClickListener {
	Context              context;
	MenuItem             item;
	OnShareLinstener     linstener;
	List<SharedPlatfrom> platFroms;
	
	public ShareActionProvider(Context context, MenuItem item, OnShareLinstener linstener) {
		super(context);
		this.context = context;
		this.item = item;
		this.linstener = linstener;
	}
	
	public ShareActionProvider(Context context, MenuItem item, OnShareLinstener linstener, List<SharedPlatfrom> platFroms) {
		super(context);
		this.context = context;
		this.item = item;
		this.linstener = linstener;
		this.platFroms = platFroms;
	}

	@SuppressLint("InflateParams") @Override
	public View onCreateActionView() {
		View rootView = LayoutInflater.from(context).inflate(R.layout.common_menuitem_share, null);
		rootView.setOnClickListener(this);
		
		ImageView iv = (ImageView) rootView.findViewById(R.id.iv);
		iv.setImageDrawable(item.getIcon());
		return rootView;
	}

	@Override
	public void onClick(View v) {
		showSharePopupWin();
	}
	
	public void showSharePopupWin() {
		if (platFroms == null) {
			SharePopupView.showSharePopupwin(item.getActionView(), linstener);
		} else {
			SharePopupView.showSharePopupwin(item.getActionView(), linstener, platFroms);
		}
	}
}
