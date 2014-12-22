package qfpay.wxshop.ui.customergallery;

import java.io.Serializable;

public class GalleryImageWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private static GalleryImageWrapper defaultWrapper;
	
	public static GalleryImageWrapper getDefault() {
		if (defaultWrapper == null) {
			defaultWrapper = new GalleryImageWrapper();
			defaultWrapper.setDefault(true);
		}
		return defaultWrapper;
	}
	
	private int     id;
	private String  path;
	private String  thumbnailPath;
	private long    modifiedDate;
	private int     bucketId;
	private String  bucketName;
	private boolean isDefault;
	private boolean isSelect;
	
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
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public long getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public int getBucketId() {
		return bucketId;
	}
	public void setBucketId(int bucketId) {
		this.bucketId = bucketId;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
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
	
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bucketId;
		result = prime * result
				+ ((bucketName == null) ? 0 : bucketName.hashCode());
		result = prime * result + id;
		result = prime * result + (int) (modifiedDate ^ (modifiedDate >>> 32));
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((thumbnailPath == null) ? 0 : thumbnailPath.hashCode());
		return result;
	}
	
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalleryImageWrapper other = (GalleryImageWrapper) obj;
		if (bucketId != other.bucketId)
			return false;
		if (bucketName == null) {
			if (other.bucketName != null)
				return false;
		} else if (!bucketName.equals(other.bucketName))
			return false;
		if (id != other.id)
			return false;
		if (modifiedDate != other.modifiedDate)
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
		return true;
	}
	
	@Override
	public String toString() {
		return "GalleryImageWrapper [id=" + id + ", path=" + path
				+ ", thumbnailPath=" + thumbnailPath + ", modifiedDate="
				+ modifiedDate + ", bucketId=" + bucketId + ", bucketName="
				+ bucketName + ", isDefault=" + isDefault + ", isSelect="
				+ isSelect + "]";
	}
}
