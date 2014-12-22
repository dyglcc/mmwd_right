package qfpay.wxshop.data.net;

import qfpay.wxshop.utils.MobAgentTools;
public interface NetCallBack {
	public void onSuccessed(String data, int which);
	public void onServerFailed(String errorCode, String errorMsg);
	public void onNetDown(String msg);
}
