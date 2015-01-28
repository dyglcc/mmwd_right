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
    private static final int DEFAULT_ID_SERVER = 0;

    @Override
    public List<Sku> mapSKUModel(CommodityModel commodityModel) {
        List<Sku> skuBeans = new ArrayList<Sku>();
        for (SKUModel model : commodityModel.getSkuList()) {
            Sku skuBean = new Sku();
            if (model.getId() <= 0) {
                skuBean.id = DEFAULT_ID_SERVER;
            } else {
                skuBean.id = model.getId();
            }
            skuBean.prop_value = model.getName();
            skuBean.price = model.getPrice();
            skuBean.amount = model.getAmount();
            skuBeans.add(skuBean);
        }
        return skuBeans;
    }

    @Override
    public List<String> mapImageModelToString(CommodityModel commodityModel) {
        List<String> imageUrls = new ArrayList<String>();
        for (PictureModel model : commodityModel.getPictureList()) {
            imageUrls.add(model.getUrl());
        }
        return imageUrls;
    }

    @Override
    public List<ItemImage> mapImageModelToBean(CommodityModel commodityModel) {
        List<ItemImage> list = new ArrayList<ItemImage>();
        for (PictureModel model : commodityModel.getPictureList()) {
            ItemImage image = new ItemImage();
            if (model.getId() <= 0) {
                image.id = DEFAULT_ID_SERVER;
            } else {
                image.id = model.getId();
            }
            image.url = model.getUrl();
            list.add(image);
        }
        return list;
    }

    @Override
    public CommodityModel mapItemWrapper(ItemWrapper itemWrapper) throws MessageException {
        CommodityModel model = new CommodityModel();
        model.setId(itemWrapper.data.item.id);
        model.setName(itemWrapper.data.item.title);
        model.setPrice(itemWrapper.data.item.price);
        model.setPostage(itemWrapper.data.item.postage);
        model.setDescription(itemWrapper.data.item.descr);
        model.setStatus(CommodityStatus.getStatus(itemWrapper.data.item.status));

        SimpleDateFormat formatter = new SimpleDateFormat(Item.DATE_FORMAT);
        Date createDate = new Date();
        try {
            createDate = formatter.parse(itemWrapper.data.item.modified);
        } catch (ParseException e) {
            Timber.e(e, "解析创建日期报错");
            MessageException exception = new ParserException("数据解析出错");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

        List<SKUModel> skuModelList = new ArrayList<SKUModel>();
        for (Sku sku : itemWrapper.data.item.specs) {
            SKUModel skuModel = new SKUModel();
            skuModel.setId(sku.id);
            skuModel.setCommodityId(sku.iid);
            skuModel.setName(sku.prop_value);
            skuModel.setPrice(sku.price);
            skuModel.setAmount(sku.amount);
            skuModelList.add(skuModel);
        }
        model.setSkuList(skuModelList);

        List<PictureModel> pictureList = new ArrayList<PictureModel>();
        for (ItemImage image : itemWrapper.data.item.images) {
            PictureModel pictureModel = new PictureModel();
            pictureModel.setUrl(image.url);
            pictureModel.setId(image.id);
            pictureList.add(pictureModel);
        }
        model.setPictureList(pictureList);
        return model;
    }
}
