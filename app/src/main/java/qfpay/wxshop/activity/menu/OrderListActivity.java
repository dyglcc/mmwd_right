package qfpay.wxshop.activity.menu;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.LoginActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderListActivity extends BaseActivity {

	private View failView;
	private WebView webView = null;
	private String sessionCookie;
	private LinearLayout llyt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_publish_goods);
		MobAgentTools.OnEventMobOnDiffUser(this, "Order management");
		llyt = (LinearLayout) findViewById(R.id.lyt_publish);
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText(getString(R.string.order_manage));
		failView = findViewById(R.id.load_fail);

		layout_progress_load = findViewById(R.id.layout_progress_load);

		ivProgress= (ImageView) findViewById(R.id.iv_progress_load);
		
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();

				} else {
					finish();
//					overridePendingTransition(R.anim.in_from_down, R.anim.quit);
				}
				// finish();
				// overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			}
		});
		// CookieSyncManager.createInstance(this);
		// CookieManager cookieManager = CookieManager.getInstance();
		// // if (sessionCookie != null) {
		// cookieManager.removeSessionCookie();
		// String cookieString ="sessionid="+
		// WxShopApplication.dataEngine.getcid() +"; domain="
		// + WDConfig.domain;
		// // String cookieString = WxShopApplication.dataEngine.getcid()
		// +"; domain="
		// // + sessionCookie.getDomain();
		// cookieManager.setCookie(WDConfig.ODERLIST_URL, cookieString);
		// CookieSyncManager.getInstance().sync();
		// }
		webView = (WebView) this.findViewById(R.id.contact_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(WDConfig.ERROR_PAGE)) {

					WxShopApplication.app.closeAllActivity();
					startActivity(new Intent(OrderListActivity.this,
							LoginActivity.class));

					return true;
				}

				Utils.setCookies(url, OrderListActivity.this);

				webView.loadUrl(url);

				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// webView.setVisibility(View.INVISIBLE);

				startProgressDialog();

			}

			public void onPageFinished(WebView view, String url) {

				if (!Utils.isCanConnectionNetWork(OrderListActivity.this)) {
					webView.setVisibility(View.INVISIBLE);
					failView.setVisibility(View.VISIBLE);
				} else {
					webView.setVisibility(View.VISIBLE);
					failView.setVisibility(View.INVISIBLE);
				}
				stopProgressDialog();

			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				webView.setVisibility(View.INVISIBLE);

				stopProgressDialog();
				failView.setVisibility(View.VISIBLE);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(
						OrderListActivity.this)
						.setTitle(getString(R.string.hint))
						.setMessage(message)
						.setPositiveButton("ok",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								});

				b2.setCancelable(false);
				b2.create();
				b2.show();
				return true;
			}
		});
		Utils.setCookies(WDConfig.getInstance().getOrderListURL(""), OrderListActivity.this);
		webView.loadUrl(WDConfig.getInstance().getOrderListURL(""));
	}


	private void startProgressDialog() {
		layout_progress_load.setVisibility(View.VISIBLE);

	}

	private void stopProgressDialog() {
		layout_progress_load.setVisibility(View.GONE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();

			} else {
				finish();
//				overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private View layout_progress_load;

	private ImageView ivProgress;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			AnimationDrawable animation = (AnimationDrawable) ivProgress
					.getBackground();
			animation.start();
		}
	}
	
}
