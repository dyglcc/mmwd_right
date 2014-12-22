package qfpay.wxshop.image.net;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.CommonJsonBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.OkApacheClient;
import qfpay.wxshop.data.net.RetrofitWrapper;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

public class ImageNetProcesser {
	private OkHttpClient mClient;
	private String       mTag;
	
	public ImageNetProcesser(String tag) {
		this.mTag = tag;
		setupClient();
	}
	
	private void setupClient() {
		if (mClient == null) {
			mClient = new OkHttpClient();
			mClient.setConnectTimeout(30, TimeUnit.SECONDS);
			mClient.setWriteTimeout(60, TimeUnit.SECONDS);
			mClient.setReadTimeout(60, TimeUnit.SECONDS);
		}
	}
	
	public String uploadToServer(FileBody fileBody) throws IOException {
		String url = "";
		if(WxShopApplication.app.miaomiaoUploadServer!=null && !WxShopApplication.app.miaomiaoUploadServer.equals("")){
			url = WxShopApplication.app.miaomiaoUploadServer;
		}else{
			url = WDConfig.getInstance().getQFUploadServer(ConstValue.HTTP_GET);
		}
		
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("file", fileBody);
		entity.addPart("category", new StringBody("3"));
		entity.addPart("source", new StringBody("3"));
		entity.addPart("tag", new StringBody("qmm"));
		
		HttpPost postRequest = new HttpPost(url);
		postRequest.setEntity(entity);
		postRequest.addHeader("User-Agent", WxShopApplication.dataEngine.getUserAgent());
		postRequest.addHeader("Cookie", "sessionid = " + WxShopApplication.dataEngine.getcid());
		
		OkApacheClient client = new OkApacheClient(mClient);
		HttpResponse response = client.execute(postRequest, mTag);
		
		if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
			Gson gson = new Gson();
			ImageUploadResponseWrapper result = gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), ImageUploadResponseWrapper.class);
			if (result.getRespcd().equals(RetrofitWrapper.SUCCESS_CODE)) {
				return result.data.url;
			}
		}
		return "";
	}
	
	public void cancel() {
		try {
			mClient.cancel(mTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class ImageUploadResponseWrapper extends CommonJsonBean {
		private static final long serialVersionUID = 1L;
		
		ImageUploadResponse data;
	}
    	
	public static class ImageUploadResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		
		String category;
		String tag;
		String url;
		String name;
	}
}
