package qfpay.wxshop.image;

/**
 * 用于控制单张图片的下载进度
 *
 * Created by LiFZhe on 1/22/15.
 */
public interface ImageProgressListener {
    public void onProgress(String path, long total, long progress);

    public void onFailure();

    public void onSuccess(String url);
}
