package qfpay.wxshop.data.net;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.json.JSONException;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.LoginActivity;
import qfpay.wxshop.ui.view.CustomProgressDialog;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 网络处理基类
 */
public abstract class AbstractNet {

	public AbstractNet(Context act) {
		this.activity = act;
		bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
	}

	private CustomProgressDialog progressDialog;
	private Handler handler = null;
	private boolean cancel = false;
	// private Map<String, Object> mapParam = null;
	private NetRunable httpNet = null;
	private final static Semaphore mutex = new Semaphore(1, true);
	protected Bundle bundle = new Bundle();
	protected String dialogMessage = "请稍候...";//
	protected Context activity;
	private boolean needShowDialog = true;

	// protected Context context;

	/**
	 * 网络请求
	 * 
	 * @param parameter
	 *            传入的参数
	 * @param handler
	 *            回调
	 * @param requestId
	 *            请求ID
	 */
	public void setNoNeedShowDialog() {
		this.needShowDialog = false;
	}

	public void request(Bundle parameter, Handler handler) {
		if (activity != null) {
			showDoalog();
		}

		this.handler = handler;
		cancel = false;
		Map<String, Object> par = setRequestParameter(parameter);
		if (par == null) {
			cancel = true;
			return;
		}
		// mapParam = par;
		// 提交的参数有错误
		if ((Integer) par.get(ConstValue.NET_RETURN) == ConstValue.NET_RETURN_PARAMETER) {
			Message msg = handler.obtainMessage();
			msg.what = (Integer) par.get(ConstValue.NET_RETURN);
			handler.sendMessage(msg);
			return;
		}

		if (!Utils.isCanConnectionNetWork(WxShopApplication.app)) {
			Message hmsg = handler.obtainMessage();
			hmsg.what = ConstValue.NET_RETURN_NO_CONNECT;
			Bundle d = new Bundle();
			hmsg.setData(d);
			handler.sendMessage(hmsg);
			return;
		}

		httpNet = new NetRunable(par);
		httpNet.start();
	}

	private void showDoalog() {
		// services undo
		if (activity instanceof Service) {
			return;
		}

		if (!needShowDialog) {
			return;
		}
		progressDialog = CustomProgressDialog.createDialog4BBS(activity);
		progressDialog.setMessage(dialogMessage);
		progressDialog.show();
	}

	/**
	 * 设置参数
	 * 
	 * @param parameter2
	 * @return
	 */
	protected abstract Map<String, Object> setRequestParameter(Bundle parameter2);

	/**
	 * 进行json解析
	 * 
	 * @param jsonStr
	 *            未解析的字符串
	 * @return 解析结果在key为constvalue.json_return中说明，如果成功，同时把解析后的数据返回
	 */
	protected abstract Bundle jsonParse(String jsonStr);

	/**
	 * 中断网络
	 */
	public void stop() {
		try {
			mutex.acquire();
			cancel = true;
			if (httpNet != null) {
				httpNet.stopNet();
			}
		} catch (Exception ex) {

		} finally {
			mutex.release();
		}
	}

	private void callback(Handler handler, Message msg) {

		// 隐藏dialog

		if (activity != null) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
		try {
			mutex.acquire();
			if (cancel) {
				msg.what = ConstValue.NET_RETURN_CANCEL;
			}
			handler.sendMessage(msg);
		} catch (Exception ex) {

		} finally {
			mutex.release();
		}
	}

	/**
	 * 网络回调
	 */
	Handler netHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			Message hmsg = handler.obtainMessage();
			if (cancel) {
				hmsg.what = ConstValue.NET_RETURN_CANCEL;
				handler.sendMessage(hmsg);
				return;
			}
			if (data != null) {
				int response = data.getInt(ConstValue.NET_RETURN);
				if (response == ConstValue.NET_RETURN_SUCCESS) {
					// 如果是json数据，要经过json解析
					String returnData = data
							.getString(ConstValue.NET_RETURN_DATA);

					try {
						if (returnData != null && !returnData.equals("")) {
							org.json.JSONObject obj = new org.json.JSONObject(
									returnData);
							String resultState = obj.getString("respcd");
							if (resultState.equals("2002")
									|| resultState.equals("2100")) {
								if (activity != null) {
									WxShopApplication.app.closeAllActivity();
									Intent intent = new Intent(activity,
											LoginActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									activity.startActivity(intent);
									return;
								}
							}
						}
					} catch (JSONException e) {
						T.e(e);
					}

					Bundle jsonReturn = jsonParse(returnData);
					// this.checkTimeout(jsonReturn);
					if (jsonReturn.getInt(ConstValue.JSON_RETURN) == ConstValue.JSON_SUCCESS) {
						hmsg.what = ConstValue.NET_RETURN_SUCCESS;
					} else {
						hmsg.what = ConstValue.NET_RETURN_ERROR;
					}
					hmsg.setData(jsonReturn);
					callback(handler, hmsg);
				} else {
					// 不成功
					hmsg.what = response;
					hmsg.setData(data);
					callback(handler, hmsg);
				}
			}

		}
	};

	private class NetRunable extends Thread {
		@SuppressWarnings("rawtypes")
		private Map par = null;
		private HttpNet http = null;
		private boolean isstop = false;

		public NetRunable(@SuppressWarnings("rawtypes") Map b) {
			par = b;
		}

		public void stopNet() {
			if (!isstop) {
				if (http != null) {
					http.stop();
				}
			}
		}

		@SuppressWarnings("unchecked")
		public void run() {
			if (par == null) {
				return;
			}
			Bundle response = null;

			http = new HttpNet();

			String requestUrl = String.valueOf(par.get(ConstValue.REQUEST_URL));
			String method = String.valueOf(par.get(ConstValue.HTTP_METHOD));
			par.remove(ConstValue.REQUEST_URL);
			par.remove(ConstValue.NET_RETURN);
			par.remove(ConstValue.HTTP_METHOD);
			if (ConstValue.HTTP_POST.equals(method)) {
				response = http.postRequest(requestUrl, par);
			} else if (ConstValue.HTTP_GET.equals(method)) {
				response = http.getRequest(requestUrl, par);
			} else if (ConstValue.HTTPS_POST.equals(method)) {
				response = http.postRequest(requestUrl, par);
			} else if (ConstValue.FENGXIANG_POST.equals(method)) {
				response = http.postFengXiangRequest(requestUrl, par);
			} else if (ConstValue.HTTP_POST_FILE.equals(method)) {
				response = http.postSingleFile(requestUrl, par);
			} else if (ConstValue.HTTP_POST_QINIU.equals(method)) {
				response = http.postQiNiu(requestUrl, par);
			} else if (ConstValue.HTTP_DELETE.equals(method)) {
				response = http.delRequest(requestUrl, par);
			} else if (ConstValue.HTTP_PUT.equals(method)) {
				response = http.putRequest(requestUrl, par);
			}
			// else if (ConstValue.HTTP_POST_MULT_PARA.equals(method)) {
			// response = http.postSingleFile4Multpara(requestUrl, par);
			// }
			Message msg = netHandler.obtainMessage();
			msg.setData(response);
			netHandler.sendMessage(msg);
			isstop = true;

		}
	}
}
