package qfpay.wxshop.ui.commodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.getui.ImageUtils.ImageSizeForUrl;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.commodity.CommodityDataController.CommodityCallback;
import qfpay.wxshop.ui.commodity.CommodityDataController.Operation;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.view.XListView;
import qfpay.wxshop.ui.view.XListView.IXListViewListener;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

@EFragment(R.layout.commodity_list)
public class CommodityListFragment extends BaseFragment implements IXListViewListener, CommodityCallback, CommodityShare {
	@ViewById XListView   listView;
	@ViewById FrameLayout fl_indictor;
	@ViewById ImageView   iv_indictor;
	@DrawableRes Drawable commodity_list_refresh;
	
	@Bean CommodityDataController dataController;
	private DataControllerAdapter adapter;
	
	@AfterViews void beforeInit() {
		refreshListView(RefreshFrom.LOADING);
	}
	
	@AfterViews @UiThread(delay = 1000) void init() {
		dataController.setCallback(this);
		initListView();
	}
	
	@Override public void onDestroy() {
		dataController.removeCallback();
		super.onDestroy();
	}
	
	@IgnoredWhenDetached @Override public void onFragmentRefresh() {
		refreshListView(RefreshFrom.REFRESH);
	}
	
	/**
	 * 初始化列表
	 */
	private void initListView() {
		listView.setPullRefreshEnable(false);
		listView.setAutoLoadEnable(false);
		listView.setXListViewListener(this);
		listView.setPullLoadEnable(false);
		
		adapter = new DataControllerAdapter();
		listView.setAdapter(adapter);
		if (dataController.getCurrentList() == null || dataController.getCurrentList().isEmpty()) {
			dataController.reloadData();
			refreshListView(RefreshFrom.LOADING);
		} else {
			refreshListView(RefreshFrom.REFRESH);
		}
	}
	
