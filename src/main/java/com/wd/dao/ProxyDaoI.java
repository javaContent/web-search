package com.wd.dao;

import java.util.List;

import com.wd.bo.ProxyInfo;

public interface ProxyDaoI {
	
	/**
	 * 获取所有的ip
	 * @return
	 */
	public List<ProxyInfo> getProxy();
	/**
	 * 
	 * @return
	 */
	public List<ProxyInfo> getProxyInit();
	/**
	 * 获取已经验证成功的ip
	 * @return
	 */
	public List<ProxyInfo> getProxyPass();
	/**
	 * 获取未验证的ip
	 * @return
	 */
	public List<ProxyInfo> getProxyNo();
	
	public void updateProxy(ProxyInfo proxyInfo);
	
	public void deleteProxy(ProxyInfo proxyInfo);

}
