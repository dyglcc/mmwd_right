package qfpay.wxshop.ui.main.fragmentcontroller;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.LovelyCardNetService;
import qfpay.wxshop.ui.lovelycard.LovelyCardPref_;
import qfpay.wxshop.utils.T;

/**
 * Created by LiFZhe on 12/1/14.
 */
@EBean
public class StartupProcessor {
    @Bean
    RetrofitWrapper              mRetrofitWrapper;
    private LovelyCardNetService mNetService;

    @Pref
    LovelyCardPref_              mLCPref;

    @AfterInject
    void onInit() {
        mNetService = mRetrofitWrapper.getNetService(LovelyCardNetService.class);
    }

    @Background(id = ConstValue.THREAD_CANCELABLE)
    public void processLovelyCard() {
        try {
            LovelyCardNetService.LovelyCardDataWrapper wrapper = mNetService.getLC();
            mLCPref.name().put(wrapper.data.name);
            mLCPref.label_first().put(wrapper.data.getTag(0));
            mLCPref.label_second().put(wrapper.data.getTag(1));
            mLCPref.descript().put(wrapper.data.descr);
            mLCPref.imgUrl().put(wrapper.data.bgimg);
        } catch (Exception e) {
            T.e(e);
        }
    }
}
