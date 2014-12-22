package qfpay.wxshop.data.netImpl;

import java.util.List;
import java.util.Map;

import qfpay.wxshop.data.beans.UnitBean;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

public interface EdititemService {
	@GET("/qmm/wd/app/v2/edititem")
	GetItemWrapper getGoodInfo(@Query("goodid") int id);
	
	class GetItemWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		private GetItemNetBean data;
		public GetItemNetBean getData() {
			return data;
		}
		public void setData(GetItemNetBean data) {
			this.data = data;
		}
	}
	
	class GetItemNetBean {
		private List<ImgNetBeanFromServer> goodgallery;
		private GoodNetBeanFromServer good;
		private List<UnitBean> gooddetails;
		
		public List<ImgNetBeanFromServer> getGoodgallery() {
			return goodgallery;
		}
		public void setGoodgallery(List<ImgNetBeanFromServer> goodgallery) {
			this.goodgallery = goodgallery;
		}
		public GoodNetBeanFromServer getGood() {
			return good;
		}
		public void setGood(GoodNetBeanFromServer good) {
			this.good = good;
		}
		public List<UnitBean> getGooddetails() {
			return gooddetails;
		}
		public void setGooddetails(List<UnitBean> gooddetails) {
			this.gooddetails = gooddetails;
		}
	}
	
	class ImgNetBeanFromServer {
		private String origin_url;
		private String id;
		
		public String getOrigin_url() {
			return origin_url;
		}
		public void setOrigin_url(String origin_url) {
			this.origin_url = origin_url;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	
	class GoodNetBeanFromServer {
		private String good_prize;
		private String postage;
		private String good_name;
		private String good_img;
		private String good_desc;
		private String good_amount;
		private String id;
		
		public String getGood_prize() {
			return good_prize;
		}
		public void setGood_prize(String good_prize) {
			this.good_prize = good_prize;
		}
		public String getPostage() {
			return postage;
		}
		public void setPostage(String postage) {
			this.postage = postage;
		}
		public String getGood_name() {
			return good_name;
		}
		public void setGood_name(String good_name) {
			this.good_name = good_name;
		}
		public String getGood_img() {
			return good_img;
		}
		public void setGood_img(String good_img) {
			this.good_img = good_img;
		}
		public String getGood_desc() {
			return good_desc;
		}
		public void setGood_desc(String good_desc) {
			this.good_desc = good_desc;
		}
		public String getGood_amount() {
			return good_amount;
		}
		public void setGood_amount(String good_amount) {
			this.good_amount = good_amount;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	
	@Multipart
	@POST("/qmm/wd/app/renew_shopbg")
	CommonJsonBean renewShopBG(@Part("bg_url") String imgUrl);
	
	@FormUrlEncoded
	@POST("/qmm/wd/app/newitem")
	EditDoneBean newItemSave(@FieldMap Map<String, String> params);
	
	@FormUrlEncoded
	@POST("/qmm/wd/app/v2/edititem")
	CommonJsonBean editItemSave(@FieldMap Map<String, String> params);
	
	class EditDoneBean extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		public EditDoneServerBean data;
	}
	
	class EditDoneServerBean {
		public String goodid;
	}
	
	@FormUrlEncoded
	@POST("/qmm/wd/app/api/item/bind_hd_image")
	CommonJsonBean bindHDImg(@Field("url") String url, @Field("goodid") String goodid);
}
