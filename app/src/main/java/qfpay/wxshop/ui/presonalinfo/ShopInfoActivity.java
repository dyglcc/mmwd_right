package qfpay.wxshop.ui.presonalinfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.activity.EditAdvertisementActivity_;
import qfpay.wxshop.activity.ShopHeaderPhotoPickerActivity_;
import qfpay.wxshop.activity.ShopNameEditActivity_;
import qfpay.wxshop.activity.WeixinEditActivity_;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.AvatorUpdateImpl;
import qfpay.wxshop.data.netImpl.UploadPic4QiNiuImpl;
import qfpay.wxshop.data.netImpl.UploadPicImpl;
import qfpay.wxshop.takepicUtils.PictureBean;
import qfpay.wxshop.takepicUtils.TakePicUtils;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.fragment.ShopFragmentsWrapper;
import qfpay.wxshop.ui.view.CustomImageView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
/*
 * 店铺信息页面
 **/
@EActivity(R.layout.main_shop_info)
public class ShopInfoActivity extends BaseActivity {

	public static final String SP_NAME_MANAGE = "config";
	public static final String SP_ITEN_ISNEW = "header_img_isnew";
	@ViewById(R.id.layout_dianputgonggao)
	View layoutDPGG;
	@ViewById(R.id.tv_shopurl)
	TextView btnClip;
	@ViewById TextView tv_shopheader;
	private AQuery aq;

	@ViewById(R.id.layout_takepic)
	View layoutTakePic;
	@ViewById(R.id.tv_text)
	TextView tvNotice;

	@ViewById(R.id.et_weixinhao)
	TextView etWexinHao;
	@ViewById(R.id.et_shopname)
	TextView etShopName;

	@ViewById(R.id.btn_back)
	Button btnBack;

	@ViewById(R.id.btn_complete)
	Button btnComplete;

	@ViewById(R.id.iv_photo)
	CustomImageView ivPhoto;
	@ViewById(R.id.iv_default)
	ImageView iv_default;

	@ViewById(R.id.tv_title)
	TextView title;
	@ViewById(R.id.ll_pickerbackground)
	View ll_pickerbackground;
	@ViewById(R.id.ll_shopname_bg)
	View ll_shopname_bg;
	@ViewById(R.id.ll_weixinhao_bg)
	View ll_weixinhao_bg;
	public static final int editShopName = 3;
	public static final int editweixhao = editShopName + 1;
	public static final int editNotice = editweixhao + 1;

	@Click
	void ll_shopname_bg() {
		ShopNameEditActivity_.intent(ShopInfoActivity.this).startForResult(editShopName);
	}

	@Click
	void ll_weixinhao_bg() {
		MobAgentTools.OnEventMobOnDiffUser(this, "edit_shop_manage_weixinhao");
		WeixinEditActivity_.intent(ShopInfoActivity.this).startForResult(editweixhao);
	}

