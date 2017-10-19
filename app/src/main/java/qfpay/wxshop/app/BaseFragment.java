package qfpay.wxshop.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import cn.sharesdk.framework.ShareSDK;

import com.actionbarsherlock.app.SherlockFragment;
//import com.adhoc.adhocsdk.AdhocTracker;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

import qfpay.wxshop.utils.*;

public class BaseFragment extends SherlockFragment {
	protected WeakReference<Activity> mParentActivityRef;
	protected WeakReference<Fragment> mParentFragmentRef;
	protected boolean attached;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		ShareSDK.initSDK(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		AdhocTracker.onFragmentCreate(getActivity(), this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attached = true;
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
//		T.i("ADHOC_SDK",this.getClass().getName() +" hitUser " + isVisibleToUser);
//		AdhocTracker.onViewPagerFragmentStat(this, isVisibleToUser);
//		Field field = null;
//		try {
//			field = ReflectionUtil.getFieldByClassName(this.getClass(), "mActivity", "Fragment");
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		}
//		if(field == null){
//
//		}
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		T.i("ADHOC_SDK", "resume : " + this.getClass().getName());
		MobclickAgent.onPageStart("CommonFragment");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		T.i("ADHOC_SDK", "is is is is resume : " + isResumed() + this.getClass().getName());
		MobclickAgent.onPageEnd("CommonFragment");
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		attached =false;
//		AdhocTracker.onFragmentDestory(getActivity(),this);
	}

	public void onFragmentRefresh() {

	}

	public BaseFragment setParent(Activity activity) {
		this.mParentActivityRef = new WeakReference<Activity>(activity);
		return this;
	}

	public BaseFragment setParent(Fragment fragment) {
		this.mParentFragmentRef = new WeakReference<Fragment>(fragment);
		return this;
	}
}
