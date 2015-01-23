package qfpay.wxshop.ui.commodity;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.GoodWrapper;
import qfpay.wxshop.data.beans.UnitBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.EdititemNetImpl;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.processer.ImageType;
import qfpay.wxshop.ui.main.fragment.ShopFragmentsWrapper;
import qfpay.wxshop.ui.view.NewitemUnitItem;
import qfpay.wxshop.ui.view.NewitemUnitItem_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import android.view.View;
import android.widget.BaseAdapter;

@EBean
public class EdititemPresenterImpl implements EdititemPresenter {
	@RootContext EditItemActivity rootActivity;
	@Bean(EdititemAdapterImpl.class) EdititemAdapter imgAdapter;
	@Bean EdititemNetImpl netImpl;
	@Bean QFImageUploader mImageUploadHelper;
	@Bean CommodityDataController commodityController;
	EditItemIView iView;
	
	List<NewitemUnitItem> unitItemList = new ArrayList<NewitemUnitItem>();
	List<ImageProcesserBean> deleteImgList = new ArrayList<ImageProcesserBean>();
	private GoodWrapper original;
	int states;

	@Override
	public void afterInit(int states) {
		iView = rootActivity;
		this.states = states;
		imgAdapter.setPresenter(this);
		netImpl.init(this, rootActivity, states);
		iView.setPhotoAdapter((BaseAdapter) imgAdapter);
		setDeflautViews();
	}

	@Override
	public void onDestroy() {
		BackgroundExecutor.cancelAll(ConstValue.THREAD_GROUP_EDITITEM, true);
		mImageUploadHelper.cancelAll();
	}

	@Override
	public void onStart() {
		
	}
	
	@Override
	public void onImgClick(int position) {
		imgAdapter.onItemClick(position);
	}
	
	@Override
	public void addUnitItem() {
		addUnitItemWithBean(null);
	}
	
	@Override
	public void deleteUnit(NewitemUnitItem item) {
		if (unitItemList.size() == 1 && !item.isOnlyStock()) {
			item.setDeleteVisibility(false);
			item.changeStates(NewitemUnitItem.STATES_ONLY_STOCK);
			return;
		}
		unitItemList.remove(item);
		iView.removeUnitView(item);
		processUnitBottomLine();
	}
	
	public void addUnitItemWithBean(UnitBean unit) {
		if (unitItemList.size() >= 10) {
			Toaster.s(rootActivity, "亲，十种规格不能再多啦！");
			return;
		}
		
		NewitemUnitItem item = null;
		int viewCount = iView.getUnitviewCount();
		if (viewCount == 0) {
			item = NewitemUnitItem_.build(rootActivity).setStates(NewitemUnitItem.STATES_ONLY_STOCK, false, this);
			item.setDeleteVisibility(false);
		}
		
		if (viewCount == 1 && iView.getUninview(0).changeStates(NewitemUnitItem.STATES_DEFAULT)) {
			item = iView.getUninview(0).setStates(NewitemUnitItem.STATES_DEFAULT, false, this);
			item.setDeleteVisibility(true);
			if (unit != null) {
				if (unit.getSize() == null || unit.getSize().equals("")) {
					item.setStates(NewitemUnitItem.STATES_ONLY_STOCK, false, this);
				} else {
					item.setUnitName(unit.getSize());
				}
				item.setStockCount(unit.getAmount() + "");
				item.setUnitId(unit.getId());
			}
			return;
		}
		
		if (item == null) {
			item = NewitemUnitItem_.build(rootActivity).setStates(NewitemUnitItem.STATES_DEFAULT, false, this);
			item.setDeleteVisibility(true);
		}
		
		if (unit != null) {
			item.setUnitName(unit.getSize());
			item.setStockCount(unit.getAmount() + "");
			item.setUnitId(unit.getId());
		}
		unitItemList.add(item);
		iView.addUnitView(item);
		processUnitBottomLine();
	}
	
	public void addUnitItemList(List<UnitBean> units) {
		if (units == null || units.size() == 0) {
			return;
		}
		if (unitItemList.size() > 1) {
			deleteAllUnit();
		}
		for (UnitBean unitBean : units) {
			addUnitItemWithBean(unitBean);
		}
	}
	
