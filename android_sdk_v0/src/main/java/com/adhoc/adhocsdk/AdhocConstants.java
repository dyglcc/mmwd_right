package com.adhoc.adhocsdk;

public final class AdhocConstants {
	private AdhocConstants() {}
	
	public static final String APP_KEY = "AdhocAppKey";
	public static final String ADHOC_TRACK_ACTIVITY = "adhoc_autoActivityTracking";
	
	// Adhoc SDK server URL. The port is generated from telephone keypad.
	public static final String ADHOC_SERVER_URL = "http://114.215.143.131:23462";
//	public static final String ADHOC_SERVER_URL = "http://tracking.appadhoc.com:23462";
//    public static final String ADHOC_SERVER_GETFLAGS = "http://api.appadhoc.com/optimizer/api/getflags.php";

        public static final String ADHOC_SERVER_GETFLAGS = "http://52.74.103.129/optimizer/api/getflags.php";
    public static final String ADHOC_PIC_SERVER = "http://api.appadhoc.com/optimizer/api/screenshot.php";

	// SDK stored files.
	public static final String FILE_UTM_INFO = "ADHOC_FILE_UTM_INFO";
	public static final String FILE_CLIENT_ID = "ADHOC_CLIENT_ID";
	public static final String SHARE_PREF_CLIENT_ID = "ADHOC_CLIENT_ID";

	public static final String COARSE_LAST_SENT_TIMESTAMP = "COARSE_LAST_SENT_TIMESTAMP";

	// Key-value's key for summary information.
	public static final String ANDROID_ID = "android_id";
	public static final String APP_VERSION = "app_version";
	public static final String COUNTRY = "country";
	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_OS_NAME = "device_os_name";
	public static final String DISPLAY_WIDTH = "display_width";
	public static final String DISPLAY_HEIGHT = "display_height";
	public static final String EMAIL = "email";
	public static final String FACEBOOK_ATTR_ID = "facebook_attr_id";
	public static final String LANGUAGE = "language";
	public static final String NETWORK_STATE = "network_state";
	// The platforms (Android) applied to this Adhoc SDK.
	public static final String OS_VERSION = "os_version";
	public static final String OS_PLATFORM = "OS";
	public static final String PACKAGE_NAME = "package_name";
	public static final String PHONE_ID = "phone_id";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String SCREEN_SIZE = "screen_size";
	// This is Google's Android SDK.
	public static final String SDK_VERSION = "sdk_version";
	public static final String WIFI_MAC = "wifi_mac";
	
	// SDK Platform. It can be iOS or customized android etc.
	public static final String ANDROID_PLATFORM = "google_android";
	
	// Facebook AttributionID URI.
	public static final String FACEBOOK_ATTR_ID_URI = "content://com.facebook.katana.provider.AttributionIdProvider";

	// Permissions
	public static final String P_ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";
	public static final String P_ACCESS_WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
	public static final String P_GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
	public static final String P_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
	
	// Network State
	public static final String WIFI_CONNECTED = "WIFI_CONNECTED";
	public static final String MOBILE_CONNECTED = "MOBILE_CONNECTED";
	public static final String NETWORK_UNCONNECTED = "NETWORK_UNCONNECTED";
	public static final String NETWORK_STATE_UNKNOWN = "NETWORK_STATE_UNKNOWN";

    // share prefrence flags key
    public static final String PREFS_ABTEST_FLAGS = "adhoc_abtest_flags" ;
    // share prefrence flags key
    public static final String ADHOC_FILE_PATH = "Adhoc" ;
    public static final String ADHOC_SCREEN_SHOT_FILE_SUFFIX = ".jpg" ;
    // screen shot quality
    public static final int QUALITY_SCREEN_SHOT= 10 ;

    // scanner 的package name
    public static final String SCANNER_PACKAGE_NAME = "com.example.scannertest" ;
    // diff flag
    public static final String FLAG_CHANGED = "flag_changed" ;
	// custom para
    public static final String CUSTOM_PARA = "custom_para" ;

}
