package qfpay.wxshop.ui.main.fragmentcontroller;

import java.lang.ref.WeakReference;

import qfpay.wxshop.app.BaseFragment;
import android.util.SparseArray;

public abstract class BasePagerFragmentWrapper implements FragmentWrapper {
	SparseArray<WeakReference<BaseFragment>> fragmentList = new SparseArray<WeakReference<BaseFragment>>();

	@Override
	public BaseFragment get(int position) {
		WeakReference<BaseFragment> ref = fragmentList.get(position);
		if (ref == null || ref.get() == null) {
			ref = new WeakReference<BaseFragment>(newFragment(position));
			fragmentList.put(position, ref);
		}
		return ref.get();
	}

	@Override
	public void refresh(int position) {
		get(position).onFragmentRefresh();
	}

	@Override
	public void clear() {
		for (int i = 0; i < fragmentList.size(); i++) {
			WeakReference<BaseFragment> ref = fragmentList.valueAt(i);
			if (ref != null) {
				ref.clear();
			}
		}
		fragmentList.clear();
	}

	@Override
	public void remove(int position) {
		WeakReference<BaseFragment> ref = fragmentList.get(position);
		if (ref != null) {
			ref.clear();
		}
		fragmentList.remove(position);
	}

	@Override
	public abstract String getUmengEventName(int position);
	
	public abstract BaseFragment newFragment(int position);
}
