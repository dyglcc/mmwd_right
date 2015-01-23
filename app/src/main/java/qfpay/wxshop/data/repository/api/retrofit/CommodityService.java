package qfpay.wxshop.data.repository.api.retrofit;

import java.util.List;

import qfpay.wxshop.data.repository.api.netbean.ItemWrapper;
import qfpay.wxshop.data.repository.api.netbean.NetDataContainer;
import qfpay.wxshop.data.repository.api.netbean.Sku;
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
    @FormUrlEncoded @POST("/qmm/item/v3/new")
    public NetDataContainer newItem(@Field("title")   String       name,
                                    @Field("descr")   String       description,
                                    @Field("postage") float        postage,
                                    @Field("specs")   List<Sku>    skuList,
                                    @Field("images")  List<String> pictureList);

    @FormUrlEncoded @POST("/qmm/item/v3/{id}")
    public NetDataContainer editItem(@Field("id")      int          id,
                                     @Field("title")   String       name,
                                     @Field("descr")   String       description,
                                     @Field("postage") float        postage,
                                     @Field("specs")   List<Sku>    skuList,
                                     @Field("images")  List<String> pictureList);

    @GET("/qmm/item/API_version/{id}")
    public ItemWrapper getItem(@Path("id") int id);
}
