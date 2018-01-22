package com.wd.module.http;

/**
 * 下载结果
 * @author Shenfu
 *
 */
public class QueryResult {
	
	/**下载耗时*/
	private long cost;
	
	/**下载到的内容*/
	private String content;
	
	public QueryResult(){
	}
	
	public QueryResult(long cost,String content){
		this.cost=cost;
		this.content=content;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
