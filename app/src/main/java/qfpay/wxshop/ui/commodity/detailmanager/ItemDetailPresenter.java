package qfpay.wxshop.ui.commodity.detailmanager;

import java.util.List;

import qfpay.wxshop.app.MMPresenter;
import qfpay.wxshop.image.ImageProgressListener;

/**
 * 商品详情的逻辑层
 *
 * Created by LiFZhe on 1/19/15.
 */
public interface ItemDetailPresenter extends MMPresenter<ItemDetailManagerView> {

    public void setCommodityId(int id);

    public void addSku(SkuViewModel skuViewModel);

    /**
     * 如果position为-1则无效
     */
    public void setSku(int position, SkuViewModel skuViewModel);

    public void deleteSku(int position);

    public void uploadPicture(PictureViewModel viewModel, ImageProgressListener listener);

    public void cancelPictureUpload(PictureViewModel viewModel);

    public void setPictureListener(String path, ImageProgressListener listener);

    public void commit(List<PictureViewModel> pictureViewModelList, String name, String postage, String description);
}
