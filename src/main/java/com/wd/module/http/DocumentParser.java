package com.wd.module.http;

/**
 * 文档解析
 * @author Shenfu
 *
 */
public interface DocumentParser<T> {
	
	public String parser(T docStr) throws ParserException,ForbiddenException;
	
	/**
	 * 解析引用
	 * @param docStr
	 * @return
	 */
	public String parserQuote(String docStr);
	
	public String getResult(String queryResult,String type);

}
