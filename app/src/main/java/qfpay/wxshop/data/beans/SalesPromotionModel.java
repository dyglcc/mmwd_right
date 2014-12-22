package qfpay.wxshop.data.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;

import qfpay.wxshop.utils.QFCommonUtils;

public class SalesPromotionModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String DATE_FORMATSTR = "yyyy-MM-dd HH:mm:ss";
	
	private int id;
	private int goodid;
	private float newprice;
	private int status_flag;
	private String starttime;
	private String endtime;
	private String createtime;
	
	public static enum PromotionState {
		DELETE, STARTED, NOT_START, ENDED, NULL
	}
	
	public int getPromotionID() {
		return id;
	}
	
	public void setCommodityID(int id) {
		this.goodid = id;
	}
	public int getCommodityID() {
		return goodid;
	}
	
	public void setPromotionPrice(float price) {
		this.newprice = price;
	}
	public float getPromotionPrice() {
		return newprice;
	}
	
	public PromotionState getPromotionFlag() {
		switch (status_flag) {
		case 0:
			return PromotionState.DELETE;
		case 1:
			return PromotionState.STARTED;
		case 2:
			return PromotionState.NOT_START;
		case 3:
			return PromotionState.ENDED;
		}
		return PromotionState.NULL;
	}
	
	public Calendar getStartTime() throws ParseException {
		return QFCommonUtils.string2Calendar(starttime, DATE_FORMATSTR);
	}
	
	public Calendar getEndTime() throws ParseException {
		return QFCommonUtils.string2Calendar(endtime, DATE_FORMATSTR);
	}
	
	public Calendar getCreateTime() throws ParseException {
		return QFCommonUtils.string2Calendar(createtime, DATE_FORMATSTR);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createtime == null) ? 0 : createtime.hashCode());
		result = prime * result + ((endtime == null) ? 0 : endtime.hashCode());
		result = prime * result + goodid;
		result = prime * result + id;
		result = prime * result + Float.floatToIntBits(newprice);
		result = prime * result
				+ ((starttime == null) ? 0 : starttime.hashCode());
		result = prime * result + status_flag;
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
		SalesPromotionModel other = (SalesPromotionModel) obj;
		if (createtime == null) {
			if (other.createtime != null)
				return false;
		} else if (!createtime.equals(other.createtime))
			return false;
		if (endtime == null) {
			if (other.endtime != null)
				return false;
		} else if (!endtime.equals(other.endtime))
			return false;
		if (goodid != other.goodid)
			return false;
		if (id != other.id)
			return false;
		if (Float.floatToIntBits(newprice) != Float
				.floatToIntBits(other.newprice))
			return false;
		if (starttime == null) {
			if (other.starttime != null)
				return false;
		} else if (!starttime.equals(other.starttime))
			return false;
		if (status_flag != other.status_flag)
			return false;
		return true;
	}
}
