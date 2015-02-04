package qfpay.wxshop.config;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Context;

public class WDConfig {
	private static WDConfig INSTANCE = null;

	public static final int RETRY_MAX_COUNT = 3;

	private WDConfig() {

	}

	public static WDConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WDConfig();
		}
		return INSTANCE;
	}

	public void init(String url, int type) {

		this.mPushServer = type;
		switch (type) {
		case 2:
			this.PIC_SERVER = "http://172.100.102.153:8181/";
			// huoyuan url
			WD_URL_HUO_YUAN = url.replaceAll("qmm.la", "mmwd.me");
			break;
		case 1:
			PIC_SERVER = "http://o2.qfpay.com/";
			WD_URL_HUO_YUAN = "http://bj.mmwd.me/";
			break;
		case -1:
			PIC_SERVER = "http://o2.qfpay.com/";
			WD_URL_HUO_YUAN = "http://bj.mmwd.me/";
			break;
		case 0:
			PIC_SERVER = "http://o2.qfpay.com/";
			WD_URL_HUO_YUAN = "http://bj.mmwd.me/";
			break;
		default:
			PIC_SERVER = "http://o2.qfpay.com/";
			WD_URL_HUO_YUAN = "http://mmwd.me/";
			break;
		}
		this.WD_URL = url;
		if (url == null || url.equals("")) {
//			 WD_URL = "http://shiyong.local.qmm.la/";
			WD_URL = "http://wx.qfpay.com/";
//			WD_URL = "http://172.100.101.150:8989/";
			 
		}
	}

	/**
	 * 图片服务器
	 * 
	 * */
	public String PIC_SERVER = "http://o2.qfpay.com/";

	/**
	 * 官网
	 * 
	 * */
	public static final String HOME_URL = "http://www.qianmiaomiao.com/";
	/**
	 * 喵喵微店访问地址
	 * */
	// public String WD_URL = "http://172.100.101.150:8400/";
