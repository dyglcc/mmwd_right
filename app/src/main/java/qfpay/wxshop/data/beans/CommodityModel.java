package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;

import qfpay.wxshop.utils.QFCommonUtils;

public class CommodityModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String DATE_FORMATSTR = "yyyy-MM-dd HH:mm:ss";
	
	private int                 id;           // 商品ID
	private String              good_name;    // 商品名称
	private String 			    good_img;     // 商品图片
	private float 				good_prize;   // 商品价格
	private int 				good_amount;  // 商品库存
	private String 				good_desc;    // 商品描述
	private float 				postage;      // 商品邮费
	private int 				sales;        // 商品销量
	private int 				good_state;   // 商品状态
	private int 				weight;       // 排序权重
	private String 				update_time;  // 更新时间
	private String 				create_time;  // 创建时间
	private SalesPromotionModel goodpanic;    // 秒杀的表示
	
	private boolean isOffshelfed = false;
	public static enum CommodityState {
		NORMAL, OFFSHELVES, DELETE, PROMOTION, NULL
	}
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return id;
	}
	
	public void setName(String name) {
		this.good_name = name;
	}
	public String getName() {
		return good_name;
	}
	
	public void setImgUrl(String imgUrl) {
		this.good_img = imgUrl;
	}
	public String getImgUrl() {
		return good_img;
	}
	
	public void setPrice(float price) {
		this.good_prize = price;
	}
	public float getPrice() {
		return good_prize;
	}
	
	public void setStock(int stock) {
		this.good_amount = stock;
	}
	public int getStock() {
		return good_amount;
	}
	
	public void setDescript(String descript) {
		this.good_desc = descript;
	}
	public String getDescript() {
		return good_desc;
	}
	
	public float getPostage() {
		return postage;
	}
	public void setPostage(float postage) {
		this.postage = postage;
	}
	
	public void setSalesCount(int count) {
		this.sales = count;
	}
	public int getSalesCount() {
		return sales;
	}
	
	public CommodityState getCommodityState() {
		switch (good_state) {
		case 0:
			return CommodityState.NORMAL;
		case 1:
			return CommodityState.OFFSHELVES;
		case 2:
			return CommodityState.DELETE;
		case 11:
			return CommodityState.PROMOTION;
		}
		return CommodityState.NULL;
	}
	public void setComodityStateForOld(int state) {
		this.good_state = state;
	}
	public int getCommodityStateForOld() {
		return good_state;
	}
	
	public void setSortWeight(int weight) {
		this.weight = weight;
	}
	public int getSortWeight() {
		return weight;
	}
	
	public Calendar getUpdateTime() throws ParseException {
		return QFCommonUtils.string2Calendar(update_time, DATE_FORMATSTR);
	}
	
	public Calendar getCreateTime() throws ParseException {
		return QFCommonUtils.string2Calendar(create_time, DATE_FORMATSTR);
	}
	
	public String getCreateTimeForOld() {
		return create_time;
	}
	
	public void setSalesPromotion(SalesPromotionModel salesModel) {
		this.goodpanic = salesModel;
	}
	public SalesPromotionModel getSalesPromotion() {
		return goodpanic;
	}
	
	public void offShelf() {
		isOffshelfed = true;
	}
	public boolean isOffshelfed() {
		return isOffshelfed;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((create_time == null) ? 0 : create_time.hashCode());
		result = prime * result + good_amount;
		result = prime * result
				+ ((good_desc == null) ? 0 : good_desc.hashCode());
		result = prime * result
				+ ((good_img == null) ? 0 : good_img.hashCode());
		result = prime * result
				+ ((good_name == null) ? 0 : good_name.hashCode());
		result = prime * result + Float.floatToIntBits(good_prize);
		result = prime * result + good_state;
		result = prime * result
				+ ((goodpanic == null) ? 0 : goodpanic.hashCode());
		result = prime * result + id;
		result = prime * result + (isOffshelfed ? 1231 : 1237);
		result = (int) (prime * result + postage);
		result = prime * result + sales;
		result = prime * result
				+ ((update_time == null) ? 0 : update_time.hashCode());
		result = prime * result + weight;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommodityModel other = (CommodityModel) obj;
		if (create_time == null) {
			if (other.create_time != null)
				return false;
		} else if (!create_time.equals(other.create_time))
			return false;
		if (good_amount != other.good_amount)
			return false;
		if (good_desc == null) {
			if (other.good_desc != null)
				return false;
		} else if (!good_desc.equals(other.good_desc))
			return false;
		if (good_img == null) {
			if (other.good_img != null)
				return false;
		} else if (!good_img.equals(other.good_img))
			return false;
		if (good_name == null) {
			if (other.good_name != null)
				return false;
		} else if (!good_name.equals(other.good_name))
			return false;
		if (Float.floatToIntBits(good_prize) != Float
				.floatToIntBits(other.good_prize))
			return false;
		if (good_state != other.good_state)
			return false;
		if (goodpanic == null) {
			if (other.goodpanic != null)
				return false;
		} else if (!goodpanic.equals(other.goodpanic))
			return false;
		if (id != other.id)
			return false;
		if (isOffshelfed != other.isOffshelfed)
			return false;
		if (postage != other.postage)
			return false;
		if (sales != other.sales)
			return false;
		if (update_time == null) {
			if (other.update_time != null)
				return false;
		} else if (!update_time.equals(other.update_time))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
}