	@OnActivityResult(editShopName)
	void onEditShopName(Intent intent, int resultcode) {
		if (resultcode == Activity.RESULT_OK) {
			String shopneame = WxShopApplication.dataEngine.getShopName();
			etShopName.setText(shopneame);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@OnActivityResult(editweixhao)
	void onEditWeixinHao(Intent intent, int resultcode) {
		if (resultcode == Activity.RESULT_OK) {
			String contract = WxShopApplication.dataEngine.getContract();
			etWexinHao.setText(contract);
		}
	}
	@OnActivityResult(editNotice)
	void onEditeditNotice(Intent intent, int resultcode) {
		if (resultcode == Activity.RESULT_OK) {
			settextNotice();
		}
	}

	@AfterViews
	void init() {
		ShopFragmentsWrapper.PREVIEW.refresh();
		initViews();
		aq = new AQuery(this);
		MobAgentTools.OnEventMobOnDiffUser(this, "edit_shop_manage");
		getSupportActionBar().setTitle("店铺装修");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setValues();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void downloadImage() {
		if (!WxShopApplication.dataEngine.getAvatar().equals(
				WDConfig.DEFAULT_AVATOR)) {

			aq.id(ivPhoto).image(WxShopApplication.dataEngine.getAvatar(),
					true, true, 120, R.drawable.defaul_tavator,
					new BitmapAjaxCallback() {

						@Override
						protected void callback(String url, ImageView iv,
								Bitmap bm, AjaxStatus status) {
							iv_default.setVisibility(View.GONE);
							ivPhoto.setVisibility(View.VISIBLE);
							ivPhoto.setBitmap_(bm);
							super.callback(url, iv, bm, status);
						}

					});
		} else if (WxShopApplication.dataEngine.getAvatar().equals("")
				|| WxShopApplication.dataEngine.getAvatar().equals(
						WDConfig.DEFAULT_AVATOR)) {
			iv_default.setVisibility(View.VISIBLE);
			ivPhoto.setVisibility(View.GONE);
		}
	}

	private void initViews() {
		btnComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String shopName = etShopName.getText().toString();
				if (shopName.equals("")) {
					Utils.showDialog(ShopInfoActivity.this, null,
							getString(R.string.mm_hint),
							getString(R.string.input_store_name),
							getString(R.string.know), "", false, true);
					return;
				}

				MobAgentTools.OnEventMobOnDiffUser(ShopInfoActivity.this, "save");

				uploadFile();

			}
		});

		layoutTakePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TakePicUtils.getInstance().init(ShopInfoActivity.this);
				TakePicUtils.getInstance().takePic(TakePicUtils.TAKE_PIC_MODE_CROP);
			}
		});

		String url = (WDConfig.SHOW_SHOP_ADDR + WxShopApplication.dataEngine
				.getShopId()).replace("http://", "");
		btnClip.setText(url);
		btnClip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.saveClipBoard(ShopInfoActivity.this, WDConfig.SHOW_SHOP_ADDR
						+ WxShopApplication.dataEngine.getShopId()
						+ "?ga_medium=" + ConstValue.android_mmwdapp_copyurl_
						+ "&ga_source=entrance");
				Toaster.l(ShopInfoActivity.this, getString(R.string.copy));
				MobAgentTools.OnEventMobOnDiffUser(ShopInfoActivity.this, "copy2");
				MobAgentTools.OnEventMobOnDiffUser(ShopInfoActivity.this,
						"CLICK_SHOPINFO_COPYLINK");
			}
		});
		MobAgentTools.OnEventMobOnDiffUser(ShopInfoActivity.this, "shop");

		ll_pickerbackground.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopHeaderPhotoPickerActivity_.intent(ShopInfoActivity.this)
						.startForResult(101);
				Editor editor = ShopInfoActivity.this.getSharedPreferences(SP_NAME_MANAGE, Context.MODE_PRIVATE).edit();
				editor.putBoolean(SP_ITEN_ISNEW, false);
				editor.commit();
				MobAgentTools.OnEventMobOnDiffUser(ShopInfoActivity.this, "CLICK_CHANGE_SHOP_IMAGE");
			}
		});

		title.setText("完善店铺信息");

		layoutDPGG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				EditAdvertisementActivity_.intent(ShopInfoActivity.this).startForResult(editNotice);

			}
		});

	}

	private void setValues() {
		settextNotice();
		downloadImage();
		String shopneame = WxShopApplication.dataEngine.getShopName();
		etShopName.setText(shopneame);

		String contract = WxShopApplication.dataEngine.getContract();
		etWexinHao.setText(contract);
		
		String shopheader = WxShopApplication.dataEngine.getShopBg();
		T.d("shopheader is " + shopheader);
		if (shopheader == null || "".equals(shopheader)) {
			tv_shopheader.setText(R.string.shopinfo_text_null);
		} else {
			tv_shopheader.setText("");
		}
	}

	@Override
	protected void onRestart() {
		setValues();
		super.onRestart();
	}

	private void settextNotice() {
		if (!WxShopApplication.dataEngine.getNoticeText().equals("")) {
			tvNotice.setText(WxShopApplication.dataEngine.getNoticeText());
		} else {
			tvNotice.setText(R.string.shopinfo_text_null);
		}
	}

	private void uploadFile() {
		if (selectedPicDIR == null || selectedPicDIR.equals("")) {
			// 未选择图片，跳过选择图片界面
			save2MiaomiaoServer();
			return;
		}
		String tokenString = Utils.getQiniuToken();

		Bundle para = new Bundle();
		UploadPicImpl net = null;
		if (WxShopApplication.app.useQiniu) {
			net = new UploadPic4QiNiuImpl(this, tokenString);
			para = new Bundle();
			para.putString("fileUrl", selectedPicDIR);
			para.putString("fileName", "avatar");
		} else {
			para = new Bundle();
			para.putString("category", "3");
			para.putString("source", "3");
			para.putString("tag", "qmm");
			para.putString("fileUrl", selectedPicDIR);
			para.putString("fileName", "avatar");
			net = new UploadPicImpl(this);
		}
		
		net.request(para, getDetailHandler);
	}

	private String url_finish_pic;
	private MainHandler getDetailHandler = new MainHandler(this) {
		@Override
		public void onSuccess(Bundle bundle) {
			url_finish_pic = bundle.getString("url");
			if (url_finish_pic != null && !url_finish_pic.equals("")) {
				save2MiaomiaoServer();
			} else {
				Toaster.l(ShopInfoActivity.this, getString(R.string.fail_upload));
			}
		}

		@Override
		public void onFailed(Bundle bundle) {
		}
	};

	protected void save2MiaomiaoServer() {
		AbstractNet net = new AvatorUpdateImpl(this);
		Bundle bun = new Bundle();

		if (selectedPicDIR != null && !selectedPicDIR.equals("")) {
			if (url_finish_pic != null && !url_finish_pic.equals("")) {
				bun.putString("intro", url_finish_pic);
			}
		} else {
			if (!WxShopApplication.dataEngine.getAvatar().equals("")) {
				bun.putString("intro", WxShopApplication.dataEngine.getAvatar());
			}
		}
		net.request(bun, new MainHandler(this) {
			@Override
			public void onSuccess(Bundle bundle) {
				if (!isHeader) {
					Toaster.l(ShopInfoActivity.this, "保存成功");
				}
				if (url_finish_pic != null && !url_finish_pic.equals("")) {
					WxShopApplication.dataEngine.setAvatar(url_finish_pic);
				}
				downloadImage();
			}

			@Override
			public void onFailed(Bundle bundle) {
			}
		});

	}

	private String selectedPicDIR;
	private boolean isHeader = false;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		selectedPicDIR = "";
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == TakePicUtils.TAKE_PIC_REQUEST_CODE) {
				TakePicUtils.getInstance().receivePics(requestCode, resultCode,
						data);
			} else if (requestCode == TakePicUtils.CROP_PICTURE_DONE) {
				PictureBean pb = TakePicUtils.getInstance().receivePics(
						requestCode, resultCode, data);
				if (pb != null) {
					selectedPicDIR = pb.getFileStr();
					if (!selectedPicDIR.equals("")) {
						iv_default.setVisibility(View.GONE);
						ivPhoto.setVisibility(View.VISIBLE);
						Bitmap map = BitmapFactory.decodeFile(selectedPicDIR);
						ivPhoto.setBitmap_(map);
						// save2Server();
						uploadFile();
					}
				}
			} else if (requestCode == 101) {
				Toaster.s(this, "背景图设置成功");
				try {
					isHeader = true;
					uploadFile();
				} catch (Exception e) {
				}
			}
		}

	}
	
	public void refresh(){
		init();
	}
}
