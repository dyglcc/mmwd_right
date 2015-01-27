package qfpay.wxshop.ui.web.huoyuan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.view.WebViewSavePic;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

@EFragment(R.layout.web_common_fragment_huoyuan)
public class CommonWebFragmentHuyuan extends BaseFragment {
	@ViewById
    WebViewSavePic webView;
	@ViewById
	LinearLayout ll_fail;
	@ViewById
	ImageView iv_loading;

	private View viewButton;

	private String url = "";
	private boolean isNeedLoadNewActivity = false;
	private List<SharedPlatfrom> platFroms;
	private String shareTitle = "", shareName = "";

	public void setCompeleteButton(Button button) {
		viewButton = button;
	}

	private Map<String, String> header = new HashMap<String, String>();

	@AfterViews
	@SuppressLint("SetJavaScriptEnabled")
	void init() {
		if (webView == null) {
			return;
		}
		// 创建缓存文件夹
//		webView.getSettings().setDomStorageEnabled(true);
//		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 20);
//		String webView_cache = ConstValue.getWebView_cache();
//		File file  = new File(webView_cache);
//		if(!file.exists()){
//			file.mkdirs();
//		}
//		webView.getSettings().setAppCachePath(webView_cache);
//		webView.getSettings().setAllowFileAccess(true);
//		webView.getSettings().setAppCacheEnabled(true);
//		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setDefaultTextEncodingName("utf-8");
//		webView.getSettings().setBuiltInZoomControls(false);
//		webView.getSettings().setSupportZoom(false);
//		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);


        header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
        header.put("qf_uid", WxShopApplication.dataEngine.getcid());

		webView.setWebViewClient(new MyWebViewClient());
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.setWebChromeClient(new MyWebChromeClient());
//		header.put("QFUID", "id="+WxShopApplication.dataEngine.getUserId());
//		Utils.setCookies(url, getActivity());
		if (url != null && !"".equals(url)) {
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            header.put("qf_uid", WxShopApplication.dataEngine.getcid());
			webView.loadUrl(url, header);
		}

		webView.addJavascriptInterface(new callCameroJavaScriptInterface(),
				"android_finish_page");
	}

	private class callCameroJavaScriptInterface {
		@JavascriptInterface
		public void clickOnAndroid() {
			if (getActivity() == null) {
				return;
			}

			CommonWebActivityHuoyuan activity = (CommonWebActivityHuoyuan) getActivity();
			activity.setVisiable();
		}
	}

	public CommonWebFragmentHuyuan init(String url,
			boolean isNeedLoadNewActivity) {
		return init(url, isNeedLoadNewActivity, null, null,
				new SharedPlatfrom[0]);
	}

	public CommonWebFragmentHuyuan init(String url,
			boolean isNeedLoadNewActivity, String shareTitle, String shareName,
			SharedPlatfrom... platForms) {
		this.url = url;
		this.isNeedLoadNewActivity = isNeedLoadNewActivity;
		this.shareName = shareName;
		this.shareTitle = shareTitle;
		this.platFroms = Arrays.asList(platForms);

		if (webView != null) {
			webView.stopLoading();
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            header.put("qf_uid", WxShopApplication.dataEngine.getcid());
			webView.loadUrl(url, header);
		}
		return this;
	}


	public boolean isCanGoback() {
		return webView.canGoBack();
	}

	public void goBack() {
		if (webView != null && webView.canGoBack()) {
			webView.goBack();
		}
	}

	private void setPageState(PageState state) {
		if (state == PageState.COMPLETE) {
			webView.setVisibility(View.VISIBLE);
			ll_fail.setVisibility(View.GONE);
			iv_loading.setVisibility(View.GONE);
		}
		if (state == PageState.ERROR) {
			webView.setVisibility(View.INVISIBLE);
			ll_fail.setVisibility(View.VISIBLE);
			iv_loading.setVisibility(View.GONE);
		}
		if (state == PageState.LOADING) {
			webView.setVisibility(View.INVISIBLE);
			ll_fail.setVisibility(View.GONE);
			iv_loading.setVisibility(View.VISIBLE);
		}
	}

	enum PageState {
		COMPLETE, ERROR, LOADING
	}

	private void startWebActivity(String url) {
		if (webView == null) {
			return;
		}
		if (shareTitle == null || "".equals(shareTitle)) {
			CommonWebActivity_.intent(getActivity()).url(url)
					.title(webView.getTitle()).start();
		} else {
			CommonWebActivity_.intent(getActivity()).url(url)
					.title(webView.getTitle()).shareName(this.shareName)
					.shareTitle(this.shareTitle).platFroms(this.platFroms)
					.start();
		}
	}

	class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (isNeedLoadNewActivity) {
				startWebActivity(url);
			} else {
                header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
                header.put("qf_uid", WxShopApplication.dataEngine.getcid());
				webView.loadUrl(url, header);
				CommonWebFragmentHuyuan.this.url = url;
			}

			return true;
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			setPageState(PageState.LOADING);
		}

		public void onPageFinished(WebView view, String url) {
			if (getActivity() == null) {
				return;
			}
			if (!Utils.isCanConnectionNetWork(getActivity())) {
				setPageState(PageState.ERROR);
			} else {
				setPageState(PageState.COMPLETE);
			}
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			setPageState(PageState.ERROR);
		}
	}

	class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			if (getActivity() == null) {
				return true;
			}
			AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity())
					.setTitle(getString(R.string.hint))
					.setMessage(message)
					.setPositiveButton(getResources().getString(R.string.OK),
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
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			if (getActivity() == null) {
				return true;
			}
			AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity())
					.setTitle(getString(R.string.hint))
					.setMessage(message)
					.setNegativeButton(
							getResources().getString(R.string.cancel),
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							})
					.setPositiveButton(getResources().getString(R.string.OK),
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
}
