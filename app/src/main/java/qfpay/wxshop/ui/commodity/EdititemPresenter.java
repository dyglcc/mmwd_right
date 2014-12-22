package qfpay.wxshop.ui.commodity;

import java.util.List;

import qfpay.wxshop.data.beans.GoodWrapper;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.view.NewitemUnitItem;

public interface EdititemPresenter {
	public void afterInit(int states);
	public void onDestroy();
	public void onStart();
	
	public void onImgClick(int position);
	
	public void addUnitItem();
	public void deleteUnit(NewitemUnitItem item);
	
	public void addImgItem(ImageProcesserBean wrapper);
	public void addImgItem(List<ImageProcesserBean> dataList);
	public void deleteAllFailImgItem();
	public void addDeleteImgItem(ImageProcesserBean wrapper);
	public int getImgItemCount();
	public ImageProcesserBean getImgItem(int position);
	
	public void getItem(int id);
	public void saveItem();
	public void onGetItemSuccess(GoodWrapper wrapper);
	public void onSaveItemSucess(GoodWrapper wrapper);
	public boolean isDataChange();
	
	public void startLoading();
	public void dismissLoading();
	public void showProgressDialog();
	public void updataProgress(int max, int current);
	public void dismissProgressDialog();
}
