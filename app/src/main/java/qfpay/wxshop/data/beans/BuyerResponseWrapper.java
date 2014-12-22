package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class BuyerResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;
	public MsgsWrapper getData() {
		return data;
	}
	public void setData(MsgsWrapper data) {
		this.data = data;
	}
	
	public static class MsgsWrapper {
		private List<BuyerShowBean> msgs;

		public List<BuyerShowBean> getMsgs() {
			return msgs;
		}

		public void setMsgs(List<BuyerShowBean> msgs) {
			this.msgs = msgs;
		}
	}
	
	public static class BuyerShowBean implements Serializable{
		private static final long serialVersionUID = 1L;
		private String id;
		private String msg_type = "1";
		private String content = "";
		private String weixinid = "";
		private String good_id = "";
		private String create_time = "";
		private String update_time = "";
		private String chineseDate;
		public String getChineseDate() {
			return chineseDate;
		}
		public void setChineseDate(String chineseDate) {
			this.chineseDate = chineseDate;
		}
		private List<ImageBean> hm_images;
		public String getMid() {
			return id;
		}
		public void setMid(String mid) {
			this.id = mid;
		}
		public String getMsg_type() {
			return msg_type;
		}
		public void setMsg_type(String msg_type) {
			this.msg_type = msg_type;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getWeixinid() {
			return weixinid;
		}
		public void setWeixinid(String weixinid) {
			this.weixinid = weixinid;
		}
		public String getGood_id() {
			return good_id;
		}
		public void setGood_id(String good_id) {
			this.good_id = good_id;
		}
		
		public List<ImageBean> getHm_images() {
			return hm_images;
		}
		public void setHm_images(List<ImageBean> hm_images) {
			this.hm_images = hm_images;
		}
		@Override
		public String toString() {
			return "BuyerShowBean [mid=" + id + ", msg_type=" + msg_type
					+ ", content=" + content + ", weixinid=" + weixinid
					+ ", good_id=" + good_id + ", hm_images=" + hm_images + "]";
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getUpdate_time() {
			return update_time;
		}
		public void setUpdate_time(String update_time) {
			this.update_time = update_time;
		}
	}
	
	public static class ImageBean  implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String id;
		private String url;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
		@Override
		public String toString() {
			return "ImageBean [id=" + id + ", url=" + url + "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
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
			ImageBean other = (ImageBean) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
	}
}
