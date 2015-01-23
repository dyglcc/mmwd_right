package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangeBankCardInfoActivity extends BaseActivity {

	private View failView;
	private WebView webView = null;
	private TextView tvTitle;

	private String url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_publish_goods);
		layout_progress_load = findViewById(R.id.layout_progress_load);

		ivProgress = (ImageView) findViewById(R.id.iv_progress_load);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
//				overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			}
		});
		// 取title
		Intent intent = getIntent();
		String title = intent.getStringExtra(ConstValue.TITLE);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title == null ? getResources()
				.getString(R.string.title) : title);
		// 取url
		url = intent.getStringExtra(ConstValue.URL);
		if (url == null || url.equals("")) {
			Toaster.l(ChangeBankCardInfoActivity.this,
					getString(R.string.wrong_address));
			return;
		}
		failView = findViewById(R.id.load_fail);

		webView = (WebView) this.findViewById(R.id.contact_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		webView.setDownloadListener(new MyWebViewDownLoadListener());

		webView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				Utils.setCookies(url, ChangeBankCardInfoActivity.this);

				webView.loadUrl(url);

				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				startProgressDialog();

			}

			public void onPageFinished(WebView view, String url) {

				if (!Utils
						.isCanConnectionNetWork(ChangeBankCardInfoActivity.this)) {
					webView.setVisibility(View.INVISIBLE);
					failView.setVisibility(View.VISIBLE);
				} else {
					webView.setVisibility(View.VISIBLE);
					failView.setVisibility(View.INVISIBLE);
				}

				tvTitle.setText(view.getTitle());

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
						ChangeBankCardInfoActivity.this)
						.setTitle(getString(R.string.hint))
						.setMessage(message)
						.setPositiveButton(
								getResources().getString(R.string.OK),
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

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(
						ChangeBankCardInfoActivity.this)
						.setTitle(getString(R.string.hint))
						.setMessage(message)
						.setNegativeButton(
								getResources().getString(R.string.cancel),
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								})
						.setPositiveButton(
								getResources().getString(R.string.OK),
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

		Utils.setCookies("qfpay.com", ChangeBankCardInfoActivity.this);

		webView.loadUrl("https://qfpay.com/mmwd/bankchange");

	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// LogUtil.i(this, "keyCode=" keyCode);
		if (webView == null) {
			finish();
			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();

			return true;
		} else {
			finish();
//			overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			return true;
		}
	}

	private View layout_progress_load;

	private ImageView ivProgress;

	private void startProgressDialog() {
		layout_progress_load.setVisibility(View.VISIBLE);

	}

	private void stopProgressDialog() {

		layout_progress_load.setVisibility(View.GONE);
	}

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
