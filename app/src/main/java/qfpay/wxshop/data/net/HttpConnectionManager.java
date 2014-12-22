package qfpay.wxshop.data.net;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpConnectionManager {
	private static HttpParams httpParams;
	private static ClientConnectionManager connectionManager;

	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 30;
	/**
	 * 获取连接的最大等待时间
	 */
	public final static int WAIT_TIMEOUT = 5000;
	/**
	 * 每个路由最大连接数
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 10;
	/**
	 * 连接超时时间
	 */
	public final static int CONNECT_TIMEOUT = 10000;
	/**
	 * 读取超时时间
	 */
	public final static int READ_TIMEOUT = 50000;

	static {
		initHttpClient();
	}
	
	public static void initHttpClient(){
		httpParams = new BasicHttpParams();
		// 设置最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams,
				MAX_TOTAL_CONNECTIONS);
		// 设置获取连接的最大等待时间
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
		// 设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(
				MAX_ROUTE_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		// 设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		// 设置读取超时时间
		HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		SSLSocketFactory.getSocketFactory().setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		SSLSocketFactory sf = null;
		try{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
	        trustStore.load(null, null);  
	        sf = new SSLSocketFactoryEx(trustStore); 
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
	        registry.register(new Scheme("https", sf, 443));
		}catch(Exception e){
			e.printStackTrace();
			registry.register(new Scheme("https", SSLSocketFactory
			.getSocketFactory(), 443));
		}
		
		connectionManager = new ThreadSafeClientConnManager(httpParams,
				registry);
	}
	 
	public static HttpClient getHttpClient() {
//		return new DefaultHttpClient(connectionManager, httpParams);
		return new DefaultHttpClient();
	}
	
	public static HttpClient getDifferentHttpClient(){
		
		HttpParams httpParams = new BasicHttpParams();
		// 设置最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams,
				MAX_TOTAL_CONNECTIONS);
		// 设置获取连接的最大等待时间
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
		// 设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(
				MAX_ROUTE_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		// 设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		// 设置读取超时时间
		HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		SSLSocketFactory.getSocketFactory().setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		SSLSocketFactory sf = null;
		try{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
	        trustStore.load(null, null);  
	        sf = new SSLSocketFactoryEx(trustStore); 
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
	        registry.register(new Scheme("https", sf, 443));
		}catch(Exception e){
			e.printStackTrace();
			registry.register(new Scheme("https", SSLSocketFactory
			.getSocketFactory(), 443));
		}
		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(httpParams,
				registry);
		
		
		return new DefaultHttpClient(connectionManager, httpParams);
	}
}
