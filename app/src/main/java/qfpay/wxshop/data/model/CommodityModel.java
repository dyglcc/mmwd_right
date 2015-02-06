package qfpay.wxshop.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 全局model层, model层用于对某个数据进行抽象建模
 * 用于全局使用同样的model进行数据传递,是所有模型相关的模块需要共同遵守的数据抽象规范
 * 这个是不完整版本的Model,后期再进行扩展
 *
 * Created by LiFZhe on 1/19/15.
 */
public class CommodityModel implements Serializable {
    private int                id;
    private String             name;
    private float              postage     = 0f;
    private String             description;
    private CommodityStatus    status;
    private List<SKUModel>     skuList     = new ArrayList<SKUModel>();
    private List<PictureModel> pictureList = new ArrayList<PictureModel>();
    private Date               lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPostage() {
        return postage;
    }

    public void setPostage(float postage) {
        this.postage = postage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommodityStatus getStatus() {
        return status;
    }

    public void setStatus(CommodityStatus status) {
        this.status = status;
    }

    public List<SKUModel> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SKUModel> skuList) {
        this.skuList = skuList;
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
        if (!(o instanceof CommodityModel)) return false;

        CommodityModel that = (CommodityModel) o;

        if (id != that.id) return false;
        if (Float.compare(that.postage, postage) != 0) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (pictureList != null ? !pictureList.equals(that.pictureList) : that.pictureList != null)
            return false;
        if (skuList != null ? !skuList.equals(that.skuList) : that.skuList != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (postage != +0.0f ? Float.floatToIntBits(postage) : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (skuList != null ? skuList.hashCode() : 0);
        result = 31 * result + (pictureList != null ? pictureList.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommodityModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", postage=" + postage +
                ", descript='" + description + '\'' +
                ", status=" + status +
                ", skuList=" + skuList +
                ", pictureList=" + pictureList +
                ", lastModified=" + lastModified +
                '}';
    }
}
