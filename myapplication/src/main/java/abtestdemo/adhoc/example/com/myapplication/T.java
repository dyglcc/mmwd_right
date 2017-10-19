package abtestdemo.adhoc.example.com.myapplication;

import android.util.Log;

public class T {
	static String AppName = "wd";

	public static Boolean isTesting = true;

	public static void i(String string) {
		if (isTesting) {
			Log.i(AppName, string);
		}
	}
	public static void i(String logName,String string) {
		if (isTesting) {
			Log.i(logName, string);
		}
	}

	public static void w(String string) {
		if (isTesting) {
			Log.e(AppName, string);
		}
	}

	public static void d(String string) {
		if (isTesting) {
			Log.d(AppName, string);
		}
	}

	public static void a(int num) {
		if (isTesting) {
			Log.d(AppName, Integer.toString(num));
		}
	}
	public static void e(Exception exception) {
		if (isTesting) {
			Log.e(AppName, exception.toString());
		}
	}
	
	public static void d(String tag, String msg) {
		if (isTesting) {
			Log.d(tag, msg);
		}
	}
}
