package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Context;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import javax.inject.Inject;

import qfpay.wxshop.app.BasePresenter;
import qfpay.wxshop.data.CommodityRepository;
import qfpay.wxshop.data.exception.MessageException;
import qfpay.wxshop.data.model.CommodityModel;
import qfpay.wxshop.data.model.PictureModel;
import qfpay.wxshop.data.model.SKUModel;
import qfpay.wxshop.image.ImageGroupUploadLinstener;
import qfpay.wxshop.image.ImageProgressListener;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.processer.ImageType;

/**
 * ItemDetail的逻辑层
 * Presenter必须与View保持同样的生命周期
 *
 * Created by LiFZhe on 1/19/15.
 */
@EBean
public class ItemDetailPresenterImpl extends BasePresenter implements ItemDetailPresenter, ImageGroupUploadLinstener {
    private ItemDetailManagerView mView;
    private CommodityModel        mModel;
    private int                   commodityId = 0;

    @Inject CommodityRepository   mRepository;
    @Bean   QFImageUploader       mUploader;

    public ItemDetailPresenterImpl(Context context) {
        super(context);

    }

    @Override
    public void onViewCreate() {
        if (commodityId > 0) {
            requestCommodityModel();
        }
    }

    @Override public void setCommodityId(int id) {
        this.commodityId = id;
    }

    @Background void requestCommodityModel() {
        try {
            onCommodityModelRequestDone(mRepository.getCommodityModel(commodityId));
        } catch (MessageException e) {
            mView.showErrorMessage(e.getMsgForToast());
        }
    }

    @UiThread void onCommodityModelRequestDone(CommodityModel model) {
        this.mModel = model;
        setViews();
    }

    private void setViews() {
        mView.setName(mModel.getName());
        mView.setDescription(mModel.getDescription());
        mView.setPostage(mModel.getPostage() + "");
        for (SKUModel model : mModel.getSkuList()) {
            SkuViewModel viewModel = new SkuViewModel();
            viewModel.setName(model.getName());
            viewModel.setPrice(model.getPrice() + "");
            viewModel.setAmount(model.getAmount() + "");
            viewModel.setId(model.getId());
            mView.addSku(viewModel);
        }
        for (PictureModel pic : mModel.getPictureList()) {
            PictureViewModel viewModel = new PictureViewModel();
            viewModel.setId(pic.getId());
            viewModel.setUploading(false);
            viewModel.setProgress(0);
            viewModel.setDefault(false);
            viewModel.setPath(pic.getPath());
            viewModel.setUrl(pic.getUrl());
            boolean isRefresh = mModel.getPictureList().indexOf(pic) == mModel.getPictureList().size() - 1;
            mView.addPicture(viewModel, isRefresh);
        }
    }

    @Override public void onViewResume() {

    }

    @Override public void onViewDestroy() {

    }

    @Override public void setView(ItemDetailManagerView view) {
        this.mView = view;
    }

    @Override public void editSku(SkuViewModel skuViewModel) throws NumberFormatException {
        boolean isHit = false;
        for (SKUModel model : mModel.getSkuList()) {
            if (skuViewModel.getId() == model.getId()) {
                model.setName(skuViewModel.getName());
                model.setPrice(Float.parseFloat(skuViewModel.getPrice()));
                model.setAmount(Integer.parseInt(skuViewModel.getAmount(), 10));
                isHit = true;
            }
        }
        if (isHit) {
            SKUModel model = new SKUModel();
            model.setName(skuViewModel.getName());
            model.setPrice(Float.parseFloat(skuViewModel.getPrice()));
            model.setAmount(Integer.parseInt(skuViewModel.getAmount(), 10));
            mModel.getSkuList().add(model);
        }
    }

    @Override public void deleteSku(SkuViewModel skuViewModel) {
        SKUModel skuModel = null;
        for (SKUModel model : mModel.getSkuList()) {
            if (model.getId() == skuViewModel.getId()) {
                skuModel = model;
            }
        }
        mModel.getSkuList().remove(skuModel);
    }

    @Override public void uploadPicture(PictureViewModel viewModel, ImageProgressListener listener) {
        mUploader.setGroupLinstener(this).
                with(viewModel.getId()).
                path(viewModel.getPath()).
                linstener(listener).
                imageType(ImageType.NORMAL).
                uploadInGroup();
    }

    @Override public void cancelPictureUpload(PictureViewModel viewModel) {
        if (viewModel.isUploading()) {
            mUploader.cancel(viewModel.getPath());
        }
    }

    @Override public void setPictureListener(String path, ImageProgressListener listener) {
        mUploader.setSingleTaskLinstener(path, listener);
    }

    @Override public void commit(List<PictureViewModel> pictureViewModelList, String name, String postage, String description) throws NumberFormatException {
        mModel.getPictureList().clear();
        for (PictureViewModel pic : pictureViewModelList) {
            if (!pic.isNative()) continue;
            PictureModel model = new PictureModel();
            model.setPath(pic.getPath());
            model.setUrl(pic.getUrl());
            model.setId(pic.getId());
            mModel.getPictureList().add(model);
        }
        mModel.setName(name);
        mModel.setPostage(Float.parseFloat(postage));
        mModel.setDescription(description);
    }

    @Override
    public void onUploadProgress(float progress) {
        mView.disableCommit();
    }

    @Override
    public void onComplete(int successCount, int failureCount) {
        // 在Group检测中才生效, 此处无作用
    }

    @Override
    public void onImageReady() {
        mView.enableCommit();
    }
}
