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
 * 店铺创建手机号注册
 *
 * */
public class ShopCreateImpl extends AbstractNet {

	public ShopCreateImpl(Activity act) {
		super(act);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("shop_name")) {

				map.put("shop_name", parameter2.getString("shop_name"));
			}
			if (parameter2.containsKey("shop_avatar")) {

				map.put("avatar", parameter2.getString("shop_avatar"));
			}
			if (parameter2.containsKey("shop_weixin")) {

				map.put("weixin", parameter2.getString("shop_weixin"));
			}

			if (parameter2.containsKey("address")) {

				map.put("address", parameter2.getString("address"));
			}
			map.put("lat", WxShopApplication.getLocationLat());
			map.put("lng",  WxShopApplication.getLocationLng());

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance().getRegUpdateShopInfoURL(ConstValue.HTTP_POST));
			
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
	protected Bundle jsonParse(String jsonStr) {activity.getSharedPreferences("Data", 0).getString("location", "");

		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {

					if (root.has("data")) {
						JSONObject dataObj = root.getJSONObject("data");
						// if (dataObj.has("shop")) {
						// JSONObject shop = dataObj.getJSONObject("shop");
						// String name = shop.getString("shopname");
						// String avator = shop.getString("avatar");
						// String contract = shop.getString("contract");
						// WxShopApplication.dataEngine.setShopName(name);
						// WxShopApplication.dataEngine.setAvatar(avator);
						// WxShopApplication.dataEngine.setContract(contract);
						// if (shop.has("id")) {
						// String shopid = shop.getString("id");
						// WxShopApplication.dataEngine.setShopId(shopid);
						// }
						// }
						if (dataObj.has("shop_info")
								&& !dataObj.isNull("shop_info")) {

							JSONObject shopinfo = dataObj
									.getJSONObject("shop_info");
							if (shopinfo.has("shopname")) {
								String name = shopinfo.getString("shopname");
								WxShopApplication.dataEngine.setShopName(name);
							}
							if (shopinfo.has("address")) {
								String address = shopinfo.getString("address");
								WxShopApplication.dataEngine
										.setAddress(address);
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
								String createTime = shopinfo.getString("create_time");
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date date = format.parse(createTime);
								WxShopApplication.dataEngine.setRegisterTimeMillis(date.getTime());
							}
						}
					}

				} else if (resultState.equals("2101")) {
					bundle.putString(ConstValue.ERROR_MSG,
							"参数错误");

				} else if (resultState.startsWith("2")) {
					bundle.putString(ConstValue.ERROR_MSG,
							root.getString("resperr"));

				} else {
                    String errorMsg = root.getString("resperr");
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