	List<UnitBean> getUnitBeanList() {
		List<UnitBean> beans = new ArrayList<UnitBean>();
		for (NewitemUnitItem item : unitItemList) {
			if (item.getUnitName() == null && item.getUnitName().equals("")) {
				continue;
			}
			UnitBean unitBean = new UnitBean();
			unitBean.setAmount(item.getStockCount());
			unitBean.setId(item.getUnitId());
			if (item.getStates() != NewitemUnitItem.STATES_ONLY_STOCK) {
				unitBean.setSize(item.getUnitName());
			}
			beans.add(unitBean);
		}
		return beans;
	}
	
	public void deleteAllUnit() {
		for (NewitemUnitItem item : unitItemList) {
			deleteUnit(item);
		}
	}
	
	boolean checkUnitName() {
		boolean isNameRight = true;
		for (NewitemUnitItem unit : unitItemList) {
			if ((unit.getUnitName() == null || unit.getUnitName().equals("")) && unit.getStates() == NewitemUnitItem.STATES_DEFAULT) {
				isNameRight = false;
			}
		}
		return isNameRight;
	}
	
	boolean checkUnitAmount() {
		for (NewitemUnitItem unit : unitItemList) {
			if (unit.getStockCount() <= 0) {
				return false;
			}
		}
		return true;
	}
	
