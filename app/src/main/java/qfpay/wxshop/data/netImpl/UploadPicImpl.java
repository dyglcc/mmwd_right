package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;
/**
 *上传照片网络请求
 * */
public class UploadPicImpl extends AbstractNet {

	public UploadPicImpl(Activity act) {
		super(act);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			
			map.put("fileUrl", parameter2.getString("fileUrl"));
			map.put("fileName", parameter2.getString("fileName"));
//			 category：分类 1. 用户凭证 2. 渠道凭证 3. 喵喵微店
//			 source：文件来源 1. web 2. app 3. 喵喵微店
//			 tag：图片标签(avatar: 头像, qmm: 喵喵微店, showcase: 商品/服务展示图片)
			map.put("category", parameter2.getString("category"));
			map.put("source", parameter2.getString("source"));
			map.put("tag", parameter2.getString("tag"));
//			if(WxShopApplication.app.miaomiaoUploadServer!=null && !WxShopApplication.app.miaomiaoUploadServer.equals("")){
//				map.put(ConstValue.REQUEST_URL,WxShopApplication.app.miaomiaoUploadServer);
//			}else{
//				map.put(ConstValue.REQUEST_URL,WDConfig.PIC_UPLOAD);
//			}
				map.put(ConstValue.REQUEST_URL,WDConfig.getInstance().getQFUploadServer(ConstValue.HTTP_POST));

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST_FILE);
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
//		bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {
					JSONObject obj = root.getJSONObject("data");
					String url = "";
					if(obj.has("url")){
						url = obj.getString("url");
					}
					bundle.putString("url", url);
					
				}  else if(resultState.startsWith("21")){
					bundle.putString(ConstValue.ERROR_MSG, root.getString("resperr"));
					
				}else{
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
