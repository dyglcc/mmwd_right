package qfpay.wxshop.ui.customergallery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GalleryBucketWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String defaultIconPath = "";
	private List<GalleryImageWrapper> imageWrappers = new ArrayList<GalleryImageWrapper>();
	private boolean isDefault;
	
	public static GalleryBucketWrapper getDefault(List<GalleryImageWrapper> allImages) {
		GalleryBucketWrapper wrapper = new GalleryBucketWrapper();
		wrapper.setDefault(true);
		wrapper.setName("所有图片");
		wrapper.setImageWrappers(allImages);
		if (allImages != null && !allImages.isEmpty() && allImages.get(0).getPath() != null) {
			wrapper.setDefaultIconPath(allImages.get(0).getPath());
		}
		return wrapper;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultIconPath() {
		return defaultIconPath;
	}
	public void setDefaultIconPath(String defaultIconPath) {
		this.defaultIconPath = defaultIconPath;
	}
	public List<GalleryImageWrapper> getImageWrappers() {
		return imageWrappers;
	}
	public void addImageWrapper(GalleryImageWrapper wrapper) {
		if (imageWrappers == null) {
			imageWrappers = new ArrayList<GalleryImageWrapper>();
		}
		imageWrappers.add(wrapper);
	}
	public void setImageWrappers(List<GalleryImageWrapper> list) {
		imageWrappers = list;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultIconPath == null) ? 0 : defaultIconPath.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((imageWrappers == null) ? 0 : imageWrappers.hashCode());
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalleryBucketWrapper other = (GalleryBucketWrapper) obj;
		if (defaultIconPath == null) {
			if (other.defaultIconPath != null)
				return false;
		} else if (!defaultIconPath.equals(other.defaultIconPath))
			return false;
		if (id != other.id)
			return false;
		if (imageWrappers == null) {
			if (other.imageWrappers != null)
				return false;
		} else if (!imageWrappers.equals(other.imageWrappers))
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
