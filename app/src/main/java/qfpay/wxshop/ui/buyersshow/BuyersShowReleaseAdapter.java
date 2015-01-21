package qfpay.wxshop.ui.buyersshow;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.ImgDeleteWrapper;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.customergallery.*;
import qfpay.wxshop.ui.view.DeleteImgProcesser;
import qfpay.wxshop.ui.view.ImgGridItem;
import qfpay.wxshop.ui.view.*;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@EBean
public class BuyersShowReleaseAdapter extends BaseAdapter implements DeleteImgProcesser {
	@RootContext BuyersShowReleaseActivity activity;
	
	private List<ImageProcesserBean> mImgs = new ArrayList<ImageProcesserBean>();
	private List<ImgDeleteWrapper> delList = new ArrayList<ImgDeleteWrapper>();

	public void onItemClick(int position) {
		if (mImgs.get(position).isDefault()) {
			CustomerGalleryActivity_.intent(activity).maxCount(BuyersShowReleaseActivity.COUNT_MAX_IMG)
				.choicedCount(BuyersShowReleaseActivity.COUNT_MAX_IMG - getImgCountSurplus()).startForResult(CustomerGalleryActivity.REQUEST_CODE);
		}
	}
	
	public void onTakedPic(Intent data) {
		@SuppressWarnings("unchecked")
		ArrayList<ImageProcesserBean> imgs = (ArrayList<ImageProcesserBean>) data.getSerializableExtra(CustomerGalleryActivity.RESULT_DATA_NAME);
		addImgData(imgs);
	}
	
	public boolean addData(ImageProcesserBean img, boolean isNotify) {
		if (mImgs.size() == BuyersShowReleaseActivity.COUNT_MAX_IMG) {
			ImageProcesserBean iw = new ImageProcesserBean();
			iw.setDefault(true);
			if (mImgs.contains(iw)) {
				mImgs.remove(iw);
				return addData(img, isNotify);
			}
			return false;
		}
		if (mImgs.size() == 0) {
			mImgs.add(img);
		} else {
			mImgs.add(mImgs.size() - 1, img);
		}
		if (isNotify) {
			notifyDataSetChanged();
		}
		return true;
	}
	
	public boolean addImgData(List<ImageProcesserBean> mImgs) {
		for (ImageProcesserBean ipb : mImgs) {
			addData(ipb, false);
		}
		notifyDataSetChanged();
		return true;
	}
	
	public boolean addNetData(List<ImageBean> mImgs) {
		for (ImageBean img : mImgs) {
			ImageProcesserBean ipb = new ImageProcesserBean();
			boolean isIdNull = img.getId() == null || "".equals(img.getId());
			boolean isUrlNull = img.getUrl() == null || "".equals(img.getUrl());
			if (isIdNull && isUrlNull) {
				continue;
			}
			if (isIdNull) {
				ipb.setId(0);
			} else {
				ipb.setId(Integer.parseInt(img.getId(), 10));
			}
			ipb.setUrl(img.getUrl());
			ipb.setFromNative(false);
			addData(ipb, false);
		}
		notifyDataSetChanged();
		return true;
	}
	
	@Override
	public boolean deleteData(ImageProcesserBean bean, boolean isNotify) {
		if (bean == null) {
			return false;
		}
		if (mImgs.size() == BuyersShowReleaseActivity.COUNT_MAX_IMG) {
			ImageProcesserBean iw =  new ImageProcesserBean();
			iw.setDefault(true);
			if (!mImgs.contains(iw)) {
				mImgs.add(iw);
			}
		}
		if (!mImgs.remove(bean)) {
			return false;
		}
		if (isNotify) {
			notifyDataSetChanged();
		}
		addDelImg(bean.getId());
		return true;
	}
	
	public void addDelImg(int id) {
		ImgDeleteWrapper idw = new ImgDeleteWrapper();
		idw.setId(id + "");
		delList.add(idw);
	}
	
	public List<String> getDelImgs() {
		List<String> delArr = new ArrayList<String>();
 		for (int i = 0; i < delList.size(); i ++) {
			delArr.add(delList.get(i).getId());
		}
 		return delArr;
	}
	
	public int getImgCountSurplus() {
		ImageProcesserBean ib = new ImageProcesserBean();
		ib.setDefault(true);
		if (!mImgs.contains(ib)) {
			return 0;
		}
		int count = BuyersShowReleaseActivity.COUNT_MAX_IMG - mImgs.size() + 1;
		if (count == 0) {
			return 1;
		}
		return count;
	}
	
	public List<ImageProcesserBean> getImgs() {
		return mImgs;
	}
	
	@Override
	public int getCount() {
		return mImgs.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImgGridItem view = (ImgGridItem) convertView;
		if (view == null) {
			view = ImgGridItem_.build(activity);
		}
		view.setData(mImgs.get(position), this);
		return view;
	}
}
