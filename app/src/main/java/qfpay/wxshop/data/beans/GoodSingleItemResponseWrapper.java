package qfpay.wxshop.data.beans;

import java.io.Serializable;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class GoodSingleItemResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private GoodWrapper data;
	public GoodWrapper getData() {
		return data;
	}
	public void setData(GoodWrapper data) {
		this.data = data;
	}
	
	public class GoodWrapper {
		private GoodItemBean good;

		public GoodItemBean getGood() {
			return good;
		}

		public void setGood(GoodItemBean good) {
			this.good = good;
		}
	}
	
	public class GoodItemBean implements Serializable{
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
		
	}
}
