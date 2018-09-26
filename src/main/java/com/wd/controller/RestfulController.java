package com.wd.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wd.bo.Hosts;
import com.wd.hostQuery.AbstractHostQueue;
import com.wd.util.IpUtil;

/**
 * 期刊查询接口实现
 * @author Shenfu
 *
 */
@Controller
@RequestMapping("/record")
public class RestfulController  {
	
	private static final Logger log=Logger.getLogger(RestfulController.class);
	
	@Autowired
	private AbstractHostQueue hostQueue;
	
	/**
	 * 固定ip
	 */
	@Autowired
	private AbstractHostQueue fixedhostQueue;
	
	@RequestMapping(value = { "/{name}" }, method = { RequestMethod.GET })
	@ResponseBody
	public String changeIp(@PathVariable String name,HttpServletRequest request) {
		String ip = request.getParameter("ip");
		if(StringUtils.isEmpty(ip)) {
			ip =IpUtil.getIpAddr(request);
		}
		Hosts host = new Hosts(name,ip);
		log.info("name:" + name + "--ip:" + ip);
		hostQueue.addHost(host);
		return ip;
	}
	
	/**
	 * 同时可以用来普通检索和检索被引
	 * @param name
	 * @param request
	 * @return
	 */
	@RequestMapping(value = { "/fixed/{name}" }, method = { RequestMethod.GET })
	@ResponseBody
	public String addFixedHost(@PathVariable String name,HttpServletRequest request) {
		String ip = request.getParameter("ip");
		if(StringUtils.isEmpty(ip)) {
			ip =IpUtil.getIpAddr(request);
		}
		Hosts host = new Hosts(name,ip);
		log.info("name:" + name + "--ip:" + ip);
		if(host.getIp().startsWith("58.")) {	//58开头的ip不用
			fixedhostQueue.dialingHost(host);
		} else {
			hostQueue.addHost(host);
			fixedhostQueue.addHost(host);
		}
		return ip;
	}
	
}
