package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.data.beans.SSNListResponseWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.main.fragment.SSNListFragment;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;

public class SSNGetDemoNetImpl extends AbstractNet {

	public SSNGetDemoNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			map.put("mid", SSNListFragment.DEMO_ID);

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.suisuiNianDatalist(ConstValue.HTTP_GET));
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
				T.i(jsonStr);
				Gson gson = new Gson();
				SSNListResponseWrapper cjb = gson.fromJson(jsonStr,
						SSNListResponseWrapper.class);
				if (cjb.getRespcd().equals("0000")) {

					
					List<SSNItemBean> msgs = cjb.getData().getMsgs();
					if(msgs.size()>0){
						SSNItemBean item = msgs.get(0);
						bundle.putSerializable("bean", item);
					}

				} else if (cjb.getRespcd().startsWith("21")) {
					bundle.putString(ConstValue.ERROR_MSG, cjb.getResperr());

				} else {
                    String errorMsg = cjb.getResperr();
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
