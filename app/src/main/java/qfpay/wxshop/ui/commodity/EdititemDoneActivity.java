package qfpay.wxshop.ui.commodity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.*;
import qfpay.wxshop.activity.share.ShareActivity;
import qfpay.wxshop.data.beans.GoodWrapper;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.LabelBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.LabelupdateNetImpl;
import qfpay.wxshop.ui.BaseActivity;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.ShareUtils;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

@EActivity(R.layout.edititem_done_layout)
public class EdititemDoneActivity extends BaseActivity {
	public static final int NEWITEM = 0;
	public static final int MIAOSHA = 1;

	@ViewById
	LinearLayout ll_share_friends, ll_share_moments, ll_share_onekey,
			ll_newitem;
	@Extra
	GoodWrapper wrapper;

	@ViewById
	TextView tv_show_label,tv_selected,tv_modify_label;

	@ViewById
	LinearLayout layout_show_label;
	@Pref AppStateSharePreferences_ statePref;
	
	private static final boolean add = true;
	private static final boolean edit = false;

	@Click
	void layout_show_label() {
		if(tv_selected.getVisibility() == View.GONE){
			MobAgentTools.OnEventMobOnDiffUser(EdititemDoneActivity.this,"click_category_baradd_post");
			startLabelActivity(add);
		}else{
			MobAgentTools.OnEventMobOnDiffUser(EdititemDoneActivity.this,"click_category_barmod_post");
			startLabelActivity(edit);
		}
	}

	@AfterViews
	void init() {
		statePref.guidePointer().put(MainActivity.GUIDE_PREVIEW);
		statePref.isShowGuide().put(true);
		ShareSDK.initSDK(this);
		tv_show_label.setVisibility(View.VISIBLE);
		tv_selected.setVisibility(View.GONE);
		tv_modify_label.setVisibility(View.GONE);
		// 自动打开标签界面
		if(!WxShopApplication.dataEngine.isFirstinShowLabel()){
			auTostartLabelActivity(add);
			WxShopApplication.dataEngine.isSetFirstinShowLabel(true);
		}
		MobAgentTools.OnEventMobOnDiffUser(EdititemDoneActivity.this, "upload_goods_success");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		btn_back();
	}
	@Click
	void btn_back() {
		// 保存tag 到后台
		finish();
	}
	
	private void saveData2Server(){
		if(lb==null){
//			Toaster.l(this, "未选择标签");
			return;
		}
		AbstractNet net  = new LabelupdateNetImpl(this);
		Bundle bun = new Bundle();
		bun.putString("goodid", wrapper.getId());
		bun.putString("tagid", lb.getId());
		net.request(bun, new MainHandler(this) {
			
			@Override
			public void onSuccess(Bundle bundle) {
				T.i("sace tag success");
//				Toaster.l(EdititemDoneActivity.this, "保存成功");
			}
			
			@Override
			public void onFailed(Bundle bundle) {
				
			}
		});
	}

	@Click
	void ll_newitem() {
		MobAgentTools.OnEventMobOnDiffUser(EdititemDoneActivity.this,
				"continue_post");
		EditItemActivity_.intent(this).start();
		finish();
	}

	@Click
	void btn_preview() {
		Intent intent = new Intent(this, ManagePreViewActivity.class);
		intent.putExtra(ConstValue.TITLE, "商品预览");
		intent.putExtra(ConstValue.URL,
				"http://"+WxShopApplication.app.getDomainMMWDUrl()+"/item_detail/" + wrapper.getId());
		MobAgentTools.OnEventMobOnDiffUser(this, "goods_preview");
		this.startActivity(intent);
	}

	@Click(R.id.ll_share_moments)
	void shareMoments() {
		ShareUtils.momentsGoodItem(getGoodsBean(wrapper), this);
		MobAgentTools.OnEventMobOnDiffUser(this, "Share_circle_publish");
	}

	@Click(R.id.ll_share_friends)
	void shareWX() {
		MobAgentTools.OnEventMobOnDiffUser(this, "Share_friends_publish");
		ShareUtils.friendGoodItem(getGoodsBean(wrapper), this);
	}

	@Click(R.id.ll_share_onekey)
	void shareOneKey() {
		MobAgentTools.OnEventMobOnDiffUser(this, "One_keyword_publish");
		WxShopApplication.shareBean = ShareUtils.getShareBean(
				getGoodsBean(wrapper), this);
		Intent intent = new Intent(EdititemDoneActivity.this, ShareActivity.class);
		intent.putExtra(ConstValue.gaSrcfrom, ConstValue.android_mmwdapp_postpreview_);
		intent.putExtra("share_content_type", ShareActivity.SHARE_CONTENT_GOOD_ITEM);
		startActivity(intent);
	}

	GoodsBean getGoodsBean(GoodWrapper wrapper) {
		GoodsBean gb = new GoodsBean();
		gb.setGoodsId(wrapper.getId());
		gb.setImageUrl(Utils.getThumblePic(wrapper.getImgWrapper(0).getUrl(),
				100));
		gb.setSrcimgUrl(wrapper.getImgWrapper(0).getUrl());
		gb.setGoodDesc(wrapper.getDescription());
		gb.setGoodName(wrapper.getName());
		gb.setPriceStr(wrapper.getPrice() + "");
		return gb;
	}

	public static final int requestCodeLabel = 9;

	@UiThread(delay=500)
	public void auTostartLabelActivity(boolean add) {
		LabelActivity_.intent(this).add(add).startForResult(requestCodeLabel);
	}
	public void startLabelActivity(boolean add) {
		LabelActivity_.intent(this).add(add).startForResult(requestCodeLabel);
	}

	LabelBean lb;

	@OnActivityResult(requestCodeLabel)
	void onSelected(Intent intent, int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			// todo result
			// String id = intent.getStringExtra("id");
			lb = (LabelBean) intent.getSerializableExtra("selected");

			setHisToryImage();

		}
	}

	private void setHisToryImage() {

		if(lb!=null){
			tv_selected.setText(lb.getName());
			tv_selected.setVisibility(View.VISIBLE);
			tv_show_label.setVisibility(View.GONE);
			tv_modify_label.setVisibility(View.VISIBLE);
		}

		saveData2Server();
		
	}

}
