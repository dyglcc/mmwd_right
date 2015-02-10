package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.os.Bundle;
/**
 * 注册
 *
 * */
public class RegImpl extends AbstractNet {

	public RegImpl(Activity act) {
		super(act);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			String mobile = parameter2.getString("mobile");
			String password = parameter2.getString("password");
			String verify_code = parameter2.getString("verify_code");
			map.put("mobile", mobile);
			map.put("password", password);
			map.put("verify_code", verify_code);
			
			// extra info
			map.put("appver", Utils.getAppVersionString(activity));
			map.put("market", Utils.getChannel(activity));
			map.put("os", "Android");
			map.put("osver", Utils.getOSVerison(activity));
			map.put("network", Utils.getNetworkInfo(activity));
			map.put("phonemodel", Utils.getDeviceName());
			map.put("deviceid", Utils.getDeviceID(activity));
			
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance().getRegRegisterURL(ConstValue.HTTP_POST));

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
				JSONObject result = new JSONObject(jsonStr);
				String resultState = result.getString("respcd");
				if (resultState.equals("0000")) {
					
					WxShopApplication.dataEngine.clearShare();
					
					JSONObject root = result.getJSONObject("data");
					String userid = root.getString("userid");
					WxShopApplication.dataEngine.setUserId(userid);
					String cid = root.getString("sessionid");
					WxShopApplication.dataEngine.setcid(cid);
					bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
					
				} else if (resultState.equals("1000")) {

					bundle.putString(ConstValue.ERROR_MSG, "注册失败");

				} else if (resultState.equals("1001")) {

					bundle.putString(ConstValue.ERROR_MSG, "验证码输入错啦！点击获取验证码重新获取~");

				} else if (resultState.equals("2301")) {

					bundle.putString(ConstValue.ERROR_MSG, "亲的手机号已经注册过了呦~直接登陆就好啦~");

				} else if(resultState.startsWith("2")){
					bundle.putString(ConstValue.ERROR_MSG, result.getString("resperr"));
					
				}else{
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
