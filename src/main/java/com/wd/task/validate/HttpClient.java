package com.wd.task.validate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.module.http.HtmlResponseHandler;
import com.wd.module.http.QueryResult;
import com.wd.util.Comm;

@Component
public class HttpClient {
	
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36";
	public static final String BASE_PATH = "http://scholar.google.com";
	long queryTime;
	String ip;
	int port;
	private CookieStore cookieStore;
	private CloseableHttpClient client;

	
	
	PoolingHttpClientConnectionManager cm = null;
	
	@Autowired
	public ValidateIP validate;
	
	@Autowired
	public ProxyDaoI proxyDao;
	
	@PostConstruct
    public void init() {
		cm = new PoolingHttpClientConnectionManager();  
        cm.setMaxTotal(Comm.MAX_TOTAL_CONNECTIONS);  
        cm.setDefaultMaxPerRoute(Comm.MAX_ROUTE_CONNECTIONS); 
    }
	
	/**
	 * 使用httpclient连接池
	 * @param proxyInfo 
	 */
	public CloseableHttpClient getHttpClient() {  
		BasicCookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder clientBuilder = HttpClients.custom()
				.setDefaultCookieStore(cookieStore);
		
		RequestConfig requestConfig = null;
		RequestConfig.Builder builder = RequestConfig
			.custom()
			.setCookieSpec("best-match")
			.setExpectContinueEnabled(true)
			.setStaleConnectionCheckEnabled(true)
			.setTargetPreferredAuthSchemes(
					Arrays.asList(new String[] { "NTLM", "Digest" }))
			.setProxyPreferredAuthSchemes(
					Arrays.asList(new String[] { "Basic" }))
			.setSocketTimeout(Comm.READ_TIMEOUT)
			.setConnectTimeout(Comm.CONNECT_TIMEOUT)
			.setConnectionRequestTimeout(Comm.REQYEST_TIMEOUT);
		requestConfig = builder.build();
		
		
		clientBuilder.setConnectionManager(cm)
		.setDefaultRequestConfig(requestConfig);
		;
		
		
		CloseableHttpClient httpClient = clientBuilder.build();
		return httpClient;
    }
	
	 /**
	  * get请求查询
	  * */
	public HttpGet getHttpGet(String url,ProxyInfo proxyInfo) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept", "*/*");
		httpGet.addHeader("Accept-Language","zh-CN,zh;q=0.8,fr;q=0.6,pt;q=0.4,en;q=0.2");
		httpGet.addHeader("Connection", "keep-alive");
		httpGet.addHeader("referer", "http://scholar.google.com/");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
		httpGet.setConfig(getRequestConfig(proxyInfo));
		return httpGet;
	}
	 
	 /**
	 * 设置代理ip
	 * @return
	 */
	private RequestConfig getRequestConfig(ProxyInfo proxyInfo) {
		RequestConfig requestConfig = null;
		RequestConfig.Builder builder = RequestConfig
			.custom()
			.setCookieSpec("best-match")
			.setExpectContinueEnabled(true)
			.setStaleConnectionCheckEnabled(true)
			.setTargetPreferredAuthSchemes(
					Arrays.asList(new String[] { "NTLM", "Digest" }))
			.setProxyPreferredAuthSchemes(
					Arrays.asList(new String[] { "Basic" }))
			.setSocketTimeout(Comm.READ_TIMEOUT)
			.setConnectTimeout(Comm.CONNECT_TIMEOUT)
			.setConnectionRequestTimeout(Comm.REQYEST_TIMEOUT);
		if (proxyInfo == null || (StringUtils.isEmpty(proxyInfo.getIp())) || (proxyInfo.getIp().equals("127.0.0.1"))
				|| (proxyInfo.getIp().equals("localhost")))
			requestConfig = builder.build();
		else {
			requestConfig = builder.setProxy(new HttpHost(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort())))
					.build();
		}
		return requestConfig;
	}
	/**
	 * 
	 * @param url		地址
	 * @param type		返回格式
	 * @param proxyInfo	代理ip
	 * @return
	 */
	public QueryResult query(String url,String type,ProxyInfo proxyInfo) {
		HttpGet httpGet = getHttpGet(url, proxyInfo);
		long start = System.currentTimeMillis();
		CloseableHttpResponse resp = null;
		String result = null;
		try {
			CloseableHttpClient client = (CloseableHttpClient) getHttpClient();
			resp = client.execute(httpGet);
			int status = getStatus(resp);
			if (status == 200) {
				result = new HtmlResponseHandler().handleResponse(resp);
			} else {
				result = null;
			}
		} catch (SSLException e) {
		} catch (Exception e) {
		} finally {
			if (resp != null)
				try {
					resp.close();
				} catch (IOException localIOException1) {
				}
			httpGet.releaseConnection();
		}
		long end = System.currentTimeMillis();
		QueryResult qr = new QueryResult(end - start, result);
		return qr;
	}
	 
	private static int getStatus(HttpResponse resp) {
		StatusLine statusLine = resp.getStatusLine();
		return statusLine.getStatusCode();
	}

}