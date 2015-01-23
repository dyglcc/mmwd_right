package qfpay.wxshop.ui;

import org.androidannotations.api.BackgroundExecutor;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cn.sharesdk.framework.ShareSDK;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.utils.Toaster;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WxShopApplication.app.addActivity(this);
        ShareSDK.initSDK(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        BackgroundExecutor.cancelAll(ConstValue.THREAD_CANCELABLE, true);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {//触摸屏幕关闭软键盘
            if (this.getCurrentFocus() != null) {
                CloseSoftInput(getCurrentFocus());
            }
        }
        return super.dispatchTouchEvent(ev);
    }



    protected void CloseSoftInput(View view) { // 关闭软键盘
        if (view != null) {
            if (view.getWindowToken() != null) {
                InputMethodManager imm;
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

}
