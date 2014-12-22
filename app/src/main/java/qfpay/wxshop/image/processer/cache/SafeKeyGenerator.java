package qfpay.wxshop.image.processer.cache;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import qfpay.wxshop.utils.QFCommonUtils;
import android.support.v4.util.LruCache;

/**
 * A class that generates and caches safe and unique string file names from {@link com.bumptech.glide.load.Key}s.
 */
public class SafeKeyGenerator {
    private final LruCache<Key, String> loadIdToSafeHash = new LruCache<Key, String>(1000);

    public String getSafeKey(Key key) {
        String safeKey;
        synchronized (loadIdToSafeHash) {
            safeKey = loadIdToSafeHash.get(key);
        }
        if (safeKey == null) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                key.updateDiskCacheKey(messageDigest);
                safeKey = QFCommonUtils.sha256BytesToHex(messageDigest.digest());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            synchronized (loadIdToSafeHash) {
                loadIdToSafeHash.put(key, safeKey);
            }
        }
        return safeKey;
    }
}
