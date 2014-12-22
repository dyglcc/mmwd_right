package qfpay.wxshop.ui.view;

import java.io.File;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.image.ImageProcesserBean;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

@EViewGroup(R.layout.newitem_img_item)
public class ImgGridItem extends RelativeLayout {
	@ViewById ImageView delete, photo;
	@SystemService WindowManager windowManager;
	DeleteImgProcesser processer;
	ImageProcesserBean imageBean;
	
	public ImgGridItem(Context context) {
		super(context);
		
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	public ImgGridItem setData(ImageProcesserBean wrapper, DeleteImgProcesser processer) {
		this.processer = processer;
		this.imageBean = wrapper;
		if (wrapper.isDefault()) {
			Picasso.with(getContext()).load(R.drawable.buyersshow_release_addphoto).fit().into(photo);
			delete.setVisibility(View.GONE);
		} else {
			delete.setVisibility(View.VISIBLE);
			if (wrapper.isOnlyNetImage()) {
				Picasso.with(getContext()).load(wrapper.getUrl()).fit().centerCrop().into(photo);
			} else {
				Picasso.with(getContext()).load(new File(wrapper.getPath())).fit().centerCrop().into(photo);
			}
		}
		return this;
	}
	
	@Click
	void delete() {
		this.processer.deleteData(imageBean, true);
	}
}
