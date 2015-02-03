package qfpay.wxshop.app.dependencies;

import android.content.Context;
import android.view.LayoutInflater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import qfpay.wxshop.WxShopApplication_;
import qfpay.wxshop.ui.commodity.detailmanager.ItemDetailManagerActivity_;
import qfpay.wxshop.ui.commodity.detailmanager.ItemDetailPresenterImpl_;

/**
 * 关系图表的跟Module
 *
 * Created by LiFZhe on 1/19/15.
 */
@Module(
        includes = {
                RepositoryModule.class,
        },
        injects = {
                WxShopApplication_.class,
                ItemDetailManagerActivity_.class,
                ItemDetailPresenterImpl_.class
        },
        library = true
)
public class RootModule {
    private final Context context;

    public RootModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    public Context provideApplicationContext() {
        return context;
    }

    @Provides public LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(context);
    }
}
