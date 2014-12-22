package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;

public class UnitBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String size = null;
	private int amount;
	private int id = -1;
	
	public boolean isOnlyStock() {
		return (size == null || size.equals("")) && (amount != 0);
	}
	
	public String getSize() {
		return size;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
}
