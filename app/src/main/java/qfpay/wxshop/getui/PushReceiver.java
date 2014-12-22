package qfpay.wxshop.getui;

import qfpay.wxshop.R;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.PreloadBean;
import qfpay.wxshop.data.netImpl.BindNetImpl;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushReceiver extends BroadcastReceiver {

	private SharedPreferences shareData;
	private Handler handler = new Handler();

	@Override
	public void onReceive(final Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		T.i("action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.CHECK_CLIENTID:
			break;
		case PushConsts.GET_SDKONLINESTATE:
			break;
		case PushConsts.GET_SDKSERVICEPID:
			break;
		case PushConsts.MAX_FEEDBACK_ACTION:
			break;
		case PushConsts.MIN_FEEDBACK_ACTION:
			break;
		case PushConsts.SETTAG_ERROR_COUNT:
			break;

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");
			

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(
					context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

			if (payload != null) {
				String data = new String(payload);
				T.i("preload :" + data);
				Gson gson = new Gson();
				PreloadBean bean = gson.fromJson(data, PreloadBean.class);

				if (bean != null) {
					String content = bean.getContent();
					String title = bean.getTitle();
					String type = bean.getType();

					try{
						if (type != null && !type.equals("")) {

							// 0，运营推送，1.订单管理 2.
							if (type.equals("0")) {
								MobAgentTools.OnEventMobOnDiffUser(context, "PUSH_RECEIVE");
								// 运营打开app
								String link = bean.getLink();
								if (link != null && link.startsWith("http")) {
									notifies(context, content, title, openWeb, link);
								} else if (link != null && link.startsWith("qf")) {
									notifies(context, content, title, openApp, link);
								}else{
									// open app
									notifies(context, content, title, openApp, link);
								}
							} else if (type.equals("1")) {
								// 订单，打开网页我的订单
								String link = WDConfig.getInstance().getOrderListURL("");
								notifies(context, content, title, openWeb, link);
							} else if (type.equals("2")) {
								// 打开 点赞网页打开 我的收入
								String link = WDConfig.getInstance().getIncomeURL("");
								notifies(context, content, title, openWeb, link);
							}
						}
					}catch(Exception e){
						T.e(e);
					}
					
				}
				T.d("Got Payload:" + data);
			}

			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			final String cid = bundle.getString("clientid");

			shareData = context.getSharedPreferences("Data", 0);

			String bindStr = shareData.getString("bindStr", "");
			String oldUser = shareData.getString("Userid", "");

			boolean needBind = checkNeedBind(bindStr, oldUser);
			if (needBind) {

				AbstractNet net = new BindNetImpl(context, handler);
				Bundle bundleBind = new Bundle();
				bundleBind.putString("clientid", cid);
				net.request(bundleBind, handler);
			}

			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*
			 * String appid = bundle.getString("appid"); String taskid =
			 * bundle.getString("taskid"); String actionid =
			 * bundle.getString("actionid"); String result =
			 * bundle.getString("result"); long timestamp =
			 * bundle.getLong("timestamp");
			 * 
			 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo",
			 * "taskid = " + taskid); Log.d("GetuiSdkDemo", "actionid = " +
			 * actionid); Log.d("GetuiSdkDemo", "result = " + result);
			 * Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
			 */
			break;
		default:
			break;
		}
	}

	private boolean checkNeedBind(String bindStr, String currenUid) {
		if (bindStr == null || bindStr.equals("")) {
			return true;
		}
		String[] strs = bindStr.split("_");
		if (strs.length < 2) {
			return true;
		}
		String userid = strs[0];
		String lastTimeStr = strs[1];
		if (lastTimeStr.equals("")) {
			return true;
		}
		long lastTime = Long.parseLong(lastTimeStr);

		if (userid.equals("")) {
			return true;
		}
		if (userid.equals(currenUid)) {
			if (System.currentTimeMillis() - lastTime > ConstValue.oneDay) {
				return true;
			} else {
				return false;
			}
		}
		if (!userid.equals(currenUid)) {
			return true;
		}
		return false;
	}

	private static boolean openApp = true;
	private static boolean openWeb = false;

	private void notifies(Context context, String text, String title,
			boolean openApp, String link) throws ClassNotFoundException {

		if(title.equals("")){
			title = "喵喵微店";
		}
		// Toast.makeText(context, "文本：" + text, Toast.LENGTH_LONG).show();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.push;
		CharSequence tickerText = "喵喵来消息了";
		if(title != null && !title.equals("")){
			tickerText = title;
		}else if(text!=null && !text.equals("")){
			tickerText = text;
		}
		long when = System.currentTimeMillis();
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(icon, tickerText, when);
		// 定义下拉通知栏时要展现的内容信息
		CharSequence contentTitle = title;
		if(contentTitle == null || contentTitle.equals("")){
			contentTitle = "喵喵微店";
		}
		CharSequence contentText = text;
		Intent notificationIntent = null;
		if (openApp) {
			notificationIntent = new Intent(context, Class.forName("qfpay.wxshop.ui.main.MainActivity_"));
			// 通过透传进入app
			notificationIntent.putExtra("preload", "1");
		} else {
			notificationIntent = new Intent(context, Class.forName("qfpay.wxshop.ui.web.CommonWebActivity_"));
//			notificationIntent = new Intent(context, CommonWebActivity_.class);
			notificationIntent.putExtra("url", link);
			notificationIntent.putExtra("push", "true");
		}
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationIntent.setAction(""+System.currentTimeMillis()); 
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(1001, notification);
	}

}
