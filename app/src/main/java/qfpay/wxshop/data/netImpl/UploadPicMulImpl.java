package qfpay.wxshop.data.netImpl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.selectpic.ImageItem;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class UploadPicMulImpl extends AbstractNet {

	public static final int MAX_TIMES = 2;
	private boolean uploadEnd = false;
	private boolean uploadSuccess;
	private int uploadTimes;

	public int getUploadTimes() {
		return uploadTimes;
	}

	public void setUploadTimes(int uploadTimes) {
		this.uploadTimes = uploadTimes;
	}

	private String returnURL;
	private Handler handler;

	public UploadPicMulImpl(Activity act, ImageItem item, Handler handler) {
		super(act);
		// TODO Auto-generated constructor stub
		setNoNeedShowDialog();
		this.item = item;
		this.handler = handler;
	}

	private ImageItem item;

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			if (uploadTimes >= MAX_TIMES) {
				setUploadSuccess(false);
				setUploadEnd(true);
				return null;
			}
			map.put("fileUrl", item.smallPicPath);
			map.put("fileName", item.smallPicPath.substring(item.smallPicPath
					.lastIndexOf(File.separator)+1));
			map.put("category", parameter2.getString("category"));
			map.put("source", parameter2.getString("source"));
			map.put("tag", parameter2.getString("tag"));

//			if(WxShopApplication.app.miaomiaoUploadServer!=null && !WxShopApplication.app.miaomiaoUploadServer.equals("")){
//				map.put(ConstValue.REQUEST_URL,WxShopApplication.app.miaomiaoUploadServer);
//			}else{
//				map.put(ConstValue.REQUEST_URL,WDConfig.PIC_UPLOAD);
//			}
			map.put(ConstValue.REQUEST_URL,WDConfig.getInstance().getQFUploadServer(ConstValue.HTTP_POST_FILE));
			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST_FILE);
			/** 参数正确 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);

			// 请求次数增加
			uploadTimes++;
		} catch (Exception e) {
			T.e(e);
			/** 请求参数错误 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
		}
		return map;
	}

	@Override
	protected Bundle jsonParse(String jsonStr) {

		// 避免重复执行handler
		synchronized (this) {
			
			if (jsonStr != null && jsonStr.length() > 0) {
				try {
					JSONObject root = new JSONObject(jsonStr);
					String resultState = root.getString("respcd");
					if (resultState.equals("0000")) {
						JSONObject obj = root.getJSONObject("data");
						if (obj.has("url")) {
							setUploadEnd(true);
							setUploadSuccess(true);
							returnURL = obj.getString("url");
						}

					} else if (resultState.startsWith("21")) {
						bundle.putString(ConstValue.ERROR_MSG,
								root.getString("resperr"));

					} else {
						bundle.putInt(ConstValue.JSON_RETURN,
								ConstValue.JSON_FAILED);
						bundle.putString(ConstValue.ERROR_MSG, "未知错误，请重试");
						T.i("jsonStr is null or jsonStr.length is 0");
						return bundle;
					}
					Long key = System.currentTimeMillis();
					/** 界面上展示的时候直接根据key取存储类的数据 */
					bundle.putString(ConstValue.CACHE_KEY, key + "");
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_SUCCESS);
				} catch (Exception e) {
					T.e(e);
					bundle.putInt(ConstValue.JSON_RETURN,
							ConstValue.JSON_FAILED);
				}
			} else {
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
				T.i("jsonStr is null or jsonStr.length is 0");
			}
			handler.sendEmptyMessage(ConstValue.CHECK_UPLOAD_STATUS);
		}

		return bundle;
	}

	public String getReturnURL() {
		// TODO Auto-generated method stub
		return returnURL;
	}

	public boolean isUploadEnd() {
		return uploadEnd;
	}

	public void setUploadEnd(boolean uploadEnd) {
		this.uploadEnd = uploadEnd;
	}

	public boolean isUploadSuccess() {
		return uploadSuccess;
	}

	public void setUploadSuccess(boolean uploadSuccess) {
		this.uploadSuccess = uploadSuccess;
	}
}
