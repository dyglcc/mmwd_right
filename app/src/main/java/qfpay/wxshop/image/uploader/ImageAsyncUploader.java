package qfpay.wxshop.image.uploader;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import qfpay.wxshop.data.dao.ImageUploaderDaoImpl;
import qfpay.wxshop.image.ImageWrapper;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.StatesHandler;
import qfpay.wxshop.image.net.CountingFileRequestEntity;
import qfpay.wxshop.image.net.ImageNetProcesser;
import qfpay.wxshop.image.net.ImageUrlSetter;
import qfpay.wxshop.image.processer.ImageDecoder;
import qfpay.wxshop.utils.T;

import com.j256.ormlite.dao.Dao;

public class ImageAsyncUploader implements Cancelable {
	private StatesHandler        mHandler;
	private ImageUrlSetter       mSetter;
	private ImageWrapper         mImage;
	private ImageUploaderDaoImpl mDao;
	
	private ImageProcesserTask   mTask;
	private ImageNetProcesser    mNetProcesser;
	private UploadUtils          mUtils;
	private long                 mDecodedSize;
	
	public ImageAsyncUploader(ImageWrapper image, Dao<ImageWrapper, Integer> dao, StatesHandler handler, ImageUrlSetter setter) {
		this.mHandler      = handler;
		this.mImage        = image;
		this.mSetter       = setter;
		this.mDao          = new ImageUploaderDaoImpl(dao);
		this.mTask         = new ImageProcesserTask();
		this.mNetProcesser = new ImageNetProcesser(mImage.getPath());
		this.mUtils        = new UploadUtils(image, mDao);
	}
	
	public String upload() {
		new Thread(mTask).start();
		return "";
	}
	
	@Override
	public void cancel() {
		if (mNetProcesser != null) {
			mNetProcesser.cancel();
			mNetProcesser = null;
		}
		if (mTask != null) {
			if (! mTask.isCancelled()) {
				mTask.cancel(true);
			}
			mTask = null;
		}
	}
	
	public void onLoading(long currentSize) {
		mHandler.onPreUploading(mImage.getPath(), currentSize);
	}
	
	private class ImageProcesserTask extends FutureTask<String> {
		public ImageProcesserTask() {
			super(new ImageProcesser());
		}
		
		@Override
		protected void done() {
			boolean isSuccess = false;
			String  result    = "";
			try {
				result = get();
				isSuccess = (get() != null || !"".equals(get()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isSuccess) {
				mImage.setNetUrl(result);
				mSetter.setImageUrl(result);
				mUtils.cacheImage(result);
			}
			mHandler.onPreUploading(mImage.getPath(), mDecodedSize);
			mHandler.onPreUploadComplete(isSuccess);
			ImageAsyncUploader.this.cancel();
			super.done();
		}
	}
	
	private class ImageProcesser implements Callable<String> {
		@Override
		public String call() throws Exception {
			if (mUtils.isUploaded()) {
				// 如果数据库中有暂存的url,则不会计算大小,但是会累计数量
				return mImage.getNetUrl();
			}
			T.d(QFImageUploader.logTag, "Start decode");
			File file = new ImageDecoder().decodeImage(mImage);
			mDecodedSize = file.length();
			mHandler.addUploadSize(mDecodedSize);
			T.d(QFImageUploader.logTag, "Decode done, File size is [" + file.length() + "], File path is [" + file.getAbsolutePath() + "]");
			
			T.d(QFImageUploader.logTag, "Start Upload");
			long startTime = System.currentTimeMillis();
			String result = "";
			int count = 0;
			while (count ++ <= 3 && (result == null || "".equals(result))) {
				result = mNetProcesser.uploadToServer(new CountingFileRequestEntity(file, ImageAsyncUploader.this));
			}
			T.d(QFImageUploader.logTag, "Upload done, Result is [" + result + "], Time is "
					+ ((System.currentTimeMillis() - startTime) / 1000) + " s]");
			return result;
		}
	}
}
