package qfpay.wxshop.data.net;

import android.os.Environment;

public class ConstValue {
	public static final String CACHE_KEY = "json_cache_key";

	public static final String PREFS_NAME = "qianfang.register";
	public static final String PREFS_MEMBER = "qianfang.member";

	public final static int SERVER_ERROR = 10;
	public final static int ACCESS_ERROR = 5;
	public final static int NET_ERROR = 2;
	public final static int REG_ERROR = 3;

	public final static int ASK_QUIT = 4;

	public static final String srcValue = "client";
	/* =========网络结果定义=============== */
	/** 网络操作成功 */
	public final static int NET_RETURN_SUCCESS = 1;
	/** 没有网络连接 */
	public final static int NET_RETURN_NO_CONNECT = 2;
	/** 连接超时 */
	public final static int NET_RETURN_TIMEOUT = 3;
	/** 已成功连接到Server,但返回不是200 */
	public final static int NET_RETURN_HTTP = 4;
	/** 在连接过程中，发生的非网络本身错误 */
	public final static int NET_RETURN_ERROR = 5;
	/** 用户取消 */
	public final static int NET_RETURN_CANCEL = 6;
	/** 参数不对 */
	public final static int NET_RETURN_PARAMETER = 7;
	/** 参数不对 */
	public final static int NET_CONNECT_SERVER_ERROR = 8;
	/** 参数不对 */
	public final static int NET_UNKNOWHOST_EXCEPTION = 9;
	public final static int NET_ACCESS_REFUSE = 10;

	
	
	//
	
	public final static int PAGE_SIZE = 10;
	/** 网络请求的结果key，在网络请求的回调中，本key说明请求的结果 */
	public final static String NET_RETURN = "response";
	/** 网络请求的结果是http内部错误的key */
	public final static String NET_RETURN_HTTP_ERROR = "http_error";
	/** 网络请求返回的数据的key */
	public final static String NET_RETURN_DATA = "response_data";

	/* =====================JSON解析结果======================== */
	/** JSON解析结果返回的key */
	public final static String JSON_RETURN = "json_return";
	/** JSON解析成功 */
	public final static int JSON_SUCCESS = 1;
	/** JSON解析失败 */
	public final static int JSON_FAILED = -1;

	/** 请求的url */
	public final static String REQUEST_URL = "request_url";
	/** 请求的json参数，此参数经过json编码，再转成字符 */
	public final static String REQUEST_PARAMETER = "json_parameter";

	public static final String HTTP_METHOD = "http_method";
	public static final String HTTP_POST = "post";
	public static final String HTTP_DELETE = "delete";
	public static final String HTTP_PUT = "put";
	public static final String HTTP_GET = "get";
	public static final String HTTP_POST_FILE = "POST_FILE";
	public static final String HTTP_POST_QINIU = "POST_QINIU";
	public static final String HTTP_POST_MULT_PARA = "POST_MULT_PARA_FILE";
	public static final String HTTPS_POST = "HttpsPost";
	/** 验证码 */
	public static final String VERIFYCODE_STATUS = "resultStr";
	public static final String MOBILE = "mobile";
	/** 会员查询 */
	public static final String USER = "user";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String PAY_POINTS = "pay_points";
	public static final String POINTS_VALUE = "points_value";

	// 身份证
	public static final String idnumber = "idnumber";
	// 收据显示名称
	public static final String nickname = "nickname";
	// 签约实体
	public static final String name = "name";
	// 经营地址（发货地址）
	public static final String address = "address";
	// 经营范围小类代码
	public static final String mcc = "mcc";
	// 省
	public static final String province = "province";

	// 省id
	public static final String provinceId = "provinceId";

	// 城市id
	public static final String cityId = "cityId";
	// bank id
	public static final String bankId = "bankId";

	// 城市
	public static final String city = "city";
	// 联系电话
	public static final String telephone = "telephone";
	// 手机号码
	public static final String mobile = "mobile";
	// 正确的手机号
	public static final String isRightPhone = "isRightPhone";

	// 验证码
	public static final String confirmCode = "confirmCode";

