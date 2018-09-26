package com.wd.module.http.google;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wd.bo.BaseCache;
import com.wd.bo.Condition;
import com.wd.bo.Hosts;
import com.wd.hostQuery.AbstractHostQueue;
import com.wd.module.http.HttpClientModule;
import com.wd.module.http.HttpQueryModule;
import com.wd.module.http.QueryStringBuilder;
import com.wd.module.task.ShellClientTask;

/**
 * 使用代理在Google中检索!
 * 因为不能直接检索到Google，所以通过代理来实现
 * @author Shenfu
 *
 */
@Service("googleQueryModule")
public class GoogleQueryModule extends HttpQueryModule {
	
	private static final Logger log=Logger.getLogger(GoogleQueryModule.class);
	
	@Autowired
	private HttpClientModule httpClientModule;
	
	@Autowired
	private AbstractHostQueue hostQueue;
	
	@Autowired
	private AbstractHostQueue fixedhostQueue;
	
	@Autowired
	public GoogleQueryStringBuilder googleQueryStringBuilder;
	
	@Autowired
	ShellClientTask shellClientTask;
	
	private BaseCache searchCache;
	
	@Value("${requestCount}")
	public int requestCount;
	
	@Value("${fixedRequestCount}")
	public int fixedRequestCount;
	
	@Value("${requestTimes}")
	public int requestTimes;	//最大请求次数
	
	public String query(Condition cdt){
		String url=null;
		String result = null;
		try{
			if(cdt.getType() != null && cdt.getType().equals(QueryStringBuilder.quotes)) {
				url = "https://scholar.google.com:7090" + cdt.getOther().replaceAll("url=", "").replaceAll("&amp;", "&");
			} else {
				url=googleQueryStringBuilder.buildQueryString(cdt);
			}
			log.info("请求地址:"+url);
			long start = System.currentTimeMillis();
			int times = 0;
			do{
				result = prepareQuery(url,cdt.getType());
				times++;
			} while(result == null && times <= requestTimes);
			long end = System.currentTimeMillis();
			log.info("请求耗时:"+ (end - start));
		}catch(Exception e){
			return null;
		} finally {
		}
		return result;
	}
	
	private String prepareQuery(String url,String type){
		log.info("进入Google");
		String result = null;
		Hosts hosts = null;
		if(type != null && "quote".equals(type)) {
			hosts = fixedhostQueue.getHost(fixedRequestCount);
		} else {
			hosts = hostQueue.getHost(requestCount);
		}
		try {
			result = httpClientModule.query(url, type, hosts);
		} catch(Exception e){
		} finally {
			hosts.decreaseUserCount();
			log.info(hosts.getIp() + ":" + hosts.getUserCount());
			if(result == null) {	//拨号判断2、vps主机访问请求返回错误
				hosts.setErrCount(1);
				hosts.setType(2);//禁止其他用户请求使用此ip，因为已经无效
				shellClientTask.executeAll(hosts);
//				hostQueue.dialingHost(hosts);
			} else {  //被引次数使用固定ip访问，每次成功访问将失败次数重置（当连续失败3次则放弃）
				hosts.setErrCount(0);
			}
		}
		return result;
	}
	
	
}
