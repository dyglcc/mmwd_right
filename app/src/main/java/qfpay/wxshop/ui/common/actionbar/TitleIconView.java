package qfpay.wxshop.ui.common.actionbar;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.view.listitemview.ItemView;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用于actionbar的popupview,其它地方不适用
 */
public class TitleIconView extends LinearLayout implements ItemView<String> {
	TextView title;
	ImageView icon;

	public TitleIconView(Context context) {
		super(context);
		inflate(context, R.layout.list_item_title, this);
		title = (TextView) findViewById(R.id.title);
		icon = (ImageView) findViewById(R.id.icon);
	}

	@Override
	public void setData(String obj) {
		title.setText(obj);
	}
	
	public void setIcon(int res) {
		icon.setImageResource(res);
		icon.setVisibility(View.VISIBLE);
		
		LayoutParams layoutParams = (LayoutParams) title.getLayoutParams();
		layoutParams.leftMargin = 0;
		title.requestLayout();
	}
	
	public void setTextColor(int color) {
		title.setTextColor(color);
	}
}
