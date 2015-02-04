package qfpay.wxshop.data.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

public class DataEngine extends Activity {

	public SharedPreferences data;
    BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationData;

	public DataEngine(Context context) {
		data = context.getSharedPreferences("Data", 0);
	}

	public void setAppID(String string) {
		setData("appID", string);
	}

	public String getAppID() {
		return getData("appID", "0");
	}

	public void setDeviceID(String string) {
		setData("deviceID", string);
	}

	public String getDeviceID(Context context) {
		return getDeviceID_(context);
	}

	public void setLocationString(String locationString) {

		setData("location", locationString);
	}

	public String getLocationString() {

		return getData("location", "");
	}

	public void setScreenWidth(int width) {
		data.edit().putInt("screenWidth", width).commit();
	}

	public int getScreenWidth() {
		return data.getInt("screenWidth", 0);
	}

	public void setScreenHeight(int height) {
		data.edit().putInt("screenHeight", height).commit();
	}
	// app open time
	public long getOpenTime() {
		return data.getLong("openApptime", 0);
	}
	
	public void setOpenTime(long currenttime) {
		data.edit().putLong("openApptime", currenttime).commit();
	}

	public int getScreenHeight() {
		return data.getInt("screenHeight", 800);
	}

	public void setData(String key, String value) {

		data.edit().putString(key, value).commit();

	}

	public String getData(String key, String defaultValue) {

		return data.getString(key, defaultValue);
	}

	public String getMobile() {
		return getData("mobile", "");
	}

	public void setMobile(String mobile) {
		setData("mobile", mobile);
	}

	// 商店名称
	public String getShopName() {
		return getData("shopName", "");
	}

	public void setShopName(String mobile) {
		setData("shopName", mobile);
	}
	// 头像地址
	public String getAvatar() {
		return getData("avatar", "");
	}

	public void setAvatar(String url) {
		setData("avatar", url);
	}

	// 店铺地址
	public String getAddress() {
		return getData("address", "");
	}

	public void setAddress(String address) {
		setData("address", address);
	}

	// userid
	public String getUserId() {
		return getData("Userid", "");
	}

	public void setUserId(String mobile) {
		setData("Userid", mobile);
	}

	// userid
	public String getcid() {
		return getData("id", "");
	}

	public void setcid(String sessionid) {
		setData("id", sessionid);
	}

	/**
	 * 是否弹出引导
	 * 
	 * @return
	 */
	public boolean getIntroduce() {
		return data.getBoolean("introduce", false);
	}

	public void setIntroduce(boolean res) {

		data.edit().putBoolean("introduce", res).commit();
	}

	/**
	 * 是否已经登录
	 * 
	 * @return
	 */
	public boolean getLoginStatus() {
		return data.getBoolean("login", false);
	}

	public void setLoginStatus(boolean login) {

		data.edit().putBoolean("login", login).commit();
	}

	/**
	 * 是否已经登录
	 * 
	 * @return
	 */
	public boolean isFirstInMainActivity() {
		return data.getBoolean("firstinmain", true);
	}

	public void setFirstInMainactivity() {

		data.edit().putBoolean("firstinmain", false).commit();
	}

	/**
	 * 申请开通账号
	 * 
	 * @return
	 */
	public boolean getApplyCardbind() {
		return data.getBoolean("applyBindCard", false);
	}

	public void setApplyCardbind(boolean flasg) {

		data.edit().putBoolean("applyBindCard", flasg).commit();
	}

	// userid
	public String getShopId() {
		return getData("shopid", "");
	}

	public void setShopId(String sessionid) {
		setData("shopid", sessionid);
	}

	// userid
	public String getContract() {
		return getData("contract", "");
	}

	public void setContract(String contract) {
		setData("contract", contract);
	}

	public String getShopBg() {
		return getData("shop_bg", "");
	}

	public void setShopBg(String shopBg) {
		setData("shop_bg", shopBg);
	}

	/**
	 * 申请开通账号
	 * 
	 * @return
	 */
	public boolean getGuidePublish() {
		return data.getBoolean("guide_publish", false);
	}

	public void setGuidePublish(boolean flag) {

		data.edit().putBoolean("guide_publish", flag).commit();
	}

	public void clearShare() {
		setMobile("");
		setShopName("");
		setAvatar("");
		setAddress("");
		setUserId("");
		setcid("");
		setLoginStatus(false);
		setShopId("");
		setApplyCardbind(false);
		// setIntroduce(true);
		setContract("");
		// setGuidePublish(false);
		setBankAccount("");
		setBankUser("");
		setBrankBranchName("");
		setRegisterTimeMillis(0l);
	}

	// 头像地址
	public String getBankAccount() {
		return getData("bankaccount", "");
	}

	public void setBankAccount(String url) {
		setData("bankaccount", url);
	}

	// 头像地址
	public String getBankUser() {
		return getData("bankUser", "");
	}

	public void setBankUser(String url) {
		setData("bankUser", url);
	}

	// 头像地址
	public String getBrankBranchName() {
		return getData("banknameBranch", "");
	}

	public void setBrankBranchName(String url) {
		setData("banknameBranch", url);
	}

	// UserAgen
	public void setUserAgent(String str) {
		setData("userAgen", str);
	}

	// 头像地址
	public String getUserAgent() {
		return getData("userAgen", "");
	}

	// splash image path
	public void setSplashImagePath(String path) {
		setData("splashImagepath", path);
	}

	// splash image path
	public String getSplashImagePath() {
		return getData("splashImagepath", "");
	}

