package qfpay.wxshop.data.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.Utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;

public class ImageWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int COMPRESS_PNG = 0;
	public static final int COMPRESS_JPG = 1;
	
	private int id = -1;
	private String imgUrl = null;
	private File imgFile = null;
	private File cacheFile = null;
	private boolean isError = false;
	
	private boolean isDeflaut = false;// 如果为true, 则所有的操作都不进行, 标识这个wrapper只是默认的加号图片或者其它的逻辑
	
	/**
	 * 设置图片的地址,可以是Url也可以是本地文件地址
	 */
	public void setImgPath(String path) {
		 Pattern pattern = Pattern.compile(Utils.getUrlRegex(), Pattern.CASE_INSENSITIVE);
		 Matcher matcher = pattern.matcher(path);
		 if (matcher.find()) {
			this.imgUrl = path;
		} else {
			this.imgFile = new File(path);
		}
	}
	
	/**
	 * 如果本地有文件则不返回URL
	 */
	public String getImgStr() {
		if (imgFile != null) {
			return getImgFilePath();
		}
		else if (imgUrl != null) {
			return getImgURL();
		}
		else {
			return "";
		}
	}
	
	/**
	 * 压缩并且缓存图片到文件
	 */
	public File compressImgFroUpload() {
		if (imgFile == null) {
			return null;
		} else {
			if (cacheFile != null) {
				return cacheFile;
			}
			Bitmap bitmap = processImgFile(imgFile);
			int degree = Utils.readPictureDegree(imgFile.getAbsolutePath());
			if (degree != 0) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
			}
			return keepBitmap(bitmap);
		}
	}
	
	File keepBitmap(Bitmap bitmap) {
		try {
			File dir = new File(ConstValue.getPICTURE_DIR());
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return null;
				}
			}
			String imgPath = dir.getAbsolutePath() + (Math.random() * 100) + (Math.random() * 100) + ".jpg";
			cacheFile = new File(imgPath);
			if (cacheFile.exists()) {
				cacheFile.delete();
			}
			cacheFile.createNewFile();
			FileOutputStream out = new FileOutputStream(cacheFile);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
			return cacheFile;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		return null;
	}
	
	Bitmap processImgFile(File file) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = Utils.getSampleSize(options);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	}
	
	public File getCachedImgFile() {
		return cacheFile;
	}

	public String getImgURL() {
		if (imgUrl != null) {
			return imgUrl;
		}
		return "";
	}
	
	public File getImgFile() {
		if(imgFile!=null){
			return imgFile.getAbsoluteFile();
		}
		return null;
	}
	
	public void deleteImgFile() {
		try {
			File file = getImgFile();
			if(file !=null){
				getImgFile().delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deleteCacheImg() {
		try {
			cacheFile.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getImgFilePath() {
		if (imgFile != null) {
			return imgFile.getAbsolutePath();
		}
		return "";
	}
	
	public String getImgFileName() {
		if (imgFile != null) {
			return imgFile.getName();
		}
		return "";
	}
	
	public boolean isOnlyNetImage() {
		if (imgUrl != null && imgFile == null) {
			return true;
		}
		return false;
	}
	
	public boolean hasUploaded() {
		return imgUrl != null;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setID(String id) {
		try {
			int idInt = Integer.parseInt(id, 10);
			this.id = idInt;
		} catch (NumberFormatException e) {
			this.id = -1;
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}

	public boolean isDeflaut() {
		return isDeflaut;
	}

	public void setDeflaut(boolean isDeflaut) {
		this.isDeflaut = isDeflaut;
	}
	
	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	@Override
	public String toString() {
		return "ImageWrapper [id=" + id + ", imgUrl=" + imgUrl + ", imgFile="
				+ imgFile + ", cachePath=" + cacheFile + ", isDeflaut="
				+ isDeflaut + "]";
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
		if (id != other.id)
			return false;
		if (imgFile == null) {
			if (other.imgFile != null)
				return false;
		} else if (!imgFile.equals(other.imgFile))
			return false;
		if (imgUrl == null) {
			if (other.imgUrl != null)
				return false;
		} else if (!imgUrl.equals(other.imgUrl))
			return false;
		if (isDeflaut != other.isDeflaut)
			return false;
		return true;
	}
}
