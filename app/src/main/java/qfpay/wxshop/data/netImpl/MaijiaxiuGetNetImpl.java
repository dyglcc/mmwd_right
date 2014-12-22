package qfpay.wxshop.data.netImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.BuyerResponseWrapper;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.MsgsWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;

public class MaijiaxiuGetNetImpl extends AbstractNet {

	public MaijiaxiuGetNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("page")) {
				map.put("page", parameter2.getString("page"));
			}
			// 买家秀 1
			map.put("msg_type", 1);

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.maiJiaXiuDatalist(ConstValue.HTTP_GET));
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
				BuyerResponseWrapper fromJson = gosn.fromJson(jsonStr,
						BuyerResponseWrapper.class);
				if (!fromJson.getRespcd().equals("0000")) {
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_FAILED);
					return bundle;
				}
				MsgsWrapper data = fromJson.getData();
				list = new ArrayList<HashMap<String, Object>>();
				map = new HashMap<String, Object>();
				// 2014-04-24 14:52:31
				// 处理日期

				for (BuyerShowBean bsb : data.getMsgs()) {
					if (bsb != null && !bsb.getUpdate_time().equals("")) {

						
						bsb.setChineseDate(getChineseDateStr(bsb.getUpdate_time()));
					}
				}
				map.put("orderList", data);
				list.add(map);

				Long key = System.currentTimeMillis();
				CacheData.getInstance().setData(key + "", list);
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

	public static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat format2 = new SimpleDateFormat("M月d日");
	public static String getChineseDateStr(String ancor) {
		if(ancor.equals("")){
			ancor = format.format(new Date());
		}
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			String todayString = format2.format(today);
			calendar.set(Calendar.DAY_OF_YEAR, -1);
			Date yestorday = calendar.getTime();
			String yestorDay = format2.format(yestorday);

			Date date_ = format.parse(ancor);
			String date_String = format2.format(date_);
			if (date_String.equals(todayString)) {
				return ConstValue.TODAY;
			} else if (date_String.equals(yestorDay)) {
				return ConstValue.YESTERDAY;
			} else {
				return date_String;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
}
