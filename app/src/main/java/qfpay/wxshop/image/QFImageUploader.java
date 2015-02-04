package qfpay.wxshop.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;

import qfpay.wxshop.data.dao.QFDataHelper;
import qfpay.wxshop.image.net.ImageUrlSetter;
import qfpay.wxshop.image.processer.ImageType;
import qfpay.wxshop.image.uploader.Cancelable;
import qfpay.wxshop.image.uploader.ImageAsyncUploader;
import qfpay.wxshop.image.uploader.ImageSyncUploader;
import android.content.Context;

import com.j256.ormlite.dao.Dao;

@EBean
public class QFImageUploader {
	public static String logTag = "QFImageUploader";
	
	@RootContext Context mContext;
	@Bean StatesHandler mHandler;
	@OrmLiteDao(helper = QFDataHelper.class) Dao<ImageWrapper, Integer> mDao;
	Map<String, Cancelable> mRequests = new HashMap<String, Cancelable>();
	
	public QFImageUploader setGroupLinstener(ImageGroupUploadLinstener linstener) {
		mHandler.setLinstener(linstener);
		return this;
	}
	
	public Builder with(int id) {
		Builder builder = new Builder(this);
		builder.id(id);
		return builder;
	}
	
	@Background(serial = "image_upload_delay", delay = 100)
	void uploadInGroup(Builder builder) {
		if (builder.mImagePath == null || builder.mImagePath.equals("")) {
			return;
		}
		
		ImageWrapper bean = new ImageWrapper();
		bean.setImageType(builder.mType);
		bean.setPath(builder.mImagePath);
		
		ImageAsyncUploader uploader = new ImageAsyncUploader(bean, mDao, mHandler, builder.mImageURlSetter, builder.mLinstener);
		uploader.upload();
		mRequests.put(bean.getPath(), uploader);
		
		mHandler.addUploadingImage();
	}
	
	String uploadSync(Builder builder) {
		if (builder.mImagePath == null || builder.mImagePath.equals("")) {
			return "";
		}
		
		ImageWrapper bean = new ImageWrapper();
		bean.setImageType(builder.mType);
		bean.setPath(builder.mImagePath);
		
		ImageSyncUploader uploader = new ImageSyncUploader(bean, mDao);
		mRequests.put(bean.getPath(), uploader);
		
		String result = uploader.upload();
		if (result == null || result.equals("")) {
			return "";
		}
		return result;
	}
	
	public void ready() {
		mHandler.ready();
	}
	
	public void cancelAll() {
		Set<String> keyset = mRequests.keySet();
		for (String path : keyset) {
			mRequests.get(path).cancel();
		}
		mRequests.clear();
		mHandler.resetState();
	}

    public void cancel(String path) {
        if (mRequests.containsKey(path)) {
            mRequests.get(path).cancel();
        }
    }

    public void setSingleTaskLinstener(String path, ImageProgressListener linstener) {
        ((ImageAsyncUploader) mRequests.get(path)).setLinstener(linstener);
    }
	
	public class Builder {
		QFImageUploader mUploader;
		int             mImageID;
		String          mImagePath;
		ImageUrlSetter  mImageURlSetter;
		ImageType       mType = ImageType.NORMAL;
        ImageProgressListener mLinstener;
		
		Builder(QFImageUploader mUploader) {
			super();
			this.mUploader = mUploader;
		}

		public Builder id(int id) {
			this.mImageID = id;
			return this;
		}
		
		public Builder path(String path) {
			this.mImagePath = path;
			return this;
		}
		
		public Builder urlSetter(ImageUrlSetter setter) {
			this.mImageURlSetter = setter;
			return this;
		}
		
		public Builder imageType(ImageType type) {
			this.mType = type;
			return this;
		}

        public Builder linstener(ImageProgressListener linstener) {
            this.mLinstener = linstener;
            return this;
        }
		
		public void uploadInGroup() {
			QFImageUploader.this.uploadInGroup(this);
		}

		public String uploadSync() {
			return QFImageUploader.this.uploadSync(this);
		}
	}
}