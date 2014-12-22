package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
public class PromoStatus {

	private boolean needRefresh;
	public boolean isNeedRefresh() {
		return needRefresh;
	}
	public void setNeedRefresh(boolean needRefresh) {
		this.needRefresh = needRefresh;
	}
	private int pos;
	private GoodMSBean gms;
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public GoodMSBean getGms() {
		return gms;
	}
	public void setGms(GoodMSBean gms) {
		this.gms = gms;
	}
	public void clear(){
		needRefresh = false;
	}
}
