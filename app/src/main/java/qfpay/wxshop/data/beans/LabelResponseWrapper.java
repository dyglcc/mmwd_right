package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class LabelResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private DataWrapper data;

	public DataWrapper getData() {
		return data;
	}

	public void setData(DataWrapper data) {
		this.data = data;
	}

	public class DataWrapper {
		private List<LabelBean> items;

		public List<LabelBean> getItems() {
			return items;
		}

		public void setItems(List<LabelBean> items) {
			this.items = items;
		}

	}

}

