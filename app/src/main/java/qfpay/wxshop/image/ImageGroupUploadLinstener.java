package qfpay.wxshop.image;

public interface ImageGroupUploadLinstener {
	public void onUploadProgress(float progress);
	public void onComplete(int successCount, int failureCount);
}
