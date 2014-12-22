package qfpay.wxshop.ui.selectpic;

import qfpay.wxshop.ui.selectpic.BitmapCache.ImageCallback;
import qfpay.wxshop.utils.MobAgentTools;

import java.lang.ref.SoftReference;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	final String TAG = getClass().getSimpleName();
	Activity act;
	List<ImageItem> dataList;
	// Map<String, String> map = new
	BitmapCache cache;
	private Handler mHandler;
	// private int selectTotal = 0;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				SoftReference<Bitmap> refBitmap = new SoftReference<Bitmap>(
						bitmap);
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(refBitmap.get());
				}
				bitmap = null;
			}
		}
	};

	public static interface TextCallback {
		public void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(Activity act, List<ImageItem> list,
			Handler mHandler, BitmapCache cache) {
		this.act = act;
		dataList = list;
		this.cache = cache;
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.iv.setTag(item.imagePath);
		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
				callback);
		if (item.isSelected) {
			holder.selected.setImageDrawable(act.getResources().getDrawable(
					R.drawable.icon_data_select));
			holder.text.setBackgroundDrawable(act.getResources().getDrawable(
					R.drawable.bgd_relatly_line));
		} else {
			holder.selected.setImageDrawable(null);
			holder.text.setBackgroundColor(act.getResources().getColor(
					android.R.color.transparent));
		}
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (WxShopApplication.paths.size() < 9) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.selected
								.setImageResource(R.drawable.icon_data_select);
						holder.text
								.setBackgroundResource(R.drawable.bgd_relatly_line);
						// selectTotal++;
						WxShopApplication.paths.add(item);
						if (textcallback != null)
							textcallback.onListen(WxShopApplication.paths
									.size());
						// 通知horiScrollView 更新界面
						AddImageItem(item);

					} else if (!item.isSelected) {
						holder.selected.setImageResource(-1);
						holder.text.setBackgroundColor(0x00000000);
						WxShopApplication.paths.remove(item);
						// selectTotal--;
						if (textcallback != null)
							textcallback.onListen(WxShopApplication.paths
									.size());
						// 通知horiScrollView 更新界面
						removeItem(item);
					}
				} else if (WxShopApplication.paths.size() >= 9) {
					// } else if ((Bimp.drr.size() + selectTotal) >= 9) {
					if (item.isSelected == true) {
						item.isSelected = !item.isSelected;
						holder.selected.setImageResource(-1);
						// selectTotal--;
						WxShopApplication.paths.remove(item);
						// 通知horiScrollView 更新界面 remove
						removeItem(item);
						if (textcallback != null)
							textcallback.onListen(WxShopApplication.paths
									.size());
					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}
			}

		});

		return convertView;
	}

	protected void removeItem(ImageItem item) {
		Bundle data = new Bundle();
		Message msg = mHandler.obtainMessage();
		msg.what = ImageGridActivity.REFRESH_HORISCROLLVIEW_REMOVE;
		data.putSerializable("item", item);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	protected void AddImageItem(ImageItem item) {
		Bundle data = new Bundle();
		Message msg = mHandler.obtainMessage();
		msg.what = ImageGridActivity.REFRESH_HORISCROLLVIEW_ADD;
		data.putSerializable("item", item);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
