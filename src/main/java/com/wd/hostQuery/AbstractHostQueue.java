package com.wd.hostQuery;

import com.wd.bo.Hosts;

//@Component("hostQueue")
public abstract class AbstractHostQueue {
	
	public abstract void init();
	
	/**
	 * 用于用户请求
	 * @return
	 */
	public abstract Hosts getHost(int requestCount);
	
	/**
	 * 拨号判断(拨号判断1、vps主机访问请求达到临界值,拨号判断2、vps主机访问请求返回错误)
	 * @param host
	 * @param isOne  是否需要连续拨号、如果需要连续拨号，就等待线程执行完毕在返回结果
	 * @return
	 */
	public abstract boolean dialingHost(Hosts host);
	
	public abstract void addHost(Hosts host);
	
	public abstract void remove(Hosts host);
	
	public abstract int getSize();
	
	/**
	 * 用来扫描vps主机是否丢失（心跳机制）
	 * @return
	 */
	public abstract Hosts getHostHeart(int num);

}
