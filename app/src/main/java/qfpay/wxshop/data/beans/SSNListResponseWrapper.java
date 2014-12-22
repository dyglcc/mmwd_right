package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class SSNListResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;
	public MsgsWrapper getData() {
		return data;
	}
	public void setData(MsgsWrapper data) {
		this.data = data;
	}
	
	public class MsgsWrapper {
		private List<SSNItemBean> msgs;

		public List<SSNItemBean> getMsgs() {
			return msgs;
		}

		public void setMsgs(List<SSNItemBean> msgs) {
			this.msgs = msgs;
		}
	}
	
	
	
	public static class ImageBean  implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String id;
		private String url;
		private String hid;// 关联图文消息id
		
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
		public String getHid() {
			return hid;
		}
		public void setHid(String hid) {
			this.hid = hid;
		}
		
	}
}
