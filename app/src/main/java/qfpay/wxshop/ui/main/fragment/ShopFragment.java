package qfpay.wxshop.ui.main.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.commodity.CommodityDataController;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.indicator.TabPageIndicator;

@SuppressLint("DefaultLocale") @EFragment(R.layout.shop_manager)
public class ShopFragment extends BaseFragment implements OnShareLinstener {
	private static final String[] CONTENT = new String[] {"商品", "预览","统计","订单"};
	
	@ViewById ViewPager                 pager;
	@ViewById TabPageIndicator          indicator;
	@Bean     CommodityDataController   commodityController;
    @Pref     AppStateSharePreferences_ statePref;
	@AfterViews
	void init() {
		ShopFragmentsWrapper.clear();
		refreshView(false);
	}
	
	@Override public void onFragmentRefresh() {
        ShopFragmentsWrapper.getFragment(0,getActivity()).onFragmentRefresh();
		refreshView(true);
	}
	
	@IgnoredWhenDetached void refreshView(boolean isRemovePage) {
		if (getActivity().isFinishing()) {
			return;
		}
		ShopItemAdapter adapter = new ShopItemAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(adapter);
		indicator.notifyDataSetChanged();
	}
	
	public void changePager(int index) {
		pager.setCurrentItem(index, true);
		if(index == 1){
			if(getActivity()!=null){
				MobAgentTools.OnEventMobOnDiffUser(getActivity(), "click_pay_supply_of_goods");
			}
		}
	}
	
	class ShopItemAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
		public ShopItemAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public BaseFragment getItem(int position) {
			return ShopFragmentsWrapper.getFragment(position, getActivity());
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
		
		@Override
		public void onPageSelected(int position) {
			ShopFragmentsWrapper.onFragmentSelect(position, getActivity());
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), ShopFragmentsWrapper.getUmenEventName(position));