//	 public String WD_URL = "http://172.100.101.150:8989/";
//	 public String WD_URL = "http://shiyong.local.qmm.la/";
	public String WD_URL = "http://0.wx.qfpay.com/";
	public int mPushServer;

	public String WD_URL_HUO_YUAN = "http://mmwd.me/";

	public String getShopUrl() {
		return "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/shop/";
	}

    /**
     * 社交API地址
     */
    public static String SOCIAL_URL = "http://bf.mmwd.me/";
	// String WD_URL = "http://1.wx.qfpay.com/";// test

	public String getRequestInfo(String get) {
//		if(get.equals(ConstValue.HTTP_GET)){
//			return "";
//		}
//		String devicename = Utils.getDeviceName();
//		if(devicename ==null){
//			devicename = "";
//		}
//		devicename = devicename.replaceAll(" ", "");
//		return "?osver=" + Utils.getOSVerison(WxShopApplication.app)
//				+ "&appver=" + Utils.getAppVersionString(WxShopApplication.app)
//				+ "&platform=android&device="+devicename;
		return "";
	}

	/**
	 * 订单列表
	 * */
	public String getOrderListURL(String get) {
		return WD_URL + "qmm/wd/app/order_list";
	}

	/**
	 * 统计
	 * */
	public String getStatURL(Context context) {

		return WD_URL + "qmm/wd/app/shop_statistics?" + "appVersion="
				+ Utils.getAppVersionString(context) + "&osversion="
				+ Utils.getOSVerison(context);
	}
	/**
	 * 粉丝货源
	 * */
	public String getFansLoadUrl(Context context) {

		return "http://"+WxShopApplication.app.getDomainMMWDUrl() + "/h5/b-fshy.html?" + "appVersion="
				+ Utils.getAppVersionString(context) + "&osversion="
				+ Utils.getOSVerison(context);
	}


	public String getIncomeURL(String get) {
		return WD_URL + "qmm/wd/app/income"+getRequestInfo(get);
	}

	/**
	 * 验证手机号
	 * 
	 * */
	public String getRegVerifyCodeURL(String get) {
		return WD_URL + "qmm/wd/app/mobile_verify"+getRequestInfo(get);
	}

	/**
	 * 开通收款
	 * 
	 * */
	public String getRegPaymentInfo(String get) {
		return WD_URL + "qmm/wd/app/user_payment"+getRequestInfo(get);
	}

	/**
	 * 注册手机号
	 * 
	 * */
	public String getRegRegisterURL(String get) {
		return WD_URL + "qmm/wd/app/reg"+getRequestInfo(get);
	}

	/**
	 * 更改店铺信息
	 * 
	 * */
	public String getRegUpdateShopInfoURL(String get) {
		return WD_URL + "qmm/wd/app/update_shop"+getRequestInfo(get);
	}

	/**
	 * 
	 * 登录
	 * */
	public String getRegLoginURL(String get) {
		return WD_URL + "qmm/wd/app/login"+getRequestInfo(get);
	}

	// -----------------------------------------------------------------------
	/**
	 * 官网地址
	 * */
	public static final String QMM_URL = "http://www.qianmiaomiao.com/";

	/**
	 * 展示店铺URL
	 * */
	public static final String SHOW_SHOP_ADDR = "http://"
			+ WxShopApplication.app.getDomainMMWDUrl() + "/shop/";

	/**
	 * 帮助
	 * */
	public static final String HELP_URL = QMM_URL + "how";
	/**
	 * 
	 * 美甲他人店铺
	 * */
	public static final String URL_ShopMeijia = HOME_URL + "weidian3";
	/**
	 * 
	 * 包
	 * */
	public static final String URL_ShopBags = HOME_URL + "weidian2";
	/**
	 * 
	 * 他人服装
	 * 
	 * */
	public static final String URL_ShopClothes = HOME_URL + "weidian1";

	/**
	 * 
	 * 上传图片
	 * 
	 * */
	public String getQFUploadServer(String get) {
		return PIC_SERVER + "util/v1/uploadfile"+getRequestInfo(get);
	}

	/**
	 * 
	 * error page session time out qmm/wd/app/item_detail
	 * */
	public static final String ERROR_PAGE = "qmm/wd/app/timeout";
	public static final String ACTION_DETAIL = "/qmm/wd/app/item_detail/";

	public static final String ACTION_EDIT_1 = "/qmm/wd/app/new"; // 上线之后去掉
	public static final String ACTION_EDIT_2 = "/qmm/wd/app/newitem";// 上线之后只用这一个


	public String pushBindServer() {
//		Toaster.l(WxShopApplication.app, " " + mPushServer);
		switch (mPushServer) {
		case 0:
			return "http://0.openapi2.qfpay.com/app/v1/getuibind"+getRequestInfo("");
		case 1:
			return "http://1.openapi2.qfpay.com/app/v1/getuibind"+getRequestInfo("");
		case 2:
			return "http://172.100.101.167:8181/app/v1/getuibind"+getRequestInfo("");
		}
		return "http://0.openapi2.qfpay.com/app/v1/getuibind"+getRequestInfo("");
	}

	/**
	 * 
	 * 论坛
	 * */
	public static final String URL_forum = "http://mb.qmm.la/?";

	/**
	 * 样例展示
	 * */
	public static final String URL_DEMO = "http://www.qianmiaomiao.com/demo/";

	public static final String ABOUT_US = "http://www.qianmiaomiao.com/w_about/";
	/**
	 * 修改密码地址
	 */
	public static final String CHANGE_PWD_URL = "https://qfpay.com/mobile/account/resetpassword";
	/**
	 * 攻略地址
	 */
	public static final String GONG_LUE = "http://m.qfpay.com/gonglue/";
	/**
	 * 更多应用地址
	 */
	public static final String MORE_APP = "http://www.qianmiaomiao.com/pinkan";
	/**
	 * 更改银行信息
	 */
	public static final String CHANGE_BANK_ACCOUNT = "http://www.qianmiaomiao.com/changeinfo";
	/**
	 * 费率相关了解更多
	 */
	public static final String RateLearnMore = "http://www.qianmiaomiao.com/learnmore";
	/**
	 * 默认头像地址
	 */
	public static final String DEFAULT_AVATOR = "http://imgstore01.qiniudn.com/defaul_tavator1.png";
	/**
	 * 默认微信收款icon
	 */
	public static final String WEIXIN_COLLECT_MONEY = "http://imgstore01.qiniudn.com/weixin_180x174.png";
	/**
	 * 默认微信收款icon
	 */
	public static final String MAIJIAXIU_SHARE_PIC = "http://imgstore01.qiniudn.com/xiu0705.png";

	/**
	 * 微信收款 172.100.101.150:8080
	 * */
	public String getCollectMoney(String get) {
		return WD_URL + "qmm/wd/app/newitem"+getRequestInfo(get);
	}

	/**
	 * 店铺公告
	 * */
	public String getNoticeText(String get) {
		return WD_URL + "qmm/wd/app/shop_intro"+getRequestInfo(get);
	}

	/**
	 * shopname 编辑
	 * */
	public String getShopNameUpdateURL(String get) {
		return WD_URL + "qmm/wd/app/api/shop/shopname"+getRequestInfo(get);
	}

	/**
	 * 微信号 编辑
	 * */
	public String getWeixinHaoUpdateURl(String get) {
		return WD_URL + "qmm/wd/app/api/shop/weixinid"+getRequestInfo(get);
	}

	/**
	 * 店铺头像 编辑
	 * */
	public String getAvatorURl(String get) {
		return WD_URL + "qmm/wd/app/api/shop/avatar"+getRequestInfo(get);
	}

	/**
	 * 每日活动
	 * */
	public final static String PROMO_URL = "http://www.qianmiaomiao.com/promo/";
	/**
	 * 喵喵购
	 * */
	public final static String MIAOMIAOGOU = "http://"
			+ WxShopApplication.app.getDomainMMWDUrl()
			+ "/h5/mmg.html?ga_medium=android_b_menu_self";
	/**
	 * 发现个喵
	 * */
	public final static String FAXIANGEMIAO = "http://www.qianmiaomiao.com/explore/?ga_medium=android_b_menu_self";

	public static final String DEFAULT_MMWD_URL = "mmwd.me";

	/**
	 * 排行榜
	 * */
	public String getPaihangbang() {
		return "http://" + WxShopApplication.app.getDomainMMWDUrl()
				+ "/h5/top.html?appversion="
				+ Utils.getAppVersionString(WxShopApplication.app)
				+ "&ga_medium=android_mmwdapp_communicate";
	}

	/**
	 * 得到商品列表
	 * @param httpGet 
	 * */
	public String getManageDatalist(String httpGet) {
		return WD_URL + "qmm/wd/app/item_manage"+getRequestInfo(httpGet);
	}

	/**
	 * 取消商品的秒杀活动
	 * */
	public String cancelPanic(String get) {
		return WD_URL + "qmm/wd/app/panicbuy_delete"+getRequestInfo(get);
	}

	/**
	 * 创建商品的秒杀活动
	 * */
	public String createPanic(String get) {
		return WD_URL + "qmm/wd/app/panicbuy_add"+getRequestInfo(get);
	}

	/**
	 * 删除商品的秒杀活动
	 * */
	public String deleteItem(String get) {
		return WD_URL + "qmm/wd/app/deleteitem"+getRequestInfo(get);
	}

	public String newItem(String get) {
		return WD_URL + "qmm/wd/app/newitem"+getRequestInfo(get);
	}

	public String getItem(String get) {
		return WD_URL + "qmm/wd/app/v2/edititem"+getRequestInfo(get);
	}

	public String topGoods(String get) {
		return WD_URL + "qmm/wd/app/top_goods"+getRequestInfo(get);
	}

	/**
	 * 得到商品信息
	 * */
	public String getGoodInfo(String get) {
		return WD_URL + "qmm/wd/app/api/item_sinfo"+getRequestInfo(get);
	}

	/**
	 * 通知中心未读消息获取
	 * */
	public String getNoticeInit(String get) {
		return WD_URL + "qmm/wd/app/api/mess/unread_count"+getRequestInfo(get);
	}

	/**
	 * 消息列表获取
	 * */
	public String getNoticelist(String get) {
		return WD_URL + "qmm/wd/app/api/mess/notification"+getRequestInfo(get);
	}

	/**
	 * 得到买家秀列表
	 * */
	public String maiJiaXiuDatalist(String get) {
		return WD_URL + "qmm/wd/app/api/hybrid_msg"+getRequestInfo(get);
	}

	/**
	 * 得到货源列表whole
	 * */
	public String getHuoyuanWholeDatalist(String get) {
		return WD_URL + "qmm/item/v1/wholesalelist"+getRequestInfo(get);
	}

	/**
	 * 得到货源订单列表whole
	 * */
	public String getHuoyuanOrderlist(String get) {
		return WD_URL + "qmm/order/v1/getorderlist"+getRequestInfo(get);
	}

	/**
	 * 得到碎碎念列表
	 * */
	public String suisuiNianDatalist(String get) {
		return WD_URL + "qmm/hmsg/v1/hybrid_msg"+getRequestInfo(get);
	}
	/**
	 * 一键代理商品列表
	 * */
	public String onkeybehlfList(String get) {
		return WD_URL + "qmm/item/v2/mycpslist"+getRequestInfo(get);
	}

	/**
	 * 得到label列表
	 * */
	public String getLabelList(String get) {
		return WD_URL + "qmm/wd/app/api/category/category_init"+getRequestInfo(get);
	}

	/**
	 * 更新label
	 * */
	public String updateLabel(String get) {
		return WD_URL + "qmm/wd/app/api/category/gc_relation"+getRequestInfo(get);
	}

	/**
	 * 添加label
	 * */
	public String addLabel(String get) {
		return WD_URL + "qmm/wd/app/api/category/cate_appl"+getRequestInfo(get);
	}

	/**
	 * 取消代理
	 * */
	public String delOnkeybehlf(String get) {
		return WD_URL + "qmm/item/v2/cancelagent"+getRequestInfo(get);
	}
	/**
	 * 获取厂商联系方式
	 * */
	public String getFactoryContract(String get) {
		return WD_URL + "qmm/shop/v1/contactsupplier"+getRequestInfo(get);
	}

	public String getCommoditySource() {
		return WD_URL + "qmm/wd/app/agent/list?appversion="
				+ Utils.getAppVersionString(WxShopApplication.app)
				+ "&ga_medium=android_b_menu_self";
	}

	public String getMaijiaxiuUrl() {
		// TODO Auto-generated method stub
		return "http://www.qianmiaomiao.com/tutorial/";
	}

	/**
	 * 预览店铺地址
	 * */
	public String getPreviewShopAddress() {
		// TODO Auto-generated method stub
        if(mPushServer !=0){
            return  "http://bj.mmwd.me/shop/";
        }
		return "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/shop/";
	}

    public String getMyDynamicNotesListUrl(){
        return SOCIAL_URL + "my_forum";
    }
    public String getGoodPreviewUrl(){

        // 非线上都用北京06
        if(mPushServer != 0){
           return  "http://bj.mmwd.me/item/";
        }
        return "http://" + WxShopApplication.app.getDomainMMWDUrl() + "/item/";
    }


}
