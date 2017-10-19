package com.adhoc.adhocsdk;

import android.os.Environment;

public class ConstValue {
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

	public static String getPICTURE_DIR() {
		return getSdcardPath() + "/qianfang/" + "pictures/";
	}
	
	public static String getThumbnailDir() {
		return getSdcardPath() + "/qianfang/" + "thumbnail/";
	}
	
	public static String getDownLoadDir() {
		return getSdcardPath() + "/qianfang/" + "download/";
	}

	public static String getMember_Dir() {
		return getPICTURE_DIR() + "member/";
	}

	public static String getMember_cache() {
		return getPICTURE_DIR() + "member/cache";
	}
	public static String getWebView_cache() {
		return getPICTURE_DIR() + "webview/cache";
	}
}