            // 在预览了店铺的时候取消开启preview的引导
            if (position == 1) {
                statePref.guidePointer().put(MainActivity.GUIDE_SHARE);
            }
		}
		@Override public void onPageScrollStateChanged(int arg0) { }
		@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
	}

	private void momentsShop() {
		if (commodityController.getCurrentList().size() == 0) {
			Toaster.s(getActivity(), "亲，店铺里面空空如也，先发布个商品再分享吧");
			return;
		}

		MobAgentTools.OnEventMobOnDiffUser(getActivity(),
				"weixin_share_moment_begin");
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.title = "微店每日上新，欢迎选购哟！";
		String content = wdb.title;
		CommodityModel model = commodityController.getCurrentList().get(0);
		if (model != null) {
			content += "#每日上新#" + model.getName();
			String desc = model.getDescript();
			if (desc.length() > 100) {
				desc = desc.substring(0, 100) + "...";
			}
			content += desc;
		}
		wdb.description = content;
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/shop/" + WxShopApplication.dataEngine.getShopId();
		wdb.imgUrl = Utils.getThumblePic(WxShopApplication.dataEngine.getAvatar(), 120);
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb,ConstValue.android_mmwdapp_home_wctimeline, getActivity());
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "circle");
	}

	private void friendShop() {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "weixin_share_friend_begin");
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.title = "店里上了好多新品，欢迎进店选购下单哟！";
		wdb.description = WxShopApplication.dataEngine.getShopName() + ",这是我的微店，商品支持手机支付购买哟~";
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/shop/" + WxShopApplication.dataEngine.getShopId();
		wdb.imgUrl = Utils.getThumblePic(WxShopApplication.dataEngine.getAvatar(), 120);
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb,ConstValue.android_mmwdapp_home_wcfriend, getActivity());
	}

	protected ShareBean getShareBean(String str, CommodityModel model) {
		if (commodityController.getCurrentList() == null) {
			return null;
		}
		if (commodityController.getCurrentList().isEmpty()) {
			Toaster.l(getActivity(), "亲，店铺里面空空如也，先发布个商品再分享吧");
			return null;
		}
		ShareBean sb = new ShareBean();
		if (model == null) {
			model = commodityController.getCurrentList().get(0);
		}
		if (str.equals("shop")) {
			sb.imgUrl = WxShopApplication.dataEngine.getAvatar();
		} else {
			sb.imgUrl = Utils.getThumblePic(model.getImgUrl(), ConstValue.shareBigPic);
		}

		if (str.equals("shop")) {
			sb.link = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/shop/"
					+ WxShopApplication.dataEngine.getShopId()+ " ";
		} else {
			sb.link = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID();
		}
		if (str.equals("shop")) {
			sb.title = WxShopApplication.dataEngine.getShopName()
					+ "又有新货上架啦，请大家进来捧个场，点个赞哟！店铺链接：" + sb.link;
			sb.from = "shoplink";
		} else {
			SalesPromotionModel promotion = model.getSalesPromotion();
			if (promotion != null && promotion.getCommodityID() != 0) {
				sb.title = "【限时秒杀】" + model.getName() + "特价"
						+ promotion.getPromotionPrice() + "元，手快有、手慢无哦！";
			} else {
				String descString = model.getDescript();
				if (descString.length() > 100) {
					descString = descString.substring(0, 98) + "...";
				}
				// 【店铺名字】又有新货上架啦，请大家多多支持，进来捧个场，点个赞哟！店铺链接：
				sb.title = "亲,我的店铺又有新宝贝了哦! " + model.getName() + " 仅需"
						+ model.getPrice() + "元,点击宝贝链接 "
						+ "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID()
						+ " 直接下单购买哦";
				sb.from = "manageshop";
			}
		}
		if (str.equals("shop")) {
			sb.desc += model.getName();
			String desc1 = model.getDescript();
			if (desc1.length() > 100) {
				desc1 = desc1.substring(0, 100) + "...";
			}
			sb.desc += desc1;
		} else {
			String desc = model.getDescript();
			if (desc.length() > 100) {
				desc = desc.substring(0, 100) + "...";
			}
			sb.desc = desc;
		}
		// qqzone
		if (str.equals("shop")) {
			sb.qq_imageUrl = WxShopApplication.dataEngine.getAvatar();
		} else {
			sb.qq_imageUrl = Utils.getThumblePic(model.getImgUrl(), ConstValue.shareBigPic);
		}
		sb.qqTitle = "亲,我的店铺又有新宝贝了哦! ";
		sb.qqTitle_url = sb.link;
		sb.qqText = sb.title;
		return sb;
	}

	@Override
	public void onShare(SharedPlatfrom which) {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_manage");
		switch (which) {
		case WXFRIEND:
			Toaster.l(getActivity(), getString(R.string.start_share));
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_wechatfriend_manage");
			friendShop();
			break;
		case WXMOMENTS:
			Toaster.l(getActivity(), getString(R.string.start_share));
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_circle_manage");
			momentsShop();
			break;
		case ONEKEY:
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_shop_onekey_manage");
			WxShopApplication.shareBean = getShareBean("shop", null);
			if (WxShopApplication.shareBean == null) {
				Toaster.s(getActivity(), "还没有商品~先发布一个喽~");
				return;
			}
			Intent intent = new Intent(getActivity(), ShareActivity.class);
			intent.putExtra(ConstValue.gaSrcfrom, ConstValue.android_mmwdapp_home_);
			intent.putExtra("share_content_type", ShareActivity.SHARE_CONTENT_SHOP);
			startActivity(intent);
			break;
		case COPY:
			Utils.saveClipBoard(getActivity(), WDConfig.SHOW_SHOP_ADDR + WxShopApplication.dataEngine.getShopId());
			MobAgentTools.OnEventMobOnDiffUser(getActivity(), "CLICK_SHOP_MAN_SHOPCOPY");
			Toaster.s(getActivity(), getResources().getString(R.string.share_item_copy));
			break;
		default:
			break;
		}
	}

	@Override
	public String getShareFromName() {
		return "店铺";
	}
}
