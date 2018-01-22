package com.wd.module.http.google;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.wd.bo.SearchDocument;
import com.wd.module.http.DocumentParser;
import com.wd.module.http.ForbiddenException;
import com.wd.module.http.QueryResult;
import com.wd.module.http.QueryStringBuilder;
import com.wd.util.JsonUtil;

@Component
public class GoogleDocumentParser implements DocumentParser<String>{

	private static final Logger log=Logger.getLogger(GoogleDocumentParser.class);
	
	private Pattern pattern = Pattern.compile("[0-9]+");
	
	/**
	 * 解析返回数据
	 * @param queryResult
	 * @param type   1是引用
	 * @return
	 */
	public String getResult(String queryResult,String type) {
		String result = null;
		if(queryResult == null) {
			return null;
		}
		if(type != null && type.equals(QueryStringBuilder.quotes)) {//如果是导出题录
			result = parserQuote(queryResult);//返回参数
		} else {
			result = parser(queryResult);//返回参数
		}
		return result;
	}
	
	/**
	 * 验证文档是否是合格的文档
	 * @param doc
	 */
	public boolean validateDocument(Document doc){
		Iterator<Element> titleIter = doc.getElementsByTag("title").iterator();
		if (titleIter.hasNext()) {
			Element titleEle = titleIter.next();
			if ("404 Not Found".equals(titleEle.text().trim())) {
			}
		}
		Element ele=doc.getElementById("gs_captcha_ccl");
		if(ele!=null){
			String content=ele.text();
			if(content.contains("请键入下图显示的字符以继续操作") || content.contains("To continue, please type the characters below") 
					|| content.contains("请键入下面的字词，这样我们就知道请求是由您而不是机器人发出的。") || content.contains("请进行人机身份验证")){
				return false;
			}else if(content.contains("我们无法对您进行人机身份验证")){
				return false;
			}//recaptcha/api.js 验证的js
		}
		return true;
	}
	
