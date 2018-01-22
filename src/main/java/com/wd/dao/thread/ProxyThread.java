package com.wd.dao.thread;

import org.springframework.beans.factory.annotation.Autowired;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.util.Comm;

/**
 * 数据库操作异步线程（）
 * @author Administrator
 *
 */
public class ProxyThread implements Runnable {
	
	public ProxyDaoI proxyDao;
	
	public ProxyInfo proxyInfo;
	
	public int type;
	
	public ProxyThread(ProxyDaoI proxyDao, ProxyInfo proxyInfo, int type) {
		this.proxyDao = proxyDao;
		this.proxyInfo = proxyInfo;
		this.type = type;
	}
	
	@Override
	public void run() {
		if(type == Comm.Mysql_Update) {
			proxyDao.updateProxy(proxyInfo);
		} else if(type == Comm.Mysql_Delete) {
			proxyDao.deleteProxy(proxyInfo);
		}
	}

}
