package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SSNCreateSucsRespWrapper;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;

import com.google.gson.Gson;

public class SuiSuiNianCreateImpl extends AbstractNet {

	public SuiSuiNianCreateImpl(Activity act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("title")) {
				String intro = (String) parameter2.get("title");
				map.put("title", intro);
			}
			if (parameter2.containsKey("content")) {
				String intro = (String) parameter2.get("content");
				map.put("content", intro);
			}
			if (parameter2.containsKey("good_id")) {
				String good_id = (String) parameter2.get("good_id");
				map.put("good_id", good_id);
			}
			if (parameter2.containsKey("img_url")) {
				String img_url = (String) parameter2.get("img_url");
				map.put("img_url", img_url);
			}
			map.put("content_type", "1");
			map.put("msg_type", "0");

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.suisuiNianDatalist(ConstValue.HTTP_PUT));

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_PUT);
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
				T.i(jsonStr);
				Gson gson = new Gson();
				SSNCreateSucsRespWrapper cjb = gson.fromJson(jsonStr,
						SSNCreateSucsRespWrapper.class);
				if (cjb.getRespcd().equals("0000")) {

					SSNItemBean item = cjb.getData().getMsg();
					bundle.putSerializable("bean", item);

				} else if (cjb.getRespcd().startsWith("21")) {
					bundle.putString(ConstValue.ERROR_MSG, cjb.getResperr());

				} else {
                    String errorMsg = cjb.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
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
