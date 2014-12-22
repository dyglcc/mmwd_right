package qfpay.wxshop.ui.view;

import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.R;
import qfpay.wxshop.activity.BankPickerActivity;
import qfpay.wxshop.data.beans.BankBean;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class BankPickerItem extends LinearLayout implements OnClickListener {
	ImageView iv_icon;
	TextView tv_bankname;
	
	BankPickerActivity activity;
	BankBean bean;
	
	public BankPickerItem(Context context, BankBean bean) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.bankpicker_item, this);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_bankname = (TextView) findViewById(R.id.tv_bankname);
		this.activity = (BankPickerActivity) context;
		this.bean = bean;
		setImg(bean.getIconFileName());
		tv_bankname.setText(bean.getbName());
		findViewById(R.id.ll_content).setOnClickListener(this);
	}
	
	void setImg(String fileName) {
		try {
			iv_icon.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(fileName)));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (this.activity != null && this.bean != null) {
			this.activity.onBankPicker(bean);
		}
	}
}
