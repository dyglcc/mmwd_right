package qfpay.wxshop.ui.buyersshow;

import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.GoodWrapper;
import qfpay.wxshop.data.netImpl.EdititemService;
import qfpay.wxshop.data.netImpl.EdititemService.GetItemWrapper;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity;
import qfpay.wxshop.ui.customergallery.*;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

@EActivity(R.layout.buyersshow_release_layout)
public class BuyersShowReleaseActivity extends BaseActivity implements BuyersShowDataprovider {
	public static final int COUNT_MAX_CONTENTEDIT = 90;
	public static final int COUNT_MAX_IMG = 9;
	public static final int REQUEST_TAKE_PIC = 101;
	public static final String IV_DELETELINK_LINK = "link";
	public static final String IV_DELETELINK_DELETE = "delete";
	
	@ViewById EditText et_input;
	@ViewById GridView photoList;
	@ViewById LinearLayout ll_link;
	@ViewById TextView tv_link, tv_indicator, tv_title;
	@ViewById ImageView iv_deletelink, iv_linkicon;
	@ViewById CheckBox cb_share_wb, cb_share_twb, cb_share_qzone;

	@Bean BuyersShowReleaseAdapter adapter;
	@Bean RetrofitWrapper retrofitWrapper;
	@Bean BuyersShowReleaseNetProcesser netProcesser;
	
	@Extra BuyerShowBean bean = new BuyerShowBean();
	
