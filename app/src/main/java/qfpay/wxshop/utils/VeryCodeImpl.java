package qfpay.wxshop.utils;

import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.MobAgentTools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

public class VeryCodeImpl extends AbstractNet {

	public VeryCodeImpl(Activity act) {
		super(act);
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			String code = parameter2.getString("code");

			map.put(ConstValue.REQUEST_URL,
					"http://wx.qfpay.com/qmm/wd/verify_code?code=" + code);

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_GET);
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

		Bundle bundle = new Bundle();
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {
					JSONObject obj = root.getJSONObject("data");
					JSONObject user = obj.getJSONObject("user");

					String openidString;
					String mobile;
					if (user.has("openid")) {

						openidString = user.getString("openid");
						bundle.putString("openid", openidString);
					}
					if (user.has("mobile")) {

						mobile = user.getString("mobile");
						bundle.putString("mobile", mobile);
					}
				}  else if(resultState.startsWith("21")){
					bundle.putString(ConstValue.ERROR_MSG, root.getString("resperr"));
					
				}else{
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_FAILED);
					T.i("jsonStr is null or jsonStr.length is 0");
					return bundle;
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
