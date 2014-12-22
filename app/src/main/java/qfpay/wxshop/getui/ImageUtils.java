package qfpay.wxshop.getui;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StatFs;

public class ImageUtils {
	public static final int DEFAULT_READ_TIMEOUT = 20 * 1000; // 20s
	public static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // 15s
	
	private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
	private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

	public static long calculateDiskCacheSize(File dir) {
		long size = MIN_DISK_CACHE_SIZE;

		try {
			StatFs statFs = new StatFs(dir.getAbsolutePath());
			@SuppressWarnings("deprecation") long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
			// Target 2% of the total space.
			size = available / 50;
		} catch (IllegalArgumentException ignored) {
		}

		// Bound inside min/max size for disk cache.
		return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE),
				MIN_DISK_CACHE_SIZE);
	}

	public static int calculateMemoryCacheSize(Context context) {
		ActivityManager am = getService(context, ACTIVITY_SERVICE);
		boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
		int memoryClass = am.getMemoryClass();
		if (largeHeap && SDK_INT >= HONEYCOMB) {
			memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
		}
		// Target ~15% of the available heap.
		return 1024 * 1024 * memoryClass / 7;
	}

	public static int getBitmapBytes(Bitmap bitmap) {
		int result;
		if (SDK_INT >= HONEYCOMB_MR1) {
			result = BitmapHoneycombMR1.getByteCount(bitmap);
		} else {
			result = bitmap.getRowBytes() * bitmap.getHeight();
		}
		if (result < 0) {
			throw new IllegalStateException("Negative size: " + bitmap);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getService(Context context, String service) {
		return (T) context.getSystemService(service);
	}
	
	/**
	 * 图片压缩算法
	 * @param options Bitmap.Options
	 * @param minSideLength 最小显示区
	 * @param maxNumOfPixels 你想要的宽度 * 你想要的高度
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
     
    public static int computeInitialSampleSize(BitmapFactory.Options options,  int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

	public static interface FileNameGenerator {

		/** Generates unique file name for image defined by URI */
		String generate(String imageUri);
	}

	public static class Md5FileNameGenerator implements FileNameGenerator {

		private static final String HASH_ALGORITHM = "MD5";
		private static final int RADIX = 10 + 26; // 10 digits + 26 letters

		@Override
		public String generate(String imageUri) {
			byte[] md5 = getMD5(imageUri.getBytes());
			BigInteger bi = new BigInteger(md5).abs();
			return bi.toString(RADIX);
		}

		private byte[] getMD5(byte[] data) {
			byte[] hash = null;
			try {
				MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
				digest.update(data);
				hash = digest.digest();
			} catch (NoSuchAlgorithmException e) {
				qfpay.wxshop.utils.T.e(e);
			}
			return hash;
		}
	}

	public static class HashCodeFileNameGenerator implements FileNameGenerator {
		@Override
		public String generate(String imageUri) {
			return String.valueOf(imageUri.hashCode());
		}
	}

	@TargetApi(HONEYCOMB)
	private static class ActivityManagerHoneycomb {
		static int getLargeMemoryClass(ActivityManager activityManager) {
			return activityManager.getLargeMemoryClass();
		}
	}

	@TargetApi(HONEYCOMB_MR1)
	private static class BitmapHoneycombMR1 {
		static int getByteCount(Bitmap bitmap) {
			return bitmap.getByteCount();
		}
	}
	
	public static enum ImageSizeForUrl {
		MAX, MID, MIN
	}
}
