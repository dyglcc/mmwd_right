package qfpay.wxshop.data.net;

import java.io.Serializable;

public class PreloadBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;
	private String link;
	private String type;
	private String act;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String title;
}