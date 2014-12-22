package qfpay.wxshop.ui.commodity;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.CommodityModel;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity;
import qfpay.wxshop.ui.view.NewitemUnitItem;
import qfpay.wxshop.ui.view.ProgressDialog;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

@EActivity(R.layout.newitem_layout)
public class EditItemActivity extends BaseActivity implements EditItemIView {
	public final static int STATES_EDIT = 0;
	public final static int STATES_NEW = 1;
	public final static int COUNT_MAX_IMG = 9;
	
	@ViewById EditText et_name, et_price, et_postage, et_description;
	@ViewById TextView tv_title;
	@ViewById LinearLayout ll_add, ll_unit, layout_progress_load;
	@ViewById Button btn_announce;
	@ViewById GridView photoList;
	@ViewById ImageView iv_progress_load;
	private ProgressDialog progressDialog;
	
	@Bean(EdititemPresenterImpl.class) EdititemPresenter presenter;
	@Extra CommodityModel commodityModel = null;
	@Extra int editPos;
	
	@AfterViews
	void init() {
		MobAgentTools.OnEventMobOnDiffUser(this, "release");
		presenter.afterInit(commodityModel == null ? STATES_NEW : STATES_EDIT);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onDestroy();
	}
	
	@OnActivityResult(CustomerGalleryActivity.REQUEST_CODE)
	void onTakedPic(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			@SuppressWarnings("unchecked")
			ArrayList<ImageProcesserBean> imgs = (ArrayList<ImageProcesserBean>) data.getSerializableExtra(CustomerGalleryActivity.RESULT_DATA_NAME);
			presenter.addImgItem(imgs);
		}
	}
	
	@ItemClick(R.id.photoList)
	void onPhotoClick(int position) {
		presenter.onImgClick(position);
	}

	@Click(R.id.btn_announce)
	void announceToServer() {
		if (!Utils.isCanConnectionNetWork(this)) {
			Toaster.s(this, "网络有问题哦~");
			return;
		}
		
		MobAgentTools.OnEventMobOnDiffUser(this, "Click_Upload_goods");
		if (et_name.getText() == null || et_name.getText().toString().equals("")) {
			Toaster.s(this, "需要填写商品名称哦~");
			startAnimation(et_name);
			return;
		}
		if (et_description.getText() == null || et_description.getText().toString().equals("")) {
			Toaster.s(this, "需要填写商品描述哦~");
			startAnimation(et_description);
			return;
		}
		if (et_price.getText() == null || et_price.getText().toString().equals("")) {
			Toaster.s(this, "需要填写商品价格哦~");
			startAnimation(et_price);
			return;
		}
		boolean isAllFail = true;
		for (int i = 0; i < presenter.getImgItemCount(); i ++) {
			if (!presenter.getImgItem(i).isUploaderError()) {
				isAllFail = false;
			}
		}
		if (isAllFail) {
			Toaster.s(this, "所有图片上传失败,请返回重试");
			return;
		}
		if (presenter.getImgItemCount() == 1 && presenter.getImgItem(0).isDefault()) {
			Toaster.s(this, "最少要添加一张图片哦~");
			return;
		}
		presenter.saveItem();
	}

	@Click(R.id.ll_add)
	void addUnitView() {
		presenter.addUnitItem();
	}

	@FocusChange void et_price(View view, boolean isFocus) {
		if (commodityModel == null || commodityModel.getSalesPromotion() == null) {
			return;
		}
		TextView tv  = (TextView) view;
		int price = 0;
		try {
			price = Integer.parseInt(tv.getText().toString(), 10);
		} catch (Exception e) {
			T.e(e);
		}
		if (price == 0) {
			return;
		}
		if (price < commodityModel.getSalesPromotion().getPromotionPrice() && !isFocus) {
			Toaster.s(this, "您设置的价格低于本商品的秒杀价格哦~");
		}
	}
	
	@Click
	void btn_back() {
		if (presenter.isDataChange()) {
			Utils.showNativeDialog(this, "喵喵微店", "亲，商品还未编辑完成，真的要放弃嘛~", "继续编辑", "确定放弃", true, 0, new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		btn_back();
	}

	@FocusChange
	void et_postage(View view, boolean hasFocus) {
		EditText textView = (EditText)view;
        String hint;
        if (hasFocus) {
            hint = textView.getHint().toString();
            textView.setTag(hint);
            textView.setHint("");
        } else {
            hint = textView.getTag().toString();
            textView.setHint(hint);
        }
	}
	
	@Override
	public CommodityModel getCommodityModel() {
		return commodityModel;
	}
	
	@Override
	public void setPhotoAdapter(BaseAdapter adapter) {
		photoList.setAdapter(adapter);
	}
	
	@Override
	public void changeViewStates(boolean isNew) {
		if (isNew) {
			tv_title.setText(R.string.add_item);
			btn_announce.setText(R.string.btn_announce);
		} else {
			tv_title.setText(R.string.edit_item);
			btn_announce.setText(R.string.btn_edit);
		}
	}
	
	@Override
	public void addUnitView(NewitemUnitItem item) {
		ll_unit.addView(item);
	}
	
	@Override
	public NewitemUnitItem getUninview(int position) {
		return (NewitemUnitItem) ll_unit.getChildAt(position);
	}
	
	@Override
	public int getUnitviewCount() {
		return ll_unit.getChildCount();
	}
	
	@Override
	public void removeUnitView(NewitemUnitItem item) {
		ll_unit.removeView(item);
	}
	
	@Override
	public void setRequestButtonText(String text) {
		btn_announce.setText(text);
	}

	@Override
	public void setName(String text) {
		et_name.setText(text);
	}
	
	@Override
	public String getName() {
		return safetyString(et_name.getText().toString());
	}
	
	@Override
	public void setPrice(String text) {
		et_price.setText(text);
	}
	
	@Override
	public String getPrice() {
		return safetyString(et_price.getText().toString());
	}
	
	@Override
	public void setPostage(String text) {
		et_postage.setText(text);
	}
	
	@Override
	public String getPostage() {
		return safetyString(et_postage.getText().toString());
	}
	
	@Override
	public void setDescription(String text) {
		et_description.setText(text);
	}
	
	@Override
	public String getDescription() {
		return safetyString(et_description.getText().toString());
	}
	
	private String safetyString(String string) {
		if (string == null) {
			return "";
		}
		if (string.equals("")) {
			return "";
		}
		return string;
	}
	
	public void startAnimation(final View view) {
		Integer colorstart = getResources().getColor(R.color.white);
		Integer colorend = getResources().getColor(R.color.title_bg_color);
		ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), colorstart, colorend);
		va.setDuration(500);
		va.setRepeatCount(2);
		va.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				view.setBackgroundColor((Integer) arg0.getAnimatedValue());
			}
		});
		va.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator arg0) {
				view.setBackgroundColor(getResources().getColor(R.color.white));
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		va.start();
	}
	
	@Override
	public void startLoading() {
		((AnimationDrawable) iv_progress_load.getBackground()).start();
		layout_progress_load.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void dismissLoading() {
		layout_progress_load.setVisibility(View.GONE);
	}
	
	@Override
	public void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.createDialog(this);
		}
		try {
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updataProgress(int max, int current) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.createDialog(this);
		}
		progressDialog.setProgress(max, current);
	}

	@Override
	public void dismissProgressDialog() {
		if (progressDialog == null || !progressDialog.isShowing()) {
			return;
		}
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		progressDialog = null;
	}
}
