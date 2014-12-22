package qfpay.wxshop.ui.commodity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.data.beans.GoodWrapper;
import qfpay.wxshop.data.beans.SalesPromotionModel;
import qfpay.wxshop.data.beans.UnitBean;
import qfpay.wxshop.data.event.LogoutEvent;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.data.netImpl.CommodityService;
import qfpay.wxshop.data.netImpl.CommodityService.CommodityListDataWrapper;
import qfpay.wxshop.ui.main.fragment.ShopFragmentsWrapper;

import com.de.greenrobot.event.EventBus;

@EBean(scope = Scope.Singleton) public class CommodityDataController {
	public static final int FIRST_PAGENUM = 1;
	
	private List<CommodityModel>             data       = new ArrayList<CommodityModel>();
	@Bean   RetrofitWrapper                  netWrapper;
	private CommodityService                 netService;
	private SoftReference<CommodityCallback> callback;
	
	private int                              pageLength = 10;
	private int 						     pageNum    = FIRST_PAGENUM;// 当前页数
	private int 							 fixNum     = 0;            // 修正数, 如果有下架商品, 则+1
	private boolean 						 hasNext    = false;
	
	@AfterInject void init() {
		netService = netWrapper.getNetService(CommodityService.class);
		EventBus.getDefault().register(CommodityDataController.this);
	}
	
	public void onEvent(LogoutEvent event) {
		pageNum = FIRST_PAGENUM;
		fixNum  = 0;
		data.removeAll(data);
	}
	
	/**
	 * 在所有操作前设置callback
	 */
	public CommodityDataController setCallback(CommodityCallback callback) {
		this.callback = new SoftReference<CommodityCallback>(callback);
		return this;
	}
	
	public void removeCallback() {
		if (callback != null) {
			callback.clear();
		}
	}
	
	/**
	 * 返回当前的数据,没有任何操作,也没有刷新
	 */
	public List<CommodityModel> getCurrentList() {
		return data;
	}
	
	/**
	 * 得到商品的位置,现在的作用主要是为了兼容旧版本代码
	 */
	public int getIndex(CommodityModel commodityModel) {
		return data.indexOf(commodityModel);
	}
	
	/**
	 * 得到商品的位置,现在的作用主要是为了兼容旧版本代码
	 */
	public CommodityModel getCommodityModel(int commodityID) {
		for (CommodityModel commodityModel : data) {
			if (commodityModel.getID() == commodityID) {
				return commodityModel;
			}
		}
		return null;
	}
	
	/**
	 * 重载当前的列表数据
	 */
	public void reloadData() {
		pageNum = FIRST_PAGENUM;
		fixNum  = 0;
		getCommodityDataFromServer(FIRST_PAGENUM, pageLength, fixNum, true);
	}
	
	/**
	 * 重载当前所有的数据(例如:当前有十五条数据,重载0到14所有条目),主要是供发布商品成功时候使用
	 * 使用data.size()是因为本地是0-14,但是size是15,加上新发布的数据
	 * fixnum传0是因为fixnum只有在加载下一页的时候才使用(fixnum的意义是在于加载下一页数据的时候加载不会出错,所以刷新的时候传fixnum没有意义)
	 */
	public void reloadCurrentData() {
		getCommodityDataFromServer(FIRST_PAGENUM, data.size(), 0, true);
	}
	
	/**
	 * 加载下一页
	 */
	public void nextPage() {
		pageNum ++;
		getCommodityDataFromServer(pageNum, pageLength, fixNum, false);
	}
	
	public boolean hasNext() {
		return hasNext;
	}
	
