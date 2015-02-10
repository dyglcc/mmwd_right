package qfpay.wxshop.data.netImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
/**
 * 登陆网络请求
 * */
public class LoginImpl extends AbstractNet {

	public LoginImpl(Activity act) {
		super(act);
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			String username = parameter2.getString("username");
			String pwd = parameter2.getString("password");
			map.put("mobile", username);
			map.put("password", pwd);
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getRegLoginURL(ConstValue.HTTP_POST));
			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST);
			/** 参数正确 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);
		} catch (Exception e) {
			T.e(e);
			/** 请求参数错误 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
		}
		return map;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Bundle jsonParse(String jsonStr) {

		T.i("cc" + " " + jsonStr.toString());
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject result = new JSONObject(jsonStr);
				String resultState = result.getString("respcd");
				if (resultState.equals("0000")) {

					// clear 缓存

					WxShopApplication.dataEngine.clearShare();
					// 设置登陆成功

					WxShopApplication.dataEngine.setLoginStatus(true);

					JSONObject root = result.getJSONObject("data");

					if (root.has("sessionid")) {
						WxShopApplication.dataEngine.setcid(root
								.getString("sessionid"));
					}
					if (root.has("userid")) {
						WxShopApplication.dataEngine.setUserId(root
								.getString("userid"));
					}
					if (root.has("mobile")) {
						WxShopApplication.dataEngine.setMobile(root
								.getString("mobile"));
					}
					if (root.has("shop_info") && !root.isNull("shop_info")) {

						JSONObject shopinfo = root.getJSONObject("shop_info");
						if (shopinfo.has("shopname")) {
							String name = shopinfo.getString("shopname");
							WxShopApplication.dataEngine.setShopName(name);
						}
						if (shopinfo.has("address")) {
							String address = shopinfo.getString("address");
							WxShopApplication.dataEngine.setAddress(address);
						}
						if (shopinfo.has("intro")) {
							String intro = shopinfo.getString("intro");
							WxShopApplication.dataEngine.setNoticeText(intro);
						}
						if (shopinfo.has("avatar")) {
							String avator = shopinfo.getString("avatar");
							WxShopApplication.dataEngine.setAvatar(avator);
						}
						if (shopinfo.has("id")) {
							String shopid = shopinfo.getString("id");
							WxShopApplication.dataEngine.setShopId(shopid);
						}
						if (shopinfo.has("weixinid")) {
							String weixinid = shopinfo.getString("weixinid");
							WxShopApplication.dataEngine.setContract(weixinid);
						}
						if (shopinfo.has("create_time")) {
							String createTime = shopinfo
									.getString("create_time");
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date date = format.parse(createTime);
							WxShopApplication.dataEngine
									.setRegisterTimeMillis(date.getTime());
						}
						if (shopinfo.has("shop_bg")) {
							String shopbg = shopinfo.getString("shop_bg");
							WxShopApplication.dataEngine.setShopBg(shopbg);
						}

					} else {
						WxShopApplication.dataEngine.setShopName("");
						WxShopApplication.dataEngine.setAvatar("");
						WxShopApplication.dataEngine.setShopId("");
						WxShopApplication.dataEngine.setAddress("");
					}

					if (root.has("profile") && !root.isNull("profile")) {

						JSONObject profile = root.getJSONObject("profile");
						String bankAccount = null;
						String bankUser = null;
						String bankname = null;
						if (profile.has("bankaccount")) {
							bankAccount = profile.getString("bankaccount");
							WxShopApplication.dataEngine
									.setBankAccount(bankAccount);
						}
						if (profile.has("bankuser")) {
							bankUser = profile.getString("bankuser");
							WxShopApplication.dataEngine.setBankUser(bankUser);
						}
						if (profile.has("bankname")) {
							bankname = profile.getString("bankname");
							WxShopApplication.dataEngine
									.setBrankBranchName(bankname);
						}
						if (profile.has("bankaccount")
								&& profile.has("bankuser")
								&& profile.has("bankname")) {
							if (!bankAccount.equals("null")
									&& !bankUser.equals("null")
									&& !bankname.equals("null")
									&& !bankAccount.equals("")
									&& !bankUser.equals("")
									&& !bankname.equals("")) {
								WxShopApplication.dataEngine
										.setApplyCardbind(true);
							}
						}
					} else {
						WxShopApplication.dataEngine.setBankAccount("");
						WxShopApplication.dataEngine.setBankUser("");
						WxShopApplication.dataEngine.setBrankBranchName("");
					}
				} else if (resultState.equals("2103")) {
					bundle.putString(ConstValue.ERROR_MSG, "用户不存在");

				} else if (resultState.equals("2104")) {
					bundle.putString(ConstValue.ERROR_MSG, "账号或密码不太对呢~请亲再输入一次~");
					T.i("cd" + " " + jsonStr.toString());

				} else if (resultState.equals("2102")) {
					bundle.putString(ConstValue.ERROR_MSG, "用户不存在");
					T.i("cd" + " " + jsonStr.toString());

				} else {
                    String errorMsg = result.getString("resperr");
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}

				Long key = System.currentTimeMillis();
				/** 界面上展示的时候直接根据key取存储类的数据 */
				bundle.putString(ConstValue.CACHE_KEY, key + "");
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_SUCCESS);
			} catch (Exception e) {
				T.e(e);
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			}
		} else {
			bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			T.i("jsonStr is null or jsonStr.length is 0");
		}
		return bundle;
	}
}
