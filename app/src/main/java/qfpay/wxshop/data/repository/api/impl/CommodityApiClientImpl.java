package qfpay.wxshop.data.repository.api.impl;

import qfpay.wxshop.data.exception.HttpRequestException;
import qfpay.wxshop.data.exception.HttpServerException;
import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;
import qfpay.wxshop.data.repository.api.CommodityApiClient;
import qfpay.wxshop.data.repository.api.CommodityDataMapper;
import qfpay.wxshop.data.repository.api.netbean.ItemWrapper;
import qfpay.wxshop.data.repository.api.netbean.NetDataContainer;
import qfpay.wxshop.data.repository.api.retrofit.CommodityService;
import qfpay.wxshop.data.repository.api.retrofit.setting.MMRetrofitCreator;
import retrofit.RetrofitError;

/**
 * 商品数据的API访问者
 *
 * Created by LiFZhe on 1/19/15.
 */
public class CommodityApiClientImpl implements CommodityApiClient {
    private CommodityService    mNetService;
    private CommodityDataMapper mMapper;

    public CommodityApiClientImpl(MMRetrofitCreator serviceCreator, CommodityDataMapper mapper) {
        this.mNetService = serviceCreator.getService(CommodityService.class);
        this.mMapper = mapper;
    }

    @Override
    public boolean newItem(CommodityModel model) throws MessageException {
        try {
            NetDataContainer container = mNetService.newItem(
                    model.getName(),
                    model.getDescription(),
                    model.getPostage(),
                    mMapper.mapSKUModel(model),
                    mMapper.mapImageModel(model));
            if (!container.respcd.equals("0000")) {
                throw new HttpServerException(container.getShownMessage());
            }
            return true;
        } catch(RetrofitError e) {
            throw new HttpRequestException(e);
        }
    }

    @Override
    public boolean editItem(CommodityModel model) throws MessageException {
        try {
            NetDataContainer container = mNetService.editItem(
                    model.getId(),
                    model.getName(),
                    model.getDescription(),
                    model.getPostage(),
                    mMapper.mapSKUModel(model),
                    mMapper.mapImageModel(model));
            if (!container.respcd.equals("0000")) {
                throw new HttpServerException(container.getShownMessage());
            }
            return true;
        } catch(RetrofitError e) {
            throw new HttpRequestException(e);
        }
    }

    @Override
    public CommodityModel getCommodityModel(int id) throws MessageException {
        try {
            ItemWrapper container = mNetService.getItem(id);
            if (!container.respcd.equals("0000")) {
                throw new HttpServerException(container.getShownMessage());
            }
            return mMapper.mapItemWrapper(container);
        } catch(RetrofitError e) {
            throw new HttpRequestException(e);
        }
    }
}
