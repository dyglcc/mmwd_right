package qfpay.wxshop.takepicUtils;

import qfpay.wxshop.utils.MobAgentTools;
public class PictureBean {
	private String fileName = "";
	private String fileStr = "";
	private int id;
	private String imgURL = null;
	
	public int getId() {
		return id;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileStr() {
		return fileStr;
	}
	public void setFileStr(String fileStr) {
		this.fileStr = fileStr;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof PictureBean) {
			if (this.fileName.equals(((PictureBean) o).getFileName()) && this.fileStr.equals(((PictureBean) o).getFileStr())) {
				return true;
			}
			return false;
		} else {
			return super.equals(o);
		}
	}
	@Override
	public String toString() {
		return "file path is : " + this.fileStr;
	}
}
