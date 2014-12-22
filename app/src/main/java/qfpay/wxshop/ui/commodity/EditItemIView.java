package qfpay.wxshop.ui.commodity;

import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.ui.view.NewitemUnitItem;
import android.widget.BaseAdapter;

public interface EditItemIView {
	public CommodityModel getCommodityModel();
	public void setPhotoAdapter(BaseAdapter adapter);
	public void changeViewStates(boolean isNew);
	
	public void addUnitView(NewitemUnitItem item);
	public NewitemUnitItem getUninview(int position);
	public int getUnitviewCount();
	public void removeUnitView(NewitemUnitItem item);
	
	public void setRequestButtonText(String text);
	public void setName(String text);
	public String getName();
	public void setPrice(String text);
	public String getPrice();
	public void setPostage(String text);
	public String getPostage();
	public void setDescription(String text);
	public String getDescription();
	
	public void startLoading();
	public void dismissLoading();
	public void showProgressDialog();
	public void updataProgress(int max, int current);
	public void dismissProgressDialog();
}
