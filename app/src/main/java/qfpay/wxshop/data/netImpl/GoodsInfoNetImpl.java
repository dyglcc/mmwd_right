package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.GoodSingleItemResponseWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.GoodWrapper;
import qfpay.wxshop.utils.T;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
/**
 * 商品信息网络请求
 * */
public class GoodsInfoNetImpl extends AbstractNet {

	public GoodsInfoNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("goodid")) {
				map.put("goodid", parameter2.getString("goodid"));
			}
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getGoodInfo(ConstValue.HTTP_GET));
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
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gosn = new Gson();
				GoodSingleItemResponseWrapper fromJson2 = gosn.fromJson(jsonStr,GoodSingleItemResponseWrapper.class);
				if (!fromJson2.getRespcd().equals("0000")) {
                    String errorMsg = fromJson2.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}else{
					bundle.putString("good_name", fromJson2.getData().getGood().getGood_name());
					bundle.putInt("id", fromJson2.getData().getGood().getId());
					bundle.putString("good_img", fromJson2.getData().getGood().getGood_img());
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
