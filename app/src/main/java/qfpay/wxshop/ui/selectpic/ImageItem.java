package qfpay.wxshop.ui.selectpic;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
	public String smallPicPath;
}
