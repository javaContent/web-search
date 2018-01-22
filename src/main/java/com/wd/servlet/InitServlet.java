package com.wd.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.ApplicationContext;

import com.wd.module.http.google.GoogleQueryModule;
import com.wd.util.SpringContextUtil;

public class InitServlet implements Servlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("InitServlet启动!");
		ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
		GoogleQueryModule httpModule=(GoogleQueryModule)applicationContext.getBean("googleQueryModule");
//		httpModule.loadProxy();
		//GFQueryModule gfHttpModule=(GFQueryModule)applicationContext.getBean("gfQueryModule");
		//gfHttpModule.getCookie();
		//System.out.print("获取cookie");
		//HttpClient.cookie = getCookies();
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
	}
	
	public static String getCookies() {
		HttpGet httpGet = new HttpGet("http://scholar.google.com");
		httpGet.addHeader("Accept","*/*");
		httpGet.addHeader("Accept-Language","zh-CN,zh;q=0.8,fr;q=0.6,pt;q=0.4,en;q=0.2");
		httpGet.addHeader("Connection","keep-alive");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
		DefaultHttpClient httpclient=new DefaultHttpClient();
		try {
			HttpResponse response=httpclient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		CookieStore cookiestore=httpclient.getCookieStore();
		List<Cookie> list = cookiestore.getCookies();
		String cookie = "";
		for(int i=0;i<list.size();i++) {
			cookie = cookie + list.get(i).getName() + "=" + list.get(i).getValue();
			if(i==0) cookie = cookie +"; ";
		}
		return cookie;
	}

}
