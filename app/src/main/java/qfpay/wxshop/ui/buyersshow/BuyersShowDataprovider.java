package qfpay.wxshop.ui.buyersshow;

import java.util.List;

import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.image.ImageProcesserBean;

public interface BuyersShowDataprovider {
	public BuyerShowBean getData();
	public List<String> getDelImgids();
	public List<ImageProcesserBean> getImgs();
	public boolean isSharedWB();
	public boolean isSharedTWB();
	public boolean isSharedQzone();
}
