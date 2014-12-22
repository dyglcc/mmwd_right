package qfpay.wxshop.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

public class BitmapUtil {
	/**
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/***
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int drawableId,
			int screenWidth, int screenHight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		InputStream stream = context.getResources().openRawResource(drawableId);
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
		return getBitmap(bitmap, screenWidth, screenHight);
	}

	/***
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Log.e("jj", "ͼƬ���" + w + ",screenWidth=" + screenWidth);
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/***
	 * 
	 * @param bm
	 * @param imgUrl
	 * @param quantity
	 */
	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;
	public final static String DIR = "/sdcard/hypers";


	public static Bitmap GetBitmap(String url) {
		Bitmap map  = null ;
		InputStream inputStream = null;
		if (url == null)
			return null;
		File file = new File(url);
		if(!file.exists()){
			return null;
		}
		try {
			inputStream = new FileInputStream(file);
			map = BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			T.e(e);
		}
		return map;
	}

	/***
	 * 
	 * @param url
	 * @return
	 */
	public static boolean Exist(String url) {
		File file = new File(DIR + url);
		return file.exists();
	}

//	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		bmp.compress(CompressFormat.PNG, 100, output);
////		if (needRecycle) {
////			bmp.recycle();
////		}
//		
//		byte[] result = output.toByteArray();
//		try {
//			output.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
	
//	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//
//        int i;
//        int j;
//        if (bmp.getHeight() > bmp.getWidth()) {
//            i = bmp.getWidth();
//            j = bmp.getWidth();
//        } else {
//            i = bmp.getHeight();
//            j = bmp.getHeight();
//        }
//        
//        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
//        Canvas localCanvas = new Canvas(localBitmap);
//        
//        while (true) {
//            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
////            if (needRecycle)
////                bmp.recycle();
//            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
//            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
//                    localByteArrayOutputStream);
//            localBitmap.recycle();
//            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
//            try {
//                localByteArrayOutputStream.close();
//                return arrayOfByte;
//            } catch (Exception e) {
//                //F.out(e);
//            }
//            i = bmp.getHeight();
//            j = bmp.getHeight();
//        }
//    }
	public static byte[] bmpToByteArrayTencent(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
//		if (needRecycle) {
//			bmp.recycle();
//		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		
		int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
        
        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        
        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
//            if (needRecycle)
//                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
            	e.printStackTrace();
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
		
	}
	
}
