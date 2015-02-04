package qfpay.wxshop.data.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.os.Bundle;

/**
 * http请求
 */
public class HttpNet {
	private HttpPost post = null;
	private HttpGet get = null;
	private HttpDelete delete = null;
	private HttpPut put = null;
	/** 是否用户取消 */
	private boolean canceled = false;

	/**
	 * post for qiniu
	 * 
	 * @param requestUrl
	 *            请求的路径
	 * @param params
	 *            请求参数
	 * @return
	 * 
	 */
	public Bundle postQiNiu(String requestUrl, Map<String, Object> params) {
		String tokenString = (String) params.get("token");
		String key = (String) params.get("key");
		// file name
		String fileUrl = (String) params.get("fileUrl");
		Bundle returnData = new Bundle();
		try {

			post = new HttpPost(requestUrl);

			MultipartEntity reqEntity = new MultipartEntity();
			// StringBody extraCrc32 = new StringBody("0");
			// reqEntity.addPart("crc32",extraCrc32);

			StringBody token = new StringBody(tokenString);
			reqEntity.addPart("token", token);

			StringBody keyString = new StringBody(key);
			reqEntity.addPart("key", keyString);

			File srcFile = new File(fileUrl);
			File tempFile = File.createTempFile("upload", ConstValue.expansion,
					null);

			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(tempFile);
			byte[] buff = new byte[1024];
			int read = -1;
			while ((read = fis.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}
			fis.close();
			fos.close();

			FileBody filebody = new FileBody(tempFile);
			reqEntity.addPart("file", filebody);

			post.setEntity(reqEntity);
			post.setHeader("User-Agent",
					com.qiniu.upload.tool.Config.USER_AGENT);
			// 设置Cookie
			// post.addHeader("Cookie", "sessionid = " +
			// WxShopApplication.dataEngine.getcid());

			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);

			HttpResponse response = client.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();

			T.d("responseCode is:" + responseCode);
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);

				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpPost:response" + data);
			} else {
				// 成功连接上server但是返回的不是200
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
				T.i("httpPost:response'responseCode is " + responseCode);
			}
		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
			T.i("httpPost:response" + e.toString());
		} catch (Exception e) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
			T.i("httpPost:response" + e.toString());
		} finally {
			if (post != null) {
				// post.abort();
				post = null;
			}
		}
		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		return returnData;

	}

	/**
	 * 
	 * @param requestUrl
	 *            请求的路径
	 * @param params
	 *            请求参数
	 * @return
	 * 
	 */
	public Bundle postSingleFile(String requestUrl, Map<String, Object> params) {

		// category：分类 1. 用户凭证 2. 渠道凭证 3. 喵喵微店
		// source：文件来源 1. web 2. app 3. 喵喵微店
		// tag：图片标签(avatar: 头像, qmm: 喵喵微店, showcase: 商品/服务展示图片)
		// file name
		String fileUrl = (String) params.get("fileUrl");
		Bundle returnData = new Bundle();
		String tag = (String) params.get("tag");
		String category = (String) params.get("category");
		String source = (String) params.get("source");
		try {

			post = new HttpPost(requestUrl);

			MultipartEntity reqEntity = new MultipartEntity();

			StringBody token = new StringBody(category);
			reqEntity.addPart("category", token);

			StringBody keyString = new StringBody(source);
			reqEntity.addPart("source", keyString);

			StringBody tagbody = new StringBody(tag);
			reqEntity.addPart("tag", tagbody);

			File srcFile = new File(fileUrl);
			File tempFile = File.createTempFile("upload", ConstValue.expansion,
					null);

			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(tempFile);
			byte[] buff = new byte[1024];
			int read = -1;
			while ((read = fis.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}
			fis.close();
			fos.close();

			FileBody filebody = new FileBody(tempFile);
			reqEntity.addPart("file", filebody);

			post.setEntity(reqEntity);
			post.setHeader("User-Agent",
					WxShopApplication.dataEngine.getUserAgent());
			// T.i("strart postRequest .." + requestUrl + ".." +
			// param.toString());

			// 设置Cookie
			post.addHeader("Cookie", "sessionid = "
					+ WxShopApplication.dataEngine.getcid());

			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);

			HttpResponse response = client.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();

			T.d("responseCode is:" + responseCode);
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);

				// 获取并设置Cookie
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// if (response.getAllHeaders()[i].getName().equalsIgnoreCase(
				// "Set-Cookie")) {
				// String cookie = response.getAllHeaders()[i].getValue();
				// WxShopApplication.app.cookie = cookie;
				// break;
				// }
				// }

				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpPost:response" + data);
			} else {
				// 成功连接上server但是返回的不是200
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
				T.i("httpPost:response'responseCode is " + responseCode);
			}
		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
			T.i("httpPost:response" + e.toString());
		} catch (Exception e) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
			T.i("httpPost:response" + e.toString());
		} finally {
			if (post != null) {
				// post.abort();
				post = null;
			}
		}
		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		return returnData;

	}

	/**
	 * post请求
	 * 
	 * @param requestUrl
	 *            请求的路径
	 * @param params
	 *            请求参数
	 * @return
	 */
	public Bundle postRequest(String requestUrl, Map<String, Object> params) {
		Bundle returnData = new Bundle();
		List<NameValuePair> param = null;
		try {

			param = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				for (Entry<String, Object> entry : params.entrySet()) {
					param.add(new BasicNameValuePair(entry.getKey(), String
							.valueOf(entry.getValue())));
				}
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(param,
					HTTP.UTF_8);
			post = new HttpPost(requestUrl);
			post.setEntity(entity);

			// zhufeng 从setHeader 改成 addHeader
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			T.i("strart postRequest .." + requestUrl + ".." + param.toString());

			// 设置Cookie
			post.addHeader("Cookie", "sessionid="
					+ WxShopApplication.dataEngine.getcid());

            T.i("push-- session id" + WxShopApplication.dataEngine.getcid());
			post.setHeader("User-Agent",
					WxShopApplication.dataEngine.getUserAgent());
            T.i("push-- user agent" + WxShopApplication.dataEngine.getUserAgent());
			disableConnectionReuseIfNecessary();
			HttpClient client = HttpConnectionManager.getHttpClient();

			HttpResponse response = client.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();

			T.d("responseCode is:" + responseCode);
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);

				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpPost:response" + data);
			} else {
				// 成功连接上server但是返回的不是200
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
				T.i("httpPost:response'responseCode is " + responseCode);
			}
		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (UnknownHostException e) {
			// 无法连接
			String err = e.toString();
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_UNKNOWHOST_EXCEPTION);
			T.i("httpPost:response" + err);

		} catch (SocketTimeoutException e) {
			// 超时
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
			T.i("httpPost:response" + e.toString());
		} catch (Exception e) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
			T.i("httpPost:response" + e.toString());
		}
		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		return returnData;

	}

	/**
	 * post请求
	 * 
	 * @param requestUrl
	 *            请求的路径
	 * @param params
	 *            请求参数
	 * @return
	 */
	public Bundle putRequest(String requestUrl, Map<String, Object> params) {
		Bundle returnData = new Bundle();
		List<NameValuePair> param = null;
		try {

			param = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				for (Entry<String, Object> entry : params.entrySet()) {
					param.add(new BasicNameValuePair(entry.getKey(), String
							.valueOf(entry.getValue())));
				}
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(param,
					HTTP.UTF_8);
			put = new HttpPut(requestUrl);
			put.setEntity(entity);

			// zhufeng 从setHeader 改成 addHeader
			put.addHeader("Content-Type", "application/x-www-form-urlencoded");
			T.i("strart putRequest .." + requestUrl + ".." + param.toString());

			// 设置Cookie
			put.addHeader("Cookie",
					"sessionid=" + WxShopApplication.dataEngine.getcid());

			put.setHeader("User-Agent",
					WxShopApplication.dataEngine.getUserAgent());
			// disableConnectionReuseIfNecessary();
			HttpClient client = HttpConnectionManager.getHttpClient();

			HttpResponse response = client.execute(put);

			int responseCode = response.getStatusLine().getStatusCode();

			T.d("responseCode is:" + responseCode);
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);

				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpPost:response" + data);
			} else {
				// 成功连接上server但是返回的不是200
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
				T.i("httpPost:response'responseCode is " + responseCode);
			}
		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
			T.i("httpPost:response" + e.toString());
		} catch (Exception e) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
			T.i("httpPost:response" + e.toString());
		}
		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		return returnData;

	}

	/**
	 * 停止网络
	 */
	public void stop() {
		try {
			canceled = true;
			if (post != null) {
				post.abort();
				post = null;
			}
			// // 当用httpcon 请求或者发送文件的时候
			// if (con != null) {
			// con.getOutputStream().close();
			// // con.getInputStream().close();
			// con = null;
			// }
			// if (httpsConn != null) {
			// httpsConn.getOutputStream().close();
			// // httpsConn.getInputStream().close();
			// httpsConn = null;
			// }
		} catch (Exception ex) {
			T.e(ex);
		}
	}

	/**
	 * 发送GET请求
	 * 
	 * @param path
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @return 请求结果, true为请求成功;false为请求失败
	 * @throws Exception
	 */
	public Bundle getRequest(String requestUrl, Map<String, Object> params) {
		Bundle returnData = new Bundle();
		try {

			get = new HttpGet();

			String url = getSendGetMethodURL(requestUrl, params);
			T.i("get request url=" + url);
			URI uri = new URI(url);
			get.setURI(uri);

			// 设置Cookie
			get.addHeader("Cookie", "sessionid = "
					+ WxShopApplication.dataEngine.getcid());

			get.setHeader("User-Agent",
					WxShopApplication.dataEngine.getUserAgent());

			HttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = null;

			response = httpClient.execute(get);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);
				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpGet:response" + data);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
			}

		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			T.i("httpGet:response" + e.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
		} catch (ConnectTimeoutException e) {
			// 超时
			T.i("httpGet:response" + e.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
		} catch (Exception ex) {
			T.i("httpGet:response" + ex.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
		} finally {
			if (get != null) {
				get = null;
			}
		}

		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		returnData.putString(ConstValue.REQUEST_URL, requestUrl);
		return returnData;
	}

	/**
	 * 发送DELETE请求
	 * 
	 * @param path
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @return 请求结果, true为请求成功;false为请求失败
	 * @throws Exception
	 */
	public Bundle delRequest(String requestUrl, Map<String, Object> params) {
		Bundle returnData = new Bundle();
		try {

			delete = new HttpDelete();

			String url = getSendGetMethodURL(requestUrl, params);
			T.i("get request url=" + url);
			URI uri = new URI(url);
			delete.setURI(uri);

			// 设置Cookie
			delete.addHeader("Cookie", "sessionid = "
					+ WxShopApplication.dataEngine.getcid());

			delete.setHeader("User-Agent",
					WxShopApplication.dataEngine.getUserAgent());

			// disableConnectionReuseIfNecessary();
			// HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpClient = new DefaultHttpClient();
			// // 请求超时
			// httpClient.getParams().setParameter(
			// CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
			// // 读取超时
			// httpClient.getParams().setParameter(
			// CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);

			HttpResponse response = null;

			response = httpClient.execute(delete);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);
				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				// // 获取并设置Cookie
				// for (int i = 0; i < response.getAllHeaders().length; i++) {
				// if (response.getAllHeaders()[i].getName().equalsIgnoreCase(
				// "Set-Cookie")) {
				// String cookie = response.getAllHeaders()[i].getValue();
				// WxShopApplication.app.cookie = cookie;
				// break;
				// }
				// }

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpGet:response" + data);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
			}

		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			T.i("httpGet:response" + e.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
		} catch (ConnectTimeoutException e) {
			// 超时
			T.i("httpGet:response" + e.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
		} catch (Exception ex) {
			T.i("httpGet:response" + ex.toString());
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
		} finally {
			if (delete != null) {
				delete = null;
			}
		}

		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		returnData.putString(ConstValue.REQUEST_URL, requestUrl);
		return returnData;
	}

	/**
	 * 设置GET请求的URL
	 * 
	 * @param reqKey
	 * @return
	 * @throws Exception
	 */
	protected String getSendGetMethodURL(String requestUrl,
			Map<String, Object> params) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(requestUrl);// 组拼URL路径
		if (params != null && !params.isEmpty()) {
			sb.append('?');
			for (Entry<String, Object> entry : params.entrySet()) {
				// 要求对字符进行编码
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(
								String.valueOf(entry.getValue()), HTTP.UTF_8))
						.append('&');
			}
			String devicename = Utils.getDeviceName();
			if(devicename ==null){
				devicename = "";
			}
			devicename = devicename.replaceAll(" ", "");
			sb.append("osver="+Utils.getOSVerison(WxShopApplication.app));
			sb.append("&appver="+Utils.getAppVersionString(WxShopApplication.app));
			sb.append("&platform=android&device="+devicename);
//			sb.deleteCharAt(sb.length() - 1);
		}
		T.i("httpGet:" + sb.toString());
		return sb.toString();
	}

	/**
	 * post请求
	 * 
	 * @param requestUrl
	 *            请求的路径
	 * @param params
	 *            请求参数
	 * @return
	 */
	public Bundle postFengXiangRequest(String requestUrl,
			Map<String, Object> params) {
		Bundle returnData = new Bundle();
		try {

			JSONObject json = (JSONObject) params.get("json");

			StringEntity s = new StringEntity(json.toString(), "utf-8");
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			post = new HttpPost(requestUrl);
			post.setEntity(s);
			String appid = (String) params.get("appid");
			String appkey = (String) params.get("appkey");
			post.addHeader("Content-Type", "application/json");
			// post.addHeader("X-Feng-Xiang-Application-Id",
			// "514c0628166a412f0d8ac54c");
			// post.addHeader("X-Feng-Xiang-REST-API-Key",
			// "51a18afa30e4469eb89fc0b8e221ab50");

			post.addHeader("X-Feng-Xiang-Application-Id", appid);
			post.addHeader("X-Feng-Xiang-REST-API-Key", appkey);

			HttpClient client = HttpConnectionManager.getHttpClient();

			HttpResponse response = client.execute(post);

			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 201) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_SUCCESS);

				String data = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				returnData.putString(ConstValue.NET_RETURN_DATA, data);
				T.i("httpPost:response" + data);
			} else {
				// 成功连接上server但是返回的不是200
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_HTTP);
				returnData.putInt(ConstValue.NET_RETURN_HTTP_ERROR,
						responseCode);
				if (500 == responseCode) {
					String failData = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					int ind = failData.indexOf("{");
					if (ind > 0)
						failData = failData.substring(ind);
					returnData.putString(ConstValue.NET_RETURN_DATA, failData);
					T.i("httpGet:response" + failData);
				}
				T.i("httpPost:response'responseCode is " + responseCode);
			}
		} catch (SocketException e) {
			// 无法连接
			String err = e.toString();
			if (err.indexOf("timed out") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_TIMEOUT);
			} else if (err.indexOf("refused") > 1) {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_ACCESS_REFUSE);
			} else {
				returnData.putInt(ConstValue.NET_RETURN,
						ConstValue.NET_RETURN_ERROR);
			}
			T.i("httpPost:response" + err);
		} catch (SocketTimeoutException e) {
			// 超时
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_TIMEOUT);
			T.i("httpPost:response" + e.toString());
		} catch (Exception e) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_ERROR);
			T.i("httpPost:response" + e.toString());
		}
		if (canceled) {
			returnData.putInt(ConstValue.NET_RETURN,
					ConstValue.NET_RETURN_CANCEL);
		}
		return returnData;

	}

	private void disableConnectionReuseIfNecessary() {
		// Work around pre-Froyo bugs in HTTP connection reuse.
		// if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO)
		// {
		System.setProperty("http.keepAlive", "false");

		// }
	}

}
