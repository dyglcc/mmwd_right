package qfpay.wxshop.image.uploader;

import java.io.File;
import java.io.IOException;

import org.apache.http.entity.mime.content.FileBody;

import qfpay.wxshop.data.dao.ImageUploaderDaoImpl;
import qfpay.wxshop.image.ImageWrapper;
import qfpay.wxshop.image.net.ImageNetProcesser;
import qfpay.wxshop.image.processer.ImageDecoder;

import com.j256.ormlite.dao.Dao;

public class ImageSyncUploader implements Cancelable {
	private ImageWrapper         mImage;
	
	private ImageNetProcesser    mNetProcesser;
	private UploadUtils          mUtils;
	private ImageDecoder         mDecoder;
	
	public ImageSyncUploader(ImageWrapper image, Dao<ImageWrapper, Integer> dao) {
		this.mImage = image;
		
		this.mNetProcesser = new ImageNetProcesser(mImage.getPath());
		this.mUtils        = new UploadUtils(image, new ImageUploaderDaoImpl(dao));
		this.mDecoder      = new ImageDecoder();
	}
	
	public String upload() {
		// 判断是否已经上传过存入数据库
		if (mUtils.isUploaded()) {
			return mImage.getNetUrl();
		}
		File compressedFile = mDecoder.decodeImage(mImage);
		if (compressedFile == null) {
			return "";
		}
		try {
			String url = mNetProcesser.uploadToServer(new FileBody(compressedFile));
			// 组拼并缓存进数据库
			mImage.setNetUrl(url);
			mUtils.cacheImage(url);
			return mImage.getNetUrl();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public void cancel() {
		mNetProcesser.cancel();
	}
}
