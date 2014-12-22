package qfpay.wxshop.image.uploader;

import qfpay.wxshop.data.dao.ImageUploaderDaoImpl;
import qfpay.wxshop.image.ImageWrapper;
import qfpay.wxshop.image.processer.ImageSignature;
import qfpay.wxshop.image.processer.cache.SafeKeyGenerator;

public class UploadUtils {
	private ImageWrapper         mImage;
	private ImageUploaderDaoImpl mDao;
	
	public UploadUtils(ImageWrapper image, ImageUploaderDaoImpl dao) {
		this.mImage = image;
		this.mDao = dao;
	}
	
	public boolean isUploaded() {
		mImage.setKey(genKey());
		String netUrl = mDao.findImageUrl(mImage.getKey());
		if (netUrl != null && !"".equals(netUrl)) {
			mImage.setNetUrl(netUrl);
			return true;
		}
		return false;
	}
	
	public String genKey() {
		if (mImage == null) {
			return "";
		}
		SafeKeyGenerator keyGen = new SafeKeyGenerator();
		return keyGen.getSafeKey(new ImageSignature(mImage));
	}
	
	public void cacheImage(String url) {
		mDao.addImageData(mImage);
	}
}
