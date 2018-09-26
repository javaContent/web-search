package com.wd.module.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.Hosts;
import com.wd.module.http.google.GoogleDocumentParser;


@Component("httpClientModule")
public class HttpClientModule {
	
	private static final Logger log=Logger.getLogger(HttpClientModule.class);
	
	@Autowired
	public GoogleDocumentParser googleDocumentParser;
	
	@Autowired
	private HttpConnectionManager httpConnectionManager;
	
	/**
	 * 通过镜像
	 * @param url
	 * @param type
	 * @param clientProxy
	 * @return
	 */
	public String query(String url,String type,Hosts hosts) {
		String result = null;
		url = url.replace("scholar.google.com", "xs.hnwd.com");
		CloseableHttpClient client = httpConnectionManager.getClient(hosts.getIp());
		QueryResult qr = httpConnectionManager.query(url, type, client);
		result = validateResult(qr.getContent(), type,qr.getCost());
		log.info(hosts.getName() + " " + hosts.getIp() + " 请求耗时：" + qr.getCost());
		return result;
	}
	
	/**
	 * 验证httpclient返回结果是否正确
	 * @param queryResult
	 * @param type
	 * @return
	 */
	private String validateResult(String queryResult,String type , long speed) {
		String result = googleDocumentParser.getResult(queryResult, type);
		if(result == null || "{\"count\":\"0\",\"timeMap\":{},\"rows\":[]}".equals(result)) {
			log.info(queryResult);
			result = null;
		} else if("出现验证码".equals(result)) {
			log.info("出现验证码");
			result = null;
		} 
		if(result != null) {
		}
		return result;
	}
	
	
}
