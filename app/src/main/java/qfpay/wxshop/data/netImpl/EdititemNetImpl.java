package qfpay.wxshop.data.netImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import qfpay.wxshop.R;
import qfpay.wxshop.data.beans.GoodWrapper;
import qfpay.wxshop.data.beans.UnitBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.data.netImpl.EdititemService.EditDoneBean;
import qfpay.wxshop.data.netImpl.EdititemService.GetItemNetBean;
import qfpay.wxshop.data.netImpl.EdititemService.GetItemWrapper;
import qfpay.wxshop.data.netImpl.EdititemService.ImgNetBeanFromServer;
import qfpay.wxshop.image.ImageGroupUploadLinstener;
import qfpay.wxshop.image.ImageProcesserBean;
import qfpay.wxshop.image.QFImageUploader;
import qfpay.wxshop.ui.commodity.EditItemActivity;
import qfpay.wxshop.ui.commodity.EdititemPresenter;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.content.Context;

import com.google.gson.Gson;

@EBean
public class EdititemNetImpl implements ImageGroupUploadLinstener {
	@Bean RetrofitWrapper retrofitWrapper;
	
	private EdititemPresenter presenter;
	private Context context;
	
	private int states = EditItemActivity.STATES_NEW;
	private GoodWrapper savingGoodWrapper;
	private boolean isLoading = false;
	
	public EdititemNetImpl init(EdititemPresenter presenter, Context context, int states) {
		this.presenter = presenter;
		this.states = states;
		this.context = context;
		return this;
	}
	
