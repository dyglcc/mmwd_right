package qfpay.wxshop.image;

import java.io.Serializable;

import qfpay.wxshop.image.net.ImageUrlSetter;

public class ImageProcesserBean implements Serializable, ImageUrlSetter {
	public static final int DEFAULT_IMG_ID = -1;
	public static final long DEFAULT_MODIFIED_DATE = -1;
	public static final int DEFAULT_GROUP_ID = -1;
	public static final String DEFAULT_GROUP_NAME = "默认";
	
	private static final long serialVersionUID = 1L;
	
	private int id = DEFAULT_IMG_ID;
	private String netId = "";
	private String url = "";
	private String path = "";
	private String thumbnailPath = "";
	private String thumbnailForUploadPath = "";
	private String groupName = DEFAULT_GROUP_NAME;
	private int groupId = DEFAULT_GROUP_ID;
	private long modifiedDate = DEFAULT_MODIFIED_DATE;
	
	private boolean isUploaderError;
	private boolean isDefault;
	private boolean isSelect;
	private boolean isCancel;
	private boolean isFromNative;

	public boolean isOnlyNetImage() {
		return path.equals("") && !url.equals("");
	}
	
	public boolean hasUploaded() {
		return !url.equals("");
	}
	
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		if(url.indexOf("pic.qfpay.com/wd") != -1){
			return url.replaceFirst("pic.qfpay.com/wd", "imgstore02.qiniudn.com");
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getThumbnailForUploadPath() {
		return thumbnailForUploadPath;
	}
	public void setThumbnailForUploadPath(String thumbnailForUploadPath) {
		this.thumbnailForUploadPath = thumbnailForUploadPath;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public long getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public boolean isUploaderError() {
		return isUploaderError;
	}
	public void setUploaderError(boolean isUploaderError) {
		this.isUploaderError = isUploaderError;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public boolean isCancel() {
		return isCancel;
	}
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}
	public boolean isFromNative() {
		return isFromNative;
	}

	public void setFromNative(boolean isFromNative) {
		this.isFromNative = isFromNative;
	}

	@Override
	public String toString() {
		return "ImageProcesserWrapper ["
				+ "id=" + id 
				+ ", url=" + url 
				+ ", path=" + path 
				+ ", thumbnailPath=" + thumbnailPath
				+ ", isUploaderError=" + isUploaderError 
				+ ", isDefault=" + isDefault 
				+ ", isSelect=" + isSelect + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result + (isSelect ? 1231 : 1237);
		result = prime * result + (isUploaderError ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((thumbnailPath == null) ? 0 : thumbnailPath.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageProcesserBean other = (ImageProcesserBean) obj;
		if (id != other.id)
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (isSelect != other.isSelect)
			return false;
		if (isUploaderError != other.isUploaderError)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (thumbnailPath == null) {
			if (other.thumbnailPath != null)
				return false;
		} else if (!thumbnailPath.equals(other.thumbnailPath))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public void setImageUrl(String url) {
		setUrl(url);
	}
}