	// 正确的验证码
	public static final String isRightCode = "isRightCode";

	// password
	public static final String password = "password";
	// password
	public static final String passwordAgain = "password2";
	// (身份证正面)
	public static final String idphoto1 = "idphoto1";
	// (身份证反面)'
	public static final String idphoto2 = "idphoto2";
	// 证明
	public static final String requirement = "requirement";
	// base64(身份证合影)
	public static final String photo4 = "photo4";
	// 开户银行(mingcheng)
	public static final String bankname = "bankname";
	// 账户名称
	public static final String bankuser = "bankuser";
	// 银行账号
	public static final String bankaccount = "bankaccount";
	// 银行账号
	public static final String bankaccountAgain = "bankaccountAgain";
	// 开户行网点编号
	public static final String brchbank_code = "brchbank_code";
	// 单笔交易预估
	public static final String pertradeamount = "pertradeamount";
	// 月交易预估
	public static final String monthtradeamount = "monthtradeamount";
	// 客户端注册
	public static final String src = "src";
	// mcca text
	public static final String mcca = "mcca";

	// // mcca id
	// public static final String mccId = "mccId";
	// 配送地址
	public static final String logisticaddr = "logisticaddr";
	// 维度
	public static final String latitude = "latitude";
	// 经度
	public static final String longitude = "longitude";
	// 经营范围
	public static final String provision = "provision";

	public static final String expansion = ".jpg";

	public static final String selectBankPosition = "selectBankPosition";

	public static final int SCALE_BE = 9;
	public static final int MEMBER_SCALE_BE = 4;
	public static final int TIMEOUT = 30000;


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

	public static final String TODAY_TRADES_COUNT = "todayTradesCount";

	public static final String isAlreadReadTradeList = "isAlreadReadTradeList";

	public static final String MONEY_SIGN = "￥";

	public static final String MCCAJSON = "mccaJson";
	public static final String MCCJSON = "mccJson";

	public static final String perTrade = "perTrade";
	public static final String monthTrade = "monthTrade";
	public static final String tariff = "tariff";
	public static final String devicePrice = "devicePrice";
	public static final String umentparaReg = "umentparaReg";
	public static final String SUISUINIAN_ACTIVITY = "SSN_ACTIVITY";
	public static final String fengefu = " ";


	public static final Object RESPCD = "0000";

	public static final Object FENGXIANG = "http://fx.qfpay.com/1/classes/reg_trace";
	// public static final Object FENGXIANG_CERTIFICATE =
	// "http://fx.qfpay.asia/1/classes/test_upload";
	public static final Object FENGXIANG_CERTIFICATE = "http://fx.qfpay.com/1/classes/certificate";

	public static final String FENGXIANG_POST = "FXPOST";
	public static final String POST_FILE = "POSTFILE";
	public static final String POST_PIC_FILE = "POST_PIC_FILE";


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


	public static final int MAX_TRS_ACCOUNT_COUNT = 5;

	public static final String TEMP_USER = "QMM_U";

	public static final String TEMPNAME_FILE_NAME = "qmm";

	public static final String QQ_APP_ID = "100515599";

	// 替换
	public static final String APP_WEIBO_KEY = "1071841130";

	public static final String WEI_BO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog";

	// 替换为开发者REDIRECT_URL
	public static final String REDIRECT_URL = "http://www.qianmiaomiao.com";
	public static final String WEI_BO_SECRET = "43b298b69e409c5dcd44a541ab5950e9";
	public static final String WEIBO_DOMAIN = "https://api.weibo.com/oauth2/";

	public static final String ONLINE_PARA_SHARE_PIC_QQ_PLAT = "qq_share_text_pic";

	public static final String ONLINE_PARA_SHARE_CONTENT_WEIBO_PLAT = "weiboPlatform_Share";
	public static final String ONLINE_PARA_SHARE_PIC_WEIBO_PLAT = "qq_share_text_pic";
	public static final String ONLINE_APK_URLS = "update_apk_para";
	public static final String DOMAIN_URL = "domain_str";
	public static final String ONLINE_RATE = "rate_para";
	public static final String ONLINE_BANK_INFO_CHANGE = "change_bankinfo";
	public static final String ONLINE_UPLOAD_PIC = "upload_pic_para";
	public static final String ONLINE_SPLASH_IMG = "splash_image_para";

