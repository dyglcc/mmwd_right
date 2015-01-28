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
    private int                 price_count;  // 有多少个价格
	
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

    public int getPrice_count() {
        return price_count;
    }

    public void setPrice_count(int price_count) {
        this.price_count = price_count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommodityModel)) return false;

        CommodityModel that = (CommodityModel) o;

        if (good_amount != that.good_amount) return false;
        if (Float.compare(that.good_prize, good_prize) != 0) return false;
        if (good_state != that.good_state) return false;
        if (id != that.id) return false;
        if (isOffshelfed != that.isOffshelfed) return false;
        if (Float.compare(that.postage, postage) != 0) return false;
        if (price_count != that.price_count) return false;
        if (sales != that.sales) return false;
        if (weight != that.weight) return false;
        if (create_time != null ? !create_time.equals(that.create_time) : that.create_time != null)
            return false;
        if (good_desc != null ? !good_desc.equals(that.good_desc) : that.good_desc != null)
            return false;
        if (good_img != null ? !good_img.equals(that.good_img) : that.good_img != null)
            return false;
        if (good_name != null ? !good_name.equals(that.good_name) : that.good_name != null)
            return false;
        if (goodpanic != null ? !goodpanic.equals(that.goodpanic) : that.goodpanic != null)
            return false;
        if (update_time != null ? !update_time.equals(that.update_time) : that.update_time != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (good_name != null ? good_name.hashCode() : 0);
        result = 31 * result + (good_img != null ? good_img.hashCode() : 0);
        result = 31 * result + (good_prize != +0.0f ? Float.floatToIntBits(good_prize) : 0);
        result = 31 * result + good_amount;
        result = 31 * result + (good_desc != null ? good_desc.hashCode() : 0);
        result = 31 * result + (postage != +0.0f ? Float.floatToIntBits(postage) : 0);
        result = 31 * result + sales;
        result = 31 * result + good_state;
        result = 31 * result + weight;
        result = 31 * result + (update_time != null ? update_time.hashCode() : 0);
        result = 31 * result + (create_time != null ? create_time.hashCode() : 0);
        result = 31 * result + (goodpanic != null ? goodpanic.hashCode() : 0);
        result = 31 * result + price_count;
        result = 31 * result + (isOffshelfed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommodityModel{" +
                "id=" + id +
                ", good_name='" + good_name + '\'' +
                ", good_img='" + good_img + '\'' +
                ", good_prize=" + good_prize +
                ", good_amount=" + good_amount +
                ", good_desc='" + good_desc + '\'' +
                ", postage=" + postage +
                ", sales=" + sales +
                ", good_state=" + good_state +
                ", weight=" + weight +
                ", update_time='" + update_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", goodpanic=" + goodpanic +
                ", price_count=" + price_count +
                ", isOffshelfed=" + isOffshelfed +
                '}';
    }
}
