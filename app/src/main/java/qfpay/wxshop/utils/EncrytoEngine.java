package qfpay.wxshop.utils;

import qfpay.wxshop.utils.MobAgentTools;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.util.Log;

public class EncrytoEngine {
	
	/** 
	 * 加载add.so库，实现EncrytoEngine.java中的本地方法
	 */
	 static
	    {
	        try
	        {
	            System.loadLibrary("enc-jni");
	        }
	        catch(Throwable e)
	        {
	            Log.e("JNI", e.toString());
	            throw new RuntimeException(e);
	        }
	    }
    
	/**
	 * 使用公钥加密aes密钥
	 * @param data aes密钥
	 * @return
	 */
    public native String  pubenc(byte[] data);
    
    
    /**
     * 使用aes密钥对数据进行加密
     * @param inputData 需要加密的数据
     * @param key aes密钥
     * @return
     */
    public native byte[] pack(byte[] inputData, byte[] key, int flag);
    
    /**
     * 使用aes密钥对数据进行解密
     * @param inputData 需要解密的数据
     * @param key aes密钥
     * @return
     */
    public native byte[] unpack(byte[] inputData, byte[] key);
    
//    String data = "{aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
//    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
//    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
//    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
//    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
//    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa}";
//    
//    String encData = "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";
//    
    
    /**
     * 使用aes密钥对数据进行加密
     * @param inputData 需要加密的数据
     * @param key aes密钥
     * @return
     */
    public byte[] aesEnc(byte[] inputData, byte[] key, int flag){
    	
    	return pack(inputData, key, flag);
    	
//    	return pack(inputData ,util.HexStringToByteArray("9AE7E06F8FFDB6CEA71F1973B61F8817D250CE68DE783D1D1D313D389659E67F"));
    }
    
    /**
     * 使用aes密钥对数据进行解密
     * @param inputData 需要解密的数据
     * @param key aes密钥
     * @return
     */
    public byte[] aesDec(byte[] inputData, byte[] key){
    	
    	return unpack(inputData, key);
    }
    
    
    /**
     * 使用公钥加密aes密钥
     * @param aesKey aes密钥
     * @return
     */
	public String encAesKey(byte[] aesKey){
		
		if(aesKey == null){
			return null;
		}
		return pubenc(aesKey);
	}
	
	/**
	 * 得到aes密钥
	 * @return
	 */
	public byte[] getAesKey(){
	    try {
	        KeyGenerator kg = KeyGenerator.getInstance("AES");
	        kg.init(256);//要生成多少位，只需要修改这里即可128, 192或256
	        SecretKey sk = kg.generateKey();
	        return sk.getEncoded();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
}
