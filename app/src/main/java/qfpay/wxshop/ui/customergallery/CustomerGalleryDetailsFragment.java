package qfpay.wxshop.ui.customergallery;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.co.senab.photoview.PhotoView;
import com.squareup.picasso.Picasso;

@EFragment(R.layout.customergallery_fragment_viewpager)
public class CustomerGalleryDetailsFragment extends BaseFragment {
	@ViewById ViewPager vp_img;
	@ViewById CheckBox  cb_choice;
	private   CustomerGalleryActivity mActivity;
	@Bean     CustomerGalleryHelper dataHelper;
	
	@AfterViews void onInit() {
		mActivity = (CustomerGalleryActivity) getActivity();
		GalleryPagerAdapter adapter = new GalleryPagerAdapter();
		vp_img.removeAllViews();
		vp_img.setAdapter(adapter);
		vp_img.setOnPageChangeListener(adapter);
		setItem();
	}
	
	@UiThread() void setItem() {
		vp_img.setCurrentItem(dataHelper.getImages().indexOf(dataHelper.getPreviewImage()), false);
	}
	
	public void onDetach() {
		super.onDetach();
		try {
			java.lang.reflect.Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		ImagePage.GRID.refresh();
	}
	
	@CheckedChange void cb_choice(CompoundButton button, boolean isCheck) {
		GalleryImageWrapper wrapper = dataHelper.getImages().get(vp_img.getCurrentItem());
		if (isCheck == wrapper.isSelect()) {
			return;
		}
		if (mActivity.onImageSelecting(isCheck, wrapper)) {
			wrapper.setSelect(isCheck);
		}
	}
	
	class GalleryPagerAdapter extends PagerAdapter implements OnPageChangeListener {
		@Override
		public int getCount() {
			return dataHelper.getImages().size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView pv = generateImageViewWithImage(position);
			container.addView(pv, 0);
			return pv;
		}

		@Override
		public void onPageSelected(int arg0) {
			cb_choice.setChecked(dataHelper.getImages().get(arg0).isSelect());
		}
		
		@Override public void onPageScrollStateChanged(int arg0) {}
		@Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
	}
	
	PhotoView generateImageViewWithImage(int position) {
		String path = dataHelper.getImages().get(position).getPath();
		PhotoView pv = generateImageView();
		Picasso.with(getActivity()).load(new File(path)).resize(800, 0).into(pv);
		return pv;
	}
	
	PhotoView generateImageView() {
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		PhotoView view = new PhotoView(mActivity);
		view.setLayoutParams(params);
		return view;
	}
}
