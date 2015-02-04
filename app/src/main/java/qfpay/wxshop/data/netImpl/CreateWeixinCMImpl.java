package qfpay.wxshop.data.netImpl;

import qfpay.wxshop.utils.MobAgentTools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;

public class CreateWeixinCMImpl extends AbstractNet {

	public CreateWeixinCMImpl(Activity act) {
		super(act);
		setNoNeedShowDialog();
	}
	
	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			// good_name（商品名称）
			// itemprice（商品价格）
			// good_desc（商品描述）
			// postage（邮费）
			// openid（userid）
			// images（url 用‘,’分割）
			// gooddetailid（如果是空传-1）
			// orderable:1 可支付商品 2. 微店收款
			String good_name = null;
			String itemprice = null;
			if (parameter2.containsKey("good_name")) {
				good_name = (String) parameter2.get("good_name");
			}

			if (parameter2.containsKey("itemprice")) {
				itemprice = (String) parameter2.get("itemprice");
			}

			map.put("good_name", good_name);
			map.put("itemprice", itemprice);
			map.put("good_desc",good_name );
			map.put("postage", "0");
			map.put("openid", WxShopApplication.dataEngine.getUserId());
			map.put("images", WDConfig.WEIXIN_COLLECT_MONEY);
			map.put("gooddetailid", "-1");
			map.put("orderable", "2");
			map.put("is_app", "android");

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getCollectMoney(ConstValue.HTTP_POST));

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

	@Override
	protected Bundle jsonParse(String jsonStr) {

		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				// {"resperr":"","respcd":"0000","respmsg":"","data":{"goodid":429}}
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {

					if (root.has("data") && !root.isNull("data")) {
						JSONObject obj = root.getJSONObject("data");
						if (obj.has("goodid") && !obj.isNull("goodid")) {
							bundle.putString("goodid", obj.getString("goodid"));
						}
					}

				} else if (resultState.startsWith("21")) {
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
