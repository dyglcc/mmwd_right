package qfpay.wxshop.config.update;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.T;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class InstalledReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getDataString();
			T.d("start main installed");
			if(packageName!=null && packageName.equals("package:qfpay.wxshop") && !WxShopApplication.MAIN_IS_RUNNING){
				
				Intent intentStart = new Intent(Intent.ACTION_MAIN);
				intentStart.addCategory(Intent.CATEGORY_LAUNCHER); 
				intentStart.setComponent(new ComponentName("qfpay.wxshop","qfpay.wxshop.ui.main.WelcomeActivity_"));
				intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentStart);
			}
		}
	}
}
