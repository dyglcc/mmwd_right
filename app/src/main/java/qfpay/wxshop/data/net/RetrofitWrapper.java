package qfpay.wxshop.data.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

@EBean
public class RetrofitWrapper {
	public static final String SUCCESS_CODE = "0000";
	
	@RootContext Context context;
	Map<String, String> headerMap = new HashMap<String, String>();
	boolean isHandleError = true;

	public <T> T getNetService(Class<T> clazz) {
		String endpoint = WDConfig.getInstance().WD_URL.substring(0, WDConfig.getInstance().WD_URL.length() - 1);
		return getNetService(clazz, endpoint);
	}
	
	public <T> T getNetService(Class<T> clazz, String endPoiont) {
		LogLevel logLevel = LogLevel.NONE;
		if (qfpay.wxshop.utils.T.isTesting) logLevel = LogLevel.HEADERS_AND_ARGS;

		RestAdapter ra = new RestAdapter.Builder()
			.setEndpoint(endPoiont)
			.setLogLevel(logLevel)
			.setRequestInterceptor(new RetrofitRequestInterceptor())
			.setErrorHandler(new RetrofitErrorHandler())
			.build();
		return ra.create(clazz);
	}

	public RetrofitWrapper handleError(boolean isHandleError) {
		this.isHandleError = isHandleError;
		return this;
	}
	
	public RetrofitWrapper addHeader(String key, String value) {
		headerMap.put(key, value);
		return this;
	}
	
	public RetrofitWrapper cleanHeader() {
		headerMap.clear();
		return this;
	}
	
	class RetrofitRequestInterceptor implements RequestInterceptor {
		@Override
		public void intercept(RequestFacade facade) {
			if (!headerMap.isEmpty()) {
				Iterator<Entry<String, String>> iteratro = headerMap.entrySet().iterator();
				while (iteratro.hasNext()) {
					Entry<String, String> entry = iteratro.next();
					facade.addHeader(entry.getKey(), entry.getValue());
				}
			} else {
				facade.addHeader("Cookie", "sessionid = " + WxShopApplication.dataEngine.getcid());
				facade.addHeader("User-Agent", WxShopApplication.dataEngine.getUserAgent());
			}
		}
	}
	
	class RetrofitErrorHandler implements ErrorHandler {
		@Override @SuppressWarnings("deprecation")
		public Throwable handleError(RetrofitError error) {
			if (isHandleError) {
				if (error.isNetworkError()) {
					showErrorMsg("网络连接失败,请检查网络是否连通~");
				} else {
					showErrorMsg("服务器异常,请稍后重试或联系瞄瞄客服~");
				}
			}
			return error;
		}
	}
	
	@UiThread
	void showErrorMsg(String msg) {
		if (context instanceof Activity || context instanceof Service) {
			Toaster.s(context, msg);
		}
	}
	
	public static class CommonJsonBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String resperr = "";
		private String respcd = "";
		private String respmsg = "";
		
		public String getResperr() {
			return resperr;
		}
		public void setResperr(String resperr) {
			this.resperr = resperr;
		}
		public String getRespcd() {
			return respcd;
		}
		public void setRespcd(String respcd) {
			this.respcd = respcd;
		}
		public String getRespmsg() {
			return respmsg;
		}
		public void setRespmsg(String respmsg) {
			this.respmsg = respmsg;
		}
	}
}
