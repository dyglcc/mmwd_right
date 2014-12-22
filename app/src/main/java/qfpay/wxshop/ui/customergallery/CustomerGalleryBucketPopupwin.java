package qfpay.wxshop.ui.customergallery;

import java.io.File;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.utils.QFCommonUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.PopupWindowCompat;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.customergallery_bucket)
public class CustomerGalleryBucketPopupwin extends LinearLayout {
	private static PopupWindowCompat win = null;
	
	@SuppressWarnings("deprecation") public static void showPopupwin(View anchor, CustomerGalleryGridFragment fragment) {
		if (win == null) {
			win = new PopupWindowCompat();
			win.setHeight(LayoutParams.WRAP_CONTENT);
			win.setWidth(LayoutParams.FILL_PARENT);
			win.setFocusable(true);
			win.setOutsideTouchable(true);
			win.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		}
		if (win.isShowing()) {
			win.dismiss();
		} else {
			win.setContentView(CustomerGalleryBucketPopupwin_.build(anchor.getContext()).setData(fragment));
			win.showAsDropDown(anchor, 0, - QFCommonUtils.dip2px(anchor.getContext(), 260 + 50));
		}
	}
	
	public static void dismissPopupwin() {
		if (win != null && win.isShowing()) {
			win.dismiss();
		}
	}

	@ViewById ListView listView;
	@Bean     CustomerGalleryHelper dataHelper;
	private CustomerGalleryGridFragment mFragment;
	
	public CustomerGalleryBucketPopupwin(Context context) {
		super(context);
	}

	public CustomerGalleryBucketPopupwin setData(CustomerGalleryGridFragment fragment) {
		this.mFragment = fragment;
		listView.setAdapter(new BucketsAdapter());
		return this;
	}
	
	@ItemClick void listView(int position) {
		mFragment.onBucketChoiced(dataHelper.getAllBuckets().get(position));
		dismissPopupwin();
	}
	
	@SuppressLint("InflateParams") class BucketsAdapter extends BaseAdapter {
		@Override public int getCount() {
			return dataHelper.getAllBuckets().size();
		}

		@Override public GalleryBucketWrapper getItem(int position) {
			return dataHelper.getAllBuckets().get(position);
		}

		@Override public long getItemId(int position) {
			return position;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.customergallery_bucket_item, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.tv_title);
			tv.setText(getItem(position).getName());
			RoundedImageView iv = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
			Picasso.with(getContext()).load(new File(getItem(position).getDefaultIconPath())).fit().into(iv);
			return convertView;
		}
	}
}
