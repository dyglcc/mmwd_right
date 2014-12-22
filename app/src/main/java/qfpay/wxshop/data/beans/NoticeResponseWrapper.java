package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class NoticeResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;

	public MsgsWrapper getData() {
		return data;
	}

	public void setData(MsgsWrapper data) {
		this.data = data;
	}

	public class MsgsWrapper {
		private int total_count;
		private List<NoticeItemBean> notifications;


		public int getTotal_count() {
			return total_count;
		}

		public void setTotal_count(int total_count) {
			this.total_count = total_count;
		}

		public List<NoticeItemBean> getNotifications() {
			return notifications;
		}

		public void setNotifications(List<NoticeItemBean> notifications) {
			this.notifications = notifications;
		}

	}
}
