package com.wd.module.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;

import org.apache.cxf.common.util.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyQueue;
import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.task.thread.ValidateThread;
import com.wd.task.validate.ValidateController;
import com.wd.task.validate.ValidateIP;
import com.wd.util.Comm;

/**
 * 连接池管理类，支持https协议
 * @author Administrator
 *
 */
@Component
public class HttpConnectionManager {
	
	
	PoolingHttpClientConnectionManager cm = null;
	
	Thread addIpThread = null;
	
	@Autowired
	public DocumentParser googleDocumentParser;
	
	@Autowired
	public ProxyDaoI proxyDao;
	
	@Autowired
	public ProxyQueue proxyQueueModule;
	
	@Autowired
	public ValidateIP validate;
	
	@PostConstruct
    public void init() {
		System.out.println("init---->HttpConnectionManager");
		cm = new PoolingHttpClientConnectionManager();  
        cm.setMaxTotal(Comm.MAX_TOTAL_CONNECTIONS);  
        cm.setDefaultMaxPerRoute(Comm.MAX_ROUTE_CONNECTIONS); 
        //初始化IP验证	
        ValidateController validPass = new ValidateController(validate,proxyDao,Comm.Type_Pass);
        Thread threadPass = new Thread(validPass,"初始化：验证已可用ip");
        threadPass.start();
        
        ValidateController addIp = new ValidateController(validate,proxyDao,Comm.Type_Pass);
        addIpThread = new Thread(addIp,"更新ip至队列");
    }
	
	/**
	 * 使用httpclient连接池
	 * @param proxyInfo 
	 */
	public CloseableHttpClient getHttpClient() {  
		BasicCookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder clientBuilder = HttpClients.custom()
				.setDefaultCookieStore(cookieStore);
		
//		RequestConfig requestConfig = null;
//		RequestConfig.Builder builder = RequestConfig
//			.custom()
//			.setCookieSpec("best-match")
//			.setExpectContinueEnabled(true)
//			.setStaleConnectionCheckEnabled(true)
//			.setTargetPreferredAuthSchemes(
//					Arrays.asList(new String[] { "NTLM", "Digest" }))
//			.setProxyPreferredAuthSchemes(
//					Arrays.asList(new String[] { "Basic" }))
//			.setSocketTimeout(Comm.READ_TIMEOUT)
//			.setConnectTimeout(Comm.CONNECT_TIMEOUT)
//			.setConnectionRequestTimeout(Comm.REQYEST_TIMEOUT);
//		requestConfig = builder.build();
		
		
		clientBuilder.setConnectionManager(cm)
//		.setDefaultRequestConfig(requestConfig);
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
		if ((StringUtils.isEmpty(proxyInfo.getIp())) || (proxyInfo.getIp().equals("127.0.0.1"))
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
	public String query(String url,String type,Boolean priority) {
		ProxyInfo proxyInfo = getProxy(priority);
		if(proxyInfo == null) return null;
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
			long end = System.currentTimeMillis();
			result = validateResult(result, type, proxyInfo,end - start);
		} catch (SSLException e) {
			System.out.println("需要证书");
		} catch (Exception e) {
			System.out.println("无法链接代理ip");
			if(!addIpThread.isAlive()) {
				addIpThread.start();
			}
		} finally {
			if (resp != null)
				try {
					resp.close();
				} catch (IOException localIOException1) {
				}
			httpGet.releaseConnection();
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		return result;
	}
	 
	private static int getStatus(HttpResponse resp) {
		StatusLine statusLine = resp.getStatusLine();
		return statusLine.getStatusCode();
	}
	
	/**
	 * 验证httpclient返回结果是否正确
	 * @param queryResult
	 * @param type
	 * @return
	 */
	private String validateResult(String queryResult,String type ,ProxyInfo proxyInfo, long speed) {
		String result = googleDocumentParser.getResult(queryResult, type);
		if(result == null || result.equals("{\"count\":\"0\",\"timeMap\":{},\"rows\":[]}")) {
			proxyInfo.setErrCount(1);
			System.out.println(queryResult);
			result = null;
		} else if(result.equals("出现验证码")) {
			proxyInfo.setSuccessCount(-1);
			result = null;
		} 
		if(result != null) {
			proxyInfo.setErrCount(-1);
			proxyInfo.setSuccessCount(1);
			proxyInfo.setSpeed(speed);
			proxyQueueModule.returnProxy(proxyInfo);
		} else {	//重新从数据库获取ip
			if(!addIpThread.isAlive()) {
				addIpThread.start();
			}
		}
		proxyDao.updateProxy(proxyInfo);
		return result;
	}
	
	/**
	  * 从队列中拿取一个ProxyInfo（如果priority为true就遍历整个队列拿取优先级最高的）
	  * @param priority  是否使用优先级最高的
	  * @return
	  */
	private ProxyInfo getProxy(Boolean priority) {
		ProxyInfo proxyInfo = null;
		proxyInfo = proxyQueueModule.pollProxy();
		if(priority) {
			for (int i = 0; i < proxyQueueModule.getProxySize(); i++) {
				ProxyInfo proxy = proxyQueueModule.peekProxy();			//返回第一个元素，不删除
				if(proxy != null && proxy.getLevel() > proxyInfo.getLevel()) {
					proxyQueueModule.returnProxy(proxyInfo);
					proxyInfo = proxyQueueModule.pollProxy();
				}
			}
		}
		return proxyInfo;
	}
	
}
