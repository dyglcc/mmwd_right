package qfpay.wxshop.ui.commodity.detailmanager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import qfpay.wxshop.R;
import qfpay.wxshop.image.ImageProgressListener;

/**
 * 图片条目
 *
 * Created by LiFZhe on 1/22/15.
 */
@EViewGroup(R.layout.itemdetail_picitem)
public class PictureItem extends RelativeLayout implements ImageProgressListener {
    @ViewById ImageView      iv_picture, iv_delete;
    @ViewById RelativeLayout rl_layer;
    @ViewById TextView       tv_msg;
    @ViewById ProgressBar    pb_loading;

    private PictureViewModel mPictureViewModel;
    private ItemDetailManagerView mView;

    public PictureItem(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public PictureItem setData(PictureViewModel pictureViewModel, ItemDetailManagerView view) {
        this.mPictureViewModel = pictureViewModel;
        this.mView = view;
        init();
        return this;
    }

    private void init() {
        loadImage();
        if (mPictureViewModel.getProgress() > 0 && mPictureViewModel.isUploading()) {
            setStatus(Status.PROGRESS);
            pb_loading.setProgress((int) (mPictureViewModel.getProgress() / 100));
        }
        if (mPictureViewModel.isSuccess()) {
            setStatus(Status.SUCCESS);
        }
        if (mPictureViewModel.isFail()) {
            setStatus(Status.FAILURE);
        }
    }

    private void loadImage() {
        Picasso.with(getContext()).cancelRequest(iv_picture);
        if (mPictureViewModel.isDefault()) {
            setStatus(Status.DEFAULT);
            Picasso.with(getContext()).load(R.drawable.newitem_img_add).into(iv_picture);
        } else {
            setStatus(Status.NORMAL);
            if (mPictureViewModel.hasNative()) {
                Picasso.with(getContext()).load(new File(mPictureViewModel.getPath())).centerCrop().into(iv_picture);
            } else {
                Picasso.with(getContext()).load(mPictureViewModel.getUrl()).centerCrop().into(iv_picture);
            }
        }
    }

    @Click void iv_delete() {
        mView.detelePicture(mPictureViewModel);
    }

    @Override @UiThread public void onProgress(String path, long total, long progress) {
        setStatus(Status.PROGRESS);
        if (path.equals(mPictureViewModel.getPath())) {
            pb_loading.setProgress((int) (progress / total));
        }
    }

    @Override @UiThread public void onFailure() {
        mPictureViewModel.complete(false);
        setStatus(Status.FAILURE);
    }

    @Override @UiThread public void onSuccess(String url) {
        mPictureViewModel.setUrl(url);
        mPictureViewModel.complete(true);
        setStatus(Status.SUCCESS);
    }

    private void setStatus(Status status) {
        if (mPictureViewModel.isDefault()) {
            status = Status.DEFAULT;
        }
        switch (status) {
            case NORMAL:
                rl_layer.setVisibility(View.GONE);
                tv_msg.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                iv_delete.setVisibility(View.VISIBLE);
                iv_picture.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                rl_layer.setVisibility(View.VISIBLE);
                tv_msg.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                iv_delete.setVisibility(View.VISIBLE);
                iv_picture.setVisibility(View.VISIBLE);
                tv_msg.setText("上传成功");
                break;
            case FAILURE:
                rl_layer.setVisibility(View.VISIBLE);
                tv_msg.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                iv_delete.setVisibility(View.VISIBLE);
                iv_picture.setVisibility(View.VISIBLE);
                tv_msg.setText("上传失败");
                break;
            case PROGRESS:
                rl_layer.setVisibility(View.VISIBLE);
                tv_msg.setVisibility(View.GONE);
                pb_loading.setVisibility(View.VISIBLE);
                iv_delete.setVisibility(View.VISIBLE);
                iv_picture.setVisibility(View.VISIBLE);
                break;
            case DEFAULT:
                rl_layer.setVisibility(View.GONE);
                tv_msg.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                iv_delete.setVisibility(View.GONE);
                iv_picture.setVisibility(View.VISIBLE);
                break;
        }
    }

    enum Status{
        SUCCESS, FAILURE, NORMAL, PROGRESS, DEFAULT
    }
}
