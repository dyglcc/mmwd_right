package qfpay.wxshop.listener;

import java.util.HashMap;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

public class ShareSdkListener implements PlatformActionListener,Callback {
	
	private Context context;
	
	public ShareSdkListener(Context context){
		this.context = context;
	}
	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
		if (plat.getName().equals("WechatMoments")) {
			T.i("分享成功：WechatMoments");
			MobAgentTools.OnEventMobOnDiffUser(context,
					"weixin_share_moment_success_sharesdk");
		} else if (plat.getName().equals("Wechat")) {
			MobAgentTools.OnEventMobOnDiffUser(context,
					"weixin_share_friend_success_sharesdk");
			T.i("分享成功：Wechat");
		}
		
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
		if (plat.getName().equals("WechatMoments")) {
			MobAgentTools.OnEventMobOnDiffUser(context,
					"weixin_share_moment_fail_sharesdk");
			T.i("分享失败：WechatMoments");
		} else if (plat.getName().equals("Wechat")) {
			MobAgentTools.OnEventMobOnDiffUser(context,
					"weixin_share_friend_fail_sharesdk");
			T.i("分享失败：Wechat");
		}
	}

	public boolean handleMessage(Message msg) {
		String text = actionToString(msg.arg2);
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
				text = context
						.getString(R.string.wechat_client_inavailable);
			} else if ("WechatTimelineNotSupportedException".equals(msg.obj
					.getClass().getSimpleName())) {
				text = context
						.getString(R.string.wechat_client_inavailable);
			} else {
				text = context.getString(R.string.fail_share2);
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

		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
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