	/**
	 * 所有对列表的刷新都需要经过这个方法
	 * 这个方法会对整体列表的状态进行fit
	 */
	void refreshListView(RefreshFrom from) {
		switch (from) {
		case REFRESH:
			if (dataController.getCurrentList().isEmpty()) {
				setListState(ListState.NULL);
			} else if (dataController.getCurrentList().size() <= 3) {
                setListState(ListState.NOT_ENOUGH);
            } else {
				setListState(ListState.NORMAL);
			}
			break;
		case NETERROR:
			setListState(ListState.ERROR);
			break;
		case SERVERERROR:
			setListState(ListState.ERROR);
			break;
		case LOADING:
			setListState(ListState.LOADING);
			break;
		}
		listView.setPullLoadEnable(dataController.hasNext());
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@ItemClick void listView(int position) {
		int pos = position - 1;
		if (adapter.getItem(pos).isMenuOpened) { 
			adapter.closeMenu(adapter.getItem(pos)); 
		} else {
			adapter.openMenu(adapter.getItem(pos));
			if (position == listView.getLastVisiblePosition()) {
				listView.setSelection(pos - 1);
			}
		}
	}
	
	@Override @UiThread @IgnoredWhenDetached public void onSuccess(Operation operation) {
		// 没有加判断是因为现在几乎所有的情况都需要刷新列表来完成
		listView.stopRefresh();
		listView.stopLoadMore();
		refreshListView(RefreshFrom.REFRESH);
	}

	@Override @UiThread @IgnoredWhenDetached public void onNetError(Operation operation) {
		if (operation == Operation.GET_LIST) {
			listView.stopRefresh();
			listView.stopLoadMore();
		}
		Toaster.s(getActivity(), "网络有问题~检查一下网络再重试一下啦");
		refreshListView(RefreshFrom.NETERROR);
	}

	@Override @UiThread @IgnoredWhenDetached public void onServerError(Operation operation, String msg) {
		if (operation == Operation.GET_LIST) {
			listView.stopRefresh();
			listView.stopLoadMore();
		}
		Toaster.s(getActivity(), msg);
		refreshListView(RefreshFrom.SERVERERROR);
	}

	@Override @UiThread @IgnoredWhenDetached public void refresh(Operation operation) {
		// 没有加判断是因为现在几乎所有的操作都需要刷新列表来完成
		refreshListView(RefreshFrom.REFRESH);
	}
	
	@Override public void onRefresh() {
		dataController.reloadData();
	}

	@Override public void onLoadMore() {
		dataController.nextPage();
	}
	
	@IgnoredWhenDetached void setListState(ListState state) {
		if (state == ListState.NULL) {
			listView.setVisibility(View.INVISIBLE);
			fl_indictor.setVisibility(View.VISIBLE);
			iv_indictor.setImageResource(R.drawable.commodity_list_nodata);
		}

        if (state == ListState.NOT_ENOUGH) {
            listView.setVisibility(View.VISIBLE);
            fl_indictor.setVisibility(View.VISIBLE);
            iv_indictor.setImageResource(R.drawable.commodity_list_notenoughdata);
        }
		
		if (state == ListState.LOADING) {
			listView.setVisibility(View.INVISIBLE);
			fl_indictor.setVisibility(View.VISIBLE);
			iv_indictor.setImageDrawable(commodity_list_refresh);
			((AnimationDrawable) (commodity_list_refresh)).start();
		}
		
		if (state == ListState.NORMAL) {
			listView.setVisibility(View.VISIBLE);
			fl_indictor.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 表示刷新的来源
	 */
	public enum RefreshFrom {
		NETERROR, SERVERERROR, REFRESH, LOADING
	}
	
	/**
	 * 表示列表的状态
	 */
	public enum ListState {
		NULL, NOT_ENOUGH, LOADING, NORMAL, ERROR
	}
	
	class DataControllerAdapter extends BaseAdapter {
		Map<Integer, CommodityWrapper> wrapperMap = new HashMap<Integer, CommodityListFragment.CommodityWrapper>();
		List<CommodityWrapper> wrapperList = new ArrayList<CommodityWrapper>();
		
		public DataControllerAdapter() {
			processData();
		}
		
		@Override
		public int getCount() {
			return wrapperList.size();
		}
		
		@Override
		public CommodityWrapper getItem(int position) {
			return wrapperList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommodityItemView item = (CommodityItemView) convertView;
			if (item == null) {
				item = CommodityItemView_.build(getActivity());
			}
			item.setData(getItem(position), CommodityListFragment.this);
			return item;
		}
		
		@Override
		public void notifyDataSetChanged() {
			processData();
			super.notifyDataSetChanged();
		}
		
		public void processData() {
			wrapperList.clear();
			for(CommodityModel commodityModel : dataController.getCurrentList()) {
				CommodityWrapper wrapper = wrapperMap.get(commodityModel.getID());
				if (wrapper == null) {
					wrapper = new CommodityWrapper();
				}
				wrapper.model = commodityModel;
				wrapperMap.put(commodityModel.getID(), wrapper);
				wrapperList.add(wrapper);
			}
		}
		
		public void openMenu(CommodityWrapper commodity) {
			commodity.isMenuOpened = true;
			commodity.isAni = true;
			for (CommodityWrapper commodityWrapper : wrapperList) {
				if (commodityWrapper.isMenuOpened && !commodity.equals(commodityWrapper)) {
					commodityWrapper.isMenuOpened = false;
					commodityWrapper.isAni = true;
				}
			}
			notifyDataSetChanged();
		}
		
		public void closeMenu(CommodityWrapper commodity) {
			commodity.isMenuOpened = false;
			commodity.isAni = true;
			notifyDataSetChanged();
		}
	}
	
	private static final int SHARE_MOMENT = 0;
	private static final int SHARE_FRIENT = 1;
	private static final int ONE_KEY_SHARE = 2;
	private static final int COPY_LINK = 3;

	@Override
	public void onShare(final CommodityModel model) {
		String[] items = getResources().getStringArray(R.array.share_friends);
		items[COPY_LINK] = "复制商品链接";
		QMMAlert.showAlert(getActivity(), getString(R.string.share2), items, null, new QMMAlert.OnAlertSelectId() {
			@Override public void onClick(int whichButton) {
				switch (whichButton) {
				case SHARE_MOMENT:
					MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_goods_circle");
					momentsGoodItem(model);
					break;
				case SHARE_FRIENT:
					MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_goods_friend");
					friendGoodItem(model);
					break;
				case ONE_KEY_SHARE:
					MobAgentTools.OnEventMobOnDiffUser(getActivity(), "share_goods_onkey");
					WxShopApplication.shareBean = getShareBean("single_item", model);
					Intent intent = new Intent(getActivity(), ShareActivity.class);
					intent.putExtra(ConstValue.gaSrcfrom, ConstValue.android_mmwdapp_manageshare_);
					intent.putExtra("share_content_type", ShareActivity.SHARE_CONTENT_GOOD_ITEM);
					startActivity(intent);
					break;
				case COPY_LINK:
					String link = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID();
					Utils.saveClipBoard(getActivity(), link);
					Toaster.s(getActivity(), getResources().getString(R.string.share_item_copy));
					break;
				}
			}
		});
	}

	private void momentsGoodItem(CommodityModel model) {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "weixin_share_moment_begin");
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl(model.getImgUrl(), ImageSizeForUrl.MIN);
		String desc = model.getDescript();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		wdb.description = desc;
		SalesPromotionModel promotionModel = model.getSalesPromotion();
		if (promotionModel != null && promotionModel.getPromotionID() != 0) {
			wdb.title = "【限时秒杀】" + model.getName() + "特价" + promotionModel.getPromotionPrice() + "元，手快有、手慢无哦！";
		} else {
			wdb.title = "【新品推荐】" + model.getName() + "仅需" + model.getPrice() + "元，欢迎进店选购下单哟！";
		}
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wctimeline, getActivity());
	}

	private void friendGoodItem(CommodityModel model) {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "weixin_share_friend_begin");
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.url = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID();
		wdb.imgUrl = QFCommonUtils.generateQiniuUrl(model.getImgUrl(), ImageSizeForUrl.MIN);
		String desc = model.getDescript();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		wdb.description = desc;
		SalesPromotionModel promotionModel = model.getSalesPromotion();
		if (promotionModel != null && promotionModel.getPromotionID() != 0) {
			wdb.title = "【限时秒杀】" + model.getName() + "特价" + promotionModel.getPromotionPrice() + "元，手快有、手慢无哦！";
		} else {
			wdb.title = "【新品推荐】" + model.getName() + "仅需" + model.getPrice() + "元，欢迎进店选购下单哟！";
		}
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_manageshare_wcfriend, getActivity());
	}

	protected ShareBean getShareBean(String str, CommodityModel model) {
		ShareBean shareBean = new ShareBean();
		shareBean.imgUrl = Utils.getThumblePic(model.getImgUrl(), ConstValue.shareBigPic);
		shareBean.link = "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item/" + model.getID();
		
		SalesPromotionModel promotionModel = model.getSalesPromotion();
		if (promotionModel != null && promotionModel.getPromotionID() != 0) {
			shareBean.title = "【限时秒杀】" + model.getName() + "特价" + promotionModel.getPromotionPrice() + "元，手快有、手慢无哦！";
		} else {
			String descString = model.getDescript();
			if (descString.length() > 100) {
				descString = descString.substring(0, 98) + "...";
			}
			shareBean.title = "亲,我的店铺又有新宝贝了哦!" + model.getName() + " "
					+ descString + " 仅需" + model.getPrice() + "元,点击宝贝链接"
					+ "http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + model.getID()
					+ " 直接下单购买哦";
			shareBean.from = "manageshop";
		}
		
		String desc = model.getDescript();
		if (desc.length() > 100) {
			desc = desc.substring(0, 100) + "...";
		}
		shareBean.desc = desc;
		shareBean.qq_imageUrl = model.getImgUrl();
		shareBean.qqTitle = "亲,我的店铺又有新宝贝了哦! ";
		shareBean.qqTitle_url = shareBean.link;
		shareBean.qqText = shareBean.title;
		return shareBean;

	}
	
	public static class CommodityWrapper {
		CommodityModel model;
		boolean isAni = false;
		boolean isMenuOpened = false;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isAni ? 1231 : 1237);
			result = prime * result + (isMenuOpened ? 1231 : 1237);
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CommodityWrapper other = (CommodityWrapper) obj;
			if (isAni != other.isAni)
				return false;
			if (isMenuOpened != other.isMenuOpened)
				return false;
			if (model == null) {
				if (other.model != null)
					return false;
			} else if (!model.equals(other.model))
				return false;
			return true;
		}
	}
//	public void destory(){
//		if(dataController!=null){
//			dataController.removeCallback();
//		}
//	}
}
