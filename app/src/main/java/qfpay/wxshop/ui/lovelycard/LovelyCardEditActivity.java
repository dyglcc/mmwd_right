package qfpay.wxshop.ui.lovelycard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.BeforeTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.data.netImpl.LovelyCardNetService;
import qfpay.wxshop.data.netImpl.LovelyCardNetService.LovelyCardBean;
import qfpay.wxshop.dialogs.ISimpleDialogListener;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.image.processer.ImageType;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity;
import qfpay.wxshop.ui.customergallery.CustomerGalleryActivity_;
import qfpay.wxshop.ui.web.CommonWebActivity_;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QFCommonUtils;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;

@EActivity(R.layout.lovelycard_edit)
public class LovelyCardEditActivity extends BaseActivity implements ISimpleDialogListener {
	public static final String SHARE_TITLE = "Hi, 我是%s, 这是我的萌片页";
	public static final String SHARE_CONTENT = "所有欢笑和泪水,一定都是与你相遇的理由.这是我的生活,却想变成你的故事.";

	@ViewById ImageView            iv_bg, iv_tips, iv_name, iv_label1, iv_label2, iv_notice;
	@ViewById EditText             tv_name, tv_label1, tv_label2, tv_notice;
	
	@Bean     RetrofitWrapper      mRetrofitWrapper;
	@Bean     QFImageUploader      mUploader;
	@Pref     LovelyCardPref_      mPref;
	private   LovelyCardNetService mNetService;
	
	private   LCState              mState;
	private   String               mBgImg;
	private   boolean              isUploading;
	private   boolean              hasEdited;
	
	@AfterViews
	void onInit() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("萌片页编辑");
		
