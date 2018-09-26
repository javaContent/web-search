package com.wd.module.http;



import org.apache.log4j.Logger;

import com.wd.bo.Condition;

/**
 * 将三种检索方式进行组合
 * 如果是输入框输入的检索，那么先从通过反向代理检索，检索不到的话通过谷粉检索，如果还是检索不到的话在通过匿名代理直接检索Google
 * 如果是检索期刊的最新发表，先通过反向代理网站检索，检索不到的话再通过匿名代理直接检索Google
 * @author Shenfu
 *
 */
public class HttpQueryModuleDecorator extends HttpQueryModule{
	
	private static final Logger log=Logger.getLogger(HttpQueryModuleDecorator.class);
	
	
	/**
	 * 使用原生的Google检索，作为备用检索方式，在最后使用
	 */
	private HttpQueryModule googleQuery;
	
	@Override
	public String query(Condition cdt) {
		return queryFromProxys(cdt);
	}
	
	private String queryFromProxys(final Condition cdt){
		String result= null;
		result = queryFromGoogle(cdt);
		return result;

	}
	
	private String queryFromGoogle(final Condition cdt){
		try{
			return googleQuery.query(cdt);
		}catch(Exception e){
			log.error("从Google下载出错!",e);
		}
		return null;
	}

	public void setGoogleQuery(HttpQueryModule googleQuery) {
		this.googleQuery = googleQuery;
	}
}
