package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.view.CustomProgressDialog;
import qfpay.wxshop.utils.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OtherShopViewActivity extends BaseActivity {

	private View failView;
	private WebView webView = null;
	private String url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_show_other_view);

		Intent intent = getIntent();
		url = intent.getStringExtra("url");

		failView = findViewById(R.id.load_fail);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
//				overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			}
		});

		webView = (WebView) this.findViewById(R.id.contact_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				webView.setVisibility(View.INVISIBLE);

				//startProgressDialog();

			}

			public void onPageFinished(WebView view, String url) {

				if (!Utils.isCanConnectionNetWork(OtherShopViewActivity.this)) {
					webView.setVisibility(View.INVISIBLE);
					failView.setVisibility(View.VISIBLE);
				} else {
					webView.setVisibility(View.VISIBLE);
					failView.setVisibility(View.INVISIBLE);
				}
				//stopProgressDialog();

			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				webView.setVisibility(View.VISIBLE);

				stopProgressDialog();
				failView.setVisibility(View.VISIBLE);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(
						OtherShopViewActivity.this)
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
		if (!url.equals("")) {
			webView.loadUrl(url);
		}
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});
	}

	private CustomProgressDialog progressDialog;

	private void startProgressDialog() {
		if (!isFinishing()) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog4BBS(OtherShopViewActivity.this);
				progressDialog.setMessage(getString(R.string.loading));
			}
			progressDialog.show();
		}

	}

	private void stopProgressDialog() {
		if (!isFinishing()) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}
}
