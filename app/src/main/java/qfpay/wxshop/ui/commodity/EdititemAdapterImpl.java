package qfpay.wxshop.ui.commodity;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.buyersshow.BuyersShowReleaseActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity;
import qfpay.wxshop.ui.customergallery.*;
import qfpay.wxshop.ui.view.DeleteImgProcesser;
import qfpay.wxshop.ui.view.ImgGridItem;
import qfpay.wxshop.ui.view.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@EBean
public class EdititemAdapterImpl extends BaseAdapter implements EdititemAdapter, DeleteImgProcesser {
	public static final int COLUMN_COUNT = 4;
	public static final int SPACING_DP = 10;
	public static final int PADDING_DP = 15;
	
	public static final int MAX_IMG_COUNT = 9;
	
	@RootContext EditItemActivity activity;
	EdititemPresenter presenter;
	
	List<ImageProcesserBean> imgList;
	
	@AfterInject
	void init() {
		imgList = new ArrayList<ImageProcesserBean>();
	}
	
	public EdititemAdapter setPresenter(EdititemPresenter presenter) {
		this.presenter = presenter;
		return this;
	}
	
	public void onItemClick(int position) {
		if (imgList.get(position).isDefault()) {
			CustomerGalleryActivity_.intent(activity).maxCount(EditItemActivity.COUNT_MAX_IMG).
				choicedCount(BuyersShowReleaseActivity.COUNT_MAX_IMG - getImgCountSurplus()).
				startForResult(CustomerGalleryActivity.REQUEST_CODE);
		}
	}
	
	public int getImgCountSurplus() {
		ImageProcesserBean ib = new ImageProcesserBean();
		ib.setDefault(true);
		if (!imgList.contains(ib)) {
			return 0;
		}
		int count = EditItemActivity.COUNT_MAX_IMG - imgList.size() + 1;
		if (count == 0) {
			return 1;
		}
		return count;
	}
	
	@Override
	public boolean addData(ImageProcesserBean wrapper, boolean isNotify) {
		ImageProcesserBean iw = new ImageProcesserBean();
		iw.setDefault(true);
		imgList.remove(iw);
		if (imgList.size() == MAX_IMG_COUNT) {
			return false;
		}
		if (!iw.equals(wrapper)) {
			imgList.add(wrapper);
		}
		if (imgList.size() != MAX_IMG_COUNT) {
			imgList.add(iw);
		}
		return true;
	}
	
	@Override
	public boolean addData(List<ImageProcesserBean> wrappers) {
		for (ImageProcesserBean imageWrapper : wrappers) {
			addData(imageWrapper, false);
		}
		notifyDataSetChanged();
		return true;
	}
	
	@Override
	public boolean deleteData(List<ImageProcesserBean> wrappers) {
		if (wrappers == null) {
			return false;
		}
		for (ImageProcesserBean imageWrapper : wrappers) {
			deleteData(imageWrapper, false);
		}
		notifyDataSetChanged();
		return false;
	}
	
	@Override
	public boolean deleteData(ImageProcesserBean wrapper, boolean isNotify) {
		if (wrapper == null) {
			return false;
		}
		if (imgList.size() == MAX_IMG_COUNT) {
			ImageProcesserBean iw =  new ImageProcesserBean();
			iw.setDefault(true);
			if (!imgList.contains(iw)) {
				imgList.add(iw);
			}
		}
		if (!imgList.remove(wrapper)) {
			return false;
		}
		if (isNotify) {
			notifyDataSetChanged();
		}
		presenter.addDeleteImgItem(wrapper);
		return true;
	}
	
	@Override
	public List<ImageProcesserBean> getImgData() {
		return imgList;
	}

	@Override
	public int getCount() {
		return imgList.size();
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImgGridItem item = (ImgGridItem) convertView;
		if (item == null) {
			item = ImgGridItem_.build(activity);
		}
		item.setData((ImageProcesserBean)getItem(position), this);
		return item;
	}
}
