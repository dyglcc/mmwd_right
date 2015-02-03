package qfpay.wxshop.data.model;

import java.io.Serializable;

/**
 * 商品的状态枚举
 *
 * Created by LiFZhe on 1/19/15.
 */
public enum CommodityStatus implements Serializable {
    NORMAL(0),
    PROMOTION(11),
    OFF_SHELF(1),
    DELETE(2);

    private int statusId;

    CommodityStatus(int statusId) {
        this.statusId = statusId;
    }

    public int getId() {
        return statusId;
    }

    public static CommodityStatus getStatus(int statusId) {
        CommodityStatus[] array = CommodityStatus.values();
        for (int i = 0; i < array.length; i++) {
            CommodityStatus status = array[i];
            if (status.getId() == statusId) {
                return status;
            }
        }
        return null;
    }
}
