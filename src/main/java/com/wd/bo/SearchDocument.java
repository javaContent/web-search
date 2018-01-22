package com.wd.bo;

import java.util.List;

/**
 * 检索文档
 * 
 * @author pan
 * 
 */
public class SearchDocument {

	/**
	 * 引用
	 */
	private String quote;
	
	private String quoteUrl;
	
	
	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getQuoteUrl() {
		return quoteUrl;
	}

	public void setQuoteUrl(String quoteUrl) {
		this.quoteUrl = quoteUrl;
	}

	/**
	 * 文章的链接地址
	 */
	private String href;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 来源
	 */
	private String source;
	/**
	 * 摘要
	 */
	private String abstract_;

	/**
	 * 被引信息
	 */
	private String quoteText;
	private String quoteLink;

	/**
	 * 相关文章连接
	 */
	private String relatedLink;

	/**
	 * 版本信息
	 */
	private String versionText;
	private String versionLink;

	/**
	 * webScience信息
	 */
	private String webScienceText;
	private String webScienceLink;
	
	//===================新版添加属性=================//
	
	/**文档类型*/
	private String docType;
	
	/**开发获取来源*/
	private String openSource;
	
	/**
	 * 开放获取来源的文档类型
	 */
	private String openSourceDocType;
	
	/**开发获取地址*/
	private String openUri;
	
	/**是否是开发获取的，默认为否*/
	private boolean isOpen=false;

	public SearchDocument() {
	}

	public SearchDocument(String title, String source, String abstract_, String href) {
		this.title = title;
		this.source = source;
		this.abstract_ = abstract_;
		this.href = href;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAbstract_() {
		return abstract_;
	}

	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getQuoteText() {
		return quoteText;
	}

	public void setQuoteText(String quoteText) {
		this.quoteText = quoteText;
	}

	public String getQuoteLink() {
		return quoteLink;
	}

	public void setQuoteLink(String quoteLink) {
		this.quoteLink = quoteLink;
	}

	public String getRelatedLink() {
		return relatedLink;
	}

	public void setRelatedLink(String relatedLink) {
		this.relatedLink = relatedLink;
	}

	public String getVersionText() {
		return versionText;
	}

	public void setVersionText(String versionText) {
		this.versionText = versionText;
	}

	public String getVersionLink() {
		return versionLink;
	}

	public void setVersionLink(String versionLink) {
		this.versionLink = versionLink;
	}

	public String getWebScienceText() {
		return webScienceText;
	}

	public void setWebScienceText(String webScienceText) {
		this.webScienceText = webScienceText;
	}

	public String getWebScienceLink() {
		return webScienceLink;
	}

	public void setWebScienceLink(String webScienceLink) {
		this.webScienceLink = webScienceLink;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getOpenSource() {
		return openSource;
	}

	public void setOpenSource(String openSource) {
		this.openSource = openSource;
	}

	public String getOpenUri() {
		return openUri;
	}

	public void setOpenUri(String openUri) {
		this.openUri = openUri;
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getOpenSourceDocType() {
		return openSourceDocType;
	}

	public void setOpenSourceDocType(String openSourceDocType) {
		this.openSourceDocType = openSourceDocType;
	}
	
}