	@Background(id = ConstValue.THREAD_GROUP_EDITITEM)
	public void getGoodWrapperFromServer(int id) {
		if (isLoading) {
			showErrorMsg("正在上传中哦~如果您不想等可以返回再重新编辑一下哦~");
			return;
		}
		isLoading = true;
		presenter.startLoading();
		
		retrofitWrapper.cleanHeader();
		EdititemService service = retrofitWrapper.getNetService(EdititemService.class);
		try {
			GetItemWrapper wrapper = service.getGoodInfo(id);
			if (wrapper.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				presenter.onGetItemSuccess(processGetitemBean(wrapper.getData()));
			} else {
				showErrorMsg(wrapper.getResperr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			showNetErrorMsg();
		}
		isLoading = false;
		presenter.dismissLoading();
	}
	
	GoodWrapper processGetitemBean(GetItemNetBean bean) {
		GoodWrapper wrapper = new GoodWrapper();
		for (ImgNetBeanFromServer img : bean.getGoodgallery()) {
			ImageProcesserBean imgWrapper = new ImageProcesserBean();
			imgWrapper.setId(Integer.parseInt(img.getId(), 10));
			imgWrapper.setUrl(img.getOrigin_url());
			wrapper.addImgWrapper(imgWrapper);
		}
		if (bean.getGooddetails() == null || bean.getGooddetails().size() == 0) {
			UnitBean unit = new UnitBean();
			unit.setAmount(Integer.parseInt(bean.getGood().getGood_amount(), 10));
			ArrayList<UnitBean> list = new ArrayList<UnitBean>();
			list.add(unit);
			wrapper.setUnitList(list);
		} else {
			wrapper.setUnitList(bean.getGooddetails());
		}
		wrapper.setId(bean.getGood().getId());
		wrapper.setName(bean.getGood().getGood_name());
		try {
			wrapper.setPrice(Float.parseFloat(bean.getGood().getGood_prize()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wrapper.setDescription(bean.getGood().getGood_desc());
		wrapper.setPostAge(bean.getGood().getPostage());
		return wrapper;
	}
	
	@Override
	public void onUploadProgress(float progress) {
		presenter.updataProgress(100, (int) (progress * 100));
	}

	@Override
	public void onComplete(int successCount, int failureCount) {
		presenter.deleteAllFailImgItem();
		if (successCount == 0 && failureCount != 0) {
			Toaster.s(context, context.getString(R.string.toast_imgupload_error_none));
			presenter.dismissProgressDialog();
			return;
		}
		if (failureCount != 0) {
			Toaster.s(context, String.format(context.getString(R.string.toast_imgupload_error), failureCount));
		}
		saveItemServer();
		T.d(QFImageUploader.logTag, "Start save to server");
	}

    @Override
    public void onImageReady() {

    }

    public void saveItem(GoodWrapper wrapper) {
		this.savingGoodWrapper = wrapper;
		presenter.showProgressDialog();
	}
	
	@Background(id = ConstValue.THREAD_GROUP_EDITITEM)
	void saveItemServer() {
		if (savingGoodWrapper == null) {
			showErrorMsg("出现未知异常,请返回重试哦~");
			return;
		}
		
		retrofitWrapper.cleanHeader();
		EdititemService service = retrofitWrapper.getNetService(EdititemService.class);
		
		boolean isSuccess = false;
		if (states == EditItemActivity.STATES_NEW) {
			isSuccess = saveNewItem(service, savingGoodWrapper);
		} else {
			isSuccess = saveEditItem(service, savingGoodWrapper);
		}
		if (isSuccess) {
			presenter.onSaveItemSucess(savingGoodWrapper);
		}
		
		presenter.dismissProgressDialog();
		isLoading = false;
	}
	
	boolean saveEditItem(EdititemService service, GoodWrapper wrapper) {
		EdititemNetBean requestBean = processEditItemBean(wrapper);
		Map<String, String> map = processRequestParams(requestBean);
		try {
			CommonJsonBean bean = service.editItemSave(map);
			if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				return true;
			} else {
				showErrorMsg(bean.getResperr());
				return false;
			}
		} catch (Exception e) {
			showNetErrorMsg();
			return false;
		}
	}
	
	boolean saveNewItem(EdititemService service, GoodWrapper wrapper) {
		NewitemNetBean requestBean = processNewItemBean(wrapper);
		Map<String, String> map = processRequestParams(requestBean);
		try {
			EditDoneBean bean = service.newItemSave(map);
			if (bean.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				wrapper.setId(bean.data.goodid);
				bindHDImg(service, wrapper.getImgWrapper(0).getUrl(), wrapper.getId());
				return true;
			} else {
				showErrorMsg(bean.getResperr());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			showNetErrorMsg();
			return false;
		}
	}
	
	@Background(id = ConstValue.THREAD_GROUP_EDITITEM) 
	void bindHDImg(EdititemService service, String imgurl, String goodid) {
		try {
			service.bindHDImg(imgurl, goodid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public EdititemNetBean processEditItemBean(GoodWrapper wrapper) {
		EdititemNetBean bean = new EdititemNetBean();
		bean.good_name = wrapper.getName();
		bean.itemprice = wrapper.getPrice() + "";
		bean.good_desc = wrapper.getDescription();
		bean.postage = wrapper.getPostAge();
		bean.goodid = wrapper.getId();
		ArrayList<ImgNetBean> imgs = new ArrayList<EdititemNetImpl.ImgNetBean>();
		for (ImageProcesserBean img : wrapper.getImgWrappers()) {
			if (img.isDefault() || !img.hasUploaded()) {
				continue;
			}
			ImgNetBean imgnet = new ImgNetBean();
			if (img.isFromNative()) {
				imgnet.id = "";
			} else {
				imgnet.id = img.getId() + "";
			}
			imgnet.url = img.getUrl();
			imgs.add(imgnet);
		}
		bean.images = new Gson().toJson(imgs);
		
		for (ImageProcesserBean img : wrapper.getDeleteImg()) {
			if (bean.delimageids == null || bean.delimageids.equals("")) {
				bean.delimageids = img.getId() + "";
				continue;
			}
			bean.delimageids = bean.delimageids + "," + img.getId();
		}
		if (wrapper.getUnitBeans().size() == 1 && wrapper.getUnitBeans().get(0).isOnlyStock()) {
			bean.total_amount = wrapper.getUnitBeans().get(0).getAmount() + "";
		} else {
			bean.details = new Gson().toJson(wrapper.getUnitBeans());
		}
		return bean;
	}
	
	public NewitemNetBean processNewItemBean(GoodWrapper wrapper) {
		NewitemNetBean bean = new NewitemNetBean();
		bean.good_name = wrapper.getName();
		bean.itemprice = wrapper.getPrice() + "";
		bean.good_desc = wrapper.getDescription();
		bean.postage = wrapper.getPostAge();
		for (ImageProcesserBean img : wrapper.getImgWrappers()) {
			if (img.isDefault() || !img.hasUploaded()) {
				continue;
			}
			if (bean.images == null || bean.images.equals("")) {
				bean.images = img.getUrl();
				continue;
			}
			bean.images = bean.images + "," + img.getUrl();
		}
		if (wrapper.getUnitBeans().size() == 1 && (wrapper.getUnitBeans().get(0).getSize() == null || wrapper.getUnitBeans().get(0).getSize().equals(""))) {
			bean.total_amount = wrapper.getUnitBeans().get(0).getAmount() + "";
		} else {
			for (UnitBean unit : wrapper.getUnitBeans()) {
				if (bean.size_app == null || bean.size_app.equals("")) {
					bean.size_app = unit.getSize();
				} else {
					bean.size_app = bean.size_app + "," + unit.getSize();
				}
				if (bean.storage_app == null || bean.storage_app.equals("")) {
					bean.storage_app = unit.getAmount() + "";
				} else {
					bean.storage_app = bean.storage_app + "," + unit.getAmount();
				}
			}
		}
		return bean;
	}
	
	public Map<String, String> processRequestParams(Object requestObj) {
		try {
			Field[] fields = requestObj.getClass().getDeclaredFields();
			Map<String, String> map = new HashMap<String, String>();
			for (Field field : fields) {
				if (field.get(requestObj) instanceof String) {
					map.put(field.getName(), (String) field.get(requestObj));
				}
			}
			return map;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	void showNetErrorMsg() {
		showErrorMsg("亲,网络访问异常了呢,请重试~");
	}
	
	@UiThread
	void showErrorMsg(String msg) {
		Toaster.s(context, msg);
	}
	
	class NewitemNetBean {
		public String good_name;
		public String itemprice;
		public String good_desc;
		public String postage;
		public String images;// 图片url 用‘,’分割
		public String gooddetailid = "-1";
		public String size_app ;// 规格名称，用，分割
		public String storage_app;// 规格数量, ","分割
		public String headimage;// 头图, 可为空
		public String total_amount;// 没有规格的时候的商品数量
		public String orderable = "1";
		public String is_app = "android";
	}
	
	class EdititemNetBean {
		String goodid;
		String good_name;
		String itemprice;
		String good_desc;
		String postage;
		String images;
		String delimageids;// 删除图片的id，用，分割
		String headimage;// 头图, 可为空
		String total_amount;// 没有规格的时候的商品数量
		String details;
		String is_app = "android";
	}
	
	class ImgNetBean {
		public String id;
		public String url;
	}
}
