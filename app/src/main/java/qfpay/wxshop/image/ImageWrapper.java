package qfpay.wxshop.image;

import java.io.File;
import java.io.Serializable;

import qfpay.wxshop.image.processer.ImageType;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "images_upload_new")
public class ImageWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true)
	               private int    _id;
	@DatabaseField private String key     = "";
	@DatabaseField private String path    = "";
	@DatabaseField private String netUrl  = "";
	@DatabaseField private int    quality = 0;
	@DatabaseField private int    width   = 0;
	@DatabaseField private int    height  = 0;
	@DatabaseField private long   size    = 0;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.size = new File(path).length();
		this.path = path;
	}
	public String getNetUrl() {
		return netUrl;
	}
	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}
	public void setImageType(ImageType imageType) {
		this.quality = imageType.quality;
		this.width = imageType.width;
		this.height = imageType.height;
	}
	public int getQuality() {
		return quality;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public long getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "ImageWrapper [key=" + key + ", path=" + path + ", netUrl="
				+ netUrl + ", quality=" + quality + ", width=" + width
				+ ", height=" + height + ", size=" + size + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((netUrl == null) ? 0 : netUrl.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + quality;
		result = prime * result + (int) (size ^ (size >>> 32));
		result = prime * result + width;
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
		ImageWrapper other = (ImageWrapper) obj;
		if (height != other.height)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (netUrl == null) {
			if (other.netUrl != null)
				return false;
		} else if (!netUrl.equals(other.netUrl))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (quality != other.quality)
			return false;
		if (size != other.size)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}
