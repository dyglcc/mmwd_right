package qfpay.wxshop.image;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;

@EBean
public class StatesHandler {
	private SoftReference<ImageGroupUploadLinstener> mLinstener;
	
	@RootContext Context mContext;
	
	private int     mTotalCount;
	private int     mSuccessCount;
	private int     mErrorCount;
	
	private long    mTotalSize;
	private HashMap<String, Long> preSizeArray  = new HashMap<String, Long>();
	
	private boolean isReady;
	
	public void setLinstener(ImageGroupUploadLinstener linstener) {
		mLinstener = new SoftReference<ImageGroupUploadLinstener>(linstener);
	}
	
	@UiThread public void resetState() {
		mTotalCount = 0;
		mSuccessCount = 0;
		mTotalSize = 0;
		preSizeArray.clear();
		isReady = false;
	}
	
	@UiThread public void addUploadingImage() {
		mTotalCount ++;
	}
	
	@UiThread public void addUploadSize(long size) {
		mTotalSize = mTotalSize + size;
	}
	
	@UiThread public void ready() {
		isReady = true;
		if (mErrorCount + mSuccessCount == mTotalCount) {
			mLinstener.get().onComplete(mSuccessCount, mErrorCount);
		}
	}
	
	@UiThread public void onPreUploading(String path, long uploadedSize) {
		preSizeArray.put(path, uploadedSize);
		if (mTotalSize == 0) {
			return;
		}
		Collection<Long> set = preSizeArray.values();
		long size = 0;
		for (Long long1 : set) {
			size += long1;
		}
		float progress = BigDecimal.valueOf(size).divide(BigDecimal.valueOf(mTotalSize), 3, BigDecimal.ROUND_HALF_UP).floatValue();
		if (progress > 0.95f) {
			progress = 0.95f;
		}
		T.d(QFImageUploader.logTag, "onLoading progress is [" + progress + "], added is [" + size + "], total size is " + mTotalSize + "]");
		mLinstener.get().onUploadProgress(progress);
	}
	
	@UiThread public void onPreUploadComplete(boolean isSuccess) {
		if (isSuccess) {
			mSuccessCount ++;
			MobAgentTools.OnEventMobOnDiffUser(mContext, "upload_succeed_yh");
		} else {
			mErrorCount ++;
			MobAgentTools.OnEventMobOnDiffUser(mContext, "upload_fail_yh");
		}
		if (mErrorCount + mSuccessCount == mTotalCount && isReady) {
			mLinstener.get().onComplete(mSuccessCount, mErrorCount);
		}
	}
}
