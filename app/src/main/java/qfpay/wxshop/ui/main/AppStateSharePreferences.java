package qfpay.wxshop.ui.main;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value = Scope.APPLICATION_DEFAULT) public interface AppStateSharePreferences {
	
	/**
	 * 是否是新用户,如果在本次安装过程中注册过,则认为是新用户
	 */
	@DefaultBoolean(false) boolean isNewUser();
	
	/**
	 * 引导进行程度的指针,指向下一次的引导
	 */
	@DefaultString(MainActivity.GUIDE_RELEASE) String guidePointer();
	@DefaultBoolean(true) boolean isShowGuide();
	
	/**
	 * 是否显示ActionBar头像的提醒
	 */
	@DefaultBoolean(true) boolean isShowInfoHeaderbtnPrompt();
	
	/**
	 * 萌片页新数据标记
	 */
	@DefaultString("") String theFirstLCTime();
	/**
	 * 是否显示萌片页tab上的小红点
	 */
	@DefaultBoolean(true) boolean isLCNew();
}
