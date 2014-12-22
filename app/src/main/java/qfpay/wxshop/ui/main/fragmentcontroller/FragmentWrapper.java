package qfpay.wxshop.ui.main.fragmentcontroller;

import qfpay.wxshop.ui.main.fragment.BaseFragment;

public interface FragmentWrapper {
	/**
	 * 得到Fragment,如果没有则新建
	 * @param position 所需的Fragment在这组Fragment中的位置
	 */
	public BaseFragment get(int position);
	
	/**
	 * 刷新这个Fragment
	 * @param position Fragment在这组Fragment中的位置
	 */
	public void refresh(int position);
	
	/**
	 * 清空这组Fragment,并且回收资源
	 */
	public void clear();
	
	/**
	 * 回收Fragment的资源
	 * @param position 需要回收的Fragment在这组Fragment中的位置
	 */
	public void remove(int position);
	
	/**
	 * 得到这组Fragment对应的Umeng事件的名称
	 */
	public String getUmengEventName(int position);
}
