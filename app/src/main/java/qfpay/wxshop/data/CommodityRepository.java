package qfpay.wxshop.data;

import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;

/**
 * 所有商品的数据操作封装
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface CommodityRepository {
    public boolean createCommodity(CommodityModel model) throws MessageException;

    public boolean updateCommodity(CommodityModel model) throws MessageException;

    public CommodityModel getCommodityModel(int id) throws MessageException;
}
