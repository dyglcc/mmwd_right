package qfpay.wxshop.ui.web.huoyuan;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
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
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
@EActivity(R.layout.web_common_activity_huoyuan)
public class CommonWebActivityHuoyuan extends BaseActivity implements
		OnShareLinstener {

	@ViewById
	WebView webView;
	@ViewById
	Button btn_back,btn_save;
	@ViewById
	TextView tv_title;
	@ViewById
	LinearLayout ll_fail;

	@FragmentById
	CommonWebFragmentHuyuan webFragment;

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

	@AfterViews
	void init() {
		webFragment.init(url, false);
		ActionBar bar = getSupportActionBar();
		bar.hide();
		setCustomView();
	}
	@UiThread
	public void setVisiable(){
		if(btn_save!=null){
			btn_save.setVisibility(View.VISIBLE);
			supportInvalidateOptionsMenu();
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

}
