package qfpay.wxshop.ui.customergallery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

@EFragment(R.layout.customergallery_fragment_grid)
public class CustomerGalleryGridFragment extends BaseFragment {
	@ViewById GridView                    gv_image;
	@ViewById TextView                    tv_choicebucket;
	@Bean     CustomerGalleryHelper       dataHelper;
	private   CustomerGalleryActivity     mActivity;
	private   CustomerGalleryImageAdapter adapter;
	
	@AfterViews void onInit() {
		mActivity = (CustomerGalleryActivity) getActivity();
		adapter = new CustomerGalleryImageAdapter();
		gv_image.setAdapter(adapter);
	}
	
	@UiThread(delay = 100) @Override public void onFragmentRefresh() {
		adapter.notifyDataSetChanged();
	}
	
	@Click void tv_choicebucket(View view) {
		CustomerGalleryBucketPopupwin.showPopupwin(view, this);
	}
	
	public void onBucketChoiced(GalleryBucketWrapper bucket) {
		tv_choicebucket.setText(bucket.getName());
		dataHelper.chooseBucket(bucket);
		adapter.notifyDataSetChanged();
	}
	
	class CustomerGalleryImageAdapter extends BaseAdapter {
		@Override public int getCount() {
			return dataHelper.getImagesWithCamera().size();
		}

		@Override public GalleryImageWrapper getItem(int position) {
			if (position >= dataHelper.getImagesWithCamera().size()) {
				return null;
			}
			return dataHelper.getImagesWithCamera().get(position);
		}

		@Override public long getItemId(int position) {
			return position;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			CustomerGalleryItem item = (CustomerGalleryItem) convertView;
			if (item == null) {
				item = CustomerGalleryItem_.build(mActivity);
			}
			item.setData(getItem(position), mActivity);
			return item;
		}
	}
}
