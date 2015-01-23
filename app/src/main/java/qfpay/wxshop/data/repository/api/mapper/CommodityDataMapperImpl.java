package qfpay.wxshop.data.repository.api.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.exception.ParserException;
import qfpay.wxshop.data.model.CommodityModel;
import qfpay.wxshop.data.model.CommodityStatus;
import qfpay.wxshop.data.model.PictureModel;
import qfpay.wxshop.data.model.SKUModel;
import qfpay.wxshop.data.repository.api.CommodityDataMapper;
import qfpay.wxshop.data.repository.api.netbean.Item;
import qfpay.wxshop.data.repository.api.netbean.ItemImage;
import qfpay.wxshop.data.repository.api.netbean.ItemWrapper;
import qfpay.wxshop.data.repository.api.netbean.Sku;
import timber.log.Timber;

/**
 * 商品的数据转换器
 *
 * Created by LiFZhe on 1/19/15.
 */
public class CommodityDataMapperImpl implements CommodityDataMapper {
    @Override
    public List<Sku> mapSKUModel(CommodityModel commodityModel) {
        List<Sku> skuBeans = new ArrayList<Sku>();
        for (SKUModel model : commodityModel.getSkuList()) {
            Sku skuBean = new Sku();
            skuBean.id = model.getId();
            skuBean.prop_value = model.getName();
            skuBean.price = model.getPrice();
            skuBean.amount = model.getAmount();
            skuBeans.add(skuBean);
        }
        return skuBeans;
    }

    @Override
    public List<String> mapImageModel(CommodityModel commodityModel) {
        List<String> imageUrls = new ArrayList<String>();
        for (PictureModel model : commodityModel.getPictureList()) {
            imageUrls.add(model.getUrl());
        }
        return imageUrls;
    }

    @Override
    public CommodityModel mapItemWrapper(ItemWrapper itemWrapper) throws MessageException {
        CommodityModel model = new CommodityModel();
        model.setId(itemWrapper.data.id);
        model.setName(itemWrapper.data.title);
        model.setPrice(itemWrapper.data.price);
        model.setPostage(itemWrapper.data.postage);
        model.setDescription(itemWrapper.data.descr);
        model.setStatus(CommodityStatus.getStatus(itemWrapper.data.status));

        SimpleDateFormat formatter = new SimpleDateFormat(Item.DATE_FORMAT);
        Date createDate = new Date();
        try {
            createDate = formatter.parse(itemWrapper.data.modified);
        } catch (ParseException e) {
            Timber.e(e, "解析创建日期报错");
            MessageException exception = new ParserException("数据解析出错");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

        for (Sku sku : itemWrapper.data.specs) {
            SKUModel skuModel = new SKUModel();
            skuModel.setId(sku.id);
            skuModel.setCommodityId(sku.iid);
            skuModel.setName(sku.prop_value);
            skuModel.setPrice(sku.price);
            skuModel.setAmount(sku.amount);

            List<PictureModel> pictureList = new ArrayList<PictureModel>();
            for (ItemImage image : sku.itemimgs) {
                PictureModel pictureModel = new PictureModel();
                pictureModel.setUrl(image.url);
                pictureList.add(pictureModel);
            }
            skuModel.setPictureList(pictureList);
        }

        List<PictureModel> pictureList = new ArrayList<PictureModel>();
        for (ItemImage image : itemWrapper.data.itemimgs) {
            PictureModel pictureModel = new PictureModel();
            pictureModel.setUrl(image.url);
            pictureList.add(pictureModel);
        }
        model.setPictureList(pictureList);
        return model;
    }
}
