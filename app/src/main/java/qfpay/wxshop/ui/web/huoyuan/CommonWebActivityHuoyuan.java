package qfpay.wxshop.ui.web.huoyuan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.getui.ImageUtils.ImageSizeForUrl;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.common.actionbar.ShareActionProvider;
import qfpay.wxshop.ui.main.MainTab;
import qfpay.wxshop.ui.main.fragment.HuoYuanFragment;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.WebViewSavePic;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
@EActivity(R.layout.web_common_activity_huoyuan)
public class CommonWebActivityHuoyuan extends BaseActivity implements
		OnShareLinstener {
	@ViewById
	Button btn_back,btn_save;
	@ViewById
	TextView tv_title;


	@Extra
	protected String url = "";
	@Extra
	protected String title = "";
	@Extra
	String push = "";
	@Extra
	List<SharedPlatfrom> platFroms;
	@Extra
	String shareTitle = "", shareName = "";

    @ViewById
    WebViewSavePic webView;
    @ViewById
    LinearLayout ll_fail;
    @ViewById
    ImageView iv_loading;

    private View viewButton;

    @DrawableRes
    Drawable commodity_list_refresh;

    public void setCompeleteButton(Button button) {
        viewButton = button;
    }

    private Map<String, String> header = new HashMap<String, String>();

	@AfterViews
	void init() {
		ActionBar bar = getSupportActionBar();
		bar.hide();
		setCustomView();

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
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);


        header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
        header.put("qf_uid", WxShopApplication.dataEngine.getUserId());

        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(new MyWebChromeClient());
        if (url != null && !"".equals(url)) {
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            header.put("qf_uid", WxShopApplication.dataEngine.getUserId());
            Toaster.l(this,"header:" + header.toString());
            webView.loadUrl(url, header);
        }

        webView.addJavascriptInterface(new callCameroJavaScriptInterface(),
                "android_finish_page");

	}
	@UiThread
	public void setVisiable(){
		if(btn_save!=null){
			btn_save.setVisibility(View.VISIBLE);
			supportInvalidateOptionsMenu();
		}
	}

    private class callCameroJavaScriptInterface {
        @JavascriptInterface
        public void clickOnAndroid() {

            setVisiable();
        }
    }
	public void setCustomView(){
		btn_back = (Button)findViewById(R.id.btn_back);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText(title);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				go2Myhuoyuan();
			}
		});
//		btn_save.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(btn_save.getVisibility() == View.VISIBLE){
					go2Myhuoyuan();
				}else{
					finish();
				}

			}
		});
	}
	public void go2Myhuoyuan(){
		HuoYuanFragment fragment = (HuoYuanFragment) MainTab.HUOYUAN
                .getFragment();
        fragment.changePager(1);
//        WxShopApplication.IS_NEED_REFRESH_MINE_HUOYUAN = true;
		Intent intent = new Intent();
		intent.putExtra("result", MaijiaxiuFragment.ACTION_HUOYUAN_ADD);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}




    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.webview_share, menu);
		MenuItem shareItem = menu.findItem(R.id.menu_share);
		if (platFroms != null) {
			ShareActionProvider shareActionProvider = new ShareActionProvider(
					this, shareItem, this, platFroms);
			shareItem.setActionProvider(shareActionProvider);
		} else {
			menu.removeItem(R.id.menu_share);
		}
		return super.onCreateOptionsMenu(menu);
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(webView != null){
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				finish();
			}
		}
	}


	@Override
	public void onShare(SharedPlatfrom which) {
		switch (which) {
		case WXFRIEND:
			shareWxFriend();
			break;
		case WXMOMENTS:
			shareWxMoments();
			break;
		default:
			break;
		}
		putShareEvent(getShareFromName());
	}

	@Override
	public String getShareFromName() {
		return shareName;
	}

	private void shareWxFriend() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = webView.getUrl();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl(
				"http://qmmwx.u.qiniudn.com/icon.png", ImageSizeForUrl.MIN);
		wdb.title = shareTitle;
		wdb.description = webView.getTitle();
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb,
				ConstValue.android_mmwdapp_manageshare_wctimeline, this);
	}

	private void shareWxMoments() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = webView.getUrl();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl(
				"http://qmmwx.u.qiniudn.com/icon.png", ImageSizeForUrl.MIN);
		wdb.title = shareTitle;
		wdb.description = webView.getTitle();
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb,
				ConstValue.android_mmwdapp_manageshare_wcfriend, this);
	}

	private void putShareEvent(String shareName) {
		if (ConstValue.SHARE_NAME_FINDMIAO.equals(shareName)) {
			MobAgentTools.OnEventMobOnDiffUser(this,
					"Click_faixiangemiao_fenxiang");
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            if(iv_loading!=null){
                AnimationDrawable  animationDrawable = (AnimationDrawable) iv_loading.getDrawable();
                animationDrawable.start();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Toaster.l(this,"long press");
        return super.onKeyLongPress(keyCode, event);
    }

    class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
            header.put("qf_uid", WxShopApplication.dataEngine.getUserId());
            Utils.setCookies(url, CommonWebActivityHuoyuan.this);
            Toaster.l(CommonWebActivityHuoyuan.this,"header:" + header.toString());
            webView.loadUrl(url, header);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setPageState(PageState.LOADING);
        }

        public void onPageFinished(WebView view, String url) {
            if (!Utils.isCanConnectionNetWork(CommonWebActivityHuoyuan.this)) {
                setPageState(PageState.ERROR);
            } else {
                setPageState(PageState.COMPLETE);
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            setPageState(PageState.ERROR);
        }

//        @SuppressLint("NewApi")
//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView view,String url) {
//            // 非超链接(如Ajax)请求无法直接添加请求头，现拼接到url末尾,这里拼接一个imei作为示例
//
//            String ajaxUrl = url;
//            // 如标识:req=ajax
//            if (url.contains("req=ajax")) {
//                ajaxUrl += "&imei=" + "nimeidsdfisdf";
//            }
//
//            return super.shouldInterceptRequest(view, ajaxUrl);
//
//        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder b2 = new AlertDialog.Builder(CommonWebActivityHuoyuan.this).setTitle(getString(R.string.hint))
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
            AlertDialog.Builder b2 = new AlertDialog.Builder(CommonWebActivityHuoyuan.this).setTitle(getString(R.string.hint))
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
    enum PageState {
        COMPLETE, ERROR, LOADING
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
