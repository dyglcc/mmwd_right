package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

@EActivity(R.layout.main_collect_money_compelete)
public class CollectMoneyCompleteActivity extends BaseActivity {
	@Extra
	String moneyString;
	@Extra
	String url;
	@Extra
	String shopName;
	@ViewById(R.id.btn_share)
	Button shareFriend;
	@ViewById(R.id.btn_smg)
	Button sendMsg;
	@ViewById(R.id.tv_title)
	TextView tvTitle;
	@ViewById(R.id.tv_back)
	Button btnBack;

	@Click
	void btn_share() {
		Toaster.l(CollectMoneyCompleteActivity.this,
				getString(R.string.launch_share));
		shareMoney();
	}

	@Click
	void btn_smg() {
		sendMsg();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void init() {

		if (moneyString == null || moneyString.equals("") || shopName == null
				|| shopName.equals("") || url == null || url.equals("")) {
			Toaster.l(this, "异常了，请重新创建收款");
			finish();
		}
		
		tvTitle.setText("向买家发起收款");
		
	}
	@Click(R.id.tv_back)
	void goBack() {
		finish();
	}

	private void shareMoney() {
		
		
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = shopName + "  金额：" + moneyString + "元。";
		msg.description = WxShopApplication.dataEngine.getShopName()
				+ "向你发起一笔交易请求，请点击付款，支持微信支付、百度钱包支付";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.weixin_collect_money);
		msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		
		req.transaction = Utils.buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		WxShopApplication.api.sendReq(req);
		

		MobAgentTools.OnEventMobOnDiffUser(CollectMoneyCompleteActivity.this,
				"Click_weixin_collect");
		

	}

	private void sendMsg() {
		
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		sendIntent.setData(Uri.parse("smsto:"));
		sendIntent.putExtra("sms_body",
				"【" + WxShopApplication.dataEngine.getShopName()
						+ "】向你发起一笔交易请求，金额：" + moneyString
						+ "请点击付款，支持微信支付、百度钱包支付。http://www.baidu.com?"
						+ moneyString);
		startActivity(sendIntent);
		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
