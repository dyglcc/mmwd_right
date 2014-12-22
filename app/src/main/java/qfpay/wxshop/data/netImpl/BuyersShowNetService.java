package qfpay.wxshop.data.netImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.data.beans.BuyerResponseWrapper;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import retrofit.http.DELETE;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface BuyersShowNetService {
	@FormUrlEncoded
	@PUT("/qmm/wd/app/api/hybrid_msg")
	BuyersShowPutResponseWrapper putHM(@FieldMap Map<String, String> params);
	
	@FormUrlEncoded
	@POST("/qmm/wd/app/api/hybrid_msg")
	BuyersShowPutResponseWrapper postHM(@FieldMap Map<String, String> params);
	
	@DELETE("/qmm/wd/app/api/hybrid_msg")
	CommonJsonBean deleteHM(@Query("mid") String id);
	
	@GET("/qmm/wd/app/api/hybrid_msg")
	BuyerResponseWrapper getHM(@Query("msg_type") String type);
	
	@GET("/qmm/wd/app/api/all_item_sinfo")
	GoodNetWrapper getGoodList();
	
	public static class BuyersShowPutResponseWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		
		private BuyersShowPutMsgWrapper data;

		public BuyersShowPutMsgWrapper getData() {
			return data;
		}

		public void setData(BuyersShowPutMsgWrapper data) {
			this.data = data;
		}
	}
	
	public static class BuyersShowPutMsgWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private BuyerShowBean msg;

		public BuyerShowBean getMsg() {
			return msg;
		}

		public void setMsg(BuyerShowBean msg) {
			this.msg = msg;
		}
	}
	
	public static class ImgDeleteWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id = "";
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImgDeleteWrapper other = (ImgDeleteWrapper) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
	
	public static class HMResponseWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		
		HMResponse data;

		public HMResponse getData() {
			return data;
		}
		public void setData(HMResponse data) {
			this.data = data;
		}
	}
	
	public static class HMResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		
		HMWrapper msg;

		public HMWrapper getMsg() {
			return msg;
		}
		public void setMsg(HMWrapper msg) {
			this.msg = msg;
		}
	}
	
	public static class HMWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private List<ImageBean> hm_images;
		private int id;

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public List<ImageBean> getHm_images() {
			return hm_images;
		}
		public void setHm_images(List<ImageBean> hm_images) {
			this.hm_images = hm_images;
		}
	}
	
	public static class GoodNetWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		
		GoodNet data;

		public GoodNet getData() {
			return data;
		}
		public void setData(GoodNet data) {
			this.data = data;
		}
	}
	
	public static class GoodNet implements Serializable {
		private static final long serialVersionUID = 1L;
		
		List<GoodWrapper> items;
		
		public List<GoodWrapper> getGood() {
			return items;
		}
		public void setGood(List<GoodWrapper> good) {
			this.items = good;
		}
	}
	
	public static class GoodWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private int id;
		private String good_name;
		private String good_img;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
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
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((good_img == null) ? 0 : good_img.hashCode());
			result = prime * result
					+ ((good_name == null) ? 0 : good_name.hashCode());
			result = prime * result + id;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GoodWrapper other = (GoodWrapper) obj;
			if (good_img == null) {
				if (other.good_img != null)
					return false;
			} else if (!good_img.equals(other.good_img))
				return false;
			if (good_name == null) {
				if (other.good_name != null)
					return false;
			} else if (!good_name.equals(other.good_name))
				return false;
			if (id != other.id)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "GoodWrapper [id=" + id + ", good_name=" + good_name
					+ ", good_img=" + good_img + "]";
		}
	}
}
