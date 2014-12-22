package qfpay.wxshop.ui.customergallery;

import java.io.File;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.squareup.picasso.Picasso;

import qfpay.wxshop.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@EViewGroup(R.layout.customergallery_item)
public class CustomerGalleryItem extends RelativeLayout {
	@ViewById RelativeLayout rl_image, rl_camera;
	@ViewById ImageView      iv_image, iv_selectlayer;
	
	CustomerGalleryActivity  mActivity;
	GalleryImageWrapper      mImageWrapper;
	@Bean CustomerGalleryHelper dataHelper;
	
	public CustomerGalleryItem(Context context) {
		super(context);
		
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setData(GalleryImageWrapper wrapper, CustomerGalleryActivity activity) {
		if (wrapper == null) {
			return;
		}
		this.mImageWrapper = wrapper;
		this.mActivity = activity;
		if (wrapper.isDefault()) {
			showPreview();
		} else {
			showImage(wrapper);
		}
	}
	
	private void showPreview() {
		rl_image.setVisibility(View.GONE);
		rl_camera.setVisibility(View.VISIBLE);
	}
	
	private void showImage(GalleryImageWrapper wrapper) {
		rl_image.setVisibility(View.VISIBLE);
		rl_camera.setVisibility(View.GONE);
		
		String path = wrapper.getThumbnailPath();
		if (path == null || path.equals("")) {
			path = wrapper.getPath();
		}
		Picasso.with(getContext()).load(new File(path)).fit().centerCrop().into(iv_image);
		processImgState();
	}
	
	@Click void rl_camera() {
		mActivity.startCamera();
	}
	
	@Click void rl_image() {
		boolean willSelect = !mImageWrapper.isSelect();
		if (mActivity.onImageSelecting(willSelect, mImageWrapper)) {
			mImageWrapper.setSelect(willSelect);
			processImgState();
		}
	}
	
	private void processImgState() {
		if (mImageWrapper.isSelect()) {
			iv_selectlayer.setVisibility(View.VISIBLE);
		} else {
			iv_selectlayer.setVisibility(View.INVISIBLE);
		}
	}
	
	@Click void iv_preview() {
		mActivity.changeLayout(ImagePage.DETAIL);
		dataHelper.onImagePreview(mImageWrapper);
	}
}