	// splash image old url
	public void setSplashOldUrl(String url) {
		setData("splashOldurl", url);
	}

	// splash image old url
	public String getSplashOldUrl() {
		return getData("splashOldurl", "");
	}

	// splash image path
	public int getDisplayTime() {
		return data.getInt("DisplayTime", 3);
	}

	// splash image old url
	public void setDisplayTime(int time) {
		data.edit().putInt("DisplayTime", time).commit();
	}

	/**
	 * 是否已经显示更新dialog
	 * 
	 * @return
	 */
	public boolean isShownUpdate() {
		return data.getBoolean("mainActivity_showUpdate", false);
	}

	public void setShownUpdate(boolean b) {
		data.edit().putBoolean("mainActivity_showUpdate", b).commit();
	}

	// splash image old url
	public void setRegisterTimeMillis(long startTime) {
		data.edit().putLong("registerTimeMillis", startTime).commit();
	}

	// splash image old url
	public long getRegTimeMillist() {
		return data.getLong("registerTimeMillis", 0l);
	}

	// splash image old url
	public void setNoticeText(String text) {
		setData("noticeText", text);
	}

	// splash image old url
	public String getNoticeText() {
		return getData("noticeText", "");
	}

	public boolean isFirstinEdvertiseMent() {
		return data.getBoolean("first_in_edertisement", true);
	}

	public void isSetEdvertiseMent(boolean b) {
		data.edit().putBoolean("first_in_edertisement", b).commit();
	}
    // 微信分享第一次总是失败，所有默认第一次分享默认重试一次
	public boolean isFirstWeixinShare() {
		return data.getBoolean("first_share_wexin", true);
	}
    // 微信分享第一次总是失败，所有默认第一次分享默认重试一次
	public void isSetFirstWeixinShare(boolean b) {
		data.edit().putBoolean("first_share_wexin", b).commit();
	}


	// 发现个喵
	public boolean isFirstinFaxiangeMiao() {
		return data.getBoolean("first_in_faxiangemiao", false);
	}

	public void isSetFirstInFaxiangeMiao(boolean b) {
		data.edit().putBoolean("first_in_faxiangemiao", b).commit();
	}
	// 设置个推标签
	public boolean isSettedGetuiTag() {
		return data.getBoolean("setgetuibiaoqian", false);
	}
	
	public void setGroupTag(boolean b) {
		data.edit().putBoolean("setgetuibiaoqian", b).commit();
	}

	// 喵喵购
	public boolean isFirstinMiaomiaoGou() {
		return data.getBoolean("first_in_miaomiaogou", false);
	}

	public void isSetFirstInMiaomiaoGou(boolean b) {
		data.edit().putBoolean("first_in_miaomiaogou", b).commit();
	}
	// 官方货源
	public boolean isFirstinOfficalActivity() {
		return data.getBoolean("first_in_official_activity", false);
	}
	
	public void isSetFirstinOfficalActivity(boolean b) {
		data.edit().putBoolean("first_in_official_activity", b).commit();
	}

	// 所有label
	public void setLabels(String label) {
		data.edit().putString("labels", label).commit();
	}

	public String getLabels() {
		return data.getString("labels", "");
	}

	// 历史label
	public void setHistoryLabels(String label) {
		data.edit().putString("hislabels", label).commit();
	}

	public String getHistoryLabels() {
		return data.getString("hislabels", "");
	}

	public boolean isFirstinShowLabel() {
		return data.getBoolean("first_show_label", false);
	}

	public void isSetFirstinShowLabel(boolean b) {
		data.edit().putBoolean("first_show_label", b).commit();
	}

	private String getDeviceID_(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String deviceid = tm.getDeviceId();
		if (deviceid != null && !deviceid.equals("")) {
			T.i("使用devices id " + deviceid);
			return deviceid;
		}
		String deviceidSaved = getData("deviceID", "");
		if (deviceidSaved != null || !deviceid.equals("")) {
			return deviceidSaved;
		}
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			T.e(e);
			return "";
		}
		String newDId = convertToHex(md
				.digest((System.currentTimeMillis() + "").getBytes()));
		setDeviceID(newDId);
		T.i("使用时间生成deviceid");
		return newDId;
	}

	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
	
	public int getInMainActivityTimes() {
		return data.getInt("timesInMainActivity", 0);
	}

	public void setInMainActivityTimes(int times) {
		data.edit().putInt("timesInMainActivity", times).commit();
	}

    /**
     * 存储商户圈关于我的消息
     * @param businessCommmunityMyNotificationDataWrapper
     */
    public void setBusinessCommunityAboutMyNotification(BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationDataWrapper){
            Gson gson = new Gson();
            data.edit().putString("notification",gson.toJson(businessCommmunityMyNotificationDataWrapper)).commit();
    }

    public BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper getBusinessCommunityAboutMyNotification(){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationDataWrapper;
        String string = getData("notification","");
        data.edit().putString("notification","").commit();
        if(!string.equals("")){
            Gson gson = new Gson();
            businessCommmunityMyNotificationDataWrapper = gson.fromJson(string, BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper.class);
            return businessCommmunityMyNotificationDataWrapper;
        }else{
            return  null;
        }
    }

    public BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper getBusinessCommmunityMyNotificationData() {
        return businessCommmunityMyNotificationData;
    }

    public void setBusinessCommmunityMyNotificationData(BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationData) {
        this.businessCommmunityMyNotificationData = businessCommmunityMyNotificationData;
    }
}
