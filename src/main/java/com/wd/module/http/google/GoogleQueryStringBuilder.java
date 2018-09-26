package com.wd.module.http.google;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import com.wd.bo.Condition;
import com.wd.bo.ConditionGroup;
import com.wd.module.http.QueryStringBuilder;

@Component
public class GoogleQueryStringBuilder implements QueryStringBuilder{
	
	private String buildGroup(ConditionGroup group){
		String line="",value=group.getValue(),field=group.getField();
		if(StringUtils.isEmpty(value)){
			return line;
		}
		if(group.getLogic()==1){
			line+="OR ";
		}else if(group.getLogic()==2){
			line+="-";
		}
		if(value.contains(" ")){
			value="\""+value+"\"";
		}
		if(StringUtils.isEmpty(field)){
			line+=value;
		}else if(field.equals("author")){//作者
			line+="author:"+value;
		}else if(field.equals("keyword")){//关键词
			line+=value;
		}else if(field.equals("title")){//标题
			line+="intitle:"+value;
		}
		return line;
	}
	
	/**
	 * as_q=Nature&as_epq=&as_oq=&as_eq=&as_occt=title&as_sauthors=&as_publication=&as_ylo=&as_yhi=&btnG=&hl=zh-CN&as_sdt=1%2C5&as_vis=1
	 * other=%2Fscholar%3Fcluster%3D6164229948202632535%26hl%3Dzh-CN%26as_sdt%3D1%2C5%26as_vis%3D1&field=version&size=20
	 * other=%2Fscholar%3Fcluster%3D719252262488457420%26hl%3Dzh-CN%26as_sdt%3D1%2C5%26as_vis%3D1&type=version&size=20
	 * @param cdt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String queryString(Condition cdt) throws UnsupportedEncodingException{
		StringBuilder urlSB = new StringBuilder();
		urlSB.append("/scholar?hl=zh-CN");
		urlSB.append("&lookup=0");
		//包含专利
		if (cdt.isPatent()) {
			urlSB.append("&as_sdt=0%2C5");
		} else {
			urlSB.append("&as_sdt=1%2C5");
		}
		//包含引用
		if (cdt.isQuote()) {
			urlSB.append("&as_vis=0");
		} else {
			urlSB.append("&as_vis=1");
		}
		//语言设置
		if ("1".equals(cdt.getWebPageType())) {
			urlSB.append("&lr=lang_zh-CN%7Clang_zh-TW");
		} else if ("2".equals(cdt.getWebPageType())) {
			urlSB.append("&lr=lang_zh-CN");
		}
		
		if (version.equals(cdt.getType()) || related.equals(cdt.getType()) || quote.equals(cdt.getType())) {//版本检索,相似文献检索,施引文献检索
			urlSB.delete(0, urlSB.length());
			urlSB.append(cdt.getOther());
		} else {
			String value=cdt.getVal();
			if(!StringUtils.isEmpty(cdt.getFileType())){
				value+=" filetype:"+cdt.getFileType();
			}
			if(cdt.getSites()!=null&&cdt.getSites().size()>0){
				int index=0;
				for(String site:cdt.getSites()){
					if(index==0){
						value+=" site:"+site;
					}else{
						value+=" OR site:"+site;
					}
					index++;
				}
			}
			if(cdt.getGroups()!=null&&cdt.getGroups().size()>0){
				for(ConditionGroup group:cdt.getGroups()){
					String str=buildGroup(group);
					if(!StringUtils.isEmpty(str)){
						value+=" "+str;
					}
				}
			}
			//期刊设置
			if (!StringUtils.isEmpty(cdt.getJournal())) {
				value+=" source:"+cdt.getJournal();
			}
			if("title".equals(cdt.getField())){//标题中检索
				urlSB.append("&as_occt=title");
				urlSB.append("&as_q=").append(URLEncoder.encode(value, "UTF-8"));
			}else {//任何位置检索
				urlSB.append("&as_occt=any");
				urlSB.append("&q=").append(URLEncoder.encode(value, "UTF-8"));
//				urlSB.append("&q=").append(value);
			}
		}

		//设置分页
		if(cdt.getOffset() != 0) {
			urlSB.append("&start=").append(cdt.getOffset());
			urlSB.append("&num=").append(20);
		} else {
			urlSB.append("&num=").append(20);
		}
		
		if(null !=cdt.getStart_y() || null != cdt.getEnd_y()){//如果有日期筛选，那么将忽略日期排序。
			if (null != cdt.getStart_y()) {
				urlSB.append("&as_ylo=").append(cdt.getStart_y());
			}
			if (null != cdt.getEnd_y()) {
				urlSB.append("&as_yhi=").append(cdt.getEnd_y());
			}
		}else if((null != cdt.getSort()) && (1 == cdt.getSort())){
			urlSB.append("&scisbd=1");
		}
		return urlSB.toString();
	}

	@Override
	public String buildQueryString(Condition cdt) throws UnsupportedEncodingException{
		//return getBaseURL();
		return getBaseURL()+queryString(cdt);
	}

	@Override
	public String getBaseURL() {
		//香港
		return "https://scholar.google.com:7090";
		//韩国
		//return "http://scholar.google.co.kr";
	}

}
