package qfpay.wxshop.data.repository.api.retrofit;

import qfpay.wxshop.data.repository.api.netbean.NewItemResponseWrapper;
import qfpay.wxshop.data.repository.api.netbean.ItemWrapper;
import qfpay.wxshop.data.repository.api.netbean.NetDataContainer;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * 所有商品相关的服务器方法表示
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface CommodityService {
    @FormUrlEncoded @POST("/qmm/item/new")
    public NewItemResponseWrapper newItem(@Field("title")   String name,
                                    @Field("descr")   String description,
                                    @Field("postage") float  postage,
                                    @Field("specs")   String skuList,
                                    @Field("images")  String pictureList);

    @FormUrlEncoded @POST("/qmm/item/{id}")
    public NetDataContainer editItem(@Path("id")       int    id,
                                     @Field("title")   String name,
                                     @Field("descr")   String description,
                                     @Field("postage") float  postage,
                                     @Field("specs")   String skuList,
                                     @Field("images")  String pictureList);

    @GET("/qmm/item/{id}")
    public ItemWrapper getItem(@Path("id") int id);
}
