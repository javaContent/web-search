package com.wd.bo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.mail.SendMail;

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
	
	@Autowired
	private SendMail sendMail;
	
	@PostConstruct
	public void initProxy() {
		
	}
	
//	public ProxyInfo takeProxy(){									// 移除并返回队列头部的元素     如果队列为空，则阻塞
//		try {
//			return queue.take();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return null;
//	} 
	
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
	
	/**
	  * 从队列中拿取一个ProxyInfo（如果priority为true就遍历整个队列拿取优先级最高的）
	  * @param priority  是否使用优先级最高的
	  * @return
	  */
	public ProxyInfo getProxy(Boolean priority) {
		ProxyInfo proxyInfo = null;
		proxyInfo = pollProxy();
		if(priority) {
			for (int i = 0; i < getProxySize(); i++) {
				ProxyInfo proxy = peekProxy();			//返回第一个元素，不删除
				if(proxy != null && proxy.getLevel() > proxyInfo.getLevel()) {
					returnProxy(proxyInfo);
					proxyInfo = pollProxy();
				}
			}
		}
		if(proxyInfo == null) {
			sendMail.execute("谷歌接口ip数量提醒", "谷歌接口ip数量只剩"+getProxySize()+"！请及时处理！","634764467@qq.com");
		}
		return proxyInfo;
	}
	
	
}
