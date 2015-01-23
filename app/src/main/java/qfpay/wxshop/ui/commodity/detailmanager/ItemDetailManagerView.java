package qfpay.wxshop.ui.commodity.detailmanager;

import java.util.List;

import qfpay.wxshop.app.MMView;
import qfpay.wxshop.data.model.PictureModel;
import qfpay.wxshop.data.model.SKUModel;

/**
 * 商品详情管理的View层
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface ItemDetailManagerView extends MMView {
    public static final int REQUEST_PIC = 0;
    public static final int REQUEST_DESC = 1;
    public static final int REQUEST_SKU = 2;

    public void addSku(SkuViewModel skuViewModel);

    public void addPicture(PictureViewModel pictureViewModel, boolean isRefresh);

    public void detelePicture(PictureViewModel pictureViewModel);

    public void setName(String name);

    public void setPostage(String postage);

    public void setDescription(String description);

    public void disableCommit();

    public void enableCommit();

    public void showErrorMessage(String message);
}
