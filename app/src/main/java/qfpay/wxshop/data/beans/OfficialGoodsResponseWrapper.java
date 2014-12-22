package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class OfficialGoodsResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;

	public MsgsWrapper getData() {
		return data;
	}

	public void setData(MsgsWrapper data) {
		this.data = data;
	}
                                                                     
	public class MsgsWrapper {
		private List<OfficialGoodItemBean> items;
	
		private int count;

		public List<OfficialGoodItemBean> getItems() {
			return items;
		}

		public void setItems(List<OfficialGoodItemBean> items) {
			this.items = items;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

	}

}
