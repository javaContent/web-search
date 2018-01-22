package com.wd.module.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.wd.bo.Condition;

/**
 * 执行Http请求，并转换请求结果
 * @author Shenfu
 *
 */
public abstract class HttpQueryModule {
	
	/** 构建查询请求 */
	private QueryStringBuilder queryStringBuilder;

	/**下载的页面内容解析*/
	private DocumentParser<?> documentParser;
	
	/**下载错误次数*/
	private AtomicInteger errorTimes=new AtomicInteger(0);
	
	/**
	 * 线程池用来将无法提供服务的客户端进行冷却
	 */
	protected final static ExecutorService fixedThreadPool=Executors.newFixedThreadPool(4);
	
	/**
	 * 执行查询请求，将Condition转换为请求的URL
	 * 执行请求，并将查询结果转换为统一的格式
	 * @param cdt
	 * @return
	 */
	public abstract String query(Condition cdt) throws DownloadException,ParserException,ForbiddenException;
	

	public QueryStringBuilder getQueryStringBuilder() {
		return queryStringBuilder;
	}
	
	public int getErrorTimes(){
		return errorTimes.get();
	}
	
	/**
	 * 下载错误次数+1
	 */
	public void errorTimesIncrement(){
		errorTimes.getAndIncrement();
	}

	public void setQueryStringBuilder(QueryStringBuilder queryStringBuilder) {
		this.queryStringBuilder = queryStringBuilder;
	}

	@SuppressWarnings("rawtypes")
	public DocumentParser getDocumentParser() {
		return documentParser;
	}

	@SuppressWarnings("rawtypes")
	public void setDocumentParser(DocumentParser documentParser) {
		this.documentParser = documentParser;
	}
	
	
}
