package com.wd.module.http.google;

import org.apache.log4j.Logger;
import org.pzy.module.mail.MailModuleI;
import org.pzy.module.mail.bo.MainInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.wd.bo.Condition;
import com.wd.mail.SendMail;
import com.wd.module.http.HttpConnectionManager;
import com.wd.module.http.HttpQueryModule;
import com.wd.module.http.QueryResult;
import com.wd.module.http.QueryStringBuilder;

/**
 * 使用代理在Google中检索!
 * 因为不能直接检索到Google，所以通过代理来实现
 * @author Shenfu
 *
 */
public class GoogleQueryModule extends HttpQueryModule {
	
	private static final Logger log=Logger.getLogger(GoogleQueryModule.class);
	
	@Autowired
	private HttpConnectionManager httpConnectionManager;
	
	public String query(Condition cdt){
		String url=null;
		String result = null;
		try{
			url=this.getQueryStringBuilder().buildQueryString(cdt);
			if(cdt.getType() != null && cdt.getType().equals(QueryStringBuilder.quotes)) {
				url = "http://scholar.google.com" + cdt.getOther().replaceAll("url=", "").replaceAll("&amp;", "&");
			}
			log.info("请求地址:"+url);
			long start = System.currentTimeMillis();
			result = prepareQuery(url,cdt.getType(),0);
			long end = System.currentTimeMillis();
			log.info("请求耗时:"+ (end - start));
			if(result == null) {
				log.info("请求失败:耗时："+ (end - start));
			}
//			if(result.equals("出现验证码")) {
//				return null;
//			}
		}catch(Exception e){
			throw new RuntimeException("构建查询请求地址失败!",e);
		} finally {
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	private String prepareQuery(final String url,String type,int num){
		Boolean priority = false;
		if(num > 0) priority = true;
		log.info("进入Google，num=" + num);
		String result = null;
		if(num == 3) {
			return result;
		}
		try {
			result = httpConnectionManager.query(url,type,priority);
			if(result == null) {
				return result = prepareQuery(url, type,num + 1);
			}
		} catch(Exception e){
			result = prepareQuery(url, type,num +1);
//			throw new RuntimeException("下载出错!",e);
		} finally {
			return result;
		}
	}
	
	/**
	 * 
	 * @param queryResult
	 * @param type   1是引用
	 * @return
	 */
	public String getResult(QueryResult queryResult,String type) {
		String result = null;
		if(queryResult == null || queryResult.getContent() == null) {
			return null;
		}
		if(type != null && type.equals(QueryStringBuilder.quotes)) {//如果是导出题录
			result = this.getDocumentParser().parserQuote(queryResult.getContent());//返回参数
		} else {
			result=this.getDocumentParser().parser(queryResult.getContent());//返回参数
		}
		return result;
	}
	
}
