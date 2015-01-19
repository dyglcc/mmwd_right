package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.ImageWrapper;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.data.netImpl.EdititemService;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.processer.ImageType;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.fragment.ShopFragmentsWrapper;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@EActivity(R.layout.web_common_fragment)
public class ShopHeaderPreviewActivity extends BaseActivity {
	@ViewById WebView webView;
	
	@Bean RetrofitWrapper retrofitWrapper;
	@Bean QFImageUploader imageUploader;
	MyRefreshActionprovider refreshProvider;
	
	@Extra ImageWrapper wrapper;
	String imgUrl;
	
	@AfterViews void init() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.shopheader_preview_pagetitle);
		refreshProvider = new MyRefreshActionprovider(this);
		
		if (wrapper != null) {
			if (wrapper.hasUploaded()) {
				onNetDone(wrapper.getImgURL());
			} else {
				processImgForUpload(wrapper);
			}
		} else {
			Toaster.s(this, "预览出错啦!返回重新试试看吧~");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.headerimg_title_menu, menu);
		menu.findItem(R.id.menu_complete).setActionProvider(refreshProvider);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			backRetry();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		backRetry();
		super.onBackPressed();
	}
	
	void backRetry() {
		MobAgentTools.OnEventMobOnDiffUser(this, "CLICK_SHOP_PIC_RESELECTION");
		finish();
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE)
	void processImgForUpload(ImageWrapper wrapper) {
		startProgressDialog();
		String url = "";
		int count = 0;
		boolean isUploadSuccess = (url != null && !"".equals(url));
		while (!isUploadSuccess && count <= 3) {
			url = imageUploader.with(wrapper.getID()).path(wrapper.getImgFilePath()).imageType(ImageType.BIG).uploadSync();
			isUploadSuccess = (url != null && !"".equals(url));
			count ++;
		}
		if (isUploadSuccess) {
			wrapper.setImgPath(url);
			onNetDone(url);
			MobAgentTools.OnEventMobOnDiffUser(this, "upload_succeed");
		}
		stopProgressDialog();
	}
	
	@UiThread
	void onNetDone(String imgUrl) {
		this.imgUrl = imgUrl;
		String url = WDConfig.getInstance().getShopUrl() + WxShopApplication.dataEngine.getShopId() + "?tempbg=" + imgUrl;
		initWebview(url);
		wrapper.deleteCacheImg();
		if (wrapper.getImgFilePath().contains(ConstValue.getPICTURE_DIR())) {
			wrapper.deleteImgFile();
		}
	}
	
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled"})
	void initWebview(String url) {
		webView.setWebViewClient(new MyWebviewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		webView.loadUrl(url);
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE)
	void save() {
		startProgressDialog();
		EdititemService service = retrofitWrapper.getNetService(EdititemService.class);
		try {
			if (imgUrl != null) {
				CommonJsonBean bean = service.renewShopBG(imgUrl);
				if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
					onNetDone();
				} else {
					showErrorMsg(bean.getResperr());
				}
			} else {
				showErrorMsg("出现未知异常,请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMsg("网络异常,请检查网络后重试");
		}
		stopProgressDialog();
	}
	
	@UiThread
	void onNetDone() {
		WxShopApplication.dataEngine.setShopBg(imgUrl);
		
		setResult(RESULT_OK, new Intent(this, ShopHeaderPhotoPickerActivity.class));
		finish();
		MobAgentTools.OnEventMobOnDiffUser(this, "SELECT_SHOP_PIC_SUCCESS");
		
		ShopFragmentsWrapper.PREVIEW.refresh();
	}
	
	@UiThread
	void startProgressDialog() {
		refreshProvider.startProgress();
	}
	
	@UiThread
	void stopProgressDialog() {
		refreshProvider.stopProgress();
	}
	
	@UiThread
	void showErrorMsg(String msg) {
		Toaster.s(this, msg);
	}
	
	class MyWebviewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
		
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			startProgressDialog();
		}
		
		public void onPageFinished(WebView view, String url) {
			stopProgressDialog();
		}
		
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			stopProgressDialog();
		}
	}
	
	@SuppressLint("InflateParams") class MyRefreshActionprovider extends ActionProvider {
		FrameLayout wrapper;
		ProgressBar refresh;
		TextView    complete;
		
		public MyRefreshActionprovider(Context context) {
			super(context);
			wrapper = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.common_menuitem_refreshcomplete, null);
			refresh = (ProgressBar) wrapper.findViewById(R.id.pb_refresh);
			complete = (TextView) wrapper.findViewById(R.id.tv_complete);
		}

		@Override
		public View onCreateActionView() {
			complete.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					save();
				}
			});
			return wrapper;
		}
		
		public void startProgress() {
			refresh.setVisibility(View.VISIBLE);
			complete.setVisibility(View.GONE);
		}
		
		public void stopProgress() {
			refresh.setVisibility(View.GONE);
			complete.setVisibility(View.VISIBLE);
		}
	}
}
