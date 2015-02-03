package qfpay.wxshop.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品规格
 *
 * Created by LiFZhe on 1/19/15.
 */
public class SKUModel implements Serializable {
    private int                id;
    private int                commodityId;
    private String             name;
    private float              price;
    private int                amount;
    private List<PictureModel> pictureList;
    private Date               lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<PictureModel> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<PictureModel> pictureList) {
        this.pictureList = pictureList;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SKUModel)) return false;

        SKUModel skuModel = (SKUModel) o;

        if (amount != skuModel.amount) return false;
        if (commodityId != skuModel.commodityId) return false;
        if (id != skuModel.id) return false;
        if (Float.compare(skuModel.price, price) != 0) return false;
        if (lastModified != null ? !lastModified.equals(skuModel.lastModified) : skuModel.lastModified != null)
            return false;
        if (name != null ? !name.equals(skuModel.name) : skuModel.name != null) return false;
        if (pictureList != null ? !pictureList.equals(skuModel.pictureList) : skuModel.pictureList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + commodityId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + amount;
        result = 31 * result + (pictureList != null ? pictureList.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SKUModel{" +
                "id=" + id +
                ", commodityId=" + commodityId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", pictureList=" + pictureList +
                ", lastModified=" + lastModified +
                '}';
    }
}
