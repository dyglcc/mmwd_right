package qfpay.wxshop.ui.customergallery;

import java.io.File;
import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.Toaster;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.GridView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@EActivity(R.layout.customergallery_layout) @SuppressLint("CommitTransaction")
public class CustomerGalleryActivity extends BaseActivity {
	public final static String RESULT_DATA_NAME = "data";
	public final static int REQUEST_CODE = 2;
	
	@Extra int maxCount = 9;
	@Extra int choicedCount = 0;
	@Extra int width = -1;
	@Extra int height = -1;
	
	@ViewById GridView gv_image;
	@ViewById Button btn_complete;

	@Bean CompleteBtnActionProvider completeProvider;
	@Bean CustomerGalleryHelper dataHelper;
	private ArrayList<ImageProcesserBean> mChoicedImages = new ArrayList<ImageProcesserBean>();
	private boolean isProcessed = false;
	
	@AfterViews void onInit() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		dataHelper.init();
		changeLayout(ImagePage.GRID);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImagePage.clear();
		dataHelper.clear();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.gallery_title_btn, menu);
		menu.findItem(R.id.menu_complete).setActionProvider(completeProvider);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startCamera() {
		if (choicedCount >= maxCount) {
			Toaster.s(this, "已达到最大的可选数量~");
		} else {
			dataHelper.startCameraToGetImage(this);
		}
	}
	
	@OnActivityResult(CustomerGalleryHelper.RESULT_CAMERA) void onCameraTaked(int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			mChoicedImages.add(newTOld(dataHelper.onCameraResult()));
			onComplete();
		}
	}
	
	/**
	 * 改变当前布局状态,暂时只有查看大图,切换方式为通过点击图片中一个按钮的方式
	 */
	public void changeLayout(ImagePage page) {
		page.showFragment(getSupportFragmentManager().beginTransaction());
	}
	
	/**
	 * 图片被选择时候的回调
	 * @return 是否允许被选择
	 */
	public boolean onImageSelecting(boolean isSelect, GalleryImageWrapper wrapper) {
		if (!isSelect) {
			choicedCount --;
			this.setChoicedCount(choicedCount, maxCount);
			mChoicedImages.remove(newTOld(wrapper));
			return true;
		}
		if (choicedCount == maxCount) {
			Toaster.s(this, String.format(getString(R.string.gallery_toast_cantselect), maxCount));
			return false;
		} else {
			choicedCount ++;
			this.setChoicedCount(choicedCount, maxCount);
			mChoicedImages.add(newTOld(wrapper));
			return true;
		}
	}
	
	/**
	 * 设置当前被选中的图片的数量,用于判断是否已经无法继续选择
	 */
	public void setChoicedCount(int choicedCount, int maxCount) {
		completeProvider.setChoicedCount(choicedCount, maxCount);
	}
	
	/**
	 * 用户点击完成时调用
	 */
	void onComplete() {
		if (width > 0 && height > 0 && !isProcessed && !mChoicedImages.isEmpty()) {
			dataHelper.cropPhoto(Uri.fromFile(new File(mChoicedImages.remove(0).getPath())), this, width, height);
            isProcessed = true;
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra(RESULT_DATA_NAME, mChoicedImages);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@OnActivityResult(CustomerGalleryHelper.RESULT_CROP) void onCroped(int resultCode) {
        if (resultCode == RESULT_OK) {
            mChoicedImages.add(newTOld(dataHelper.onCropResult()));
            onComplete();
		} else {
			isProcessed = false;
		}
	}
	
	private ImageProcesserBean newTOld(GalleryImageWrapper wrapper) {
		ImageProcesserBean bean = new ImageProcesserBean();
		bean.setPath(wrapper.getPath());
		bean.setFromNative(true);
		bean.setSelect(true);
		return bean;
	}
}
