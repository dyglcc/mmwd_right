package qfpay.wxshop.ui.BusinessCommunity;


import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.utils.Utils;

@EActivity(R.layout.activity_shop_detail)
public class ShopDetailActivity extends BaseActivity {
    @Extra
    String shopUrl;
    @ViewById
    WebView webView;
    @ViewById
    FrameLayout fl_indictor;
    @DrawableRes
    Drawable commodity_list_refresh;
    @ViewById
    ImageView iv_indictor;
    @ViewById
    LinearLayout ll_fail;
    private Map<String, String> header = new HashMap<String, String>();
    com.actionbarsherlock.app.ActionBar bar;

    @AfterViews
    void init() {
        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
        webView.setWebViewClient(new MyWebClient());
        webView.loadUrl(shopUrl, header);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setPageState(PageState.LOADING);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (ShopDetailActivity.this == null) {
                return;
            }
            if (!Utils.isCanConnectionNetWork(ShopDetailActivity.this)) {
                setPageState(PageState.ERROR);
            } else {
                setPageState(PageState.COMPLETE);
                bar.setTitle(webView.getTitle());
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            setPageState(PageState.ERROR);
        }
    }

    private void setPageState(PageState state) {
        if (state == PageState.COMPLETE) {
            webView.setVisibility(View.VISIBLE);
            ll_fail.setVisibility(View.GONE);
            fl_indictor.setVisibility(View.GONE);
        }
        if (state == PageState.ERROR) {
            webView.setVisibility(View.INVISIBLE);
            ll_fail.setVisibility(View.VISIBLE);
            fl_indictor.setVisibility(View.GONE);
        }
        if (state == PageState.LOADING) {
            webView.setVisibility(View.INVISIBLE);
            ll_fail.setVisibility(View.GONE);
            fl_indictor.setVisibility(View.VISIBLE);
            iv_indictor.setImageDrawable(commodity_list_refresh);
            ((AnimationDrawable) (commodity_list_refresh)).start();
        }
    }

    enum PageState {
        COMPLETE, ERROR, LOADING
    }
}
