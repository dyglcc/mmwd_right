package qfpay.wxshop.data.netImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.LabelBean;
import qfpay.wxshop.data.beans.LabelResponseWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.style.BulletSpan;

import com.google.gson.Gson;
/**
 * 保存标签网络请求
 * */
public class LabelupdateNetImpl extends AbstractNet {

	public LabelupdateNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			if(parameter2!=null){
				map.put("goodid", parameter2.getString("goodid"));
				map.put("cateid", parameter2.getString("tagid"));
			}
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.updateLabel(ConstValue.HTTP_POST));
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
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gosn = new Gson();
				CommonJsonBean fromJson = gosn.fromJson(jsonStr,
						CommonJsonBean.class);
				if (!fromJson.getRespcd().equals("0000")) {
                    String errorMsg = fromJson.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
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
