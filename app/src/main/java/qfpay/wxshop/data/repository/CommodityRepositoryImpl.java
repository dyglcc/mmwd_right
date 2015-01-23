package qfpay.wxshop.data.repository;

import qfpay.wxshop.data.CommodityRepository;
import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;
import qfpay.wxshop.data.repository.api.CommodityApiClient;

/**
 * 所有商品的数据操作封装
 *
 * Created by LiFZhe on 1/19/15.
 */
public class CommodityRepositoryImpl implements CommodityRepository {
    private CommodityApiClient apiClient;

    public CommodityRepositoryImpl(CommodityApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public boolean createCommodity(CommodityModel model) throws MessageException {
        return apiClient.newItem(model);
    }

    @Override
    public boolean updateCommodity(CommodityModel model) throws MessageException {
        return apiClient.editItem(model);
    }

    @Override
    public CommodityModel getCommodityModel(int id) throws MessageException {
        return apiClient.getCommodityModel(id);
    }
}
