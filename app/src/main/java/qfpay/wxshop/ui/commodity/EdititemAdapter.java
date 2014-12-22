package qfpay.wxshop.ui.commodity;

import java.util.List;

import qfpay.wxshop.image.ImageProcesserBean;

public interface EdititemAdapter {
	public EdititemAdapter setPresenter(EdititemPresenter presenter);
	public void onItemClick(int position);
	public boolean addData(ImageProcesserBean wrapper, boolean isNotify);
	public boolean addData(List<ImageProcesserBean> wrappers);
	public List<ImageProcesserBean> getImgData();
	public boolean deleteData(List<ImageProcesserBean> wrappers);
}
