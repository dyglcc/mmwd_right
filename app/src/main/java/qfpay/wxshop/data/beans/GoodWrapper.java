package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.image.ImageProcesserBean;

public class GoodWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id = "";
	private String name = "";
	private float price = 0;
	private String description = "";
	private String postAge = "";
	private List<ImageProcesserBean> deleteImgs = new ArrayList<ImageProcesserBean>();
	private List<ImageProcesserBean> imgWrapper = new ArrayList<ImageProcesserBean>();
	private List<UnitBean> unitList = new ArrayList<UnitBean>();
	
	public void setImgWrapper(List<ImageProcesserBean> imgWrapper) {
		this.imgWrapper = imgWrapper;
	}
	public void addImgWrapper(ImageProcesserBean wrapper) {
		imgWrapper.add(wrapper);
	}
	public ImageProcesserBean getImgWrapper(int position) {
		return imgWrapper.get(position);
	}
	public void cleanImgWrapper() {
		imgWrapper.clear();
	}
	public List<ImageProcesserBean> getImgWrappers() {
		return imgWrapper;
	}
	public void addUnitWrapper(UnitBean unitBean) {
		unitList.add(unitBean);
	}
	public UnitBean getUnitWrapper(int position) {
		return unitList.get(position);
	}
	public void cleanUnit() {
		unitList.clear();
	}
	public List<UnitBean> getUnitBeans() {
		return unitList;
	}
	public void setUnitList(List<UnitBean> unitList) {
		this.unitList = unitList;
	}
	public void setDeleteImgFromServer(List<ImageProcesserBean> list) {
		this.deleteImgs = list;
	}
	public void deleteImgFromServer(ImageProcesserBean wrapper) {
		if (!wrapper.isDefault()) {
			return;
		}
		deleteImgs.add(wrapper);
	}
	public void cleanDeleteList() {
		deleteImgs.clear();
	}
	public List<ImageProcesserBean> getDeleteImg() {
		return deleteImgs;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPostAge() {
		return postAge;
	}
	public void setPostAge(String postAge) {
		this.postAge = postAge;
	}
}
