package com.wd.hostQuery.impl;


import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.Hosts;
import com.wd.hostQuery.AbstractHostQueue;
import com.wd.module.task.ShellClientTask;


/**
 * 除被引次数
 * @author Administrator
 *
 */
@Component("hostQueue")
public class DialingHostQueue extends AbstractHostQueue {
	
	private static final Logger log=Logger.getLogger(DialingHostQueue.class);
	
	@Autowired
	ShellClientTask shellClientTask;
	
	private static CopyOnWriteArrayList<Hosts> data;
	
	private static int count;
	
	@Override
    public void init() {
		if(data == null || data.size() == 0) {
			data=new CopyOnWriteArrayList<Hosts>();
		}
	}
	
	/**
	 * 用于用户请求
	 * @return
	 */
	@Override
    public Hosts getHost(int requestCount) {
		Hosts host = getAvailableHost(0);
		if(host.getCount() >= requestCount) { //拨号判断1、vps主机访问请求达到临界值
			if(dialingHost(host)) {				//进行拨号ing
				host = getAvailableHost(0);
			}
		}
		host.increaseUserCount();
		log.info(host.getIp() + ":" + host.getUserCount());
		count++;
		return host;
	}
	
	/**
	 * 获取可用使用的vps（hosts）
	 * @param num
	 * @return
	 */
	private Hosts getAvailableHost(int num) {
		if(num > data.size()) {
            return null;
        }
		count = count % data.size();
		Hosts host = data.get(count);
		if(host == null || host.getType() != 1) {
			count++;
			num++;
			host = getAvailableHost(num);
		}
		return host;
	}
	
	/**
	 * 拨号判断(拨号判断1、vps主机访问请求达到临界值,拨号判断2、vps主机访问请求返回错误)
	 * @param host
	 * @return
	 */
	@Override
    public boolean dialingHost(Hosts host) {
		if(host.getUserCount() <= 0 && shellClientTask.execute(host)) { //没有用户使用并且进行拨号（不保证拨号成功！如果拨号失败，会有心跳程序定时扫描）
			return true;
		}
		return false;
	}
	
	@Override
    public synchronized void addHost(Hosts host) {
		for (Hosts oldHost : data) {
			if(oldHost.equals(host)) {
				data.remove(oldHost);
			}
		}
		data.add(host);
	}
	
	@Override
    public void remove(Hosts host) {
		data.remove(host);
	}
	
	@Override
    public int getSize() {
		return data.size();
	}
	
	/**
	 * 用来扫描vps主机是否丢失（心跳机制）
	 * @return
	 */
	@Override
    public Hosts getHostHeart(int num) {
		if(num < data.size()) {
			return data.get(num);
		} else {
			return null;
		}
	}

}
