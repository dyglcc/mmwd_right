package qfpay.wxshop.ui.main.fragment;import org.androidannotations.annotations.AfterViews;import org.androidannotations.annotations.Background;import org.androidannotations.annotations.Bean;import org.androidannotations.annotations.EFragment;import org.androidannotations.annotations.IgnoredWhenDetached;import org.androidannotations.annotations.UiThread;import org.androidannotations.annotations.ViewById;import org.androidannotations.annotations.sharedpreferences.Pref;import qfpay.wxshop.R;import qfpay.wxshop.WxShopApplication;import qfpay.wxshop.app.BaseFragment;import qfpay.wxshop.data.net.RetrofitWrapper;import qfpay.wxshop.data.netImpl.LovelyCardNetService;import qfpay.wxshop.data.netImpl.LovelyCardNetService.CommonNetBean;import qfpay.wxshop.share.OnShareLinstener;import qfpay.wxshop.share.SharedPlatfrom;import qfpay.wxshop.ui.main.AppStateSharePreferences_;import qfpay.wxshop.ui.main.MainActivity;import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController;import qfpay.wxshop.ui.main.fragmentcontroller.MainFragmentController.WrapperType;import qfpay.wxshop.utils.MobAgentTools;import qfpay.wxshop.utils.T;import android.annotation.SuppressLint;import android.content.Intent;import android.graphics.Color;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentManager;import android.support.v4.app.FragmentPagerAdapter;import android.support.v4.view.ViewPager;import android.support.v4.view.ViewPager.OnPageChangeListener;import android.text.Spannable;import android.text.SpannableString;import android.text.style.ForegroundColorSpan;import android.widget.TextView;import com.indicator.TabPageIndicator;import com.indicator.TabPageIndicator.TabClickListener;@SuppressLint("DefaultLocale") @EFragment(R.layout.shop_manager)public class PopularizingFragment extends BaseFragment implements TabClickListener, OnShareLinstener {	private static final String[] CONTENT = new String[] {"碎碎念", "买家秀", "萌片页"};		@ViewById ViewPager pager;	@ViewById TabPageIndicator indicator;	@Pref AppStateSharePreferences_ pref;	@Bean RetrofitWrapper mLCNetWrapper;	private LovelyCardNetService mLCNetService;	private OnShareLinstener mShareLinstener;		@AfterViews	void init() {		String lcUrl = WxShopApplication.app.getDomainMMWDUrl();		if (!lcUrl.contains("http://")) {			lcUrl = "http://" + lcUrl;		}		mLCNetService = mLCNetWrapper.getNetService(LovelyCardNetService.class, lcUrl);		processLCNewer();		refreshView(false);	}		@Override public void onFragmentRefresh() {		processLCNewer();		refreshView(true);	}		@IgnoredWhenDetached void refreshView(boolean isRemovePage) {		if (getActivity().isFinishing()) {			return;		}		PopularizingItemAdapter adapter = new PopularizingItemAdapter(getChildFragmentManager());		pager.setAdapter(adapter);		indicator.setmClickTabListener(this);		indicator.setViewPager(pager);		indicator.setOnPageChangeListener(adapter);		indicator.notifyDataSetChanged();	}		public void changePager(int index) {		if(pager!=null){			pager.setCurrentItem(index, true);		}	}		/**	 * 拉取当前萌片页评论列表,判断是否有新的评论	 */	@Background void processLCNewer() {		try {			if (pref.isLCNew().get()) {				setLCBadger(true);				return;			}			CommonNetBean bean = mLCNetService.getCommentList(WxShopApplication.dataEngine.getShopId(), 0, 1);			boolean isNew = !bean.data.records.get(0).created.equals(pref.theFirstLCTime().get());			setLCBadger(isNew);			pref.isLCNew().put(isNew);			pref.theFirstLCTime().put(bean.data.records.get(0).created);		} catch (Exception e) {			T.e(e);		}	}	/**	 * 设置萌片页的Tab的小红点	 */	@UiThread	public void setLCBadger(boolean isBadger) {		try {			if (isBadger) {				SpannableString string = new SpannableString(CONTENT[2] + " ●");				ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);				string.setSpan(span, 3, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);				((TextView) indicator.getTabView(2)).setText(string);			} else {				((TextView) indicator.getTabView(2)).setText(CONTENT[2]);			}		} catch (Exception e) {			T.e(e);		}	}		class PopularizingItemAdapter extends FragmentPagerAdapter implements OnPageChangeListener {		public PopularizingItemAdapter(FragmentManager fragmentManager) {			super(fragmentManager);		}		@Override		public BaseFragment getItem(int position) {			return MainFragmentController.get(WrapperType.POPULARIZING).get(position).setParent(PopularizingFragment.this);		}				@Override		public CharSequence getPageTitle(int position) {			return CONTENT[position % CONTENT.length].toUpperCase();		}		@Override		public int getCount() {			return CONTENT.length;		}				@Override		public void onPageSelected(int position) {			MainFragmentController.get(WrapperType.POPULARIZING).refresh(position).sendUmengEvent(getActivity(), position);						Fragment fragment = MainFragmentController.get(WrapperType.POPULARIZING).get(position);			MainActivity activity = (MainActivity) getActivity();			if (fragment instanceof OnShareLinstener) {				mShareLinstener = (OnShareLinstener) fragment;				activity.showShareButton(mShareLinstener);			} else {				mShareLinstener = null;				activity.hideShareButton();			}		}		@Override public void onPageScrollStateChanged(int arg0) { }		@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }	}		@Override	public void onActivityResult(int requestCode, int resultCode, Intent data) {		super.onActivityResult(requestCode, resultCode, data);	}	@Override	public void onTabClick(int newSelected) {		if(getActivity() == null){			return;		}		if(newSelected == 0){			MobAgentTools.OnEventMobOnDiffUser(getActivity(), "Click_HybridText");		}	}	@Override	public void onShare(SharedPlatfrom which) {		if (mShareLinstener != null) {			mShareLinstener.onShare(which);		}	}	@Override	public String getShareFromName() {		if (mShareLinstener != null) {			return mShareLinstener.getShareFromName();		}		return "";	}}