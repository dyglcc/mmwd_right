package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class OneKeybehalfListResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;
	public MsgsWrapper getData() {
		return data;
	}
	public void setData(MsgsWrapper data) {
		this.data = data;
	}
	
	public class MsgsWrapper {
		private List<OnekeybehalfItemBean> items;

		public List<OnekeybehalfItemBean> getItems() {
			return items;
		}

		public void setMsgs(List<OnekeybehalfItemBean> items) {
			this.items = items;
		}
	}

}
