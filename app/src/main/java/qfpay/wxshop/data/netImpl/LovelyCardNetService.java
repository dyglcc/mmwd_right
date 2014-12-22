package qfpay.wxshop.data.netImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface LovelyCardNetService {
	@GET("/qmm/shop/card/view")
	LovelyCardDataWrapper getLC();
	
	@FormUrlEncoded
    @POST("/qmm/shop/card/create")
    CommonJsonBean createLC(
    		@Field(value = "name")  String name, 
    		@Field(value = "bgimg") String bgimg, 
    		@Field(value = "descr") String descr, 
    		@Field(value = "tags")  String tags,
    		@Field(value = "shopid")String shopid);
	
	@FormUrlEncoded
	@POST("/qmm/shop/card/update")
	CommonJsonBean updateLC(
			@Field(value = "name")  String name, 
			@Field(value = "bgimg") String bgimg, 
			@Field(value = "descr") String descr, 
			@Field(value = "tags")  String tags);
	
	public static class LovelyCardDataWrapper extends CommonJsonBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public LovelyCardBean data;
	}
	
	public static class LovelyCardBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String name = "";
		public String descr = "";
		public String bgimg = "";
		public List<String> tags = new ArrayList<String>();
		
		public void addTag(String tag) {
			tags.add(tag);
		}
		
		public String getTag() {
			String tags = "";
			for (String string : this.tags) {
				if (string == this.tags.get(0)) {
					tags = string;
					continue;
				}
				tags = tags + "," + string;
			}
			return tags;
		}

		public String getTag(int position) {
			if (tags.size() > position) return tags.get(position);
			return "";
		}
	}
	
	@GET("/api/shop/v2/comment/list")
	CommonNetBean getCommentList(@Query("shopid") String shopid, @Query("start") int start, @Query("len") int len);
	
	public static class CommonNetBean extends CommonJsonBean implements Serializable {
		private static final long serialVersionUID = 1L;
		public CommentListBean data;
	}
	
	public static class CommentListBean implements Serializable {
		private static final long serialVersionUID = 1L;
		public int count;
		public List<CommentBean> records;
	}
	
	public static class CommentBean implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String content;
        public String created;
        public CommentUserinfo userinfo;
	}
	
	public static class CommentUserinfo implements Serializable {
		private static final long serialVersionUID = 1L;
		public String province;
		public String city;
		public String headimgurl;
		public String country;
		public String sex;
		public String[] privilege;
		public String nickname;
	}
}
