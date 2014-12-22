package qfpay.wxshop.ui.web;

import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.ui.BaseActivity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;

@EActivity(R.layout.web_activity)
public class WebActivity extends BaseActivity {
	@ViewById
	WebView webView;
	@ViewById
	LinearLayout ll_fail;

	@Extra
	String url = "";

	@ViewById
	Button btn_back;

	@ViewById
	TextView tv_title;

	@ViewById
	Button btn_save;
	private Map<String, String> header = new HashMap<String, String>();
	@AfterViews
	void init() {
		ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.hide();
		tv_title.setText("订单管理");
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
		new WebViewTask().execute();
	}
	
	

	@Click
	void btn_back() {
		
//		Intent intent = new Intent();
//		intent.putExtra("result", OrderFragment_.REFRESH);
//		setResult(Activity.RESULT_OK, intent);
		finish();
		
	}

	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
		String sessionCookie;
		CookieManager cookieManager;

		@Override
		protected void onPreExecute() {

			qfpay.wxshop.utils.Utils.setCookiesOrderList(url, WebActivity.this);
			super.onPreExecute();
		}

		protected Boolean doInBackground(Void... param) {
			// this is very important - THIS IS THE HACK
			SystemClock.sleep(1000);
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			webView.setWebViewClient(new WebViewClient() {

				public void onReceivedSslError(WebView view,
						SslErrorHandler handler, SslError error) {
					handler.proceed();
					webView.loadUrl(url,header);
					super.onReceivedSslError(view, handler, error);
				}

				public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					qfpay.wxshop.utils.Utils.setCookiesOrderList(url, WebActivity.this);
					webView.loadUrl(url,header);
					return true;
				}

				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
				}

				public void onPageFinished(WebView view, String url) {
				}

				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
				}
			});
//			qfpay.wxshop.utils.Utils.setCookiesOrderList(url, WebActivity.this);
			webView.loadUrl(url,header);
		}
	}
//	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
//		String sessionCookie;
//		CookieManager cookieManager;
//		
//		@Override
//		protected void onPreExecute() {
//			CookieSyncManager.createInstance(WebActivity.this);
//			cookieManager = CookieManager.getInstance();
//			
//			sessionCookie = WxShopApplication.dataEngine.getcid();
//			
//			if (sessionCookie != null) {
//				// delete old cookies
//				cookieManager.removeSessionCookie();
//			}
//			super.onPreExecute();
//		}
//		
//		protected Boolean doInBackground(Void... param) {
//			// this is very important - THIS IS THE HACK
//			SystemClock.sleep(1000);
//			return false;
//		}
//		
//		@Override
//		protected void onPostExecute(Boolean result) {
//			if (sessionCookie != null) {
//				cookieManager.removeAllCookie();
//				cookieManager.setCookie(url, sessionCookie);
//				CookieSyncManager.getInstance().sync();
//			}
//			
//			WebSettings webSettings = webView.getSettings();
//			webSettings.setJavaScriptEnabled(true);
//			webSettings.setDefaultTextEncodingName("utf-8");
//			webSettings.setBuiltInZoomControls(false);
//			webSettings.setSupportZoom(false);
//			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//			webSettings.setDomStorageEnabled(true);
//			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//			// webSettings.setUserAgent();
//			webView.setWebViewClient(new WebViewClient() {
//				
//				public void onReceivedSslError(WebView view,
//						SslErrorHandler handler, SslError error) {
//					handler.proceed();
//					webView.loadUrl(url,header);
//					super.onReceivedSslError(view, handler, error);
//				}
//				
//				public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					webView.loadUrl(url,header);
//					return true;
//				}
//				
//				public void onPageStarted(WebView view, String url,
//						Bitmap favicon) {
//				}
//				
//				public void onPageFinished(WebView view, String url) {
//				}
//				
//				public void onReceivedError(WebView view, int errorCode,
//						String description, String failingUrl) {
//				}
//			});
//			webView.loadUrl(url,header);
//		}
//	}

}
