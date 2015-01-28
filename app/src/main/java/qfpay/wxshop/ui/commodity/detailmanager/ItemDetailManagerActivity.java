package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;
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
    @ViewById TextView       tv_description, tv_title, tv_save;
    @ViewById LinearLayout   ll_skus;

    @Extra    int            id;
    @Extra    boolean        isPromotation;

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

    @Override
    public void setTitle(String string) {
        tv_title.setText(string);
    }

    @Override public void addPicture(PictureViewModel pictureViewModel, boolean isRefresh) {
        mAdapter.addData(pictureViewModel);
        if (isRefresh) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override public void detelePicture(PictureViewModel pictureViewModel) {
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

    @Click void ll_add_sku() {
        if (isPromotation) {
            showErrorMessage("正在进行秒杀活动的商品无法添加规格");
            return;
        }
        ItemDetailSkuEditActivity_.intent(this).startForResult(ItemDetailManagerView.REQUEST_SKU_ADD);
    }

    /**
     * 页面中SkuItem的编辑按钮回调方法
     */
    public void onSkuEditClick(int position, SkuViewModel skuViewModel) {
        ItemDetailSkuEditActivity_.intent(this).skuViewModel(skuViewModel).position(position).startForResult(ItemDetailManagerView.REQUEST_SKU_EDIT);
    }

    @Override public void addSku(SkuViewModel skuViewModel) {
        SkuItem item = SkuItem_.build(this).setData(skuViewModel).setParentView(this);
        ll_skus.addView(item, 0);
    }

    @Override public void setSku(int position, SkuViewModel skuViewModel) {
        if (position < 0) return;
        ((SkuItem) ll_skus.getChildAt(position)).setData(skuViewModel);
    }

    @Override public void deleteSku(int position) {
        ll_skus.removeViewAt(position);
    }

    @OnActivityResult(REQUEST_SKU_ADD) void onSkuAdded(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        mPresenter.addSku((SkuViewModel) data.getSerializableExtra("SkuViewModel"));
    }

    @OnActivityResult(REQUEST_SKU_EDIT) void onSkuEdited(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data.hasExtra("SkuViewModel")) {
            mPresenter.setSku(data.getIntExtra("position", -1), (SkuViewModel) data.getSerializableExtra("SkuViewModel"));
        } else {
            mPresenter.deleteSku(data.getIntExtra("position", -1));
        }
    }

    @Override public void setName(String name) {
        et_name.setText(name);
    }

    @Override public void setPostage(String postage) {
        et_postage.setText(postage);
    }

    @Override public void setDescription(String description) {
        tv_description.setText(description);
    }

    @Override public void disableCommit() {
        tv_save.setClickable(false);
        tv_save.setTextColor(getResources().getColor(R.color.common_text_grey));
    }

    @Override public void enableCommit() {
        tv_save.setClickable(true);
        tv_save.setTextColor(getResources().getColor(R.color.common_white));
    }

    @Override @UiThread public void showErrorMessage(String message) {
        Toaster.s(this, message);
    }

    @Click void ll_description() {
        ItemDetailDescriptionEditActivity_.intent(this).description(tv_description.getText().toString()).startForResult(ItemDetailManagerView.REQUEST_DESC);
    }

    @OnActivityResult(REQUEST_DESC) void onDescriptionEdited(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        tv_description.setText(data.getStringExtra("description"));
    }

    @Click(R.id.iv_close) void onCloseClick() {
        onBackPressed();
    }

    @Click(R.id.tv_save) void onSaveClick() {
        mPresenter.commit(mAdapter.getData(), et_name.getText().toString(), et_postage.getText().toString(), tv_description.getText().toString());
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
