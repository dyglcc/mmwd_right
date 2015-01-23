package qfpay.wxshop.ui.commodity.detailmanager;

import java.io.Serializable;

import qfpay.wxshop.data.model.SKUModel;

/**
 * Sku的界面数据表示
 *
 * Created by LiFZhe on 1/20/15.
 */
public class SkuViewModel implements Serializable {
    private int    id;
    private String name;
    private String price;
    private String amount;
    private int position;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
