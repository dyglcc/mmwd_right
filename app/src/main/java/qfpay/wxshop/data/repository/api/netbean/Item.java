package qfpay.wxshop.data.repository.api.netbean;

import java.util.List;

/**
 * 商品详情的服务器数据结构
 *
 * Created by LiFZhe on 1/19/15.
 */
public class Item {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public int id;
    public String img;// 头图
    public String title;// 商品名称
    public int status;// 状态：0.正常 1.下架 2.删除 11.抢购
    public float price;
    public List<Sku> specs;// 多规格, 若用户创建商品时未使用多规格，则本列表为空。价格、库存请取 item 的 price、amount 属性
    public float postage;
    public String descr;
    public List<ItemImage> images;
    public String modified;
}
