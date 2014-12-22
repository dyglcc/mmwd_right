package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewItemBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id = "";// 商品ID
	private String goodName = "";// 商品名称
	private String itemPrice = "";// 商品价格
	private String goodDesc = "";// 商品描述
	private String postAge = "";// 邮费
	private String images = "";// 图片地址, 用","分割
	private List<UnitBean> unitList = new ArrayList<UnitBean>();// 规格对应的map, key为规格名称, value为规格数量
	private String total_amount = "";// 无规格的时候的商品数量
	/** 1 : 可支付商品  2 : 微店收款 */
	private Integer orderAble = -1;
	private String goodDetailId = "";
	private String openId = "";
	private String delimageids = "";
	
	public String getDelimageids() {
		return delimageids;
	}
	public void setDelimageids(String delimageids) {
		this.delimageids = delimageids;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<UnitBean> getUnitList() {
		return unitList;
	}
	public void setUnitList(List<UnitBean> unitList) {
		this.unitList = unitList;
	}
	public String getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	public String getGoodName() {
		return goodName;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getGoodDesc() {
		return goodDesc;
	}
	public void setGoodDesc(String goodDesc) {
		this.goodDesc = goodDesc;
	}
	public String getPostAge() {
		return postAge;
	}
	public void setPostAge(String postAge) {
		this.postAge = postAge;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getGoodDetailId() {
		return goodDetailId;
	}
	public void setGoodDetailId(String goodDetailId) {
		this.goodDetailId = goodDetailId;
	}
	public Integer getOrderAble() {
		if (orderAble == -1) {
			return 0;
		}
		return orderAble;
	}
	public void setOrderAble(Integer orderAble) {
		this.orderAble = orderAble;
	}
	
}
