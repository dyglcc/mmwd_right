package qfpay.wxshop.data.beans;

import java.util.List;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class FactoryContractResponseWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;

	public MsgsWrapper getData() {
		return data;
	}

	public void setData(MsgsWrapper data) {
		this.data = data;
	}

	public class MsgsWrapper {

		private Contract contact;

		public Contract getContact() {
			return contact;
		}

		public void setContact(Contract contact) {
			this.contact = contact;
		}

	}

	public class Contract {

		private String qq;

		public String getQq() {
			return qq;
		}

		public void setQq(String qq) {
			this.qq = qq;
		}

		public String getWeixin() {
			return weixin;
		}

		public void setWeixin(String weixin) {
			this.weixin = weixin;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		private String weixin;
		private String telephone;
	}

}
