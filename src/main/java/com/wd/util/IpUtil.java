package com.wd.util;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * IP工具类
 * 
 * @author pan
 * 
 */
public class IpUtil {
	
	private static final Logger logger=Logger.getLogger(IpUtil.class);

	/**
	 * 获取登录用户IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {//Nginx
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}else if(ip.contains(",")){
			logger.info("使用了代理的IP:"+ip);
			String[] ips = ip.split(",");
			int i=0;
			while(i<ips.length){
				if (ips[i] != null && ips[i].length() != 0 && !"unknown".equalsIgnoreCase(ips[i])) {
					ip=ips[i];
					break;
				}
				i++;
			}
		}
		return ip;
	}


}