	volatile boolean isGetDataRunning = false;
	@Background
	void getCommodityDataFromServer(int pageNum, int pageLength, int fixNum, boolean isReload) {
		if (isGetDataRunning) {
			return;
		}
		isGetDataRunning = true;
		try {
			CommodityListDataWrapper dataWrapper = netService.getCommodityList(pageNum, pageLength, fixNum, "app");
			if (dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				if (dataWrapper.data.items.size() == 0) {
					hasNext = false;
				}
				if (dataWrapper.data.items.size() == pageLength) {
					hasNext = true;
				} else {
					hasNext = false;
				}
				if (isReload) {
					data = dataWrapper.data.items;
				} else {
					data.addAll(dataWrapper.data.items);
				}
				onSuccess(Operation.GET_LIST);
			} else {
				onServerError(Operation.GET_LIST, dataWrapper.getResperr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			onNetError(Operation.GET_LIST);
		}
		isGetDataRunning = false;
	}
	
	public void processPagenum() {
		
	}
	
	/**
	 * 为了兼容旧的结构
	 */
	public void updateCommodity(GoodWrapper wrapper) {
		if (wrapper == null) {
			return;
		}
		getGoodsBean(wrapper, getCommodityModel(Integer.parseInt(wrapper.getId(), 10)));
		refresh(Operation.GET_LIST);
	}
	
	CommodityModel getGoodsBean(GoodWrapper wrapper, CommodityModel cm) {
		cm.setID(Integer.parseInt(wrapper.getId(), 10));
		cm.setImgUrl(wrapper.getImgWrapper(0).getUrl());
		cm.setDescript(wrapper.getDescription());
		cm.setName(wrapper.getName());
		cm.setPrice(wrapper.getPrice());
		int count = 0;
		for (UnitBean unit : wrapper.getUnitBeans()) {
			count += unit.getAmount();
		}
		cm.setStock(count);
		return cm;
	}
	
	/**
	 * 将一个商品置顶
	 */
	public void takeTop(CommodityModel model) {
		for (CommodityModel commodityModel : data) {
			if (commodityModel.getSortWeight() > 0) {
				commodityModel.setSortWeight(0);
				break;
			}
		}
		model.setSortWeight(1);
		changeWeight(model.getID(), model.getSortWeight());
		refresh(Operation.TAKE_TOP);
	}
	
	public void cancelTop(CommodityModel model) {
		model.setSortWeight(0);
		changeWeight(model.getID(), model.getSortWeight());
		refresh(Operation.TAKE_TOP);
	}
	
	/**
	 * 判断是否为一个被置顶的商品
	 */
	public boolean isTop(CommodityModel model) {
		if (model.getSortWeight() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前被置顶的数据
	 * 如果没有被置顶的数据返回值只为null
	 */
	public List<CommodityModel> getTopList() {
		List<CommodityModel> topList = new ArrayList<CommodityModel>();
		for (CommodityModel commodityModel : topList) {
			if (commodityModel.getSortWeight() > 0) {
				topList.add(commodityModel);
			}
		}
		if (topList.isEmpty()) {
			topList = null;
		}
		return topList;
	}
	
	@Background
	void changeWeight(int id, int weight) {
		try {
			CommonJsonBean bean = netService.topGoods(id + "", weight + "");
			if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				onSuccess(Operation.TAKE_TOP);
			} else {
				onServerError(Operation.TAKE_TOP, bean.getResperr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			onNetError(Operation.TAKE_TOP);
		}
	}
	
	public void setPromotion(SalesPromotionModel salesPromotionModel) {
		CommodityModel model = null;
		for (CommodityModel commodityModel : data) {
			if (commodityModel.getID() == salesPromotionModel.getCommodityID()) {
				model = commodityModel;
			}
		}
		if (model != null) {
			model.setSalesPromotion(salesPromotionModel);
		}
		refresh(Operation.CHANGE_PROMOTION);
	}
	
	/**
	 * 取消商品的秒杀状态
	 */
	public void cancelPromotion(CommodityModel model) {
		cancelPromotion(model.getID(), model.getSalesPromotion().getPromotionID());
		model.setSalesPromotion(null);
		refresh(Operation.CHANGE_PROMOTION);
	}
	
	@Background
	void cancelPromotion(int id, int promotionID) {
		try {
			CommonJsonBean bean = netService.cancelPromotion(id, promotionID);
			if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				onSuccess(Operation.CHANGE_PROMOTION);
			} else {
				onServerError(Operation.CHANGE_PROMOTION, bean.getResperr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			onNetError(Operation.CHANGE_PROMOTION);
		}
	}
	
	/**
	 * 移除一个商品
	 */
	public void remove(int index) {
		fixNum ++;
		removeCommodityData(data.remove(index).getID());
		refresh(Operation.DELETE_ITEM);
	}
	
	/**
	 * 移除一个商品
	 */
	public void remove(CommodityModel commodityModel) {
		fixNum ++;
		data.remove(commodityModel);
		removeCommodityData(commodityModel.getID());
		refresh(Operation.DELETE_ITEM);
	}
	
	@Background
	void removeCommodityData(int id) {
		try {
			CommonJsonBean dataWrapper = netService.deleteCommodity(id);
			if (!dataWrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				onServerError(Operation.DELETE_ITEM, dataWrapper.getResperr());
			}
			onSuccess(Operation.DELETE_ITEM);
		} catch (Exception e) {
			e.printStackTrace();
			onNetError(Operation.DELETE_ITEM);
		}
	}
	
	private void onSuccess(Operation operation) {
		if (callback != null && callback.get() != null) {
			callback.get().onSuccess(operation);
		}
		if (operation != Operation.GET_LIST) {
			// 商品信息变更以后需要通知预览界面进行页面更新
			ShopFragmentsWrapper.PREVIEW.refresh();
		}
	}
	
	private void onNetError(Operation operation) {
		if (callback != null && callback.get() != null) {
			callback.get().onNetError(operation);
		}
	}
	
	private void onServerError(Operation operation, String msg) {
		if (callback != null && callback.get() != null) {
			callback.get().onServerError(operation, msg);
		}
	}
	
	private void refresh(Operation operation) {
		if (callback != null && callback.get() != null) {
			callback.get().refresh(operation);
		}
	}
	
	/**
	 * 只在商品数据管理模块使用的一个回调,普遍情况下只有列表引用这个callback
	 * 以后的拓展可能会使用类似于把所有的回调放入一个数据结构,如果有消息发出,则遍历这个数据结构并且回调相应的方法
	 */
	public static interface CommodityCallback {
		void onSuccess(Operation operation);
		
		void onNetError(Operation operation);
		
		void onServerError(Operation operation, String msg);
		
		void refresh(Operation operation);
	}
	
	/**
	 * 表示操作的一个枚举
	 */
	public static enum Operation {
		GET_LIST, DELETE_ITEM, CHANGE_PROMOTION, TAKE_TOP
	}
}