	@AfterViews
	void init() {
		netProcesser.init(this);
		initViews();
		if (bean == null || bean.getMid() == null || "".equals(bean.getMid())) {
			CustomerGalleryActivity_.intent(this).maxCount(COUNT_MAX_IMG).startForResult(CustomerGalleryActivity.REQUEST_CODE);
		} else {
			tv_title.setText("编辑买家秀");
		}
		et_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(COUNT_MAX_CONTENTEDIT)});
		cb_share_wb.setChecked(ShareSDK.getPlatform(SinaWeibo.NAME).isValid());
		cb_share_twb.setChecked(ShareSDK.getPlatform(TencentWeibo.NAME).isValid());
		cb_share_qzone.setChecked(ShareSDK.getPlatform(QZone.NAME).isValid());
	}
	
	void initViews() {
		ImageProcesserBean iw = new ImageProcesserBean();
		iw.setDefault(true);
		adapter.addData(iw, false);
		photoList.setAdapter(adapter);
		
		List<ImageBean> imgs = bean.getHm_images();
		if (imgs != null && !imgs.isEmpty()) {
			adapter.addNetData(imgs);
		}
		
		et_input.setText(bean.getContent());
		
		if (bean.getGood_id() != null && !bean.getGood_id().equals("") && !bean.getGood_id().equals("0")) {
			getGoodWrapperFromServer(Integer.parseInt(bean.getGood_id(), 10));
		}
	}
	
	@Background(id = ConstValue.THREAD_GROUP_BUYERSSHOW)
	void getGoodWrapperFromServer(int id) {
		retrofitWrapper.cleanHeader();
		EdititemService service = retrofitWrapper.getNetService(EdititemService.class);
		try {
			GetItemWrapper wrapper = service.getGoodInfo(id);
			if (wrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				setGoodName(wrapper.getData().getGood().getGood_name(), wrapper.getData().getGoodgallery().get(0).getOrigin_url(), id);
			} else {
				showErrorMsg(wrapper.getResperr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMsg("亲,网络访问异常了呢,请重试~");
		}
	}
	
	@UiThread
	void setGoodName(String name, String imgPath, int goodId) {
		if (imgPath == null && name == null) {
			tv_link.setText("添加商品购买链接");
			tv_link.setHeight(Utils.dip2px(this, 30));
			iv_deletelink.setTag(IV_DELETELINK_LINK);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_in);
			iv_linkicon.setImageResource(R.drawable.buyersshow_link_icon_grey);
		} else {
			tv_link.setText("已链接商品：" + name);
			tv_link.setHeight(Utils.dip2px(this, 20));
			iv_deletelink.setTag(IV_DELETELINK_DELETE);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_delete);
			iv_linkicon.setImageResource(R.drawable.buyersshow_link_icon);
		}
		bean.setGood_id(goodId + "");
	}
	
	@Click
	void iv_deletelink(View v) {
		if (v.getTag() == null) {
			return;
		}
		String tag = (String) v.getTag();
		if (IV_DELETELINK_DELETE.equals(tag)) {
			setGoodName(null, null, 0);
		}
		if (IV_DELETELINK_LINK.equals(tag)) {
			return;
		}
	}
	
	@AfterTextChange
	void et_input(Editable text, TextView tv) {
		tv_indicator.setText(text.length() + "/" + COUNT_MAX_CONTENTEDIT);
	}
	
	@Click
	void btn_back() {
		Utils.showNativeDialog(this, "喵喵微店", "亲，买家秀还未发布完成，真的要放弃嘛~", "继续编辑", "确定放弃", true, 0, new OnClickListener(){
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
	}
	
	protected void finishActivity() {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		intent.putExtra("onlyfinish", "finish");
		BuyersShowReleaseActivity.this.finish();
	}

	@Override
	public void onBackPressed() {
		btn_back();
	}
	
	@ItemClick
	void photoList(int position) {
		adapter.onItemClick(position);
	}
	
	@OnActivityResult(CustomerGalleryActivity.REQUEST_CODE)
	void onTakedPic(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			adapter.onTakedPic(data);
			netProcesser.uploadImg(adapter.getImgs());
		}
	}
	
	@Click
	void ll_link() {
		GoodsListForBuyersShowActivity_.intent(this).startForResult(REQUEST_TAKE_PIC);
	}
	
	@OnActivityResult(REQUEST_TAKE_PIC)
	void takedLink(int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			GoodWrapper goodWrapper = (GoodWrapper) data.getSerializableExtra(GoodsListForBuyersShowActivity.RESULT_GOOD_KEY);
			setGoodName(goodWrapper.getGood_name(), goodWrapper.getGood_img(), goodWrapper.getId());
		}
	}
	
	@Click
	void btn_release() {
		boolean isImageRight = adapter.getImgs() != null && !adapter.isEmpty() && !adapter.getImgs().get(0).isDefault();
		if (!isImageRight) {
			showErrorMsg("至少来一张图片嘛~");
			return;
		}
		
		bean.setContent(et_input.getText().toString());
		if (bean.getContent() == null || "".equals(bean.getContent())) {
			showErrorMsg("至少写一句话嘛~");
			return;
		}
		
		
		netProcesser.releaseData();
		
		Intent intent = new Intent();
		if (adapter.getImgs() != null && !adapter.isEmpty()) {
			ImageProcesserBean img = adapter.getImgs().get(0);
			if (img.isOnlyNetImage()) {
				intent.putExtra("data", img.getUrl());
			} else {
				intent.putExtra("data", img.getPath());
			}
		}
		if(bean == null){
			intent.putExtra("result", MaijiaxiuFragment.ACTION_ADD_MAIJIAXIU);
		}else{
			intent.putExtra("result", MaijiaxiuFragment.ACTION_EDIT_MAIJIAXIU);
		}
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public List<String> getDelImgids() {
		return adapter.getDelImgs();
	}

	@Override
	public List<ImageProcesserBean> getImgs() {
		return adapter.getImgs();
	}
	
	@CheckedChange({R.id.cb_share_wb, R.id.cb_share_twb, R.id.cb_share_qzone})
	void initShare(final CompoundButton checkBox, boolean isCheck) {
		if (!isCheck) {
			return;
		}
		String tag = (String) checkBox.getTag();
		String temp = "";
		if (tag.equals("sina")) {
			temp = SinaWeibo.NAME;
			MobAgentTools.OnEventMobOnDiffUser(this, "maijiaxiu_sina_auth");
		} else
		if (tag.equals("qqweibo")) {
			temp = TencentWeibo.NAME;
			MobAgentTools.OnEventMobOnDiffUser(this, "maijiaxiu_tencent_weibo_auth");
		} else
		if (tag.equals("qzone")) {
			temp = QZone.NAME;
			MobAgentTools.OnEventMobOnDiffUser(this, "maijiaxiu_qqzone_auth");
		}
		final String platfromName = temp;
		Platform platform = ShareSDK.getPlatform(this, platfromName);
		if (platform.isValid()) {
			return;
		}
		PlatformActionListener authorListener = new PlatformActionListener() {
			@Override
			public void onCancel(Platform arg0, int arg1) {
				checkBox.setChecked(false);
			}
			@Override
			public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {
				checkBox.setChecked(platform.isValid());
				MobAgentTools.OnEventMobOnDiffUser(BuyersShowReleaseActivity.this, platfromName + "_auth_success");
			}
			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
				checkBox.setChecked(false);
				MobAgentTools.OnEventMobOnDiffUser(BuyersShowReleaseActivity.this, platfromName + "_auth_success");
			}
		};
		platform.setPlatformActionListener(authorListener);
		platform.authorize();
	}
	
	@UiThread
	void showErrorMsg(String msg) {
		if (msg != null && !msg.equals("")) {
			Toaster.s(this, msg);
			return;
		}
		Toaster.s(this, "网络出错了");
	}

	@Override
	public BuyerShowBean getData() {
		return bean;
	}

	@Override
	public boolean isSharedWB() {
		return cb_share_wb.isChecked();
	}

	@Override
	public boolean isSharedTWB() {
		return cb_share_twb.isChecked();
	}

	@Override
	public boolean isSharedQzone() {
		return cb_share_qzone.isChecked();
	}
}
