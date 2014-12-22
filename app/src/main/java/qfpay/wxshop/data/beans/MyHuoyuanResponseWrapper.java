package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class MyHuoyuanResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;

	public MsgsWrapper getData() {
		return data;
	}

	public void setData(MsgsWrapper data) {
		this.data = data;
	}

	public class MsgsWrapper {
		private List<MyHuoyuanItemBean> orders;

		public List<MyHuoyuanItemBean> getOrders() {
			return orders;
		}

		public void setOrders(List<MyHuoyuanItemBean> orders) {
			this.orders = orders;
		}


	}

}
