package qfpay.wxshop.ui.selectpic;

import qfpay.wxshop.ui.selectpic.BitmapCache.ImageCallback;
import qfpay.wxshop.utils.MobAgentTools;

import java.lang.ref.SoftReference;
import java.util.List;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.CacheData;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageBucketAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();

	Activity act;
	/**
	 * 图片集列表
	 */
	private List<ImageBucket> dataList;
	private BitmapCache cache;
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				SoftReference<Bitmap> refBitmap = new SoftReference<Bitmap>(bitmap);
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(refBitmap.get());
				} 
				// recycle
				bitmap = null;
			} 
		}
	};

	public ImageBucketAdapter(Activity act, List<ImageBucket> list,BitmapCache instance) {
		this.act = act;
		dataList = list;
		cache = instance;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0:dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	final class Holder {
		private ImageView iv;
//		private ImageView selected;
		private TextView name;
		private TextView count;
	}

	@Override
	public View getView(int arg0, View converView, ViewGroup arg2) {
		
		Holder holder;
		if (converView == null) {
			holder = new Holder();
			converView = View.inflate(act, R.layout.item_image_bucket, null);
			holder.iv = (ImageView) converView.findViewById(R.id.image);
//			holder.selected = (ImageView) arg1.findViewById(R.id.isselected);
			holder.name = (TextView) converView.findViewById(R.id.name);
			holder.count = (TextView) converView.findViewById(R.id.count);
			converView.setTag(holder);
		} else {
			holder = (Holder) converView.getTag();
		}
		ImageBucket item = dataList.get(arg0);
		holder.count.setText(item.count + "张");
		holder.name.setText(item.bucketName);
//		holder.selected.setVisibility(View.GONE);
		if (item.imageList != null && item.imageList.size() > 0) {
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageBitmap(null);
			Log.e(TAG, "no images in bucket " + item.bucketName);
		}
		return converView;
	}

}
