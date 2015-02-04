package qfpay.wxshop.ui.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

@EFragment(R.layout.web_common_fragment)
public class CommonWebFragment extends BaseFragment {
    @ViewById
    qfpay.wxshop.ui.view.WebViewSavePic webView;
    @ViewById
    LinearLayout ll_fail;
    @ViewById
    ImageView iv_loading;
    @DrawableRes
    Drawable commodity_list_refresh;

    private String url = "";
    private boolean isNeedLoadNewActivity = false;
    private List<SharedPlatfrom> platFroms;
    private String shareTitle = "",
            shareName = "",
            title = "";

    private Map<String, String> header = new HashMap<String, String>();

    @AfterViews
    @SuppressLint("SetJavaScriptEnabled")
    void init() {
        if (webView == null) {
            return;
        }

        iv_loading.setImageDrawable(commodity_list_refresh);
        ((AnimationDrawable) (commodity_list_refresh)).start();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(new MyWebChromeClient());

        header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
        Utils.setCookies(url, getActivity());
        if (url != null && !"".equals(url)) {
            webView.loadUrl(url, header);
        }
    }
    public CommonWebFragment init(String url, boolean isNeedLoadNewActivity) {
        return init(url, isNeedLoadNewActivity, null, null, new SharedPlatfrom[0]);
    }
    public CommonWebFragment init(String url, boolean isNeedLoadNewActivity, String title) {
        return init(url, isNeedLoadNewActivity, null, null, title, new SharedPlatfrom[0]);
    }
    public CommonWebFragment init(String url, boolean isNeedLoadNewActivity, String shareTitle, String shareName, SharedPlatfrom... platForms) {
        this.url = url;
        this.isNeedLoadNewActivity = isNeedLoadNewActivity;
        this.shareName = shareName;
        this.shareTitle = shareTitle;
        this.platFroms = Arrays.asList(platForms);

        if (getActivity() != null) {
            Utils.setCookies(url, getActivity());
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            if (url != null && !"".equals(url)) {
                webView.loadUrl(url,header);
            }
        }

        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl(url,header);
        }
        return this;
    }
    public CommonWebFragment init(String url, boolean isNeedLoadNewActivity, String shareTitle, String shareName, String title, SharedPlatfrom... platForms) {
        this.url = url;
        this.isNeedLoadNewActivity = isNeedLoadNewActivity;
        this.shareName = shareName;
        this.shareTitle = shareTitle;
        this.title = title;
        this.platFroms = Arrays.asList(platForms);

        if (getActivity() != null) {
            Utils.setCookies(url, getActivity());
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            if (url != null && !"".equals(url)) {
                webView.loadUrl(url, header);
            }
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
            iv_loading.setImageDrawable(commodity_list_refresh);
            ((AnimationDrawable) (commodity_list_refresh)).start();
        }
    }

    enum PageState {
        COMPLETE, ERROR, LOADING
    }

    private void startWebActivity(String url) {
        if (webView == null) {
            return;
        }
        if (title == null || title.equals("")) {
            title = webView.getTitle();
        }
        if (shareTitle == null || "".equals(shareTitle)) {
            CommonWebActivity_.intent(getActivity()).url(url).title(title).start();
        } else {
            CommonWebActivity_.intent(getActivity()).url(url).title(title)
                    .shareName(this.shareName).shareTitle(this.shareTitle).platFroms(this.platFroms).start();
        }
    }

    class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isNeedLoadNewActivity) {
                startWebActivity(url);
            } else {
                webView.loadUrl(url, header);
                CommonWebFragment.this.url = url;
            }
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
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

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            setPageState(PageState.ERROR);
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            if (getActivity() == null) {
                return true;
            }
            AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.hint))
                    .setMessage(message).setPositiveButton(getResources().getString(R.string.OK),
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            });
            b2.setCancelable(false);
            b2.create();
            b2.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            if (getActivity() == null) {
                return true;
            }
            AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.hint))
                    .setMessage(message).setNegativeButton(getResources().getString(R.string.cancel),
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .setPositiveButton(getResources().getString(R.string.OK),
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
