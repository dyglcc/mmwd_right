package qfpay.wxshop.ui.web.huoyuan;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewTreeObserver;
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

import com.actionbarsherlock.app.ActionBar;

@EFragment(R.layout.web_common_fragment_huoyuan)
public class CommonWebFragmentHuyuan extends BaseFragment {
	@ViewById
	WebView webView;
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
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 20);
		String webView_cache = ConstValue.getWebView_cache();
		File file  = new File(webView_cache);
		if(!file.exists()){
			file.mkdirs();
		}
		webView.getSettings().setAppCachePath(webView_cache);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(new MyWebViewClient());
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.setWebChromeClient(new MyWebChromeClient());
		header.put("Qfuid", WxShopApplication.dataEngine.getUserId());
		Utils.setCookies(url, getActivity());
		if (url != null && !"".equals(url)) {
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
			// Toaster.l(getActivity(), "exe function");
//			ActionBar supportActionBar = activity.getSupportActionBar();
//			View view = supportActionBar.getCustomView();
//			try {
//				view.setVisibility(View.INVISIBLE);
//			} catch (Exception e) {
//				T.i(e.toString());
//			}
//
//			Button btn_save = (Button) view.findViewById(R.id.btn_save);
//			try {
//				btn_save.setVisibility(View.VISIBLE);
//			} catch (Exception e) {
//				T.i(e.toString());
//			}
//			activity.supportInvalidateOptionsMenu();
//
//			view.setVisibility(View.VISIBLE);
//			// T.i("sdddddddddddddddddddddd");
//			// Toaster.l(getActivity(), "visiable");
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
		//
		if (getActivity() != null) {
			Utils.setCookies(url, getActivity());
		}

		if (webView != null) {
			webView.stopLoading();
			webView.loadUrl(url, header);
		}
		return this;
	}

	@Override
	public void onFragmentRefresh() {
		if (webView != null) {
			webView.loadUrl(url, header);
		}
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
				webView.loadUrl(url, header);
				CommonWebFragmentHuyuan.this.url = url;
			}

			
			Utils.setCookies(url, getActivity());
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
