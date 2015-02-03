package qfpay.wxshop.app.dependencies;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.CommodityRepository;
import qfpay.wxshop.data.repository.CommodityRepositoryImpl;
import qfpay.wxshop.data.repository.api.CommodityApiClient;
import qfpay.wxshop.data.repository.api.CommodityDataMapper;
import qfpay.wxshop.data.repository.api.impl.CommodityApiClientImpl;
import qfpay.wxshop.data.repository.api.mapper.CommodityDataMapperImpl;
import qfpay.wxshop.data.repository.api.retrofit.setting.MMErrorHandler;
import qfpay.wxshop.data.repository.api.retrofit.setting.MMInterceptor;
import qfpay.wxshop.data.repository.api.retrofit.setting.MMRetrofitCreator;
import qfpay.wxshop.utils.T;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;

/**
 * 数据有关的依赖图表
 *
 * Created by LiFZhe on 1/19/15.
 */
@Module(
        complete = false,
        library = true
)
public class RepositoryModule {
    @Provides @Named("session_id") String provideSessionId() {
        return WxShopApplication.dataEngine.getcid();
    }

    @Provides @Named("user_agent") String provideUserAgent() {
        return WxShopApplication.dataEngine.getUserAgent();
    }

    @Provides @Named("endpoint") String provideEndPoint() {
        return WDConfig.getInstance().WD_URL.substring(0, WDConfig.getInstance().WD_URL.length() - 1);
    }

    @Provides @Named("istesting") boolean provideIsTesting() {
        return T.isTesting;
    }

    @Provides RequestInterceptor provideRequestInterceptor(@Named("session_id") String sessionId, @Named("user_agent") String userAgent) {
        return new MMInterceptor(sessionId, userAgent);
    }

    @Provides ErrorHandler provideRequestInterceptor() {
        return new MMErrorHandler();
    }

    @Provides MMRetrofitCreator provideMMRetrofitCreator(@Named("endpoint") String             endPoint,
                                                                            RequestInterceptor interceptor,
                                                                            ErrorHandler       errorHandler,
                                                         @Named("istesting") boolean           isTesting) {
        return new MMRetrofitCreator(endPoint, interceptor, errorHandler, isTesting);
    }

    @Provides CommodityApiClient provideApiClient(MMRetrofitCreator serviceCreator, CommodityDataMapper mapper) {
        return new CommodityApiClientImpl(serviceCreator, mapper);
    }

    @Provides CommodityDataMapper provideCommodityDataMapper() {
        return new CommodityDataMapperImpl();
    }

    @Provides CommodityRepository provideCommodityRepository(CommodityApiClient apiClient) {
        return new CommodityRepositoryImpl(apiClient);
    }
}
