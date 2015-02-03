package qfpay.wxshop.ui.commodity.detailmanager;

/**
 * 图片的ViewModel
 *
 * Created by LiFZhe on 1/22/15.
 */
public class PictureViewModel {
    private int     id;
    private String  path;
    private String  url;
    private float   progress; // 上传进度, 百分比
    private boolean isDefault;
    private boolean isUploading;
    private boolean isSuccess;
    private boolean isFail;

    /**
     * 是否只有本地有
     * @return
     */
    public boolean isNative() {
        return url == null || url.equals("");
    }

    /**
     * 本地是否有
     * @return
     */
    public boolean hasNative() {
        return path != null && !path.equals("");
    }

    public boolean isNeedUpload() {
        return !isUploading && isNative();
    }

    public boolean isUploading() {
        return isUploading && isNative();
    }

    public void complete(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.isFail = !isSuccess;
        this.isUploading = false;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFail() {
        return isFail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public float getProgress() {
        return progress;
    }

    /**
     * 最大为1
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setUploading(boolean isUploading) {
        this.isUploading = isUploading;
    }
}
