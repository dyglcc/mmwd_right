/**
 * AnalyticsConfigLoader is a helper class used to load our analytics configuration file.
 * The configuration file is put in res/values/adhoc_analytics.xml in the customer's application.
 */
package com.adhoc.adhocsdk;

import android.content.Context;
import android.content.res.Resources;

public class AnalyticsConfigLoader {
	
	private final Context mContext;
	private final Resources mResource;
	private final String mPackageName;
	
	public AnalyticsConfigLoader(Context context) {
		mContext = context.getApplicationContext();
		mResource = mContext.getResources();
		mPackageName = mContext.getPackageName();
	}
	
	public String getString(String key) {
		int resId = mResource.getIdentifier(key, "string", mPackageName);
		if (resId == 0) return null;
		return mResource.getString(resId);
	}

	/*
	 * Default value is 0 if nothing exists.
	 */
	public int getInt(String key) {
		int resId = mResource.getIdentifier(key, "integer", mPackageName);
		if (resId == 0) return 0;
		try {
			return Integer.parseInt(mResource.getString(resId));
		} catch (NumberFormatException e){
			return 0;
		}
	}
	
	public boolean getBoolean(String key) {
		int resId = mResource.getIdentifier(key, "bool", mPackageName);
		if (resId == 0) return false;
		return "true".equalsIgnoreCase(mContext.getString(resId));
	}
}
