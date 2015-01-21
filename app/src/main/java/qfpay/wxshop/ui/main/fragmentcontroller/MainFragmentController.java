package qfpay.wxshop.ui.main.fragmentcontroller;

import java.util.HashMap;

import android.content.Context;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;


public class MainFragmentController {
	private static HashMap<WrapperType, MainFragmentController> map = new HashMap<MainFragmentController.WrapperType, MainFragmentController>();
	
	private FragmentWrapper wrapper;
	
	private MainFragmentController(WrapperType type) {
		this.wrapper = type.getFragmentWrapper();
	}
	
	public static MainFragmentController get(WrapperType type) {
		if (map.get(type) == null) {
			map.put(type, new MainFragmentController(type));
		}
		return map.get(type);
	}
	
	public BaseFragment get(int position) {
		return wrapper.get(position);
	}

	public MainFragmentController refresh(int position) {
		wrapper.refresh(position);
		return this;
	}
	
	public MainFragmentController clear() {
		wrapper.clear();
		return this;
	}
	
	public MainFragmentController remove(int position) {
		wrapper.remove(position);
		return this;
	}
	
	public MainFragmentController sendUmengEvent(Context context, int position) {
		MobAgentTools.OnEventMobOnDiffUser(context, wrapper.getUmengEventName(position));
		QFCommonUtils.collect(wrapper.getUmengEventName(position), context);
		return this;
	}

	public enum WrapperType {
		MAIN              (new PopularizingFragmentWrapper()),
		SHOP              (new PopularizingFragmentWrapper()),
		ORDER             (new PopularizingFragmentWrapper()),
		POPULARIZING      (new PopularizingFragmentWrapper()),
		BUSINESS_COMMUNITY(new BusinessCommunityFragmentWrapper());
		
		private FragmentWrapper wrapper;
		
		WrapperType(FragmentWrapper wrapper) {
			this.wrapper = wrapper;
		}
		
		public FragmentWrapper getFragmentWrapper() {
			return wrapper;
		}
	}
}
