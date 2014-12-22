package qfpay.wxshop.image.processer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.getui.ImageUtils;
import qfpay.wxshop.image.ImageWrapper;
import qfpay.wxshop.image.processer.cache.DiskCache;
import qfpay.wxshop.image.processer.cache.DiskLruCacheWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class ImageDecoder {
	public static final int CACHE_SIZE  = 20 * 1024 * 1024;
	
	private ImageWrapper image;
	
	public File decodeImage(ImageWrapper image) {
		this.image = image;
		DiskCache cache = DiskLruCacheWrapper.get(generatCacheDir(), CACHE_SIZE);
		if (cache.get(image.getKey()) == null) {
			cache.put(image.getKey(), new ImageWriter(compress()));
		}
		return cache.get(image.getKey());
	}
	
	private Bitmap compress() {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;
		BitmapFactory.decodeFile(image.getPath(), options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = ImageUtils.computeSampleSize(options, image.getWidth(), image.getWidth() * image.getHeight());
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		return BitmapFactory.decodeFile(image.getPath(), options);
	}
	
	private File generatCacheDir() {
		return new File(ConstValue.getThumbnailDir());
	}
	
	class ImageWriter implements DiskCache.Writer {
        private final WeakReference<Bitmap> data;

        public ImageWriter(Bitmap data) {
            this.data = new WeakReference<Bitmap>(data);
        }

        @Override
        public boolean write(File file) {
            boolean success = false;
            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(file));
                if (data == null || data.get() == null) {
					return false;
				}
                return data.get().compress(CompressFormat.JPEG, image.getQuality(), os);
            } catch (FileNotFoundException e) {
            	e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
                }
            }
            return success;
        }
    }
}
