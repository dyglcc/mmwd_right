package qfpay.wxshop.data.netImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.NoticeItemBean;
import qfpay.wxshop.data.beans.NoticeResponseWrapper;
import qfpay.wxshop.data.beans.NoticeResponseWrapper.MsgsWrapper;
import qfpay.wxshop.data.beans.NoticeUnReadResponseWrapper;
import qfpay.wxshop.data.beans.OfficialGoodsResponseWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera.Size;
import android.os.Bundle;

import com.google.gson.Gson;

public class NoticeListNetImpl extends AbstractNet {

	public NoticeListNetImpl(Context act) {
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
			map.put("app_ver",
					qfpay.wxshop.utils.Utils.getAppVersionString(activity));
			map.put("device", qfpay.wxshop.utils.Utils.getDeviceName());
			int page = parameter2.getInt("page");

			map.put("page_size", ConstValue.PAGE_SIZE);
			map.put("page_num", page);

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getNoticelist(ConstValue.HTTP_GET));
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
		ArrayList<HashMap<String, Object>> list = null;
		HashMap<String, Object> map = null;
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gosn = new Gson();
				NoticeResponseWrapper fromJson = gosn.fromJson(jsonStr,
						NoticeResponseWrapper.class);
				if (!fromJson.getRespcd().equals("0000")) {
                    String errorMsg = fromJson.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}else{
                    List<NoticeItemBean> notification = fromJson.getData()
                            .getNotifications();
//				getClickAbleData(notification);
                    list = new ArrayList<HashMap<String, Object>>();
                    map = new HashMap<String, Object>();
                    map.put("orderList", notification);
                    list.add(map);

                    Long key = System.currentTimeMillis();
                    CacheData.getInstance().setData(key + "", list);
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
