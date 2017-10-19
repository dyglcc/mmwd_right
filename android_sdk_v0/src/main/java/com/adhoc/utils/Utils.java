package com.adhoc.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import com.adhoc.adhocsdk.SharePrefHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {


	public static boolean isCanConnectionNetWork(Context context) {
		if (context == null) {
			return true;
		}
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager == null){
			return false;
		}
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null
				|| networkInfo.isConnectedOrConnecting() == false) {
			return false;
		}
		return true;
	}


	public static String frontZeroFill(int sourceStr, int formatLength) {
		/*
		 * 0 指前面补充零 formatLength 字符总长度为 formatLength d 代表为正数。
		 */
		String newString = String.format("%0" + formatLength + "d", sourceStr);
		return newString;
	}


	public static String getNetworkInfo(Context context) {
		if (context == null) {
			return "Unknown";
		}

		String typeName = "Unknown";
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			typeName = info.getTypeName().toLowerCase();
			if (typeName.equals("wifi")) {
			} else {
				typeName = info.getExtraInfo().toLowerCase();
				// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
			}
			if (typeName == null || typeName.equals("")) {
				typeName = "Unknown";
			}

		} catch (Exception e) {
			T.e(e);
		}
		return typeName;
	}


	// 从share中得到boolean
	public static boolean getBooleanValue(SharedPreferences share, String key) {
		return share.getBoolean(key, false);
	}

	// 从share中得到整形数
	public static int getNums(SharedPreferences share, String tradeSuccessCount) {
		return share.getInt(tradeSuccessCount, 0);
	}

	// 保存boolean值
	public static void saveBooleanShareData(SharedPreferences share,
			String key, boolean value) {
		Editor editor = share.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}


	public static String getMd5(String m_szLongID) {
		// TODO Auto-generated method stub
		String m_szUniqueID = "";
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();
		// create a hex string
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		} // hex string to uppercase
		return m_szUniqueID;
	}

	public static SharedPreferences getSharePreference(Context context) {

		return context.getSharedPreferences(SharePrefHandler.SHARED_PREFERENCE,
				0);
	}

	/**
	 * save share中的String value
	 * */
	public static void saveStringShareData(SharedPreferences share, String key,
			String value) {
		Editor editor = share.edit();
		editor.putString(key, value);
		editor.commit();
        T.i("保存到本地信息：key & value "+ key +"&" + value);
	}

	/**
	 * 得到 share中的String value
	 * */
	public static String getStringShareData(SharedPreferences share, String key) {
		return share.getString(key, "");
	}


	public static String getDataFromNative(String fileName, Context context) {

		ByteArrayOutputStream baos = null;

		BufferedInputStream bis = null;

		String returnString = null;
		// 初始化错误代码
		try {

			baos = new ByteArrayOutputStream();
			bis = new BufferedInputStream(context.getAssets().open(fileName));
			int CHUNK_SIZE = 32 * 1024;
			byte[] mFileIOBuffer = new byte[CHUNK_SIZE];
			while (true) {
				int bytesRead = bis.read(mFileIOBuffer);

				if (bytesRead < 0) {
					break;
				}

				baos.write(mFileIOBuffer, 0, bytesRead);
			}
			byte[] buffer = baos.toByteArray();

			returnString = new String(buffer);

			baos.flush();
			baos.close();
			baos = null;

			bis.close();
			bis = null;

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (Exception e) {

				}
			}

			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {

				}
			}

		}

		return returnString;
	}




	// 获取AppKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	public static void hideSoftKeyboard(Activity act) {
		if (act == null) {
			return;
		}
		InputMethodManager manager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager == null) {
			return;
		}
		if (act.getCurrentFocus() == null) {
			return;
		}
		if (act.getCurrentFocus().getWindowToken() == null) {
			return;
		}
		manager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
				0);
	}


	public static String getOSVerison(Context activity) {
		return Build.VERSION.RELEASE;
	}

	public static String getAppVersionString(Context context) {
		if (context == null) {
			return "";
		}
		String str = null;
		try {
			str = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			T.e(e1);
		}
		return str;
	}

	public static String getDeviceName() {
		return Build.MODEL;
	}

	public static String getDeviceID(Context context) {
		if (context == null) {
			return "";
		}
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getDeviceId();
	}




	/**
	 * 图片压缩算法
	 * 
	 * @param options
	 *            Bitmap.Options
	 * @param minSideLength
	 *            最小显示区
	 * @param maxNumOfPixels
	 *            你想要的宽度 * 你想要的高度
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
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

	public static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
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

	/**
	 * 得到图片角度
	 * */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			T.e(e);
		}
		return degree;
	}


	public static String getUrlRegex() {
		String regex = "(https?|ftp|mms)://([A-z0-9]+[_-]?[A-z0-9]+.)*[A-z0-9]+-?[A-z0-9]+.[A-z]{2,}(/.*)*/?";
		return regex;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		if (context == null) {
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		if (context == null) {
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

    /**
     * 取sdcard的路径
     *
     * @return 如果Sdcard没有，或者不可写，返回null
     */
    public static String getSdcardPath() {
        if (haveSdcard()) {
            return Environment.getExternalStorageDirectory()
                    .getPath();
        } else {
            return null;
        }
    }
    /**
     * 是否有SDCARD
     *
     * @return 有SDCARD,返回true,否则返回false
     */
    public static boolean haveSdcard() {
        return (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED));
    }

    public static String getAdhocdir() {
        if(haveSdcard()){
            File file = new File(getSdcardPath()+"/adhoc/");
            if(file.exists()){
                return file.getAbsolutePath();
            }else{
                file.mkdirs();
            }
        }else{
            T.e(new Exception("没有sdcard"));
        }
        return null;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        long t1 = System.currentTimeMillis();

        ActivityManager activityManager = (ActivityManager)context. getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        System.out.println(System.currentTimeMillis() - t1);

        return false;
    }

}
