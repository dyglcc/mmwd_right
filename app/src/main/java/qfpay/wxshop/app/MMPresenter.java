package qfpay.wxshop.app;

/**
 * 逻辑层
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface MMPresenter<T extends MMView> {
    void onViewCreate();

    void onViewResume();

    void onViewDestroy();

    void setView(T view);
}
