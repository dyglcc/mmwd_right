package qfpay.wxshop.data.netImpl;

import qfpay.wxshop.utils.MobAgentTools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.os.Bundle;

public class CreatePanicImpl extends AbstractNet {

	public CreatePanicImpl(Activity act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			String goodid = parameter2.getString("goodid");
			String newPrice = parameter2.getString("promoPrice");
			String startTime = parameter2.getString("startTime");
			String endTime = parameter2.getString("endTime");
			map.put("newprice", newPrice);
			map.put("starttime", startTime);
			map.put("endtime", endTime);
			map.put("is_app", "android v" +Utils.getAppVersionString(activity));
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance().createPanic(ConstValue.HTTP_GET) + "/"+goodid + WDConfig.getInstance().getRequestInfo(ConstValue.HTTP_POST));

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
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {
					bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
					
				}else{
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_FAILED);
					T.i("jsonStr is null or jsonStr.length is 0");
					bundle.putString(ConstValue.ERROR_MSG,
							"未知错误，请重试");
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
