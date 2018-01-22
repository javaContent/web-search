package com.wd.module.http;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JSoupClient {
	
	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36";
	
	private Map<String,String> cookies;
	
	public void getCookie(String url){
		System.out.println("获取Cookie:"+url);
		try {
			cookies=Jsoup.connect(url).execute().cookies();
			printCookie(cookies);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printCookie(Map<String,String> cookies){
		for(Map.Entry<String, String> entry:cookies.entrySet()){
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}
	
	public static void main(String args[]){
		JSoupClient c=new JSoupClient();
		c.getCookie("http://yv04.spischolar.com");
	}
	
	public Document query(String url,String baseURL){
		try {
			URL _url=new URL(baseURL);
			return Jsoup.connect(url).header("Accept","*/*")
					.header("Accept-Language", "zh-CN,zh;q=0.8,fr;q=0.6,pt;q=0.4,en;q=0.2")
					.header("Connection", "keep-alive")
					.header("User-Agent", USER_AGENT)
					.header("Referer",baseURL)
					.header("Host",_url.getHost())
					.cookies(cookies)
					.get();
		} catch (IOException e) {
			throw new DownloadException("下载出错!"+url,e);
		}
	}
	
}
