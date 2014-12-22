package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
public class BankBean {
	private String bName;
	private String bnum;
	private String iconFileName;
	public String getbName() {
		return bName;
	}
	public void setbName(String bName) {
		this.bName = bName;
	}
	public String getBnum() {
		return bnum;
	}
	public void setBnum(String bnum) {
		this.bnum = bnum;
	}
	public String getIconFileName() {
		return iconFileName;
	}
	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}
}
