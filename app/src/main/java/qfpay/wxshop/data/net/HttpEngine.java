package qfpay.wxshop.data.net;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.utils.EncrytoEngine;
import qfpay.wxshop.utils.T;

public class HttpEngine {
	/** Called when the activity is first created. */

	private static X509TrustManager xtm = new X509TrustManager() {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			System.out.println("cert: " + chain[0].toString() + ", authType: "
					+ authType);

		}
	};

	static X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };

	private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();

	private static final int CONNECT_SOCKET_TIMEOUT = 30000;

	private static final int READ_SOCKET_TIMEOUT = 50000;

	public String post(HashMap<String, String> parameters, int retryMax,  int EncryType)
			throws IOException, KeyManagementException,
			NoSuchAlgorithmException, SocketTimeoutException,
			UnknownHostException, NullPointerException, TimeoutException {

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
		do {
			if (retryCount > retryMax) {
				break;
			}

			System.setProperty("http.keepAlive", "false");

			// POST data
			URL apiUrl = new URL(url);

			// Https Method
			HttpsURLConnection connection = getConnection(apiUrl);

//			if(WxShopApplication.needHost){
//				connection.addRequestProperty("HOST", WxShopApplication.DOMAIN);
//			}
			
			connection.addRequestProperty("Content-Encoding", "gzip");
			connection.addRequestProperty("Accept-Encoding", "gzip");

			connection.setConnectTimeout(CONNECT_SOCKET_TIMEOUT);
			connection.setReadTimeout(READ_SOCKET_TIMEOUT);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			if (WxShopApplication.app.cookie != null
					&& !WxShopApplication.app.cookie.equals("")) {
				connection.addRequestProperty("Cookie",
						WxShopApplication.app.cookie);
			}

			// GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
			// connection.getOutputStream());

			OutputStream wr = connection.getOutputStream();
			
			if(EncryType == 1){
				encryKey = WxShopApplication.app.aeskey;
			}
			
			byte[] jsonByte = engine.pack(dataBfr.toString().getBytes(), encryKey, EncryType);
			
			wr.write(jsonByte);
			wr.flush();
			wr.close();

			int statusCode = connection.getResponseCode();
			T.d(Integer.toString(statusCode));

			if (statusCode == HttpURLConnection.HTTP_OK) {
				T.d("Reading response");
				String cookie = connection.getHeaderField("Set-Cookie");
				if (cookie != null) {
					WxShopApplication.app.cookie = cookie;
				}

				InputStream is = connection.getInputStream();
				if (connection.getContentEncoding() != null
						&& connection.getContentEncoding().contains("gzip")) {
					is = new GZIPInputStream(is);
				}

				int dataLength = (int) connection.getContentLength();
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

			if (retDate != null) {
				break;
			}
			retryCount++;

		} while (true);
		
		if(retDate != null){
			retString = new String(engine.unpack(retDate, encryKey));
		}
		

		return retString;
	}

	/**
	 * Open an URL connection. If HTTPS, accepts any certificate even if not
	 * valid, and connects to any host name.
	 * 
	 * @param url
	 *            The destination URL, HTTP or HTTPS.
	 * @return The URLConnection.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private static HttpsURLConnection getConnection(URL url)
			throws IOException, NoSuchAlgorithmException,
			KeyManagementException, TimeoutException, SocketTimeoutException {
		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
		if (connection instanceof HttpsURLConnection) {
			// Trust all certificates
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(new KeyManager[0], xtmArray, new SecureRandom());
			SSLSocketFactory socketFactory = context.getSocketFactory();
			connection.setSSLSocketFactory(socketFactory);

			// Allow all hostnames
			connection.setHostnameVerifier(HOSTNAME_VERIFIER);

		}

		return connection;
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
	public byte[] getRequest(String requestUrl, Map<String, Object> params) throws Exception {
		byte[] returnData = null;
		HttpGet get = null;
		get = new HttpGet();

		String url = getSendGetMethodURL(requestUrl, params);
		T.i("get request url=" + url);
		URI uri = new URI(url);
		get.setURI(uri);

		HttpParams httpParameters = new BasicHttpParams();
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = null;

		response = httpClient.execute(get);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200) {
			returnData = EntityUtils.toByteArray(response.getEntity());
		}
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
			sb.deleteCharAt(sb.length() - 1);
		}
		T.i("httpGet:" + sb.toString());
		return sb.toString();
	}

}
