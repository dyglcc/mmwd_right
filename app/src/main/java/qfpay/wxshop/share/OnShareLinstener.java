package qfpay.wxshop.share;

public interface OnShareLinstener {
	/**
	 * 分享的回调
	 */
	public void onShare(SharedPlatfrom which);
	
	/**
	 * 返回当前需要分享的模块名称
	 */
	public String getShareFromName();
}
