package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.os.Bundle;

public class UserPaymentImpl extends AbstractNet {

	public UserPaymentImpl(Activity act) {
		super(act);
	}

	private String card_number;
	private String card_holder_name;

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			card_number = parameter2.getString("card_number");
			card_holder_name = parameter2.getString("card_holder_name");
			String card_bank = parameter2.getString("card_bank");
			map.put("bankaccount", card_number);
			map.put("bankuser", card_holder_name);
			map.put("bankname", card_bank);
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getRegPaymentInfo(ConstValue.HTTP_POST));
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

					if (root.has("data") && !root.isNull("data")) {

						JSONObject profile = root.getJSONObject("data");
						String bankAccount = null;
						String bankUser = null;
						String bankname = null;
						if (profile.has("bankaccount")) {
							bankAccount = profile.getString("bankaccount");
							bankAccount = Utils.showMaskCardNum(bankAccount);
							WxShopApplication.dataEngine
									.setBankAccount(bankAccount);
						}

						if (profile.has("bankuser")) {
							bankUser = profile.getString("bankuser");
							WxShopApplication.dataEngine.setBankUser(bankUser);
						}
						if (profile.has("bankname")) {
							bankname = profile.getString("bankname");
							WxShopApplication.dataEngine
									.setBrankBranchName(bankname);
						}
						if (profile.has("bankaccount")
								&& profile.has("bankuser")
								&& profile.has("bankname")) {
							if (!bankAccount.equals("null")
									&& !bankUser.equals("null")
									&& !bankname.equals("null")
									&& !bankAccount.equals("")
									&& !bankUser.equals("")
									&& !bankname.equals("")) {
								WxShopApplication.dataEngine
										.setApplyCardbind(true);
							}
						}
					} else {
						WxShopApplication.dataEngine.setBankAccount("");
						WxShopApplication.dataEngine.setBankUser("");
						WxShopApplication.dataEngine.setBrankBranchName("");
					}

				} else {
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.NET_RETURN_SUCCESS);
					String strError = root.getString("resperr");
					if (strError == null || strError.equals("")) {
						strError = root.getString("respmsg");
					}
					bundle.putString(ConstValue.ERROR_MSG, strError);
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
