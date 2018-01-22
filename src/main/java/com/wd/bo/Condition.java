package com.wd.bo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wd.module.http.QueryStringBuilder;

public class Condition {

	/**
	 * 检索词位置,title:标题，any:任何位置，默认为any
	 */
	private String field;
	private String val;
	private int offset = 0;
	private int size = 10;
	private String journal;
	private String other;
	private Integer start_y;
	private Integer end_y;
	private Integer sort;
	/**检索类型：版本检索,相似文献检索,施引文献检索,普通检索*/
	private String type;
	private boolean patent = false;
	private boolean quote = false;
	
	private String fileType;
	
	private List<String> sites;
	
	private List<ConditionGroup> groups;
	
	/**用户代理*/
	private String userAgent;
	
	/**用户Cookie*/
	private String cookie;
	
	/**用户端标识*/
	private String token;
	
	/**
	 * 1、包含中文（简体和繁体）
	 * 2、只包含简体中文
	 */
	private String webPageType;
	
	public String toString(){
		String str="";
		if(sites!=null){
			for(String site:sites){
				str+=site;
			}
		}
		if(groups!=null){
			for(ConditionGroup group:groups ){
				str+=group.toString();
			}
		}
		return field+val+offset+size+journal+other+start_y+end_y+sort+patent+quote+webPageType+type+fileType+str;
	}
	
	/**
	 * 将对象序列化成XML字符串
	 * @return
	 */
	public String toXMLString(){
		StringBuilder sbuilder=new StringBuilder("<condition>");
		if(!StringUtils.isEmpty(field)){
			sbuilder.append("<field>"+field+"</field>");
		}
		if(!StringUtils.isEmpty(journal)){
			sbuilder.append("<journal>"+journal+"</journal>");
		}
		if(!StringUtils.isEmpty(val)){
			sbuilder.append("<val>"+val+"</val>");
		}
		if(offset>0){
			sbuilder.append("<offset>"+offset+"</offset>");
		}
		if(size!=10){
			sbuilder.append("<size>"+size+"</size>");
		}
		if(start_y!=null){
			sbuilder.append("<startYear>"+start_y+"</startYear>");
		}
		if(end_y!=null){
			sbuilder.append("<endYear>"+end_y+"</endYear>");
		}
		if(sort!=null){
			sbuilder.append("<sort>"+sort+"</sort>");
		}
		if(!StringUtils.isEmpty(type)){
			sbuilder.append("<type>"+type+"</type>");
		}
		if(!StringUtils.isEmpty(other)){
			sbuilder.append("<other>"+other+"</other>");
		}
		if(patent){
			sbuilder.append("<patent>"+patent+"</patent>");
		}
		if(quote){
			sbuilder.append("<quote>"+quote+"</quote>");
		}
		if(!StringUtils.isEmpty(webPageType)){
			sbuilder.append("<webPageType>"+webPageType+"</webPageType>");
		}
		if(!StringUtils.isEmpty(fileType)){
			sbuilder.append("<filetype>"+fileType+"</filetype>");
		}
		if(sites!=null&&sites.size()>0){
			sbuilder.append("<sites>");
			for(String site:sites){
				sbuilder.append("<site>"+site+"</site>");
			}
			sbuilder.append("</sites>");
		}
		if(groups!=null&&groups.size()>0){
			sbuilder.append("<fields>");
			for(ConditionGroup group:groups){
				sbuilder.append(group.toXMLString());
			}
			sbuilder.append("</fields>");
		}
		sbuilder.append("</condition>");
		return sbuilder.toString();
	}
	
	/**
	 * 判断是否是检索期刊的最新发表
	 * @return
	 */
	public boolean isJournalCdt(){
		return QueryStringBuilder.journal.equals(getField())&&!StringUtils.isEmpty(getJournal());
	}

	public Condition() {
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getVal() {
		if (null == val)
			return "";
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getJournal() {
		if (null == journal)
			return "";
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public Integer getStart_y() {
		return start_y;
	}

	public void setStart_y(Integer start_y) {
		this.start_y = start_y;
	}

	public Integer getEnd_y() {
		return end_y;
	}

	public void setEnd_y(Integer end_y) {
		this.end_y = end_y;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public boolean isPatent() {
		return patent;
	}

	public void setPatent(boolean patent) {
		this.patent = patent;
	}

	public boolean isQuote() {
		return quote;
	}

	public void setQuote(boolean quote) {
		this.quote = quote;
	}

	public String getWebPageType() {
		return webPageType;
	}

	public void setWebPageType(String webPageType) {
		this.webPageType = webPageType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public List<String> getSites() {
		return sites;
	}

	public void setSites(List<String> sites) {
		this.sites = sites;
	}

	public List<ConditionGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ConditionGroup> groups) {
		this.groups = groups;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
