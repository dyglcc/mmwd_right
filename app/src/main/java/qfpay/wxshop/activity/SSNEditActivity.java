package qfpay.wxshop.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.data.beans.SsnContentBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.handler.MainHandlerMulSelectPics;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.GoodWrapper;
import qfpay.wxshop.data.netImpl.GoodsInfoNetImpl;
import qfpay.wxshop.data.netImpl.SuiSuiNianEditImpl;
import qfpay.wxshop.data.netImpl.UploadPicImpl;
import qfpay.wxshop.data.netImpl.UploadPicMulImpl;
import qfpay.wxshop.dialogs.SimpleDialogFragment;
import qfpay.wxshop.listener.onScrollviewFocusListener;
import qfpay.wxshop.takepicUtils.PictureBean;
import qfpay.wxshop.takepicUtils.TakePicUtils;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.buyersshow.*;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.selectpic.ImageItem;
import qfpay.wxshop.ui.view.EditorView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
/**
 * 碎碎念编辑
 * */
@EActivity(R.layout.main_suisuinian_pub)
public class SSNEditActivity extends BaseActivity implements
		onScrollviewFocusListener {

	@ViewById(R.id.parent)
	EditorView mScrollview;
	public static final String IV_DELETELINK_LINK = "link";
	public static final String IV_DELETELINK_DELETE = "delete";
	public static final int REQUEST_TAKE_PIC = 1;
	Button btn_save, btn_back;
	@ViewById
	EditText et_title;
	@ViewById
	LinearLayout layout_link, layout_add_pic, layout_link_close;

	@ViewById
	ImageView iv_deletelink, iv_linkicon, iv_title, iv_content;
	private ArrayList<UploadPicMulImpl> uploadMulList = new ArrayList<UploadPicMulImpl>();

	@ViewById
	TextView tv_link;
	@ViewById
	View layout_addpic_spaceing;
	private Map<String, String> mapFileSize = new HashMap<String, String>();

	@Extra
	int editpos;
	@Extra
	SSNItemBean item;

	@Click
	void layout_add_pic() {
		if(!Utils.isCanConnectionNetWork(SSNEditActivity.this)){
			Toaster.l(this, "喵~没有联网");
			return;
		}
		
		if (!onEditorFocus) {
			Toaster.l(SSNEditActivity.this, "选择添加位置");
			return;
		}
		MobAgentTools.OnEventMobOnDiffUser(SSNEditActivity.this, "Click_HybridText_addphoto");
		TakePicUtils.getInstance().init(SSNEditActivity.this);
		TakePicUtils.getInstance().takePic(
				TakePicUtils.TAKE_PIC_MODE_ONLY_SELECT_MUL_PICS);
	}

	@Click
	void layout_addpic_spaceing() {
		layout_add_pic();
	}

	@AfterViews
	void inits() {

		mScrollview.addOnFocusListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.common_menuitem_suisuinian);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = actionBar.getCustomView();
		btn_back = (Button) view.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showDialogConfirm();

			}
		});
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MobAgentTools.OnEventMobOnDiffUser(SSNEditActivity.this, "Click_HybridText_save");
				String str = mScrollview.getDelMidStr();
				save2Server(mScrollview.getContent(mapFileSize), str);
			}
		});

		et_title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (et_title.getText().toString().length() == 0) {
					et_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							getResources()
									.getDimension(R.dimen.text_size_small));
				} else {
					et_title.setTextSize(
							TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(
									R.dimen.text_size_normal));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		et_title.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					onEditorFocus = false;
					// editor view 设置hint
					mScrollview.setHint();
					// 设置选中背景图片
					iv_title.setImageResource(R.drawable.icon_ssl_title_p);
					iv_content.setImageResource(R.drawable.icon_ssl_text);
				} else {
					iv_title.setImageResource(R.drawable.icon_ssl_title_p);
				}
			}
		});
		tv_link.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (tv_link.getText().toString().length() == 0) {
					tv_link.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							getResources()
									.getDimension(R.dimen.text_size_small));
				} else {
					tv_link.setTextSize(
							TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(
									R.dimen.text_size_normal));
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		// init MapFileSize
		initMapFileSize();
		initEditContent();
	}

	@UiThread(delay = 500)
	void initEditContent() {
		// TODO Auto-generated method stub
		mScrollview.init(item.getContent());
		et_title.setText(item.getTitle());
		T.i("goooid is : " + item.getId());
		if (item.getGood_id() != null && !item.getGood_id().equals("0")) {
			layout_link.setTag(item.getGood_id());
			getGoodInfoNetWork();
		}
	}

	private void getGoodInfoNetWork() {
		// TODO Auto-generated method stub
		AbstractNet net = new GoodsInfoNetImpl(SSNEditActivity.this);
		Bundle bun = new Bundle();
		bun.putString("goodid", item.getGood_id());
		net.request(bun, new MainHandler(SSNEditActivity.this) {

			@Override
			public void onSuccess(Bundle bundle) {
				String good_name = bundle.getString("good_name");
//				int id = bundle.getInt("id");
				String good_img = bundle.getString("good_img");
				setGoodName(good_name, good_img);
			}

			@Override
			public void onFailed(Bundle bundle) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void initMapFileSize() {
		List<SsnContentBean> content = item.getContent();
		for (int i = 0; i < content.size(); i++) {
			SsnContentBean scb = content.get(i);
			if (scb.getType().equals(EditorView.FLAG_PIC)) {
				mapFileSize.put(scb.getContent(), scb.getAttach());
			}
		}
		mapFileSize.put(WDConfig.MAIJIAXIU_SHARE_PIC, SSNPublishActivity
				.getPicSizeStr(SSNEditActivity.this, R.drawable.ic_launcher));

	}

	@Click
	void btn_back() {
		// showDialog
		showDialogConfirm();
	}

	@Override
	public void onBackPressed() {

		showDialogConfirm();
	}

	protected void showDialogConfirm() {
		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(getString(R.string.mm_hint)).setMessage("亲，退出编辑吗")
				.setNegativeButtonText("继续编辑").setPositiveButtonText("确定放弃")
				.setCancelable(true).setRequestCode(-1)
				.setPositiveClick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}

	private void save2Server(String content, String delStr) {

		if (!isCan()) {
			return;
		}
		if (item.getMid() == null) {
			Toaster.l(SSNEditActivity.this, "编辑取消，消息id找不到了");
			return;
		}
		AbstractNet net = new SuiSuiNianEditImpl(SSNEditActivity.this);
		Bundle bun = new Bundle();
		bun.putString("title", et_title.getText().toString());
		bun.putString("content", content);
		bun.putString("mid", item.getMid());

		if (layout_link.getTag() != null) {
			bun.putString("good_id", layout_link.getTag() + "");
		}
		if (delStr != null && !delStr.equals("")) {
			bun.putString("del_imageids", "[" + delStr + "]");
		}
		bun.putString("img_url", mScrollview.getImageUrl());
		net.request(bun, new MainHandler(SSNEditActivity.this) {

			@Override
			public void onSuccess(Bundle bundle) {
				MobAgentTools.OnEventMobOnDiffUser(SSNEditActivity.this, "Success_HybridText_Public");
				
				Toaster.l(SSNEditActivity.this, "编辑成功");
				item = (SSNItemBean) bundle.getSerializable("bean");
				finishSuccessEdit();
			}

			@Override
			public void onFailed(Bundle bundle) {

			}
		});
	}

	private boolean isCan() {
		boolean checkUploadSuccess = mScrollview.checkUploadSuccess();
		if (!checkUploadSuccess) {
			Toaster.l(SSNEditActivity.this, "正在上传图片..");
			return false;
		}
		String image_url = mScrollview.getImageUrl();
		if (image_url == null) {
			Toaster.l(SSNEditActivity.this, "亲，上传一张图片吧");
			return false;
		}
		if (TextUtils.isEmpty(et_title.getText().toString())) {
			Toaster.l(SSNEditActivity.this, "没有标题，填写一个标题吧");
			et_title.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TakePicUtils.TAKE_PIC_REQUEST_CODE) {

			if (resultCode != RESULT_OK) {
				return;
			}

			PictureBean fb = TakePicUtils.getInstance().receivePics(
					requestCode, resultCode, data);

			if (fb != null) {

				uploadFile2Server(fb);

			}

		} else if (requestCode == TakePicUtils.SELECT_PIC_REQUEST_CODE) {

			if (WxShopApplication.paths.size() != 0) {

				// 多选图片处理
				startUploadFiles();
			}
		}

	}

	private void uploadFile2Server(final PictureBean pb) {

		MobAgentTools.OnEventMobOnDiffUser(SSNEditActivity.this,
				"Click_HybridText_Camera");
		AbstractNet net = new UploadPicImpl(SSNEditActivity.this);
		Bundle para = new Bundle();
		para.putString("fileUrl", pb.getFileStr());
		para.putString("fileName", "suisuinian");

		final FrameLayout layout = (FrameLayout) mScrollview.addpic(pb
				.getFileStr());
		if (layout == null) {
			Toaster.l(SSNEditActivity.this, "添加图片失败");
			return;
		}

		// category：分类 1. 用户凭证 2. 渠道凭证 3. 喵喵微店
		// source：文件来源 1. web 2. app 3. 喵喵微店
		// tag：图片标签(avatar: 头像, qmm: 喵喵微店, showcase: 商品/服务展示图片)
		para.putString("category", "3");
		para.putString("source", "3");
		para.putString("tag", "qmm");
		net.request(para, new MainHandler(SSNEditActivity.this) {

			@Override
			public void onSuccess(Bundle bundle) {
				// TODO Auto-generated method stub
				// Toaster.l(SSNEditActivity.this, "上传图片成功");
				String object = (String) bundle.get("url");
				mapFileSize.put(object,
						SSNPublishActivity.getPicSizeStr(pb.getFileStr()));
				layout.setTag(object);
				// layout.findViewById(R.id.progressBar1).setVisibility(View.GONE);
			}

			@Override
			public void onFailed(Bundle bundle) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void startUploadFiles() {

		// 处理图片为小图片

		int count = WxShopApplication.paths.size();

		TakePicUtils.getInstance().init(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < WxShopApplication.paths.size(); i++) {
					ImageItem item = WxShopApplication.paths.get(i);
					if (item == null || item.imagePath == null
							|| item.imagePath.equals("")) {
						continue;
					}
					String desPathFileName = item.imagePath
							.substring(item.imagePath
									.lastIndexOf(File.separator) + 1);
					TakePicUtils.dealPic(ConstValue.getPICTURE_DIR()
							+ desPathFileName, item.imagePath,
							SSNEditActivity.this);
					item.smallPicPath = ConstValue.getPICTURE_DIR()
							+ desPathFileName;
				}
				upLoadPicHandler.sendEmptyMessage(UPLOAD_FILES);
			}
		}).start();

	}

	private static final int UPLOAD_FILES = 4;
	public static final int CHECK_UPLOAD_STATUS = UPLOAD_FILES + 1;
	public static final int SSN_EDIT = CHECK_UPLOAD_STATUS + 1;
	// 重复上传bug1.调用两次js--解决方法：1。定义标志只调用一次。
	private Handler upLoadPicHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPLOAD_FILES:
				upoadFiles2Server();
				break;
			case CHECK_UPLOAD_STATUS:
				break;
			}

		};
	};

	private void upoadFiles2Server() {

		MobAgentTools.OnEventMobOnDiffUser(SSNEditActivity.this,
				"Click_HybridText_addalbum");
		for (int i = 0; i < WxShopApplication.paths.size(); i++) {
			final ImageItem item = WxShopApplication.paths.get(i);
			final UploadPicMulImpl net = new UploadPicMulImpl(this, item,
					upLoadPicHandler);
			final FrameLayout addpic = (FrameLayout) mScrollview
					.addpic(item.smallPicPath);
			if (addpic == null) {
				Toaster.l(SSNEditActivity.this, "添加图片失败");
				return;
			}
			Bundle para = new Bundle();
			// category：分类 1. 用户凭证 2. 渠道凭证 3. 喵喵微店
			// source：文件来源 1. web 2. app 3. 喵喵微店
			// tag：图片标签(avatar: 头像, qmm: 喵喵微店, showcase: 商品/服务展示图片)
			para.putString("category", "3");
			para.putString("source", "3");
			para.putString("tag", "qmm");
			uploadMulList.add(net);
			net.request(para, new MainHandlerMulSelectPics(
					SSNEditActivity.this, net) {

				@Override
				public void onSuccess(Bundle bundle) {
					// TODO Auto-generated method stub
					// Toaster.l(SSNEditActivity.this, "上传成功");
					// addpic.findViewById(R.id.progressBar1).setVisibility(
					// View.GONE);
				}

				@Override
				public void onFailed(Bundle bundle) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFinish(boolean success, UploadPicMulImpl net) {
					if(mapFileSize == null ){
						return;
					}
					if (success) {
						String object = net.getReturnURL();
						mapFileSize.put(object, SSNPublishActivity
								.getPicSizeStr(item.smallPicPath));
						if(addpic!=null){
							addpic.setTag(object);
						}
					}
				}

			});

		}
	}

	@Click
	void layout_link() {
		GoodsListForBuyersShowActivity_.intent(this).startForResult(
				REQUEST_TAKE_PIC);
	}

	@OnActivityResult(REQUEST_TAKE_PIC)
	void takedLink(int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			GoodWrapper goodWrapper = (GoodWrapper) data
					.getSerializableExtra(GoodsListForBuyersShowActivity.RESULT_GOOD_KEY);
			layout_link.setTag(goodWrapper.getId());
			setGoodName(goodWrapper.getGood_name(), goodWrapper.getGood_img());
		}
	}

	@UiThread
	void setGoodName(String name, String imgPath) {
		if (imgPath == null && name == null) {
			tv_link.setHint(getString(R.string.ssn_publish_goodlink));
			tv_link.setText("");
			iv_deletelink.setTag(IV_DELETELINK_LINK);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_in);
		} else {
			tv_link.setText("已链接商品：" + name);
			iv_deletelink.setTag(IV_DELETELINK_DELETE);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_delete);
		}
	}

	@Click
	void layout_link_close(View v) {
		if (iv_deletelink.getTag() == null) {
			layout_link();
			return;
		}
		String tag = (String) iv_deletelink.getTag();
		if (IV_DELETELINK_DELETE.equals(tag)) {
			setGoodName(null, null);
		}
		if (IV_DELETELINK_LINK.equals(tag)) {
			return;
		}
	}

	private boolean onEditorFocus = false;

	@Override
	public void onfocus() {
		onEditorFocus = true;
		iv_content.setImageResource(R.drawable.icon_ssl_text_p);
		iv_title.setImageResource(R.drawable.icon_ssl_title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapFileSize.clear();
		mapFileSize = null;
	}

	private void finishSuccessEdit() {
		if (item == null) {
			finish();
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("bean", item);
		intent.putExtra("editpos", editpos);
		intent.putExtra("result", MaijiaxiuFragment.ACTION_EDIT_SSN);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