	public void processUnitBottomLine() {
		int size = unitItemList.size();
		for (int i = 0; i < size; i++) {
			NewitemUnitItem item = unitItemList.get(i);
			if (i == (size - 1)) {
				item.setBottomVisibility(View.GONE);
			} else {
				item.setBottomVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void addImgItem(ImageProcesserBean wrapper) {
		imgAdapter.addData(wrapper, true);
		mImageUploadHelper
			.setGroupLinstener(netImpl)
			.with(wrapper.getId())
			.path(wrapper.getPath())
			.urlSetter(wrapper)
			.uploadInGroup();
	}
	
	@Override
	public void addImgItem(List<ImageProcesserBean> dataList) {
		imgAdapter.addData(dataList);
		for (ImageProcesserBean imageProcesserBean : dataList) {
			boolean isFirst = imgAdapter.getImgData().indexOf(imageProcesserBean) == 0;
			if (isFirst) {
				mImageUploadHelper
					.setGroupLinstener(netImpl)
					.with(imageProcesserBean.getId())
					.path(imageProcesserBean.getPath())
					.urlSetter(imageProcesserBean)
					.imageType(ImageType.BIG)
					.uploadInGroup();
			} else {
				mImageUploadHelper
					.setGroupLinstener(netImpl)
					.with(imageProcesserBean.getId())
					.path(imageProcesserBean.getPath())
					.urlSetter(imageProcesserBean)
					.uploadInGroup();
			}
		}
	}
	
	@Override
	public void deleteAllFailImgItem() {
		List<ImageProcesserBean> deletelistBeans = new ArrayList<ImageProcesserBean>();
		for (ImageProcesserBean img : imgAdapter.getImgData()) {
			if (img.isUploaderError()) {
				deletelistBeans.add(img);
			}
		}
		imgAdapter.deleteData(deletelistBeans);
	}

	@Override
	public void addDeleteImgItem(ImageProcesserBean wrapper) {
		if (wrapper.isOnlyNetImage()) {
			deleteImgList.add(wrapper);
		}
	}
	
	@Override
	public ImageProcesserBean getImgItem(int position) {
		return imgAdapter.getImgData().get(position);
	}
	
	@Override
	public int getImgItemCount() {
		return imgAdapter.getImgData().size();
	}
	
	public void setDeflautViews() {
		ImageProcesserBean wrapper = new ImageProcesserBean();
		wrapper.setDefault(true);
		addImgItem(wrapper);
		addUnitItem();
		iView.setRequestButtonText(rootActivity.getResources().getString(R.string.btn_edit));
		if (states == EditItemActivity.STATES_EDIT) {
			iView.changeViewStates(false);
			getItem(iView.getCommodityModel().getID());
		} else {
			iView.changeViewStates(true);
		}
	}
	
	@Override
	public void getItem(int id) {
		netImpl.getGoodWrapperFromServer(id);
	}
	
	@Override
	public void saveItem() {
		if (!checkUnitName()) {
			Toaster.s(rootActivity, "亲，还没有填规格呦~懒得填？点减号关掉它！");
			return;
		}
		if (!checkUnitAmount()) {
			Toaster.s(rootActivity, "亲，库存要写啊，没有库存别人怎么买呢!");
			return;
		}
		GoodWrapper wrapper = new GoodWrapper();
		wrapper.setDescription(iView.getDescription());
		wrapper.setName(iView.getName());
		wrapper.setPostAge(iView.getPostage());
		try {
			wrapper.setPrice(Float.parseFloat(iView.getPrice()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wrapper.setUnitList(getUnitBeanList());
		wrapper.setImgWrapper(imgAdapter.getImgData());
		wrapper.setDeleteImgFromServer(deleteImgList);
		if (iView.getCommodityModel() == null) {
			wrapper.setId("-1");
		} else {
			wrapper.setId(iView.getCommodityModel().getID() + "");
		}
		MobAgentTools.OnEventMobOnDiffUser(rootActivity, "start_upload_pic");
		
		netImpl.saveItem(wrapper);
		mImageUploadHelper.ready();
	}
	
	@Override @UiThread
	public void onGetItemSuccess(GoodWrapper wrapper) {
		this.original = wrapper;
		iView.setName(wrapper.getName());
		iView.setDescription(wrapper.getDescription());
		iView.setPostage(wrapper.getPostAge());
		iView.setPrice(wrapper.getPrice() + "");
		addUnitItemList(wrapper.getUnitBeans());
		addImgItem(wrapper.getImgWrappers());
	}
	
	@Override @UiThread
	public void onSaveItemSucess(GoodWrapper wrapper) {
		// 信息变更以后需要通知预览界面进行相应的刷新
		ShopFragmentsWrapper.PREVIEW.refresh();
		if (states == EditItemActivity.STATES_NEW) {
			EdititemDoneActivity_.intent(rootActivity).wrapper(wrapper).start();
			commodityController.reloadCurrentData();
		} else {
			commodityController.updateCommodity(wrapper);
			Toaster.s(rootActivity, rootActivity.getResources().getString(R.string.edit_toast_editedsuccess));
		}
		rootActivity.finish();
	}

	@Override public boolean isDataChange() {
		if (original == null) return false;

		GoodWrapper wrapper = new GoodWrapper();
		wrapper.setDescription(iView.getDescription());
		wrapper.setName(iView.getName());
		wrapper.setPostAge(iView.getPostage());
		try {
			wrapper.setPrice(Float.parseFloat(iView.getPrice()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wrapper.setUnitList(getUnitBeanList());
		wrapper.setImgWrapper(imgAdapter.getImgData());
		if (iView.getCommodityModel() == null) {
			wrapper.setId("-1");
		} else {
			wrapper.setId(iView.getCommodityModel().getID() + "");
		}

		if (!original.getName().equals(wrapper.getName())) return true;
		if (!original.getDescription().equals(wrapper.getDescription())) return true;
		if (!original.getPostAge().equals(wrapper.getPostAge())) return true;
		if (original.getPrice() != wrapper.getPrice()) return true;
		if (wrapper.getDeleteImg() != null && !wrapper.getDeleteImg().isEmpty()) return true;
		for (ImageProcesserBean img : wrapper.getImgWrappers())
			if (img.getPath() != null && !img.getPath().equals("")) return true;
		return false;
	}

	@Override @UiThread
	public void startLoading() {
		iView.startLoading();
	}
	
	@Override @UiThread
	public void dismissLoading() {
		iView.dismissLoading();
	}
	
	@Override @UiThread
	public void showProgressDialog() {
		iView.showProgressDialog();
	}

	@Override @UiThread
	public void updataProgress(int max, int current) {
		iView.updataProgress(max, current);
	}

	@Override @UiThread
	public void dismissProgressDialog() {
		rootActivity.dismissProgressDialog();
	}
}
