package qfpay.wxshop.data.beans;

import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;

public class SSNCreateSucsRespWrapper extends CommonJsonBean {
	private static final long serialVersionUID = 1L;
	private MsgsWrapper data;
	public MsgsWrapper getData() {
		return data;
	}
	public void setData(MsgsWrapper data) {
		this.data = data;
	}
	
	public class MsgsWrapper {
		private SSNItemBean msg;

		public SSNItemBean getMsg() {
			return msg;
		}

		public void setMsg(SSNItemBean msg) {
			this.msg = msg;
		}
	}
		
}
