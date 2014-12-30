package qfpay.wxshop.ui.web;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
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
import qfpay.wxshop.ui.main.MainActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@EActivity(R.layout.web_common_activity)
public class CommonWebActivity extends BaseActivity implements OnShareLinstener {
	@ViewById WebView webView;
	@ViewById LinearLayout ll_fail;
	
	@FragmentById CommonWebFragment webFragment;

	@Extra String url = "";
	@Extra String title = "";
	@Extra String push = "";
	@Extra List<SharedPlatfrom> platFroms;
	@Extra String shareTitle = "", shareName = "", shareDescript = "";
	@Extra String pointName = "";

	@AfterViews void init() {
		getSupportActionBar().setTitle(title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		webFragment.init(url, false,title);
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.webview_share, menu);
		MenuItem shareItem = menu.findItem(R.id.menu_share);
		if (platFroms != null) {
			ShareActionProvider shareActionProvider = new ShareActionProvider(this, shareItem, this, platFroms);
			shareItem.setActionProvider(shareActionProvider);
		} else {
			menu.removeItem(R.id.menu_share);
		}
		return super.onCreateOptionsMenu(menu);
	};
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			exePush();
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
                exePush();
            }
        }
    }

	private void exePush(){
		if(push!=null && !push.equals("")){
			Intent intent = new Intent (this,MainActivity_.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
        finish();
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
		case COPY:
			String copyStr = webView.getUrl();
			Utils.saveClipBoard(this, copyStr);
			Toaster.l(this, shareName + "地址复制成功");
			break;
		default:
			break;
		}
		if (!pointName.equals("")) {
			MobAgentTools.OnEventMobOnDiffUser(this, pointName);
			QFCommonUtils.collect(pointName, this);
		}
	}

	@Override
	public String getShareFromName() {
		return shareName;
	}
	
	private void shareWxFriend() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = webView.getUrl();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl("http://qmmwx.u.qiniudn.com/icon.png", ImageSizeForUrl.MIN);
		wdb.title = shareTitle;
		wdb.description = shareDescript;
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wctimeline, this);
	}

	private void shareWxMoments() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = webView.getUrl();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl("http://qmmwx.u.qiniudn.com/icon.png", ImageSizeForUrl.MIN);
		wdb.title = shareTitle;
		wdb.description = shareDescript;
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wcfriend, this);
	}
	
	private void putShareEvent(String shareName) {
		if (ConstValue.SHARE_NAME_FINDMIAO.equals(shareName)) {
			MobAgentTools.OnEventMobOnDiffUser(this, "Click_faixiangemiao_fenxiang");
		}
	}

}
