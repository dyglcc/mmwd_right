package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;

/**
 * 秒杀data
 * */
public class GoodMSBean implements Serializable {
	
	private String newprice;
	private String id;
	private String status_flag;
	private String starttime;
	private String endtime;
	private String createtime;

	public String getNewprice() {
		return newprice;
	}

	public void setNewprice(String newprice) {
		this.newprice = newprice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus_flag() {
		return status_flag;
	}

	public void setStatus_flag(String status_flag) {
		this.status_flag = status_flag;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

}
