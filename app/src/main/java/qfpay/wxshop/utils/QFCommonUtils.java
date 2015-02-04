package qfpay.wxshop.utils;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.CollectNetService;
import qfpay.wxshop.dialogs.BaseDialogFragment;
import qfpay.wxshop.dialogs.SimpleDialogFragment;
import qfpay.wxshop.getui.ImageUtils.ImageSizeForUrl;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View.OnClickListener;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

public class QFCommonUtils {
	private static final int QINIU_IMGSIZE_MAX = 1000;
	private static final int QINIU_IMGSIZE_MID = 500;
	private static final int QINIU_IMGSIZE_MIN = 100;
	
	public static String generateQiniuUrl(String preUrl, ImageSizeForUrl size) {
		switch (size) {
		case MAX:
			return generateQiniuUrl(preUrl, QINIU_IMGSIZE_MAX, QINIU_IMGSIZE_MAX);
		case MID:
			return generateQiniuUrl(preUrl, QINIU_IMGSIZE_MID, QINIU_IMGSIZE_MID);
		case MIN:
			return generateQiniuUrl(preUrl, QINIU_IMGSIZE_MIN, QINIU_IMGSIZE_MIN);
		default:
			return preUrl;
		}
	}
	
	public static String generateQiniuUrl(String preUrl, int width, int height) {
		if (preUrl.contains("?imageView2/1/w/")) {
			return preUrl;
		}
		String s = "%s?imageView2/1/w/%d/h/%d";
		return String.format(s, preUrl, width, height);
	}
	
	public static String getAppVersionString(Context context) {
		String str = "";
		try {
			str = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			T.e(e1);
		}
		return str;
	}
	
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;
        return height;
	}
	
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static int getAnimationDuration(AnimationDrawable ani) {
		int duration = 0;
		for(int i=0;i<ani.getNumberOfFrames();i++){
            duration += ani.getDuration(i);
        }
		return duration - 50;
	}
	
	public static Calendar string2Calendar(String dataStr, String formatStr) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		Date date = dateFormat.parse(dataStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	public static int bytesToInt(byte[] bytes) {
		int addr = bytes[0] & 0xFF;
		addr |= ((bytes[1] << 8) & 0xFF00);
		addr |= ((bytes[2] << 16) & 0xFF0000);
		addr |= ((bytes[3] << 24) & 0xFF000000);
		return addr;
	}
	
	/** 
	 * 对字符串进行MD5加密
	 */
	public static String encodeByMD5ToString(byte[] origin) {
		if (origin != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] results = md.digest(origin);
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase(Locale.getDefault());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}
	
	public static int encodeByMD5ToInt(byte[] origin) {
		if (origin != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] results = md.digest(origin);
				return bytesToInt(results);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * @param 字节数组
	 * @return 十六进制字符串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	// 十六进制下数字到字符的映射数组
	public final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/** 将一个字节转化成十六进制形式的字符串 */
	public static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	
	private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
    // 32 bytes from sha-256 -> 64 hex chars.
    private static final char[] SHA_256_CHARS = new char[64];
    // 20 bytes from sha-1 -> 40 chars.
    private static final char[] SHA_1_CHARS = new char[40];
	
	/**
     * Returns the hex string of the given byte array representing a SHA256 hash.
     */
    public static String sha256BytesToHex(byte[] bytes) {
        return bytesToHex(bytes, SHA_256_CHARS);
    }

    /**
     * Returns the hex string of the given byte array representing a SHA1 hash.
     */
    public static String sha1BytesToHex(byte[] bytes) {
        return bytesToHex(bytes, SHA_1_CHARS);
    }

    // Taken from:
    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java/9655275#9655275
    private static String bytesToHex(byte[] bytes, char[] hexChars) {
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
	
	public static DialogFragment showDialog(FragmentActivity context, String titleStr, String msgStr, 
			String negativeButtonStr, String positiveStr, boolean cancleAble, int requestCode, OnClickListener positiveListener) {
		BaseDialogFragment dialogFragment = (BaseDialogFragment) SimpleDialogFragment.createBuilder(context, context.getSupportFragmentManager())
				.setTitle(titleStr).setMessage(msgStr).setNegativeButtonText(negativeButtonStr).setPositiveButtonText(positiveStr)
				.setCancelable(cancleAble).setRequestCode(requestCode).setPositiveClick(positiveListener).show();
		return dialogFragment;
	}
	
	public static DialogFragment showDialog(FragmentActivity context, String title, String msg, OnClickListener positiveListener) {
		return showDialog(context, title, msg, context.getResources().getString(R.string.dialog_btn_negative), 
				context.getResources().getString(R.string.dialog_btn_positive), false, -1, positiveListener);
	}
	public static DialogFragment showDialogCancelBehalf(FragmentActivity context, String title, String msg, OnClickListener positiveListener) {
		return showDialog(context, title, msg, "继续保留",
				"确定", false, -1, positiveListener);
	}

	public static DialogFragment showSingleButtonDialog(FragmentActivity context, String title, String msg, OnClickListener positiveListener) {
		return showDialog(context, title, msg, null, context.getResources().getString(R.string.dialog_btn_positive), false, -1, positiveListener);
	}

	public static void collect(final String type, final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					RestAdapter ra = new RestAdapter.Builder()
							.setEndpoint("http://mmwd.me")
							.build();
					CollectNetService service = ra.create(CollectNetService.class);
					service.collectData(
							ConstValue.COLLECT_OS,
							ConstValue.COLLECT_APP,
							WxShopApplication.dataEngine.getUserId(),
							type,
							System.currentTimeMillis() + "",
							Utils.getAppVersionString(context));
				} catch (Exception e) {
					T.e(e);
				}
			}
		}).start();
	}

    public static boolean isFirstLaunch(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getBoolean("FIRST_LAUNCHER");
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
