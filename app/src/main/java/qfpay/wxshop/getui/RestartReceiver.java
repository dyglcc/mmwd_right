package qfpay.wxshop.getui;

import qfpay.wxshop.WxShopApplication;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
/**
 * 重新启动应用
 * */
public class RestartReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		if (!WxShopApplication.MAIN_IS_RUNNING) {
			Intent intentStart = new Intent(Intent.ACTION_MAIN);
			intentStart.addCategory(Intent.CATEGORY_LAUNCHER); 
			intentStart.setComponent(new ComponentName("qfpay.wxshop","qfpay.wxshop.ui.main.WelcomeActivity_"));
			intentStart.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intentStart);
		}
	}
}
