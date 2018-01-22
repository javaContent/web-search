package com.wd.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.wd.command.ICommand;
import com.wd.util.SpringContextUtil;

/**
 * 执行请求
 * @author Administrator
 *
 */
public class CommandServlet extends  HttpServlet{
	
	private static final Logger log=Logger.getLogger(CommandServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		excuteCommand(req,resp);
	}
	
	/**
	 * 执行Command请求,会使用传递过来的name参数，去Spring中查找是否有
	 * 使用该参数作为ID的Command Bean。
	 * @param req
	 * @param resp
	 */
	private void excuteCommand(HttpServletRequest req, HttpServletResponse resp){
		ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
		String name=req.getParameter("name");
		ICommand command=null;
		if(StringUtils.isEmpty(name)){
			name="status";
		}
		try{
			command=(ICommand)applicationContext.getBean(name);
			String result=command.excute(getParams(req));
			sendResponse(resp,name,result);
		}catch(Exception e){
			log.debug("处理出错!", e);
			sendResponse(resp,name,String.format("{error:1,msg:'处理%sCommand出错,错误:%s'}",name,e.getMessage()));
		}
	}
	
	/**
	 * 发送请求
	 * @param resp
	 * @param result
	 */
	private void sendResponse(HttpServletResponse resp,String command,String result){
		resp.setContentType("text/javascript");
		resp.setCharacterEncoding("UTF-8");
		try {
			resp.getWriter().write(String.format("var result=%s;%sCallback(result);", result,command));
		} catch (IOException e) {
			log.debug("发送数出错!",e);
		}
	}

	/**
	 * 读取请求参数
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,String> getParams(HttpServletRequest req){
		Map<String,String> params=new HashMap<String,String>();
		String key;
		Enumeration<String> enumera=req.getParameterNames();
		while(enumera.hasMoreElements()){
			key=enumera.nextElement();
			if(!key.equals("name")){
				params.put(key, req.getParameter(key));
			}
		}
		return params;
	}
	
}
