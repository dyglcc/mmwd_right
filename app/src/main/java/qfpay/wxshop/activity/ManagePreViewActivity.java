package qfpay.wxshop.activity;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.BaseActivity;

import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

public class ManagePreViewActivity extends BaseActivity {
    private View failView;
    WebView webView = null;
    private TextView tvTitle;
    private ImageView ivProgress;

    @ViewById
    LinearLayout ll_fail;
    @DrawableRes
    Drawable commodity_list_refresh;
    @ViewById
    ImageView iv_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_preview_webview);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        String title = getIntent().getStringExtra(ConstValue.TITLE);
        tvTitle.setText(title == null ? getResources().getString(R.string.title) : title);

        boolean isLoadFirst = getIntent().getBooleanExtra("isLoadFirst", true);
        if (isLoadFirst) {
            String url = getIntent().getStringExtra(ConstValue.URL);
            if (url == null || url.equals("")) {
                Toaster.l(ManagePreViewActivity.this, getString(R.string.wrong_address));
                return;
            }
            init(url);
        }
    }

    void init(String url) {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                Utils.setCookies(url, ManagePreViewActivity.this);
                webView.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setPageState(PageState.LOADING);
            }

            public void onPageFinished(WebView view, String url) {
                if (!Utils.isCanConnectionNetWork(ManagePreViewActivity.this)) {
                    setPageState(PageState.ERROR);
                } else {
                    setPageState(PageState.COMPLETE);
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                setPageState(PageState.ERROR);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(
                        ManagePreViewActivity.this)
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
                        ManagePreViewActivity.this)
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
        Utils.setCookies(url, ManagePreViewActivity.this);
        webView.loadUrl(url);
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    enum PageState {
        COMPLETE, ERROR, LOADING
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView == null) {
            finish();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();

            return true;
        } else {
            finish();
            return true;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            iv_loading.setImageDrawable(commodity_list_refresh);
            ((AnimationDrawable) (commodity_list_refresh)).start();
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
}

