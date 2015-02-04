package qfpay.wxshop.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.sharesdk.framework.ShareSDK;

import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

public class BaseFragment extends SherlockFragment {
	protected WeakReference<Activity> mParentActivityRef;
	protected WeakReference<Fragment> mParentFragmentRef;
	protected boolean attached;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ShareSDK.initSDK(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		attached = true;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("CommonFragment");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("CommonFragment");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		attached =false;
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
