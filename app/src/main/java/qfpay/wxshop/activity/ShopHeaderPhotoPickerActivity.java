package qfpay.wxshop.activity;

import java.io.File;
import java.io.IOException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.ImageWrapper;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

@EActivity(R.layout.shopimg_picker)
public class ShopHeaderPhotoPickerActivity extends BaseActivity {
	public static final int REQUEST_TAKE_PHOTO = 100;
	public static final int REQUEST_PICK_PHOTO = 101;
	public static final int REQUEST_CROP_PHOTO = 102;
	public static final int REQUEST_SAVE = 103;
	
	@ViewById LinearLayout ll_fromgallery, ll_takephoto, ll_fromserver;
	@ViewById ImageView iv_photo;
	@ViewById RelativeLayout rl_photo;
	@Bean     RetrofitWrapper retrofitWrapper;
	
	ImageWrapper imgWrapper = new ImageWrapper();
	ImageWrapper imgCacheWrapper = new ImageWrapper();
	
	@AfterViews
	void afterInit() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.shopheader_pagetitle);
		
		rl_photo.getLayoutParams().height = processImageHeight();
		rl_photo.setLayoutParams(rl_photo.getLayoutParams());
		setPreImg();
		newImg();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	int processImageHeight() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		int height = width * 33 / 64;
		return height;
	}
	
	@Click
	void ll_fromgallery() {
		MobAgentTools.OnEventMobOnDiffUser(this, "CLICK_SELECT_PIC_FROM_MOBILE");
		pickPhoto();
	}
	
	@Click
	void ll_takephoto() {
		MobAgentTools.OnEventMobOnDiffUser(this, "CLICK_SELECT_SHOP_PIC_FROM_TAKEPIC");
		takePhoto();
	}
	
	@Click
	void ll_fromserver() {
		MobAgentTools.OnEventMobOnDiffUser(this, "CLICK_SELECT_FROM_DESIGNED");
		SelectPicActivity_.intent(this).startForResult(REQUEST_SAVE);;
	}
	
	void takePhoto() {
		newCacheImg();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCacheImgFile()));
		startActivityForResult(intent, REQUEST_TAKE_PHOTO);
	}
	
	@OnActivityResult(REQUEST_TAKE_PHOTO)
	void onPhotoTaked(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			cropPhoto(Uri.fromFile(getCacheImgFile()));
		} else {
			deleteCache();
		}
	}
	
	void pickPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_PICK_PHOTO);
	}
	
	@OnActivityResult(REQUEST_PICK_PHOTO)
	void onPhotoPicked(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			Uri photoUri = data.getData();
			if (photoUri != null) {
				cropPhoto(photoUri);
			}
		}
	}
	
	void cropPhoto(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 64);
        intent.putExtra("aspectY", 33);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgWrapper.getImgFile()));
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }
	
	@OnActivityResult(REQUEST_CROP_PHOTO)
	void onPhotoCroped(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			startReview(imgWrapper);
		}
		deleteCache();
	}
	
	@UiThread
	void startReview(ImageWrapper wrapper) {
		Intent intent = new Intent(this, ShopHeaderPreviewActivity_.class);
		intent.putExtra(ConstValue.TITLE, "预览背景图");
		intent.putExtra("wrapper", wrapper);
		intent.putExtra("isLoadFirst", false);
		startActivityForResult(intent, REQUEST_SAVE);
	}
	
	@OnActivityResult(REQUEST_SAVE)
	void onSaveDone(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
	
	@UiThread
	void setPreImg() {
		if (WxShopApplication.dataEngine.getShopBg() == null || WxShopApplication.dataEngine.getShopBg().equals("")) {
			iv_photo.setImageResource(R.drawable.header_deflaut);
		} else {
			Picasso.with(this).load(WxShopApplication.dataEngine.getShopBg()).fit().centerCrop().placeholder(R.drawable.header_deflaut).
				error(R.drawable.header_deflaut).into(iv_photo);
		}
	}
	
	void processDir(ImageWrapper imgWrapper, String fileName) {
		String dirName = ConstValue.getPICTURE_DIR();
		File dirFile = new File(dirName);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs()) {
				Toaster.s(this, "出现未知异常哦~重新试试吧");
			}
		}
		String imgPath = dirName + fileName;
		File imgCacheFile = new File(imgPath);
		if (imgCacheFile.exists()) {
			imgCacheFile.delete();
		}
		try {
			imgCacheFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Toaster.s(this, "出现未知异常哦~重新试试吧");
		}
		imgWrapper.setImgPath(dirName + fileName);
	}
	
	void deleteCache() {
		imgCacheWrapper.deleteImgFile();
	}
	
	File getCacheImgFile() {
		return imgCacheWrapper.getImgFile();
	}
	
	void newCacheImg() {
		processDir(imgCacheWrapper, "cache" + System.currentTimeMillis() + ".jpg");
	}
	
	void newImg() {
		processDir(imgWrapper, "img" + System.currentTimeMillis() + ".jpg");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		deleteCache();
	}
}
