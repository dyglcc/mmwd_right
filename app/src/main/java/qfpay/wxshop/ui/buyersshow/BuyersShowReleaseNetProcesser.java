package qfpay.wxshop.ui.buyersshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import qfpay.wxshop.data.beans.BuyerResponseWrapper.BuyerShowBean;
import qfpay.wxshop.data.beans.BuyerResponseWrapper.ImageBean;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.netImpl.BuyersShowNetService;
import qfpay.wxshop.data.netImpl.BuyersShowNetService.BuyersShowPutResponseWrapper;
import qfpay.wxshop.image.ImageGroupUploadLinstener;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.listener.MaijiaxiuUploadListener;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.content.Context;

import com.google.gson.Gson;

@EBean(scope = Scope.Singleton)
public class BuyersShowReleaseNetProcesser implements ImageGroupUploadLinstener {
	@RootContext Context context;
	@Bean QFImageUploader mImageUploadHelper;
	@Bean RetrofitWrapper retrofitWrapper;
	private BuyersShowDataprovider dataProvider;
	private MaijiaxiuUploadListener maijiaxiuUploadLinstener;
	
	private BuyerShowBean dataBean;
	private List<String> delImgs;
	private List<ImageProcesserBean> imgs;
	private boolean isWbShare = false;
	private boolean isTwbShare = false;
	private boolean isQzoneShare = false;
	private boolean isEdit = false;
	int index;
	
	public void init(BuyersShowDataprovider provider) {
		mImageUploadHelper.cancelAll();
		this.dataProvider = provider;
		mImageUploadHelper.setLinstener(this);
	}
	
	public void setMaijiaxiuLinstener(MaijiaxiuUploadListener maijiaxiuUploadLinstener, int index) {
		this.maijiaxiuUploadLinstener = maijiaxiuUploadLinstener;
		this.index = index;
	}
	
	public void uploadImg(List<ImageProcesserBean> imgs) {
		for (ImageProcesserBean imageProcesserBean : imgs) {
			mImageUploadHelper
				.setLinstener(this)
				.with(imageProcesserBean.getId())
				.path(imageProcesserBean.getPath())
				.urlSetter(imageProcesserBean)
				.uploadInGroup();
		}
	}
	
	@Override
	public void onUploadProgress(float progress) {
		if (maijiaxiuUploadLinstener != null) {
			maijiaxiuUploadLinstener.onUpload((int) (progress * 100));
		}
	}

	@Override
	public void onComplete(int successCount, int failureCount) {
		if (failureCount == 0) {
			releaseBuyersShow(isEdit);
		} else {
			maijiaxiuUploadLinstener.onUploadFaild();
		}
		
	}
	
	public void releaseData() {
		this.dataBean = dataProvider.getData();
		this.isEdit = this.dataBean.getMid() != null && !this.dataBean.getMid().equals("");
		this.delImgs = dataProvider.getDelImgids();
		this.imgs = dataProvider.getImgs();
		this.isWbShare = dataProvider.isSharedWB();
		this.isTwbShare = dataProvider.isSharedTWB();
		this.isQzoneShare = dataProvider.isSharedQzone();
		
		mImageUploadHelper.ready();
		if (maijiaxiuUploadLinstener != null) {
			maijiaxiuUploadLinstener.onInitProgress(BuyersShowReleaseActivity.COUNT_MAX_IMG, dataBean, isWbShare, isTwbShare, isQzoneShare, isEdit);
		} else {
			throw new NullPointerException("买家秀中没有Linstener");
		}
	}
	
	public void retryRelease() {
		mImageUploadHelper.cancelAll();
		for (ImageProcesserBean bean : dataProvider.getImgs()) {
			mImageUploadHelper
				.setLinstener(this)
				.with(bean.getId())
				.path(bean.getPath())
				.urlSetter(bean)
				.uploadInGroup();
		}
		mImageUploadHelper.ready();
		if (maijiaxiuUploadLinstener != null) {
			maijiaxiuUploadLinstener.onInitProgress(BuyersShowReleaseActivity.COUNT_MAX_IMG, dataBean, isWbShare, isTwbShare, isQzoneShare, isEdit);
		} else {
			throw new NullPointerException("买家秀中没有Linstener");
		}
	}
	
	@Background
	void releaseBuyersShow(boolean isPost) {
		retrofitWrapper.cleanHeader();
		BuyersShowNetService service = retrofitWrapper.getNetService(BuyersShowNetService.class);
		try {
			Map<String, String> params = processRequestParams(dataBean, delImgs, imgs);
			BuyersShowPutResponseWrapper bean;
			if (isPost) {
				bean = service.postHM(params);
			} else {
				bean = service.putHM(params);
			}
			T.d(params.toString());
			if (!bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				if (maijiaxiuUploadLinstener != null) {
					maijiaxiuUploadLinstener.onUploadFaild();
                    toast(bean.getResperr());
				}
				return;
			}
			
			this.dataBean.setMid(bean.getData().getMsg().getMid() + "");
			this.dataBean.setHm_images(bean.getData().getMsg().getHm_images());
			if (maijiaxiuUploadLinstener != null) {
				boolean isAdd = dataBean.getMid() == null || "".equals(dataBean.getMid());
				maijiaxiuUploadLinstener.onSuccess(isAdd, index, dataBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (maijiaxiuUploadLinstener != null) {
				maijiaxiuUploadLinstener.onUploadFaild();
			}
		}
	}
	
	Map<String, String> processRequestParams(BuyerShowBean bean, List<String> delImgList, List<ImageProcesserBean> imgList) {
		Map<String, String> params = new HashMap<String, String>();
		if (bean.getMid() != null && !bean.getMid().equals("")) {
			params.put("mid", bean.getMid());
		}
		params.put("msg_type", "1");
		params.put("content", bean.getContent());
		params.put("good_id", bean.getGood_id());
		params.put("del_imageids", delImgList.toString());
		bean.setHm_images(processImgs(imgList));
		Gson gson = new Gson();
		params.put("hm_images", gson.toJson(bean.getHm_images()));
		return params;
	}
	
	List<ImageBean> processImgs(List<ImageProcesserBean> imgList) {
		List<ImageBean> imgs = new ArrayList<ImageBean>();
		for (ImageProcesserBean imageProcesserBean : imgList) {
			if (imageProcesserBean.isDefault() || !imageProcesserBean.hasUploaded()) {
				continue;
			}
			ImageBean img = new ImageBean();
			img.setUrl(imageProcesserBean.getUrl());
			if (imageProcesserBean.isFromNative()) {
				img.setId("");
			} else {
				img.setId(imageProcesserBean.getId() + "");
			}
			imgs.add(img);
		}
		return imgs;
	}

    @UiThread void toast(String string) {
        Toaster.s(context, string);
    }
}
