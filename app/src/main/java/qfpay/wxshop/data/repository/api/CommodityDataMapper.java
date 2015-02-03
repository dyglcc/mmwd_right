package qfpay.wxshop.data.repository.api;

import java.util.List;

import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;
import qfpay.wxshop.data.model.SKUModel;
import qfpay.wxshop.data.repository.api.netbean.ItemImage;
import qfpay.wxshop.data.repository.api.netbean.ItemWrapper;
import qfpay.wxshop.data.repository.api.netbean.Sku;

/**
 * 商品的数据转换器
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface CommodityDataMapper {
    public List<Sku> mapSKUModel(CommodityModel commodityModel);

    public List<String> mapImageModelToString(CommodityModel commodityModel);

    public List<ItemImage> mapImageModelToBean(CommodityModel commodityModel);

    public CommodityModel mapItemWrapper(ItemWrapper itemWrapper) throws MessageException;
}
