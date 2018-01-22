package com.wd.bo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Proxy代理ip队列
 * @author Administrator
 *
 */
@Component("proxyQueueModule")
public class ProxyQueue {
	
	private static final Logger logger = Logger.getLogger(ProxyQueue.class);
	
	/**ProxyInfo代理ip队列*/
	private static ArrayBlockingQueue<ProxyInfo> queue = new ArrayBlockingQueue<ProxyInfo>(30);
	
	@PostConstruct
	public void initProxy() {
		
	}
	
	public ProxyInfo takeProxy(){									// 移除并返回队列头部的元素     如果队列为空，则阻塞
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ProxyInfo pollProxy(){
		try {
			return queue.poll(100,TimeUnit.MICROSECONDS);			//移除并返问队列头部的元素    如果队列为空，则返回null
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ProxyInfo peekProxy(){
		return queue.peek();					//返回队列头部的元素             如果队列为空，则返回null
	}
	
	public int getProxySize(){
		logger.info("当前可用的IP总数为:"+queue.size());
		return queue.size();
	}
	
	public boolean returnProxy(ProxyInfo proxy){
		boolean isOffer = queue.offer(proxy);				//不阻塞
		return isOffer;
	}
	
	
}
