package qfpay.wxshop.data.netImpl;

import java.io.Serializable;
import java.util.List;

import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CommodityService {
	@GET("/qmm/wd/app/item_manage")
	CommodityListDataWrapper getCommodityList(@Query("page") Integer page, @Query("length") Integer length, 
			@Query("fixnum") Integer fixnum, @Query("format") String format);
	
	@FormUrlEncoded@POST("/qmm/wd/app/deleteitem")
	CommonJsonBean deleteCommodity(@Field("goodid") Integer id);
	
	@FormUrlEncoded @POST("/qmm/wd/app/top_goods/{goodid}")
	CommonJsonBean topGoods(@Path("goodid") String id, @Field("weight") String weight);
	
	@FormUrlEncoded @POST("/qmm/wd/app/panicbuy_delete")
	CommonJsonBean cancelPromotion(@Field("goodid") int goodid, @Field("panicid") int panicid);
	
	public static class CommodityListDataWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		public CommodityListWrapper data;
	}
	
	public static class CommodityListWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		public List<CommodityModel> items;
	}
}
