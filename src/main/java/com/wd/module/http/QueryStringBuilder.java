package com.wd.module.http;

import java.io.UnsupportedEncodingException;

import com.wd.bo.Condition;

/**
 * 构建查询请求
 * @author Shenfu
 *
 */
public interface QueryStringBuilder {
	
	/**
	 * 被引文档搜索
	 */
	public static final String quote = "quote";
	/**
	 * 相关文档搜索
	 */
	public static final String related = "related";
	/**
	 * 版本搜索
	 */
	public static final String version = "version";
	public static final String article = "article";
	/**
	 * 导出题录（必应：引用）
	 */
	public static final String quotes = "quotes";
	public static final String journal = "journal";
	public static final String journal_article = "journalArticle";
	
	/**
	 * 构建查询请求条件
	 * @param cdt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	String buildQueryString(Condition cdt) throws UnsupportedEncodingException;
	
	String getBaseURL();

}
