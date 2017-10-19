package com.adhoc.utils;

import android.util.Log;

public class T {
	static String AppName = "ADHOC_SDK";

	public static Boolean DEBUG = true;

	public static void i(String string) {
		if (DEBUG) {
			Log.i(AppName, string);
		}
	}
	public static void i(String tag,String string) {
		if (DEBUG) {
			Log.i(tag, string);
		}
	}

	public static void w(String string) {
		if (DEBUG) {
			Log.e(AppName, string);
		}
	}
	public static void a(int num) {
		if (DEBUG) {
			Log.d(AppName, Integer.toString(num));
		}
	}
	public static void e(Exception exception) {
			Log.e(AppName, exception.toString());
	}

	public static void d(String msg) {
		if (DEBUG) {
			Log.d(AppName, msg);
		}
	}
}