	public String parser(String docStr){
		Document doc=Jsoup.parse(docStr);//JSoupe解析文档
		if(!validateDocument(doc)) return "出现验证码";
		try{
		List<SearchDocument> list = new ArrayList<SearchDocument>();
		Map<String, String> timeMap = new LinkedHashMap<String, String>(3);
		String count = "";
		Element totalElement = doc.getElementById("gs_ab_md");
		if (null != totalElement) {
			String text = totalElement.text();
			Pattern pattern = Pattern.compile("约 (.*) 条");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				count = matcher.group(1).trim();
			} else {
				pattern = Pattern.compile("获得(.*)条");
				matcher = pattern.matcher(text);
				if (matcher.find()) {
					count = matcher.group(1).trim();
				}
			}
		}
		extractDocList(list, doc);
		Elements elements = doc.select("#gs_lnv_ylo li a");
		Iterator<Element> iterEle = elements.iterator();
		while (iterEle.hasNext()) {
			Element ele = iterEle.next();
			String time = ele.text();
			if (time.matches("^[0-9].*")) {
				Matcher matcher = pattern.matcher(time);
				if (matcher.find()) {
					timeMap.put(matcher.group(), time);
				}
			}
		}
		/*if(list.size()==0){
			log.error("解析内容为空:"+doc);
			return null;
		}*/
		if(count==null||StringUtils.isEmpty(count)){//如果没有解析到数量，原因是Google数据量少于1页
			count=list.size()+"";
		}
		String result="{\"count\":\"" + count + "\",\"timeMap\":" + JsonUtil.obj2Json(timeMap) + ",\"rows\":" + JsonUtil.obj2Json(list) + "}";
		/*if("{\"count\":\"\",\"timeMap\":{},\"rows\":[]}".equals(result)){//没有数据
			return null;
		}*/
		log.debug("获取结果:"+result);
		return  result;
		}catch(Exception e){
			try {
				throw new Exception("解析结果出错!",e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return  null;
		}
	}
	
	//private static final String redStart = "&lt; <font color=\"#DD4B39\">%s</font>&gt;";
	//private static final String redEnd = "&lt;/<font color=\"#DD4B39\">%s</font>&gt;";
	private static final String simpleStart = "&lt;%s&gt;";
	private static final String simpleEnd = "&lt;/%s&gt;";

	private void extractDocList(List<SearchDocument> list, Document doc) {
		Elements elements = doc.select("#gs_bdy .gs_r");
		Iterator<Element> iterEle = elements.iterator();
		/*Elements elements = doc.select("#gs_res_bdy .gs_r");
		Iterator<Element> iterEle = elements.iterator();
		if(elements == null && !iterEle.hasNext()) {
			elements = doc.select("#gs_bdy .gs_r");
			iterEle = elements.iterator();
		}*/
		while (iterEle.hasNext()) {
			Element ele = iterEle.next();
			Elements tables=ele.getElementsByTag("table");//排除第一行的“**用户个人学术档案”
			if(tables!=null&&tables.size()>0){
				continue;
			}
			// 获取标题信息
			Elements titleEles = ele.select(".gs_ri h3.gs_rt a");
			if (titleEles.isEmpty()) {
				titleEles = ele.select(".gs_ri h3.gs_rt");
			}
			Element titleEle = titleEles.listIterator().next();
			String title = titleEle.html().replaceAll("\'", "&apos;").replaceAll(String.format(simpleStart, "[ ]*sub"), "<sub>").replaceAll(String.format(simpleEnd, "sub"), "</sub>");
			String href = titleEle.attr("href");
			/*if(href!=null&&href.startsWith("http://www.cqvip.com")){//替换维普URL
				href=href.replaceFirst("http://www.cqvip.com", "http://lib.cqvip.com");
			}*/
			// 获取出处信息
			Elements sourceEles = ele.select(".gs_ri div.gs_a");
			String source = null;
			if (sourceEles.size() > 0) {
				Element sourceEle = sourceEles.listIterator().next();
				source = sourceEle.html().replaceAll("\n", "").replaceAll("<a[^>]+>", "").replaceAll("</a[^>]+>", "");
			}
			// 获取摘要
			Elements abstractEles = ele.select(".gs_ri div.gs_rs");
			String abstract_ = "";
			if (abstractEles.size() > 0) {
				Element abstractEle = abstractEles.listIterator().next();
				abstract_ = abstractEle.html();
			}
			abstract_ = abstract_.replaceAll("\'", "&apos;");

			SearchDocument searchDocument = new SearchDocument(title, source, abstract_, href);
			
			//---------
			//获取文档类型
			Elements docTypeEles=ele.select(".gs_ri h3.gs_rt span.gs_ctc span.gs_ct1");
			if(docTypeEles.size()>0){
				Element docTypeEle=docTypeEles.listIterator().next();
				String docType=docTypeEle.text();
				searchDocument.setDocType(docType);
			}
			//获取开发获取信息
			Elements openEles=ele.select("div.gs_ggs div.gs_ggsd a");
			if(openEles.size()>0){
				Element openHrefEle=openEles.listIterator().next();
				searchDocument.setIsOpen(true);
				searchDocument.setOpenUri(openHrefEle.attr("href"));
				Elements openInfoEles=openHrefEle.select("span.gs_ggsS");
				if(openInfoEles.size()>0){
					Element openSourceEle=openInfoEles.listIterator().next();
					String openSource=openSourceEle.ownText();
					searchDocument.setOpenSource(openSource);
					Elements osTypeEles=openSourceEle.select("span.gs_ctg2");
					if(osTypeEles.size()>0){
						Element osTypeEle=osTypeEles.listIterator().next();
						searchDocument.setOpenSourceDocType(osTypeEle.text());
					}
				}
			}
			//---------

			Elements otherEles = ele.select(".gs_ri div.gs_fl");
			if (otherEles.size() > 0) {
				Element tmpEle = otherEles.listIterator().next();
				Elements aTagEles = tmpEle.getElementsByTag("a");
				ListIterator<Element> aTagIter = aTagEles.listIterator();
				while (aTagIter.hasNext()) {
					Element aEle = aTagIter.next();
					String text = aEle.text();
					if (text.trim().startsWith("被引用次数")) {
						// 获取被引用信息
						searchDocument.setQuoteText(text);
						searchDocument.setQuoteLink(aEle.attr("href"));
					} else if (text.trim().startsWith("相关文章")) {
						// 获取相关文章
						searchDocument.setRelatedLink(aEle.attr("href"));
					} else if (text.trim().startsWith("所有")) {
						// 获取版本信息
						searchDocument.setVersionText(text);
						searchDocument.setVersionLink(aEle.attr("href"));
					} else if (text.trim().startsWith("Web of Science")) {
						// 获取 Web Science信息
						searchDocument.setWebScienceText(text);
						searchDocument.setWebScienceLink(aEle.attr("href"));
					} 
					
					
					//else if(text.trim().startsWith("引用")){
					else if(aEle.attr("title").equals("引用")){
						//获取引用数据
						String key = ele.attr("data-cid");
						String dataRp = ele.attr("data-rp");
						/*String key =aEle.attr("onclick");
						key=key.replaceAll("return gs_ocit\\(", "");
						key=key.replaceAll("\\)","");
						key=key.replaceAll("'","");
						String array[] =key.split(",");*/
						String url ="/scholar?q=info:"+key+":scholar.google.com/&amp;output=cite&amp;scirp="+dataRp+"&amp;hl=zh-CN";

						searchDocument.setQuote(text);
						searchDocument.setQuoteUrl(url);
					}
					
					
					
				}
			}
			list.add(searchDocument);
		}
	}
	
	@Override
	public String parserQuote(String docStr) {
		Document doc=Jsoup.parse(docStr);//JSoupe解析文档
		if(!validateDocument(doc)) return "出现验证码";
		String t1 = "GB/T 7714";
		String r1 = docStr.substring(docStr.indexOf("GB/T 7714")+9, docStr.indexOf("MLA"));
		r1 = r1.substring(r1.indexOf("<div"), r1.indexOf("</div>"));
		String t2 = "MLA";
		String r2 = docStr.substring(docStr.indexOf("MLA")+3, docStr.indexOf("APA"));
		r2 = r2.substring(r2.indexOf("<div"), r2.indexOf("</div>"));
		String t3 = "APA";
		String r3 = docStr.substring(docStr.indexOf("APA")+3, docStr.indexOf("BibTeX"));
		r3 = r3.substring(r3.indexOf("<div"), r3.indexOf("</div>"));
		JSONObject obj=new JSONObject();
		obj.put("t1", t1);
		obj.put("t2", t2);
		obj.put("t3", t3);
		obj.put("r1", r1);
		obj.put("r2", r2);
		obj.put("r3", r3);
		return obj.toString();
	}

}
