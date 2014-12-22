package qfpay.wxshop.data.netImpl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.selectpic.ImageItem;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class UploadPicMul4QniuImpl extends UploadPicMulImpl {

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
	private String token;
	private Handler handler;
	private int pos;

	public UploadPicMul4QniuImpl(Activity act, ImageItem item, Handler handler,
			int pos, String token) {
		super(act,item,handler);
		// TODO Auto-generated constructor stub
		setNoNeedShowDialog();
		this.item = item;
		this.handler = handler;
		this.pos = pos;
		this.token = token;
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
			map.put("key", WxShopApplication.dataEngine.getUserId()
					+ System.currentTimeMillis() + pos + ".jpg");
			map.put("token", token);
			map.put("fileName", item.smallPicPath.substring(item.smallPicPath
					.lastIndexOf(File.separator)));

			map.put(ConstValue.REQUEST_URL,
					com.qiniu.upload.tool.Config.UP_HOST);

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST_QINIU);
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
			// "hash":"FpcEcfQBhiVDNrJMhDJCvdHqdgBH","key":"244431-1396192181600-0"},
			// response=1
			if (jsonStr != null && jsonStr.length() > 0) {
				try {
					JSONObject root = new JSONObject(jsonStr);
					if (root.has("key")) {
						setUploadEnd(true);
						setUploadSuccess(true);
						returnURL = "http://"
								+ com.qiniu.upload.tool.Config.domain + "/"
								+ root.getString("key");

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
