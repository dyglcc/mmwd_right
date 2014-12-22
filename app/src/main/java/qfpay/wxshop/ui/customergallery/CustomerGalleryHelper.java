package qfpay.wxshop.ui.customergallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.RootContext;

import qfpay.wxshop.data.net.ConstValue;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.SparseArray;
import qfpay.wxshop.utils.T;

@EBean(scope = Scope.Singleton)
public class CustomerGalleryHelper {
	public final static int RESULT_CAMERA = 1;
	public final static int RESULT_CROP = 1;
	public final static int ID_FROM_CAMERA = 2;
	
	@RootContext Context context;
	List<GalleryImageWrapper> mImages = new ArrayList<GalleryImageWrapper>();
	SparseArray<GalleryBucketWrapper> mBuckets = new SparseArray<GalleryBucketWrapper>();
	GalleryBucketWrapper mChoiceBucket;
	GalleryImageWrapper mPreviewImage;
	
	String cameraTakenPath = ""; // 调用系统相册或者裁减的时候保存临时的图片地址
	
	/**
	 * 调用所有操作方法之前必须先调用这个方法
	 */
	public void init() {
		clear();
		initList(initThumbnail());
	}
	
	public void clear() {
		mImages = new ArrayList<GalleryImageWrapper>();
		mBuckets = new SparseArray<GalleryBucketWrapper>();
		mChoiceBucket = null;
		mPreviewImage = null;
	}
	
	public List<GalleryImageWrapper> getImages() {
		List<GalleryImageWrapper> list;
		if (mChoiceBucket == null) {
			list = mImages;
		} else {
			list = mChoiceBucket.getImageWrappers();
		}
		// 在详情页面不需要最前面的一个默认的图片
		list.remove(GalleryImageWrapper.getDefault());
		return list;
	}
	
	public List<GalleryImageWrapper> getImagesWithCamera() {
		List<GalleryImageWrapper> list;
		if (mChoiceBucket == null) {
			list = mImages;
		} else {
			list = mChoiceBucket.getImageWrappers();
		}
		// 需要默认的相册图片
		if (!list.contains(GalleryImageWrapper.getDefault())) {
			list.add(0, GalleryImageWrapper.getDefault());
		}
		return list;
	}
	
	public List<GalleryBucketWrapper> getAllBuckets() {
		List<GalleryBucketWrapper> list = new ArrayList<GalleryBucketWrapper>();
		list.add(GalleryBucketWrapper.getDefault(mImages));
		for (int i = 0; i < mBuckets.size(); i++) {
			list.add(mBuckets.valueAt(i));
		}
		return list;
	}
	
	public void chooseBucket(GalleryBucketWrapper bucket) {
		this.mChoiceBucket = bucket;
	}
	
	public void onImagePreview(GalleryImageWrapper image) {
		this.mPreviewImage = image;
	}
	
	public GalleryImageWrapper getPreviewImage() {
		return mPreviewImage;
	}
	
	public void startCameraToGetImage(Activity activity) {
		cameraTakenPath = "";
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(processDir()));
		activity.startActivityForResult(intent, RESULT_CAMERA);
	}
	
	public GalleryImageWrapper onCameraResult() {
		GalleryImageWrapper wrapper = new GalleryImageWrapper();
		wrapper.setId(ID_FROM_CAMERA);
		wrapper.setSelect(true);
		wrapper.setPath(cameraTakenPath);
		return wrapper;
	}
	
	public void cropPhoto(Uri uri, Activity activity, int width, int height){
		cameraTakenPath = "";
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(processDir()));
        activity.startActivityForResult(intent, RESULT_CROP);
    }
	
	public GalleryImageWrapper onCropResult() {
		GalleryImageWrapper wrapper = new GalleryImageWrapper();
		wrapper.setSelect(true);
		wrapper.setPath(cameraTakenPath);
		return wrapper;
	}
	
	File processDir() {
		String dirName = ConstValue.getPICTURE_DIR();
		File dirFile = new File(dirName);
		if (!dirFile.mkdirs() && !dirFile.isDirectory()) {
			return null;
		}
		cameraTakenPath = dirName + "cache" + System.currentTimeMillis();
		File imgCacheFile = new File(cameraTakenPath);
		boolean isDeleted = true;
		boolean isCreated = true;
		try {
			// 如果文件存在则删除
			if (imgCacheFile.exists()) {
				isDeleted = imgCacheFile.delete();
			}
			// 创建一个新文件
			isCreated = imgCacheFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (! isCreated || ! isDeleted) {
			T.d("Create file error isCreated is " + isCreated + " isDeleted is " + isDeleted);
		}
		return imgCacheFile;
	}
	
	/**
	 * 初始化相册和所有照片
	 */
	void initList(Map<Integer, String> thumbnailMap) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID, 
        		MediaStore.Images.Media.DISPLAY_NAME, 
        		MediaStore.Images.Media.DATA, 
        		MediaStore.Images.Media.SIZE, 
        		MediaStore.Images.Media.DATE_MODIFIED,
        		MediaStore.Images.Media.BUCKET_DISPLAY_NAME, 
        		MediaStore.Images.Media.BUCKET_ID};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor = contentResolver.query(uri, projection, null, null, sortOrder);
        if (cursor == null) {
        	return;
        }
        
        if (cursor.getCount() == 0) {
			return;
		}
        
        try {
        	cursor.moveToFirst();
            
            do {
            	if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)) < 100) {
    				continue;
    			}
            	GalleryImageWrapper image = new GalleryImageWrapper();
            	image.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            	image.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            	image.setThumbnailPath(thumbnailMap.get(image.getId()));
            	image.setModifiedDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
            	image.setBucketId(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
            	image.setBucketName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
            	mImages.add(image);
            	
            	if (mBuckets.get(image.getBucketId()) == null) {
            		GalleryBucketWrapper bucket = new GalleryBucketWrapper();
    				bucket.setDefaultIconPath(image.getThumbnailPath());
    				bucket.setId(image.getBucketId());
    				bucket.setName(image.getBucketName());
    				bucket.setDefaultIconPath(image.getPath());
    				bucket.addImageWrapper(image);
    				mBuckets.put(image.getBucketId(), bucket);
    			} else {
    				mBuckets.get(image.getBucketId()).addImageWrapper(image);
    			}
    		} while (cursor.moveToNext());
		} finally {
			if (!cursor.isClosed()) cursor.close();
		}
    }
	
	/**
	 * 初始化所有缩略图
	 */
	@SuppressLint("UseSparseArrays") Map<Integer, String> initThumbnail() {
		String[] projection = { 
				Thumbnails._ID, 
				Thumbnails.IMAGE_ID, 
				Thumbnails.DATA };
		Cursor cursor = context.getContentResolver().query(
				Thumbnails.EXTERNAL_CONTENT_URI, 
				projection,
				null,
				null, 
				null);
		if (cursor == null) {
        	return null;
        }
		try {
			cursor.moveToFirst();
	        Map<Integer, String> map = new HashMap<Integer, String>();
	        while (cursor.moveToNext()) {
	        	map.put(cursor.getInt(cursor.getColumnIndex(Thumbnails.IMAGE_ID)), 
	        			cursor.getString(cursor.getColumnIndex(Thumbnails.DATA)));
	        }
	        return map;
		} finally {
			if (!cursor.isClosed()) cursor.close();
		}
	}
}
