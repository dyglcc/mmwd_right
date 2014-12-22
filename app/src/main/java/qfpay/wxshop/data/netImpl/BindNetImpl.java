package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;

public class BindNetImpl extends AbstractNet {

	private static int retryTime = 0;
	private String cliendid;
	private Handler handler;

	public BindNetImpl(Context act, Handler handler) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (retryTime >= 3) {
				return null;
			}
			retryTime++;
			String clientid = parameter2.getString("clientid");
			this.cliendid = clientid;
			map.put("clientid", clientid);
			
			map.put("apptype",
					ConstValue.APP_TYPE);
			map.put("appver",
					Utils.getAppVersionString(activity));
			map.put("deviceid",
					WxShopApplication.dataEngine.getDeviceID(activity));

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance().pushBindServer());
			// map.put(ConstValue.REQUEST_URL,
			// "http://172.100.101.167:8282/app/v1/bindqmm");

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

	private SharedPreferences share ;
	@Override
	protected Bundle jsonParse(String jsonStr) {

		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gson = new Gson();
				CommonJsonBean bean = gson.fromJson(jsonStr,
						CommonJsonBean.class);
				if (!bean.getRespcd().equals("0000")) {
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_FAILED);
					Bundle bun = new Bundle();
					bun.putString("clientid", this.cliendid);
					this.request(bun, handler);
				} else {
					
					share = activity.getSharedPreferences("Data", 0);
					String userid = share.getString("Userid", "");
					
					Editor editor = share.edit();
					editor.putString("bindStr", userid+"_" + System.currentTimeMillis());
					editor.commit();
					
					boolean settecTag = WxShopApplication.dataEngine
							.isSettedGetuiTag();
					if (!settecTag) {

						Tag[] tagParam = new Tag[1];
						Tag t = new Tag();
						t.setName(Utils.getAppVersionString(activity));
						tagParam[0] = t;

						int i = PushManager.getInstance().setTag(activity,
								tagParam);
						String text = "ERROR";

						switch (i) {
						case PushConsts.SETTAG_SUCCESS:
							text = "设置标签成功";
							break;
						case PushConsts.SETTAG_ERROR_COUNT:
							text = "设置标签失败，tag数量过大";
							break;
						default:
							text = "设置标签失败，setTag异常";
							break;
						}
						Toaster.l(activity, text);
					}
				}

				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_SUCCESS);
			} catch (Exception e) {
				T.e(e);
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			}
		} else {
			bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
		}
		return bundle;
	}
}
