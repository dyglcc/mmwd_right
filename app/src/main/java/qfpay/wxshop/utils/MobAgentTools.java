package qfpay.wxshop.utils;

import java.util.Map;

import qfpay.wxshop.WxShopApplication;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class MobAgentTools {

	// umeng 中的配置需要有双份的事件
	// 例如：click_button_takepic .. click_button_takepic_newuser
	private static boolean isStatic = true;
	private static long extra = 3600 * 1000 * 24 * 7;

	public static void OnEventMobOnDiffUser(Context context, String key) {
		if (!isStatic) {
			return;
		}
		long startTime = WxShopApplication.dataEngine.getRegTimeMillist();
		if (startTime == 0) {
			MobclickAgent.onEvent(context, key);
		} else {
			if ((System.currentTimeMillis() - startTime) > extra) {
				MobclickAgent.onEvent(context, key);
			} else {
				MobclickAgent.onEvent(context, key + "_newuser");
			}
		}
	}
	public static void OnEventMobOnDiffUser(Context context, String key,Map<String, String> map) {
		if (!isStatic) {
			return;
		}
		long startTime = WxShopApplication.dataEngine.getRegTimeMillist();
		if (startTime == 0) {
			MobclickAgent.onEvent(context, key,map);
		} else {
			if ((System.currentTimeMillis() - startTime) > extra) {
				MobclickAgent.onEvent(context, key,map);
			} else {
				MobclickAgent.onEvent(context, key + "_newuser",map);
			}
		}
	}

	public static void OnvalueMobOnDiffUser(Context context, String key,
			Map<String, String> map, int value) {
		
		if (!isStatic) {
			return;
		}
		long startTime = WxShopApplication.dataEngine.getRegTimeMillist();
		if (startTime == 0) {
			MobclickAgent.onEventValue(context, key,map, value);
		} else {
			if ((System.currentTimeMillis() - startTime) > extra) {
				MobclickAgent.onEventValue(context, key, map, value);
			} else {
				MobclickAgent.onEventValue(context, key + "_newuser", map, value);
			}
		}
	}
}
