package com.wd.module.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wd.module.http.google.GoogleDocumentParser;
import com.wd.util.CustomDnsResolver;


/**
 * 连接池管理类，支持https协议
 * @author Administrator
 *
 */
@Service("httpConnectionManager")
public class HttpConnectionManager {
	
	private static final Logger log=Logger.getLogger(HttpConnectionManager.class);
	
	@Autowired
	public GoogleDocumentParser googleDocumentParser;
	
	RequestConfig requestConfig;
	
	@PostConstruct
    public void init() {
		
//		RequestConfig requestConfig = null;
		RequestConfig.Builder builder = RequestConfig
				.custom()
				.setCookieSpec("best-match")
				.setExpectContinueEnabled(true)
				.setStaleConnectionCheckEnabled(true)
				.setTargetPreferredAuthSchemes(
						Arrays.asList(new String[] { "NTLM", "Digest" }))
				.setProxyPreferredAuthSchemes(
						Arrays.asList(new String[] { "Basic" }))
				.setSocketTimeout(10000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000);
		requestConfig = builder.build();
    }
	
	/**
	 * 使用httpclient连接池
	 * @param proxyInfo 
	 */
	public CloseableHttpClient getClient(String ip) {
		BasicCookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder clientBuilder = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(requestConfig);
		clientBuilder.setDnsResolver(new CustomDnsResolver(ip));
		
		// 全部信任 不做身份鉴定
		SSLContextBuilder builder = new SSLContextBuilder();
		SSLConnectionSocketFactory sslsf = null;
        try {
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
	        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
	                .register("http", new PlainConnectionSocketFactory())
	                .register("https", sslsf)
	                .build();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
        clientBuilder.setSSLSocketFactory(sslsf);
       
        
		CloseableHttpClient httpClient = clientBuilder.build();
		return httpClient;
	}
	
	 /**
	  * get请求查询
	  * */
	public HttpGet getHttpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Accept", "*/*");
		httpGet.addHeader("Accept-Language","zh-CN,zh;q=0.8,fr;q=0.6,pt;q=0.4,en;q=0.2");
		httpGet.addHeader("Connection", "keep-alive");
		// httpGet.addHeader("User-Agent",
		// "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
		// httpGet.addHeader("cookie", cookie);
		// httpGet.addHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		// httpGet.addHeader("accept-encoding","gzip, deflate, br");
		// httpGet.addHeader("accept-language","zh-CN,zh;q=0.8");
		// httpGet.addHeader("cache-control","max-age=0");
		// httpGet.addHeader("cookie","GSP=A=6QlFYg:CPTS=1502517808:LM=1502517808:S=h1pRkg8kzgsLNRHx; NID=109=GKKscoE2CxNOGmRjPNaSsJDn3ZhNyFHAoZ_CYZlgzTnu1tIesiE5NLoqr2_u5S6BkzvKGX8MNORuiRtol0Lq1Jzy8GNB1X_4V2MgXqmBdh9vTBYEHQevX4BncjOxARZQ");
		httpGet.addHeader("referer", "http://scholar.google.com/");
		// httpGet.addHeader("upgrade-insecure-requests","1");
		// httpGet.addHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
		return httpGet;
	}
	 
	public QueryResult query(String url,String type,CloseableHttpClient client) {
		HttpGet httpGet = getHttpGet(url);
		CloseableHttpResponse resp = null;
		String result = null;
		long start = 0, end=0;
		try {
			start = System.currentTimeMillis();
			resp = client.execute(httpGet);
			int status = getStatus(resp);
			if (status == 200) {
				result = new HtmlResponseHandler().handleResponse(resp);
			} else {
				result = null;
			}
		}  catch (Exception e) {
			log.info("无法链接代理ip");
		} finally {
			if (resp != null) {
                try {
                    resp.close();
                } catch (IOException localIOException1) {
                }
            }
			httpGet.releaseConnection();
		}
		end = System.currentTimeMillis();
		return new QueryResult(end-start , result);
	}
	
	private static int getStatus(HttpResponse resp) {
		StatusLine statusLine = resp.getStatusLine();
		return statusLine.getStatusCode();
	}
	
}
