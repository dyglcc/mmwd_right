package qfpay.wxshop.data.net;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.EncrytoEngine;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.app.Activity;

public class LMHttpEngine extends Activity {
	

	private  boolean isIgnoreReturn;
	
	public void setIgnoreReturn(boolean flag){
		isIgnoreReturn = flag;
	}
	
	public String post(HashMap<String, String> parameters, int retryMax, int EncryType)
			throws Exception {
		//是否需要加解密
		String isNeedEncryString = parameters.get("isNeedEncry");
		boolean isNeedEncry = true;
		if(isNeedEncryString != null && isNeedEncryString.equals("false")){
			isNeedEncry = false;
		}
		
		String url = parameters.get("url");
		T.d(url);
		parameters.remove("url");

		// Construct data
		StringBuilder dataBfr = new StringBuilder();
		dataBfr.append(parameters.get("data"));

		EncrytoEngine engine = new EncrytoEngine();
		byte[] retDate = null;
		String retString = null;
		int retryCount = 0;
		byte[] encryKey = WxShopApplication.DEFAULT_KEY.getBytes();
		if(EncryType == 1){
			encryKey = WxShopApplication.app.aeskey;
		}
		
		do {
			if (retryCount > retryMax) {
				break;
			}
//			java.util.Date utilDate = new java.util.Date();
//			SimpleDateFormat formatter = new SimpleDateFormat("ss:SSS");
//			long time = System.currentTimeMillis();

			InputStream is = null;
			HttpClient httpClient = null;
			HttpPost httpPost = null;
			try{
				httpClient =  HttpConnectionManager.getHttpClient();
				
				httpPost = new HttpPost(url);
				
				//如果要加密数据的API访问
				if(isNeedEncry){
					byte[] jsonByte = engine.pack(dataBfr.toString().getBytes(), encryKey, EncryType);
					httpPost.setEntity(new ByteArrayEntity(jsonByte));
				}else{//如果是非加密数据的API访问
					httpPost.setEntity(new StringEntity(parameters.get("data"), "UTF-8"));
				}
				
				if (WxShopApplication.app.cookie != null
						&& !WxShopApplication.app.cookie.equals("")) {
					httpPost.addHeader("Cookie", WxShopApplication.app.cookie);
				}
				String userAgent = "Android/" + Utils.getOSVerison(getApplicationContext())
				+ " HttpClient/4.2.2 QPOS/" + Utils.getAppVersionString(getApplicationContext());
				httpPost.addHeader("User-Agent", userAgent);
				T.d("Posting data");
				
				HttpResponse httpResponse  = httpClient.execute(httpPost);

				if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
//					utilDate.setTime(System.currentTimeMillis() - time);
//					T.d("API Time:" + formatter.format(utilDate));
					 HttpEntity entity = httpResponse.getEntity();
					 is = entity.getContent();
					
					int dataLength = (int)entity.getContentLength();
					if (dataLength == -1) {
						ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
						int ch;
						while (-1 != (ch = is.read())) {
							bStrm.write(ch);
						}
						retDate = bStrm.toByteArray();
						bStrm.close();
					} else {
						retDate = new byte[dataLength];
						new DataInputStream(is).readFully(retDate);
					}

					Header[] header = httpResponse.getHeaders("Set-Cookie");
					for (int i = 0; i < header.length; i++) {
						if (header[i].getName().equalsIgnoreCase("Set-Cookie")) {
							String cookie = header[i].getValue();
							if (cookie != null) {
								if(!isIgnoreReturn){
									WxShopApplication.app.cookie = cookie;
								}
							}
						}
					}
					
				}
			}catch(Exception e){e.printStackTrace();}
			finally{
				if (is != null){  
					try{
						is.close ();
					}catch (IOException e){  
						e.printStackTrace ();  
					}
				}
			}

			if (retDate != null) {
				break;
			}else{
				httpPost.abort();
			}
			retryCount++;
		} while (true);

		
		if(retDate != null){
			//如果是加密后的数据
			if(isNeedEncry){
				retString = new String(engine.unpack(retDate, encryKey));
			}else{//如果是非加密返回的数据
				retString = new String(retDate);
			}
		}
		return retString;
	}

	
	public static String testCdn(HashMap<String, String> parameters, int retryMax, int EncryType)
	throws Exception {
		
		String url = parameters.get("url");
		parameters.remove("url");
		
		// Construct data
		StringBuilder dataBfr = new StringBuilder();
		dataBfr.append(parameters.get("data"));
		
		EncrytoEngine engine = new EncrytoEngine();
		byte[] retDate = null;
		String retString = null;
		int retryCount = 0;
		byte[] encryKey = WxShopApplication.DEFAULT_KEY.getBytes();
		if(EncryType == 1){
			encryKey = WxShopApplication.app.aeskey;
		}
		
		do {
			if (retryCount > retryMax) {
				break;
			}
		
			InputStream is = null;
			HttpClient httpClient = null;
			HttpPost httpPost = null;
			try{
				httpClient =  HttpConnectionManager.getDifferentHttpClient();
				
				httpPost = new HttpPost(url);
				
				byte[] jsonByte = engine.pack(dataBfr.toString().getBytes(), encryKey, EncryType);
				httpPost.setEntity(new ByteArrayEntity(jsonByte));
				
				HttpResponse httpResponse  = httpClient.execute(httpPost);
		
				if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
					 HttpEntity entity = httpResponse.getEntity();
					 is = entity.getContent();
					
					int dataLength = (int)entity.getContentLength();
					if (dataLength == -1) {
						ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
						int ch;
						while (-1 != (ch = is.read())) {
							bStrm.write(ch);
						}
						retDate = bStrm.toByteArray();
						bStrm.close();
					} else {
						retDate = new byte[dataLength];
						new DataInputStream(is).readFully(retDate);
					}
		
				}
			}catch(Exception e){e.printStackTrace();}
			finally{
				if (is != null){  
					try{
						is.close ();
					}catch (IOException e){  
						e.printStackTrace ();  
					}
				}
			}
		
			if (retDate != null) {
				break;
			}else{
				httpPost.abort();
				httpClient.getConnectionManager().shutdown();
			}
			retryCount++;
		} while (true);
		
		if(retDate != null){
			//如果是加密后的数据
			retString = new String(engine.unpack(retDate, encryKey));
		}
		
		return retString;
		
		
	}

} 
