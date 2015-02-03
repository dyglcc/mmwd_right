package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Context;
import android.view.View;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
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
import qfpay.wxshop.ui.commodity.CommodityDataController;
import qfpay.wxshop.ui.commodity.EdititemDoneActivity_;
import qfpay.wxshop.utils.Utils;

/**
 * ItemDetail的逻辑层
 * Presenter必须与View保持同样的生命周期
 *
 * Created by LiFZhe on 1/19/15.
 */
@EBean
public class ItemDetailPresenterImpl extends BasePresenter implements ItemDetailPresenter, ImageGroupUploadLinstener {
    private Context               mContext;
    private ItemDetailManagerView mView;
    private CommodityModel        mModel;
    private int                   commodityId = -1;
    private boolean               isSkuEdited = false;

    @Inject CommodityRepository   mRepository;
    @Bean   QFImageUploader       mUploader;
    @Bean   CommodityDataController mCommodityListController;

    public ItemDetailPresenterImpl(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override public void setView(ItemDetailManagerView view) {
        this.mView = view;
    }

    @Override public void onViewCreate() {
        if (commodityId > 0) {
            requestCommodityModel();
            mView.setTitle("编辑商品");
        } else {
            mModel = new CommodityModel();
            mView.setTitle("新添加商品");
        }
    }

    @Override public void onViewResume() {

    }

    @Override public void onViewDestroy() {

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

    @Override public void addSku(SkuViewModel skuViewModel) {
        SKUModel model = new SKUModel();
        model.setName(skuViewModel.getName());
        model.setPrice(Float.parseFloat(skuViewModel.getPrice()));
        model.setAmount(Integer.parseInt(skuViewModel.getAmount(), 10));
        mModel.getSkuList().add(0, model);
        mView.addSku(skuViewModel, 0);
        isSkuEdited = true;
    }

    @Override public void setSku(int position, SkuViewModel skuViewModel) {
        if (position < 0) return;
        SKUModel skuModel = mModel.getSkuList().get(position);
        skuModel.setName(skuViewModel.getName());
        skuModel.setPrice(Float.parseFloat(skuViewModel.getPrice()));
        skuModel.setAmount(Integer.parseInt(skuViewModel.getAmount(), 10));
        mView.setSku(position, skuViewModel);
        isSkuEdited = true;
    }

    @Override public void deleteSku(int position) {
        mModel.getSkuList().remove(mModel.getSkuList().get(position));
        mView.deleteSku(position);
        isSkuEdited = true;
    }

    @Override public List<SKUModel> getSkuModelList() {
        return mModel.getSkuList();
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

    @Override public void commit(List<PictureViewModel> pictureViewModelList, String name, String postage, String description) {
        if (! checkCommoitData(pictureViewModelList, mModel.getSkuList(), name, postage, description)) {
            return;
        }

        mModel.getPictureList().clear();
        for (PictureViewModel pic : pictureViewModelList) {
            if (pic.isNative()) continue;
            PictureModel model = new PictureModel();
            model.setPath(pic.getPath());
            model.setUrl(pic.getUrl());
            model.setId(pic.getId());
            mModel.getPictureList().add(model);
        }
        mModel.setName(name);
        mModel.setPostage(Float.parseFloat(postage));
        mModel.setDescription(description);

        if (commodityId > 0) {
            editServerModel(mModel);
        } else {
            newServerModel(mModel);
        }
    }

    @Override
    public void onClose(List<PictureViewModel> pictureViewModelList, String name, String postage, String description) {
        if (isSkuEdited) {
            showCloseDialog();
            return;
        }
        List<PictureModel> list = new ArrayList<PictureModel>();
        for (PictureViewModel pic : pictureViewModelList) {
            if (pic.isNative()) continue;
            PictureModel model = new PictureModel();
            model.setPath(pic.getPath());
            model.setUrl(pic.getUrl());
            model.setId(pic.getId());
            list.add(model);
        }
        if (!list.equals(mModel.getPictureList())){
            showCloseDialog();
            return;
        }
        if (!name.equals(mModel.getName())) {
            showCloseDialog();
            return;
        }
        if (Float.parseFloat(postage) != mModel.getPostage()) {
            showCloseDialog();
            return;
        }
        if (!description.equals(mModel.getDescription())) {
            showCloseDialog();
            return;
        }
        mView.finish();
    }

    private void showCloseDialog() {
        Utils.showNativeDialog((android.support.v4.app.FragmentActivity) mContext, "提示", "编辑信息有变化,要放弃吗?", "确认放弃", "继续编辑", true, 1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public boolean checkCommoitData(List<PictureViewModel> pictureViewModelList, List<SKUModel> skuModels, String name, String postage, String description) {
        if (isNull(name)) {
            mView.showErrorMessage("请输入名称");
            return false;
        } else if (isNull(postage)) {
            mView.showErrorMessage("请输入邮费");
            return false;
        } else if (isNull(description)) {
            mView.showErrorMessage("请输入描述");
            return false;
        } else if (pictureViewModelList == null || pictureViewModelList.isEmpty()) {
            mView.showErrorMessage("请选择至少一张图片");
            return false;
        } else if (mModel.getSkuList() == null || mModel.getSkuList().isEmpty()){
            mView.showErrorMessage("请选择添加至少一个规格");
            return false;
        } else if (! checkSku(skuModels) && skuModels.size() != 1) {
            mView.showErrorMessage("您有规格的名称为空, 请补全规格名称");
            return false;
        }
        return true;
    }

    public boolean checkSku(List<SKUModel> skuModels) {
        for (SKUModel sku : skuModels) {
            if (sku.getName() == null || sku.getName().equals("")) {
                return false;
            }
        }
        return true;
    }

    public boolean isNull(String string) {
        return string == null || string.equals("");
    }

    @Background void editServerModel(CommodityModel model) {
        try {
            mRepository.updateCommodity(mModel);
            onModelRequestDone(mModel.getId());
        } catch (MessageException e) {
            mView.showErrorMessage(e.getMsgForToast());
        }
    }

    @Background void newServerModel(CommodityModel model) {
        try {
            onModelRequestDone(mRepository.createCommodity(mModel));
        } catch (MessageException e) {
            mView.showErrorMessage(e.getMsgForToast());
        }
    }

    @UiThread void onModelRequestDone(int id) {
        mCommodityListController.reloadData();
        mModel.setId(id);
        EdititemDoneActivity_.intent(mContext).wrapper(mModel).start();
        mView.finish();
    }

    @Override public void onUploadProgress(float progress) {
        mView.disableCommit();
    }

    @Override public void onComplete(int successCount, int failureCount) {
        // 在Group检测中才生效, 此处无作用
    }

    @Override public void onImageReady() {
        mView.enableCommit();
    }
}
