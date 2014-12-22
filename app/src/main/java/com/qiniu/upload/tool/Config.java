package com.qiniu.upload.tool;


/**
 * The Config class is a global configuration file for the sdk, used for serve
 * side only.
 */
public class Config {
	public static String USER_AGENT="qiniu java-sdk v6.0.0";
	
	/**
	 * You can get your accesskey from <a href="https://dev.qiniutek.com"
	 * target="blank"> https://dev.qiniutek.com </a>
	 */
	public static String ACCESS_KEY = "vdc6zqJGZLdU2z_lXXBJ_NLXK-M18XQ2Y7E5cyjb";

	/**
	 * You can get your accesskey from <a href="https://dev.qiniutek.com"
	 * target="blank"> https://dev.qiniutek.com </a>
	 */
	public static String SECRET_KEY = "iANjdgatLuHhmXVTE5ibQ5OnIfg8AqKbWzYI-HXr";

	public static String RS_HOST = "http://rs.qbox.me";

	public static String UP_HOST = "http://up.qbox.me";
	
	public static String RSF_HOST = "http://rsf.qbox.me";
	
	public static String bucketName = "mmwd-client";
	public static String domain = bucketName + ".qiniudn.com";

}