		mNetService = mRetrofitWrapper.getNetService(LovelyCardNetService.class);
		mBgImg = mPref.imgUrl().get();
		initViews();
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (hasEdited) {
			Utils.showNativeDialog(this, "提示", "编辑信息有变化,要放弃吗?", "确认放弃", "继续编辑", true, 1, new OnClickListener() {
				@Override public void onClick(View v) {

				}
			});
		} else {
			finish();
		}
	}

	void initViews() {
		boolean isCreate = true;
		if (!mPref.name().get().equals(""))         isCreate = false;

		if (isCreate) {
			mState = LCState.CREATE;
		} else {
			mState = LCState.UPDATE;
		}

		tv_name.setText(  mPref.name().get());
		tv_notice.setText(mPref.descript().get());
		tv_label1.setText(mPref.label_first().get());
		tv_label2.setText(mPref.label_second().get());
		if (mBgImg != null && !mBgImg.equals(""))
		Picasso.with(LovelyCardEditActivity.this)
				.load(QFCommonUtils.generateQiniuUrl(mBgImg, 92, 138))
				.fit()
				.centerCrop()
				.error(R.drawable.lc_addbg)
				.placeholder(R.drawable.lc_addbg)
				.into(iv_bg);
	}
	
	@Click
	void iv_bg() {
		CustomerGalleryActivity_.intent(this).maxCount(1).choicedCount(0).width(6).height(11).startForResult(CustomerGalleryActivity.REQUEST_CODE);
		QFCommonUtils.collect("namecard_bkgpic_clicks", this);
		MobAgentTools.OnEventMobOnDiffUser(this, "namecard_bkgpic_clicks");
	}
	
	@OnActivityResult(CustomerGalleryActivity.REQUEST_CODE)
	void onTakedPic(Intent data, int resultCode) {
		if (resultCode == RESULT_OK) {
			@SuppressWarnings("unchecked")
			ArrayList<ImageProcesserBean> imgs = (ArrayList<ImageProcesserBean>) data.getSerializableExtra(CustomerGalleryActivity.RESULT_DATA_NAME);
			if (imgs != null && !imgs.isEmpty()) {
				ImageProcesserBean bean = imgs.get(0);
				hasEdited = true;
				isUploading = true;
				uploadImage(bean);
			}
		}
	}
	
	@Background
	void uploadImage(ImageProcesserBean bean) {
		String imgUrl = mUploader.with(bean.getId()).path(bean.getPath()).imageType(ImageType.BIG).uploadSync();
		if (imgUrl != null && !"".equals(imgUrl)) onUploaded(imgUrl, bean);
	}
	
	@UiThread
	void onUploaded(String imgUrl, ImageProcesserBean bean) {
		isUploading = false;
		mBgImg = imgUrl;
		Picasso.with(this).load(new File(bean.getPath())).fit().centerCrop().into(iv_bg);

		QFCommonUtils.collect("namecard_bkgpic_success", this);
		MobAgentTools.OnEventMobOnDiffUser(this, "namecard_bkgpic_success");
	}
	
	@Click @SuppressWarnings("deprecation") @SuppressLint("InflateParams")
	void iv_tips() {
		final PopupWindow win = new PopupWindow(this);
		win.setFocusable(true);
        win.setOutsideTouchable(true);
		win.setHeight(LayoutParams.FILL_PARENT);
		win.setWidth(LayoutParams.FILL_PARENT);

		win.setBackgroundDrawable(new ColorDrawable(R.color.common_background_layer));
        View view = LayoutInflater.from(this).inflate(R.layout.lovelycard_edit_tips, null);
        view.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                win.dismiss();
            }
        });
		win.setContentView(view);

		int location[] = { -1, -1};
		iv_tips.getLocationOnScreen(location);
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		win.showAsDropDown(iv_tips, 0, - location[1] - statusBarHeight);
	}
	
	@Click
	void btn_preview() {
		if (isUploading) {
			toast("正在上传中哦~稍等一下再预览辣~");
			return;
		}
		
		QFCommonUtils.collect("namecard_preview", this);
		MobAgentTools.OnEventMobOnDiffUser(this, "namecard_preview");

		// 从View中取得数据
		String name   = tv_name.getText().toString(),
			   desc   = tv_notice.getText().toString(),
			   label1 = tv_label1.getText().toString(),
			   label2 = tv_label2.getText().toString();

		// 校验Name
		LovelyCardBean wrapper = new LovelyCardBean();
		if (name.trim().equals("")) {
            toast("名字不能是空的哦~填写以后再保存吧~");
            return;
        } else {
            wrapper.name  = name;
        }
		// 校验Descript
        if (desc.trim().equals("")) {
            toast("描述不能是空的哦~填写以后再保存吧~");
            return;
        } else {
            wrapper.descr = desc;
        }
		// 校验Background Image
		if (mBgImg == null || mBgImg.equals("")) {
			toast("还没有选择背景图哦~选择以后再保存吧~");
			return;
		} else {
			wrapper.bgimg = mBgImg;
			mPref.imgUrl().put(mBgImg);
		}
        wrapper.addTag(label1);
        wrapper.addTag(label2);

		updateLC(wrapper);
	}
	
	@Background
	void updateLC(LovelyCardBean wrapper) {
		try {
			String name   = wrapper.name,
				   desc   = wrapper.descr,
				   labels = wrapper.getTag(),
				   bgimg  = wrapper.bgimg;
			CommonJsonBean result;
            if (mState == LCState.CREATE) {
				result = mNetService.createLC(name, bgimg, desc, labels, WxShopApplication.dataEngine.getShopId());
			} else {
				result = mNetService.updateLC(name, bgimg, desc, labels);
			}
			if (result.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				CommonWebActivity_.intent(this)
						.url(getLovelyCardUrl("android_mmwdapp_namecardpreview_"))
						.shareTitle(String.format(SHARE_TITLE, name))
						.shareName("萌片页")
						.shareDescript(SHARE_CONTENT)
                        .shareIconUrl(bgimg)
						.platFroms(Arrays.asList(SharedPlatfrom.WXFRIEND, SharedPlatfrom.WXMOMENTS, SharedPlatfrom.COPY))
						.start();
				if (hasEdited) toast("保存成功辣!");
				mPref.name().put(name);
				mPref.label_first().put(wrapper.getTag(0));
				mPref.label_second().put(wrapper.getTag(1));
				mPref.descript().put(desc);
			} else {
				toast(result.getResperr());
			}
			hasEdited = false;
		} catch (Exception e) {
			T.e(e);
		}
	}

	/**
	 * Dialog的确认事件
	 */
	@Override
	public void onPositiveButtonClicked(int requestCode) {

	}

	/**
	 * Dialog的取消事件
	 */
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		finish();
	}
	
	@FocusChange void tv_name(boolean isFocus) {
		iv_name.setSelected(isFocus);
	}

	@FocusChange void tv_label1(boolean isFocus) {
		iv_label1.setSelected(isFocus);
	}

	@FocusChange void tv_label2(boolean isFocus) {
		iv_label2.setSelected(isFocus);
	}

	@FocusChange void tv_notice(boolean isFocus) {
		iv_notice.setSelected(isFocus);
	}

	@BeforeTextChange({R.id.tv_name, R.id.tv_label1, R.id.tv_label2, R.id.tv_notice})
	void onTextChanged(TextView tv, CharSequence text) {
		if (text != null && !text.toString().equals("")) {
			hasEdited = true;
		}
	}

	@UiThread void toast(String content) {
		Toaster.s(this, content);
	}

	public static String getLovelyCardUrl(String medium) {
		return String.format("http://" + WxShopApplication.app.getDomainMMWDUrl()  + "/h5/profile.html?shopid=%s&qfuid=%s&ga_medium=%s&ga_source=entrance",
				WxShopApplication.dataEngine.getShopId(), WxShopApplication.dataEngine.getUserId(), medium);
	}
	
	enum LCState {
		CREATE, UPDATE
	}
}
