package qfpay.wxshop.data.netImpl;

import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.MobAgentTools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;

/**
 * 上传七牛照片请求
 * */
public class UploadPic4QiNiuImpl extends UploadPicImpl {

	private String tokenString;

	public UploadPic4QiNiuImpl(Activity act, String token) {
		super(act);
		tokenString = token;
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			map.put("fileUrl", parameter2.getString("fileUrl"));
			map.put("fileName", parameter2.getString("fileName"));
			map.put("key",
					WxShopApplication.dataEngine.getUserId()
							+ System.currentTimeMillis() + ".jpg");
			map.put("token", tokenString);
			map.put(ConstValue.REQUEST_URL, com.qiniu.upload.tool.Config.UP_HOST);

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST_QINIU);
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
		// bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject root = new JSONObject(jsonStr);
				if (root.has("key")) {
					bundle.putString("url","http://"
							+ com.qiniu.upload.tool.Config.domain + "/"
							+ root.getString("key"));
				} else {
                    String errorMsg = root.getString("resperr");
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
