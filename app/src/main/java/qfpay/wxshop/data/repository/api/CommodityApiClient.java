package qfpay.wxshop.data.repository.api;

import qfpay.wxshop.data.exception.HttpServerException;
import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;

/**
 * 商品数据的API访问者
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface CommodityApiClient {
    public int newItem(CommodityModel model) throws MessageException;

    public void editItem(CommodityModel model) throws MessageException;

    public CommodityModel getCommodityModel(int id) throws MessageException;
}
