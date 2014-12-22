package qfpay.wxshop.data.beans;

import java.io.Serializable;

public class GoodsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String goodsId;
	private String goodName;
	private String dateStr;
	private String updateDateStr;
	private String priceStr;
	private String stock;
	private String saled;
	private int weight;
	private int editPos;
	private String imageUrl;
	private String srcimgUrl;
	private boolean isPromotion;
	private String shareTitle;
	private String shareDesc;
	private String shareUrl;
	private String QfUid;
	private String updateTime;
	private String postage;
	private String goodstate;
	private String goodDesc;
	private GoodMSBean msBean;
	
	private boolean isMenuOpened = false;
	private boolean isAni = true;

	public boolean isAni() {
		return isAni;
	}
	public void setAni(boolean isAni) {
		this.isAni = isAni;
	}
	public boolean isMenuOpened() {
		return isMenuOpened;
	}
	public void setMenuOpened(boolean isMenuOpened) {
		this.isMenuOpened = isMenuOpened;
	}
	public int getEditPos() {
		return editPos;
	}
	public void setEditPos(int editPos) {
		this.editPos = editPos;
	}
	public String getUpdateDateStr() {
		return updateDateStr;
	}
	public void setUpdateDateStr(String updateDateStr) {
		this.updateDateStr = updateDateStr;
	}
	public String getSrcimgUrl() {
		return srcimgUrl;
	}
	public void setSrcimgUrl(String srcimgUrl) {
		this.srcimgUrl = srcimgUrl;
	}
	public String getQfUid() {
		return QfUid;
	}
	public void setQfUid(String qfUid) {
		QfUid = qfUid;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getPostage() {
		return postage;
	}
	public void setPostage(String postage) {
		this.postage = postage;
	}
	public String getGoodstate() {
		return goodstate;
	}
	public void setGoodstate(String goodstate) {
		this.goodstate = goodstate;
	}
	public String getGoodDesc() {
		return goodDesc;
	}
	public void setGoodDesc(String goodDesc) {
		this.goodDesc = goodDesc;
	}
	public GoodMSBean getMsBean() {
		return msBean;
	}
	public void setMsBean(GoodMSBean msBean) {
		this.msBean = msBean;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodName() {
		return goodName;
	}
	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}
	public String getDateStr() {
		return dateStr;
	}
	public void setCreateDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public String getPriceStr() {
		return priceStr;
	}
	public void setPriceStr(String priceStr) {
		this.priceStr = priceStr;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getSaled() {
		return saled;
	}
	public void setSaled(String saled) {
		this.saled = saled;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public boolean isPromotion() {
		return isPromotion;
	}
	public void setPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}
	public String getShareTitle() {
		return shareTitle;
	}
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}
	public String getShareDesc() {
		return shareDesc;
	}
	public void setShareDesc(String shareDesc) {
		this.shareDesc = shareDesc;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
