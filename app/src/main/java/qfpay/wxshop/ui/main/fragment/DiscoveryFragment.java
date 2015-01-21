package qfpay.wxshop.ui.main.fragment;


import android.os.Handler;
import android.os.Message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import qfpay.wxshop.R;
import qfpay.wxshop.dialogs.ISimpleDialogListener;

/**
 * 显示商户圈中“发现”页面
 */
@EFragment(R.layout.main_discovery)
public class DiscoveryFragment extends BaseFragment implements
        ISimpleDialogListener,PlatformActionListener,Handler.Callback{

    @AfterViews
    void init(){

    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {

    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
