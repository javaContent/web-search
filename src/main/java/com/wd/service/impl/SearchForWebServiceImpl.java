package com.wd.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.wd.bo.Condition;
import com.wd.bo.ConditionGroup;
import com.wd.module.HttpModule;
import com.wd.service.SearchForWebServiceI;

public class SearchForWebServiceImpl implements SearchForWebServiceI {
	
	private static final Logger log=Logger.getLogger(SearchForWebServiceImpl.class);

	private HttpModule httpModule;

	@Override
	public String search(@WebParam(name = "param") String requestParam) {
		log.info("接受请求:"+requestParam);
		System.out.println(requestParam);
		Condition cdt = null;
		try {
			cdt = parserCondition(requestParam);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		if (null != cdt) {
			try {
				return httpModule.search(cdt);
			} catch (Exception e) {
				throw new RuntimeException("下载出错!",e);
			}
		}
		throw new RuntimeException("下载出错!");
	}

	/**
	 * 解析请求参数
	 * xml格式如下：
	 * 
	 * <pre>
	 * 	<condition>
	 * 		<journal></journal><!-- 期刊检索范围 -->
	 * 		<field></field><!--检索词位置,title:标题，any:任何位置，默认为any-->
	 * 		<val></val><!--检索值-->
	 * 		<offset></offset><!-- 偏移量 -->
	 * 		<size></size><!-- 每页显示记录数 -->
	 * 		<other></other><!-- 其它条件 -->
	 * 		<startYear></startYear><!--开始年份-->
	 * 		<endYear></endYear><!--结束年份-->
	 * 		<sort></sort><!--排序方式-->
	 * 		<patent></patent><!-- 是否包含专利 -->
	 * 		<quote></quote><!-- 是否包含引用 -->
	 * 		<type></type><!--检索类型：版本检索,相似文献检索,施引文献检索,普通检索-->
	 * 		<webPageType></webPageType>网页类型
	 * 		<sites>
	 * 			<site></site><!--包含站点-->
	 * 		</sites>
	 * 		<filetype></filetype><!--文档类型-->
	 * 		<fields><!--检索的域-->
	 * 			<group>
	 * 				<logic><logic><!--逻辑关系-->
	 * 				<field></field><!--检索的域-->
	 * 				<value></value><!--检索的值-->
	 * 			</group>
	 * 		</fields>
	 * 		<userAgent></userAgent>
	 * 		<cookie></cookie>
	 * 		<token></token>
	 * 	</condition>
	 * </pre>
	 * 
	 * @param requestParam
	 * @return
	 * @throws DocumentException
	 */
	private Condition parserCondition(String requestParam) throws DocumentException {

		Condition condition = new Condition();

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new StringReader(requestParam));
		Element root = document.getRootElement();
		String field = "";
		Node fieldNode = root.selectSingleNode("/condition/field");
		if (null != fieldNode) {
			field = fieldNode.getText().trim();
		}
		condition.setField(field);

		String journal = "";
		Node journalNode = root.selectSingleNode("/condition/journal");
		if (null != journalNode) {
			journal = journalNode.getText().trim();
		}
		condition.setJournal(journal);

		String val = "";
		Node valNode = root.selectSingleNode("/condition/val");
		if (null != valNode) {
			val = valNode.getText().trim();
		}
		condition.setVal(val);

		int offset = 0;
		Node offsetNode = root.selectSingleNode("/condition/offset");
		if (null != offsetNode) {
			try {
				offset = Integer.parseInt(offsetNode.getText().trim());
			} catch (NumberFormatException e) {
			}
		}
		condition.setOffset(offset);

		int size = 10;
		Node sizeNode = root.selectSingleNode("/condition/size");
		if (null != sizeNode) {
			try {
				size = Integer.parseInt(sizeNode.getText().trim());
			} catch (NumberFormatException e) {
			}
		}
		Node otherNode = root.selectSingleNode("/condition/other");
		if (null != otherNode) {
			condition.setOther(otherNode.getText());
		}

		Node startYearNode = root.selectSingleNode("/condition/startYear");
		if (null != startYearNode && null != startYearNode.getText()) {
			try {
				condition.setStart_y(Integer.parseInt(startYearNode.getText()));
			} catch (NumberFormatException e) {
			}
		}
		Node endYearNode = root.selectSingleNode("/condition/endYear");
		if (null != endYearNode && null != endYearNode.getText()) {
			try {
				condition.setEnd_y(Integer.parseInt(endYearNode.getText()));
			} catch (NumberFormatException e) {
			}
		}
		Node sortNode = root.selectSingleNode("/condition/sort");
		if (null != sortNode && null != sortNode.getText()) {
			try {
				condition.setSort(Integer.parseInt(sortNode.getText()));
			} catch (NumberFormatException e) {
			}
		}

		Node patentNode = root.selectSingleNode("/condition/patent");
		if (null != patentNode && null != patentNode.getText()) {
			condition.setPatent(true);
		}

		Node quoteNode = root.selectSingleNode("/condition/quote");
		if (null != quoteNode && null != quoteNode.getText()) {
			condition.setQuote(true);
		}
		Node type = root.selectSingleNode("/condition/type");
		if (null != type && null != type.getText()) {
			condition.setType(type.getText().trim());
		}
		Node webPageTypeNode = root.selectSingleNode("/condition/webPageType");
		if (null != webPageTypeNode && null != webPageTypeNode.getText()) {
			condition.setWebPageType(webPageTypeNode.getText());
		}
		condition.setSize(size);
		
		//新增
		//文档类型
		Node fileTypeNode =root.selectSingleNode("/condition/filetype");
		if(null!=fileTypeNode && null!=fileTypeNode.getText()){
			condition.setFileType(fileTypeNode.getText());
		}
		Node userAgentNode=root.selectSingleNode("/condition/userAgent");
		if(null!=userAgentNode && null!= userAgentNode.getText()){
			condition.setUserAgent(userAgentNode.getText());
		}
		Node cookieNode=root.selectSingleNode("/condition/cookie");
		if(null!=cookieNode && null!= cookieNode.getText()){
			condition.setCookie(cookieNode.getText());
		}
		Node tokenNode=root.selectSingleNode("/condition/token");
		if(null!=tokenNode && null!= tokenNode.getText()){
			condition.setToken(tokenNode.getText());
		}
		//站点
		List siteNodes=root.selectNodes("/condition/sites/site");
		if(siteNodes!=null &&siteNodes.size()>0){
			List<String> sites=new ArrayList<String>();
			Node siteNode=null;
			for(int i=0;i<siteNodes.size();i++){
				siteNode=(Node)siteNodes.get(i);
				if(siteNode!=null&&!StringUtils.isEmpty(siteNode.getText())){
					sites.add(siteNode.getText());
				}
			}
			if(sites.size()>0){
				condition.setSites(sites);
			}
		}
		List groupNodes=root.selectNodes("/condition/fields/group");
		if(groupNodes!=null&&groupNodes.size()>0){
			List<ConditionGroup> groups=new ArrayList<ConditionGroup>();
			Element groupNode=null;
			ConditionGroup group=null;
			for(int i=0;i<groupNodes.size();i++){
				groupNode=(Element)groupNodes.get(i);
				group=parseGroup(groupNode);
				if(group!=null){
					groups.add(group);
				}
			}
			if(groups.size()>0){
				condition.setGroups(groups);
			}
		}
		return condition;
	}
	
	private ConditionGroup parseGroup(Element ele){
		@SuppressWarnings("rawtypes")
		Iterator ite=ele.elementIterator();
		Element subEle=null;
		ConditionGroup group=new ConditionGroup();
		while(ite.hasNext()){
			subEle=(Element)ite.next();
			if(subEle.getName().equalsIgnoreCase("logic")){
				group.setLogic(Short.parseShort(subEle.getText()));
			}else if(subEle.getName().equalsIgnoreCase("field")){
				group.setField(subEle.getText());
			}else if(subEle.getName().equalsIgnoreCase("value")){
				if(StringUtils.isEmpty(subEle.getText())){
					return null;
				}
				group.setValue(subEle.getText());
			}
		}
		return group;
	} 

	public void setHttpModule(HttpModule httpModule) {
		this.httpModule = httpModule;
	}

	@Override
	public Boolean cleanCache(String validateCode) {
		//没有实现验证
		try{
			this.httpModule.cleanCache();
			return true;
		}catch(Exception e){
			log.error("清除缓存失败!",e);
			return false;
		}
	}

	/**
	 * 测试网络连通性
	 */
	@Override
	public String test(String requestParam){
		return "1";
	}
	
}
