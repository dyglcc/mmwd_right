package qfpay.wxshop.image.processer;

public enum ImageType {
	BIG(100, 1500, 1500), 
	NORMAL(70, 1000, 1000), 
	SMALL(50, 500, 500);
	
	public int quality, width, height;

	private ImageType(int quality, int width, int heigh) {
		this.quality = quality;
		this.width = width;
		this.height = heigh;
	}
}
