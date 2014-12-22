package qfpay.wxshop.image.processer;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import qfpay.wxshop.image.ImageWrapper;
import qfpay.wxshop.image.processer.cache.Key;

public class ImageSignature implements Key {
    private final ImageWrapper signature;

    public ImageSignature(ImageWrapper signature) {
        if (signature == null) {
            throw new NullPointerException("Signature cannot be null!");
        }
        this.signature = signature;
    }

    @Override
	public String toString() {
		return "ImageSignature [signature=" + signature + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
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
		ImageSignature other = (ImageSignature) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}

	@Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update((signature.getPath() + "").getBytes());
        messageDigest.update((signature.getQuality() + "").getBytes());
        messageDigest.update((signature.getWidth() + "").getBytes());
        messageDigest.update((signature.getHeight() + "").getBytes());
        messageDigest.update((signature.getSize() + "").getBytes());
    }
}
