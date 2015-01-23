package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity_;
import qfpay.wxshop.utils.Toaster;

/**
 * 商品详情管理,包括发布商品和编辑商品
 *
 * Created by LiFZhe on 1/19/15.
 */
@EActivity(R.layout.itemdetail_layout)
public class ItemDetailManagerActivity extends BaseActivity implements ItemDetailManagerView {
    @Bean(ItemDetailPresenterImpl.class) ItemDetailPresenter mPresenter;
    private   PictureAdapter mAdapter;

    @ViewById GridView       gv_image;
    @ViewById EditText       et_name, et_postage;
    @ViewById LinearLayout   ll_skus;

    private   String         description;
    @Extra    int            id;

    @Override protected void onResume() {
        super.onResume();
        mPresenter.onViewResume();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDestroy();
    }

    @AfterViews void onInit() {
        mPresenter.setView(this);
        mPresenter.setCommodityId(id);
        mPresenter.onViewCreate();
        mAdapter = new PictureAdapter();
        gv_image.setAdapter(mAdapter);
    }

    @Click void ll_add_sku() {
        ItemDetailSkuEditActivity_.intent(this).startForResult(ItemDetailManagerView.REQUEST_SKU);
    }

    @Override public void addSku(SkuViewModel skuViewModel) {
        SkuItem item = SkuItem_.build(this).setData(skuViewModel).setParentView(this);
        ll_skus.addView(item);
    }

    @Override public void addPicture(PictureViewModel pictureViewModel, boolean isRefresh) {
        mAdapter.addData(pictureViewModel);
        if (isRefresh) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void detelePicture(PictureViewModel pictureViewModel) {
        mAdapter.removeView(pictureViewModel);
        mAdapter.notifyDataSetChanged();
        mPresenter.cancelPictureUpload(pictureViewModel);
    }

    @ItemClick void gv_image(PictureViewModel viewModel) {
        if (viewModel.isDefault()) {
            CustomerGalleryActivity_.intent(this).
                    maxCount(PictureAdapter.MAX_PIC_COUNT).
                    choicedCount(mAdapter.getPicCount()).
                    startForResult(ItemDetailManagerView.REQUEST_PIC);
        }
    }

    @OnActivityResult(ItemDetailManagerView.REQUEST_PIC) void onPicSelected(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<ImageProcesserBean> imgs = (ArrayList<ImageProcesserBean>) data.getSerializableExtra(CustomerGalleryActivity.RESULT_DATA_NAME);
            for (ImageProcesserBean bean : imgs) {
                mAdapter.addData(bean);
                if (imgs.indexOf(bean) == imgs.size() - 1) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void onSkuEditClick(SkuViewModel skuViewModel) {
        ItemDetailSkuEditActivity_.intent(this).skuViewModel(skuViewModel).startForResult(ItemDetailManagerView.REQUEST_SKU);
    }

    @OnActivityResult(REQUEST_SKU) void onSkuEdited(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        mPresenter.editSku((SkuViewModel) data.getSerializableExtra("SkuViewModel"));
    }

    @Override public void setName(String name) {
        et_name.setText(name);
    }

    @Override public void setPostage(String postage) {
        et_postage.setText(postage);
    }

    @Override public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void disableCommit() {

    }

    @Override
    public void enableCommit() {

    }

    @Override @UiThread public void showErrorMessage(String message) {
        Toaster.s(this, message);
    }

    @Click void ll_description() {
        ItemDetailDescriptionEditActivity_.intent(this).description(description).startForResult(ItemDetailManagerView.REQUEST_DESC);
    }

    @OnActivityResult(REQUEST_DESC) void onDescriptionEdited(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        description = data.getStringExtra("description");
    }

    @Click(R.id.iv_close) void onCloseClick() {
        onBackPressed();
    }

    @Click(R.id.tv_save) void onSaveClick() {
        mPresenter.commit(mAdapter.getData(), et_name.getText().toString(), et_postage.getText().toString(), description);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
    }

    class PictureAdapter extends BaseAdapter {
        static final int MAX_PIC_COUNT = 9;

        private List<PictureViewModel> mData = new ArrayList<PictureViewModel>();

        public void addData(PictureViewModel model) {
            mData.add(model);
        }

        public void addData(ImageProcesserBean bean) {
            PictureViewModel viewModel = new PictureViewModel();
            viewModel.setPath(bean.getPath());
            viewModel.setDefault(false);
            viewModel.setProgress(0);
            viewModel.setUploading(false);
            viewModel.setId(bean.getId());
            addData(viewModel);
        }

        public void removeView(PictureViewModel viewModel) {
            mData.remove(viewModel);
        }

        public int getPicCount() {
            int count = 0;
            for (PictureViewModel viewModel : mData) {
                if (!viewModel.isDefault()) {
                    count ++;
                }
            }
            return count;
        }

        public List<PictureViewModel> getData() {
            return mData;
        }

        @Override public int getCount() {
            if (mData.size() < 9) {
                return mData.size() + 1;
            } else {
                return mData.size();
            }
        }

        @Override public PictureViewModel getItem(int position) {
            if (position == mData.size()) {
                PictureViewModel viewModel = new PictureViewModel();
                viewModel.setDefault(true);
                return viewModel;
            }
            return mData.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            PictureItem item = (PictureItem) convertView;
            if (item == null) {
                item = PictureItem_.build(ItemDetailManagerActivity.this);
            }
            item.setData(getItem(position), ItemDetailManagerActivity.this);
            if (!getItem(position).isDefault() && getItem(position).isNeedUpload()) {
                mPresenter.uploadPicture(getItem(position), item);
            }
            return item;
        }
    }
}
