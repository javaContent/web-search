package com.wd.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.wd.bo.BaseCache;
import com.wd.bo.Condition;
import com.wd.module.http.HttpQueryModule;
import com.wd.util.MD5;

public class HttpModule {
	
	private static final Logger log=Logger.getLogger(HttpModule.class);

	private BaseCache searchCache;

	private HttpQueryModule httpQueryModule;
	

	public HttpModule() {
		InputStream inputStream = HttpModule.class.getClassLoader().getResourceAsStream("oscache.properties");
		Properties prop = new Properties();
		if (null == inputStream) {
			throw new RuntimeException("获取获取oscache.properties文件的输入流");
		}
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int cacheTime = 60 * 60 * 23 + 60 * 57;
		searchCache = new BaseCache("search", cacheTime, prop);
	}
	
	/**
	 * 清除缓存
	 */
	public void cleanCache(){
		synchronized (searchCache) {
			searchCache.flushAll();
		}
	}
	
	/**
	 * 重缓存中查询
	 * @param key
	 * @return
	 */
	private String getFromCache(String key){
		String val = null;
		try {//先检查缓存
			val = (String) searchCache.get(key);
		} catch (NeedsRefreshException e1) {
		}
		if (null != val && !"".equals(val)) {
			// 缓存中有检索结果，直接使用缓存的结果
			return val;
		}
		return null;
	}
	
	private String query(Condition cdt){
		try{
			return httpQueryModule.query(cdt);
		}catch(Exception e){
			throw new RuntimeException("下载出错!",e);
		}
	}

	/**
	 * 从google学术中搜索文章
	 * 
	 * @param cdt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String search(Condition cdt) throws UnsupportedEncodingException {
		log.info("接受请求，从缓存中查询!");
		//检测缓存
		String cacheKey = MD5.getMD5(cdt.toString().getBytes());
		String result = "";
		result=getFromCache(cacheKey);
		if(!StringUtils.isEmpty(result)&&!"{\"count\":\"\",\"timeMap\":{},\"rows\":[]}".equals(result)){
		return result;
		}
		result=query(cdt);
		if("{\"count\":\"\",\"timeMap\":{},\"rows\":[]}".equals(result)||StringUtils.isEmpty(result)){//没有数据
			throw new RuntimeException("下载内容为空!");
		}
		if(result != null && !result.equals("ip数量少于10个")) {
			searchCache.put(cacheKey, result);
		}
		//log.info(result);
		return result;
	}
	
	public HttpQueryModule getHttpQueryModule() {
		return httpQueryModule;
	}

	public void setHttpQueryModule(HttpQueryModule httpQueryModule) {
		this.httpQueryModule = httpQueryModule;
	}
}
