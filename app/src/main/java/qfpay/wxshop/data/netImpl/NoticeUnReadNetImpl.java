package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.NoticeUnReadResponseWrapper;
import qfpay.wxshop.data.beans.NoticeUnReadResponseWrapper.DataWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
/**
 * 未读消息列表网络请求
 * */
public class NoticeUnReadNetImpl extends AbstractNet {

	public NoticeUnReadNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			map.put("app", "mmwd");
			map.put("uid", WxShopApplication.dataEngine.getUserId());
			map.put("platform", "android");
			map.put("os_ver", qfpay.wxshop.utils.Utils.getOSVerison(activity));
			map.put("app_ver", qfpay.wxshop.utils.Utils.getAppVersionString(activity));
			map.put("device", qfpay.wxshop.utils.Utils.getDeviceName());

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getNoticeInit(ConstValue.HTTP_GET));
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

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Bundle jsonParse(String jsonStr) {
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gosn = new Gson();
				NoticeUnReadResponseWrapper fromJson = gosn.fromJson(jsonStr,
						NoticeUnReadResponseWrapper.class);
				if (!fromJson.getRespcd().equals("0000")) {
                    String errorMsg = fromJson.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}else{
                    DataWrapper dataWrapper  = fromJson.getData();
                    bundle.putInt("unread", dataWrapper.getUnread());
                    Long key = System.currentTimeMillis();
                    /** 界面上展示的时候直接根据key取存储类的数据 */
                    bundle.putString(ConstValue.CACHE_KEY, key + "");
                }

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