	public static final String FIRST_TIME_LOAD_BASE_RECORD = "load_first_time_record";
	public static final int FIRST_TIME_LOAD_BASE_Record_count = 500;
	public static final String FIRST_ACCESS_POSSETTING = "FIRST_ACCESS_POSSETTING";
	public static final long QMM_SINA_UID = 3699848493l;

	public static final String ERROR_MSG = "errorMsg";
	public static final String SUCCESS = "success";

	public static final String URL = "url";
	public static final String TITLE = "title";
	public static final int MSG_ERROR_FROM_MAINHANDLER= 67;
	public static final int PAGE_SIZE_MANAGE= 10;
	
	public static final String TOMORROW = "明天";
	public static final String TODAY = "今天";
	public static final String YESTERDAY = "昨天";
	
	private static final int UPLOAD_FILES = 4;
	public static final int CHECK_UPLOAD_STATUS = UPLOAD_FILES + 1;

	/**
	 * 线程ID
	 */
	public static final String THREAD_GROUP_EDITITEM = "edititemnetimgpl";
	public static final String THREAD_GROUP_BUYERSSHOW = "buyersshow";
	public static final long minute5 = 30 * 1000; 
	public static final long oneDay = 1* 24 * 60 * 60 * 1000; 
	public static final long twoDay = 2* 24 * 60 * 60 * 1000; 
	public static final long threeDay = 3* 24 * 60 * 60 * 1000;
	public static final long fourDay = 4* 24 * 60 * 60 * 1000;
	public static final int shareBigPic = 500;
	
	// 微信id
	public static final String APP_ID = "wx8fa0f7931d181217";
	public static final boolean circle_share = true;
	public static final boolean friend_share = false;

	public static final String APP_TYPE = "101";
	
	// ga 统计
	public static final String android_mmwdapp_home_wcfriend = "android_mmwdapp_home_wcfriend";
	public static final String android_mmwdapp_home_wctimeline = "android_mmwdapp_home_wctimeline";
	public static final String android_mmwdapp_manageshare_wctimeline = "android_mmwdapp_manageshare_wctimeline";
	public static final String android_mmwdapp_manageshare_wcfriend = "android_mmwdapp_manageshare_wcfriend";
	public static final String android_mmwdapp_home_ = "android_mmwdapp_home_";
	public static final String android_mmwdapp_manageshare_ = "android_mmwdapp_manageshare_";
	public static final String android_mmwdapp_postshare_ = "android_mmwdapp_postshare_";
	public static final String android_mmwdapp_managepreview_ = "android_mmwdapp_managepreview_";
	public static final String android_mmwdapp_postpreview_ = "android_mmwdapp_postpreview_";
	public static final String android_mmwdapp_copyurl_ = "android_mmwdapp_copyurl_";
//	public static final String android_mmwdapp_majiaxiu = "android_mmwdapp_maijiaxiu";
	public static final String gaSrcfrom = "gaSrcfrom";

	// 埋点服务需要的字段
	public static final String COLLECT_OS = "Android";
	public static final String COLLECT_APP = "mmwdapp";

//	1 android_mmwdapp_home_: android 首页上面的按钮，
//	2、android_mmwdapp_manageshare_wctimeline:android店铺管理页分享到朋友圈（另外的sns还包括sinaweibo，tecentweibo,qzone，wcfriend）
//	3、android_mmwdapp_postshare_timeline: android 发布成功页分享到朋友圈（另外的sns还包括sinaweibo，tecentweibo,qzone，wcfriend）
//	4、android_mmwdapp_managepreview_: android 店铺管理页预览按钮
//	5、android_mmwdapp_postpreview_: android 发布成功页预览按钮
//	6、如果是单独打包的应用（例如喵喵购），写为android_mmgapp
	
	
	public static final String THREAD_CANCELABLE = "cancelable";
	
	public static final String 	SHARE_NAME_FINDMIAO = "发现个喵";
}
