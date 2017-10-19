/**
 * BuildParameters class is used to build a JSON object containing client's information such as
 * ClientId, App's TrackingId, timestamp of event, Event type.
 */

package com.adhoc.adhocsdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.adhoc.utils.T;


public class BuildParameters {
	private static final String TAG = BuildParameters.class.getSimpleName();
	private static BuildParameters mInstance;
	
	private String mClientId;
	private Context mContext;
	private String mTrackingId;
	private JSONObject mSummary;
	private boolean mNeedTrackActivity;
	private AnalyticsConfigLoader mConfigLoader;
	private JSONObject customPara;

	public static BuildParameters getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BuildParameters(context);
		}
		return mInstance;
	}

	public void makeCustomPara(HashMap<String,String> map) {

		customPara = new JSONObject();
		for(Entry<String,String> entry:map.entrySet()){
			try {
				customPara.put(entry.getKey(),entry.getValue());
			} catch (JSONException e) {
				T.e(e);
			}
		}

	}
	
	private BuildParameters(Context context) {
		mContext = context.getApplicationContext();
		mConfigLoader = new AnalyticsConfigLoader(mContext);
		getBasicParameters();
	}
	
	private void getBasicParameters() {
		mClientId = AdhocClientIDHandler.getInstance(mContext).getClientId();
		
		// Gets Configurations from adhoc_analytics.xml too.
        // TODO: ADHOC_APP_TRACKING_ID mapping to APP_ID
		mTrackingId = mConfigLoader.getString(AdhocConstants.APP_KEY);
		mNeedTrackActivity = mConfigLoader.getBoolean(AdhocConstants.ADHOC_TRACK_ACTIVITY);

        try {
            mSummary = ParameterUtils.getSummary(mContext);
        } catch (JSONException e) {
            mSummary = null;
            T.e(e);
        }
        T.d(" aptext id : " + mTrackingId);
	}
	
	public boolean needTrackActivity() {
		return mNeedTrackActivity;
	}

    public String clientId() {
        return mClientId;
    }

	public String trackingId() {
		return mTrackingId;
	}
	
	/*
	 * Load all <key-value> pairs here.
	 * 1) All given <key-value> pairs in the map.
	 * 2) Basic information there.
	 * 
	 * Returns null if there is no Client ID or Adhoc App ID.
	 */
	public JSONObject buildParametersForServer(Map<String, Object> keyValue) {
		// If the precondition does not exists.
		if (mClientId == null) return null;
		
		JSONObject json = new JSONObject();
		
		// 1) All given <key-value> pairs in the map.
		boolean hasTimestamp = false;
		boolean hasClientId = false;
		boolean hasAppTrackId = false;
		boolean hasReferrer = false;
		Iterator<Entry<String, Object>> it = keyValue.entrySet().iterator();
		while (it.hasNext()) {
	    	@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry)it.next();
	    	try {
	    		String key = (String) pairs.getKey();
	    		if (key.equals(KeyFields.TIMESTAMP)) {
	    			hasTimestamp = true;
	    		}
	    		if (key.equals(KeyFields.CLIENT_ID)) {
	    			hasClientId = true;
	    		}
	    		if (key.equals(KeyFields.ADHOC_APP_TRACK_ID)) {
	    			hasAppTrackId = true;
	    		}
	    		if (key.equals(KeyFields.INSTALL_REFERRER)) {
	    			hasReferrer = true;
	    		}
 				json.put((String)pairs.getKey(), pairs.getValue());
			} catch (JSONException e) {
				Log.e(TAG, "JSONException when filling general key-value parameters.");
				return null;
			}
	    }
	    
		// 2) Basic information.
		try {
			if (!hasTimestamp) {
				json.put(KeyFields.TIMESTAMP, System.currentTimeMillis());
			}
			if (!hasClientId) {
				json.put(KeyFields.CLIENT_ID, mClientId);
			}
			if (!hasAppTrackId) {
				json.put(KeyFields.ADHOC_APP_TRACK_ID, mTrackingId);
			}
//			if (!hasReferrer && mFileHandler != null) {
//				String utmInfo = mFileHandler.getUtmInfo();
//				if (utmInfo != null) {
//					json.put(KeyFields.INSTALL_REFERRER, utmInfo);
//				}
//			}
			// Includes other information.
			if (mSummary != null) {
				json.put(KeyFields.SUMMARY, mSummary);
			}
			if(customPara!=null && customPara.length()>0){

				json.put(AdhocConstants.CUSTOM_PARA,customPara);
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException when filling basic key-value parameters.");
			return null;
		}
	    
	    return json;
	}
}
