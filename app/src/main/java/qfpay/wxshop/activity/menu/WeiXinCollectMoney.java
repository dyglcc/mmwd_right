package qfpay.wxshop.activity.menu;

import java.util.HashMap;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.CreateWeixinCMImpl;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.view.MoneyEditTextView;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

public class WeiXinCollectMoney extends BaseActivity implements
		PlatformActionListener, Callback {
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ConstValue.MSG_ERROR_FROM_MAINHANDLER) {
				if (layoutProgress != null) {
					layoutProgress.setVisibility(View.INVISIBLE);
				}
			} else if (msg.what == 2) {
				Toaster.l(WeiXinCollectMoney.this, "短信内容已复制");
			} else {
				Toaster.l(WeiXinCollectMoney.this,
						getString(R.string.start_share));
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_collect_money_weixin);
		initView();
		if (!initShare) {
			ShareSDK.initSDK(this);
			initShare = true;
		}
	}

	private boolean initShare;
	private MoneyEditTextView etMoney;
	private EditText etGoodsName;
	private Button btnSave;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (initShare) {
			ShareSDK.stopSDK(this);
		}
		MobAgentTools.OnEventMobOnDiffUser(WeiXinCollectMoney.this, "receipt");

	}

	private LinearLayout layoutProgress;

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		ShareSDK.initSDK(this);
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText("我要收款");
		etMoney = (MoneyEditTextView) findViewById(R.id.et_money);
		etGoodsName = (EditText) findViewById(R.id.et_goods_name);
		btnSave = (Button) findViewById(R.id.btn_save);

		layoutProgress = (LinearLayout) findViewById(R.id.layout_progress_load);

		layoutProgress.setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出对话框
				save2Server();
				MobAgentTools.OnEventMobOnDiffUser(WeiXinCollectMoney.this,
						"go_with");

			}
		});
	}
	
	
	
	private void shareMoney(String url) {
		
		if(!WxShopApplication.api.isWXAppInstalled()){
			Toaster.s(WeiXinCollectMoney.this, getString(R.string.tip_needinstallkehuduan));
			return;
		}

		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.description = WxShopApplication.dataEngine.getShopName()
				+ "向你发起一笔交易请求，请点击付款，支持微信支付、银行卡支付";
		wdb.scope = ConstValue.friend_share;
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.weixin_collect_money);
		
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 200,200, true);
//		bmp.recycle();
		wdb.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
		wdb.url = url;
		wdb.title = shopName + "  金额：" + moneString + "元。"; 
		UtilsWeixinShare.shareWeb(wdb, null, this);
	}
	
	protected void save2Server() {

		// 检查参数是否录入
		if (!isRight()) {
			return;
		}
		layoutProgress.setVisibility(View.VISIBLE);
		AbstractNet net = new CreateWeixinCMImpl(WeiXinCollectMoney.this);
		Bundle para = new Bundle();
		para.putString("good_name", shopName);
		para.putString("itemprice", moneString);
		net.request(para, new MainHandler(WeiXinCollectMoney.this, handler) {

			@Override
			public void onSuccess(Bundle bundle) {
				// Toaster.l(WeiXinCollectMoney.this, "返回结果");
				if (bundle == null) {
					return;
				}

				String goodid = bundle.getString("goodid");
				if (goodid == null || goodid.equals("")) {
					return;
				}

				String url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/receipt/" + goodid;

				PopUpShare(url);

				// Intent intent = new
				// Intent(WeiXinCollectMoney.this,CollectMoneyCompleteActivity_.class);
				//
				// intent.putExtra("shopName", shopName);
				// intent.putExtra("moneyString", moneString);
				// intent.putExtra("url", url);
				// startActivity(intent);
				//
				// finish();
				layoutProgress.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onFailed(Bundle bundle) {
				layoutProgress.setVisibility(View.INVISIBLE);
			}
		});
	}

	private String shopName;
	private String moneString;

	protected boolean isRight() {
		shopName = etGoodsName.getText().toString();
		moneString = etMoney.getText().toString();
		if (shopName.equals("")) {
			Toaster.l(WeiXinCollectMoney.this, "亲，还没有填写商品名称哦");
			return false;
		}
		if (moneString.equals("") || moneString.equals(".")) {
			Toaster.l(this, "亲，没有金额可是收不了款的啦");
			return false;
		}
		return true;
	}

	// private void shareMoney() {
	//
	// Wechat.ShareParams spChat = new Wechat.ShareParams();
	// spChat.shareType = Platform.SHARE_WEBPAGE;
	// spChat.title = shopName + "  金额：" + moneString;
	// spChat.text = WxShopApplication.dataEngine.getShopName()
	// + "向你发起一笔交易请求，请点击付款，支持支付宝、百度钱包支付";
	// spChat.url = "http://www.baidu.com?money=" + moneString;
	// Bitmap imageData = BitmapFactory.decodeResource(getResources(),
	// R.drawable.ic_launcher);
	// spChat.setImageData(imageData);
	//
	// Platform plat = ShareSDK.getPlatform(WeiXinCollectMoney.this, "Wechat");
	// plat.setPlatformActionListener(WeiXinCollectMoney.this);
	// plat.share(spChat);
	// MobAgentTools.OnEventMobOnDiffUser(WeiXinCollectMoney.this,
	// "Click_weixin_collect");
	// }

	private String[] items;
	private static final int SHARE_friend = 0;
	private static final int SHARE_MESSAGE = 1;
	private static final int SHARE_CANCEL = 2;

	private void PopUpShare(final String url) {

		// 提示开始分享
		items = getResources().getStringArray(R.array.weixin_collect_money);

		QMMAlert.showAlert(WeiXinCollectMoney.this, "选择", items, null,
				new QMMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case SHARE_friend:
							// 分享到朋友
							MobAgentTools.OnEventMobOnDiffUser(
									WeiXinCollectMoney.this, "with_wechat");

							shareMoney(url);
							break;
						case SHARE_MESSAGE:
							MobAgentTools.OnEventMobOnDiffUser(
									WeiXinCollectMoney.this, "with_contacts");

							sendMsg(url);

							break;
						case SHARE_CANCEL:
							MobAgentTools.OnEventMobOnDiffUser(
									WeiXinCollectMoney.this, "receipt_cancel");

							break;
						default:
							break;
						}
					}

				});
	}

	private void sendMsg(String url) {

		// 商品名称，价格2999元。点击链接就可以直接付款唷！
		// 支持支付宝、百度钱包支付
		// http：//XXX。【店铺名】
		// 谢谢亲的惠顾！么么哒!
		String msgContent = shopName + "，价格" + moneString
				+ "元。点击链接就可以直接付款唷！支持银行卡支付" + url + " 。【"
				+ WxShopApplication.dataEngine.getShopName() + "】谢谢亲的惠顾！么么哒!";
		if (Utils.getDeviceName().toLowerCase().equals("nexus 5")) {

			Utils.saveClipBoard(WeiXinCollectMoney.this, msgContent);
			handler.sendEmptyMessage(2);

			Uri smsToUri = Uri.parse("smsto:");
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
			sendIntent.putExtra("sms_body", msgContent);
			startActivity(sendIntent);

		} else {
			Uri smsToUri = Uri.parse("smsto:");
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
			sendIntent.putExtra("sms_body", msgContent);
			startActivity(sendIntent);
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// LogUtil.i(this, "keyCode=" keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			// overridePendingTransition(R.anim.in_from_down, R.anim.quit);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);
	}

	public boolean handleMessage(Message msg) {
		String text = WeiXinCollectMoney.actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: {
			// 成功
			Platform plat = (Platform) msg.obj;
			text = plat.getName() + "分享成功";
		}
			break;
		case 2: {
			// 失败
			if ("WechatClientNotExistException".equals(msg.obj.getClass()
					.getSimpleName())) {
				text = WeiXinCollectMoney.this
						.getString(R.string.wechat_client_inavailable);
			} else if ("WechatTimelineNotSupportedException".equals(msg.obj
					.getClass().getSimpleName())) {
				text = WeiXinCollectMoney.this
						.getString(R.string.wechat_client_inavailable);
			} else {
				text = getString(R.string.fail_share2);
			}
		}
			break;
		case 3: {
			// 取消
			Platform plat = (Platform) msg.obj;
			text = "取消分享";
		}
			break;
		}

		Toast.makeText(WeiXinCollectMoney.this, text, Toast.LENGTH_LONG).show();
		return false;
	}

	/** 将action转换为String */
	public static String actionToString(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING:
			return "认证中";
		case Platform.ACTION_GETTING_FRIEND_LIST:
			return "得到朋友列表";
		case Platform.ACTION_FOLLOWING_USER:
			return "取粉丝朋友";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
			return "发送消息";
		case Platform.ACTION_TIMELINE:
			return "时间轴";
		case Platform.ACTION_USER_INFOR:
			return "用户信息";
		case Platform.ACTION_SHARE:
			return "分享";
		default: {
			return "未知";
		}
		}
	}

}
