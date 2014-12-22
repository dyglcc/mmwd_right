package qfpay.wxshop.data.net;

import qfpay.wxshop.utils.MobAgentTools;
public class FileBean {

	private String fileName;
	private String fileDir;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
	public FileBean(String name,String dir){
		this.fileName = name;
		this.fileDir = dir;
	}
}
