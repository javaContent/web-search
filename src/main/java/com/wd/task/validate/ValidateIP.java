package com.wd.task.validate;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyInfo;
import com.wd.bo.ProxyQueue;
import com.wd.dao.ProxyDaoI;
import com.wd.dao.thread.ProxyThread;
import com.wd.mail.SendMail;
import com.wd.module.http.DocumentParser;
import com.wd.module.http.HttpConnectionManager;
import com.wd.module.http.QueryResult;
import com.wd.util.Comm;

@Component
public class ValidateIP {
	
	private static final Logger log=Logger.getLogger(ValidateIP.class);
	
	@Autowired
	public DocumentParser googleDocumentParser;
	
	@Autowired
	public HttpClient httpClient;
	
	@Autowired
	public ProxyDaoI proxyDao;
	
	@Autowired
	public ProxyQueue proxyQueueModule;
	
	@Autowired
	private SendMail sendMail;

	private String[] urls = {
			"http://scholar.google.com/scholar?hl=zh-CN&q=milk&btnG=&lr=",
			"http://scholar.google.com/scholar?q=movie&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=literature&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=physical&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=journal+of+cellular+biochemistry&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Preparation&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Communication&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Geochimica&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Electrochimica&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Transactions&btnG=&hl=zh-CN&as_sdt=0%2C5",
			"http://scholar.google.com/scholar?q=Advanced+Engineering+Science&btnG=&hl=zh-CN&as_sdt=0%2C5" };
	
	
	public void validateList(List<ProxyInfo> list, int type) {
		log.info("validateList：" + type);
		if(type == Comm.Type_Init) {
			for (ProxyInfo proxyInfo : list) {
				boolean isOffer = proxyQueueModule.returnProxy(proxyInfo);
				if(!isOffer) return;
			}
		}
		if(type == Comm.Type_Queue) {
			int proxySize =proxyQueueModule.getProxySize();
			for (int i = 0; i < proxySize; i++) {
				ProxyInfo proxyInfo = proxyQueueModule.pollProxy();
				validate(proxyInfo);
			}
		}
		if(type == Comm.Type_No) {
			for (ProxyInfo proxyInfo : list) {
				validate(proxyInfo);
			}
		} else {
			for (ProxyInfo proxyInfo : list) {
				boolean isContent = false;
				int proxySize =proxyQueueModule.getProxySize();
				for (int i = 0; i < proxySize; i++) {
					ProxyInfo proxy = proxyQueueModule.pollProxy();
					proxyQueueModule.returnProxy(proxy);
					if(proxy.getId() == proxyInfo.getId()) {
						isContent = true;
						break;
					}
				}
				if(!isContent) {
					log.info("开始验证：" + proxyInfo.getIp());
					boolean isOffer = validate(proxyInfo);
					log.info(proxyInfo.getIp() + " ip验证结束");
					if(!isOffer) break;
				}
			}
			if(proxyQueueModule.getProxySize() < 15) {
				sendMail.execute("谷歌接口ip数量提醒", "谷歌接口ip数量只剩"+proxyQueueModule.getProxySize()+"！请及时处理！","634764467@qq.com");
			}
		}
	}
	/**
	 * 如果返回false表示队列已满，验证程序可以退出了
	 * @param proxyInfo
	 * @return
	 */
	public boolean validate(ProxyInfo proxyInfo) {
		boolean isOffer = true;
		for (int i = 0; i < 2; i++) {
			try{
				QueryResult queryResult = null;
				String result = null;
				int n = (int) (1.0D + Math.random() * 10.0D);
				queryResult = httpClient.query(this.urls[n],null,proxyInfo);
				if(queryResult != null) {
					result = googleDocumentParser.getResult(queryResult.getContent(), null);
				}
				proxyInfo.setSpeed(queryResult.getCost());
				if(result == null || result.equals("{\"count\":\"0\",\"timeMap\":{},\"rows\":[]}")) {			//失败
					proxyInfo.setErrCount(1);
					System.out.println(queryResult.getContent());
				} else if(result.equals("出现验证码")) {
					proxyInfo.setSuccessCount(-1);
					result = null;
					break;
				} else {
					proxyInfo.setErrCount(-1);
					proxyInfo.setSuccessCount(1);
					proxyInfo.setSpeed(queryResult.getCost());
					isOffer = proxyQueueModule.returnProxy(proxyInfo);
					break;
				} 
			} catch(Exception e) {
			}finally{
			}
		}
		if(proxyInfo.getErrCount() >=4) {
			ProxyThread proxyThread = new ProxyThread(proxyDao, proxyInfo, Comm.Mysql_Delete);
	        Thread thread = new Thread(proxyThread,"数据库操作：Delete");
	        thread.start();
		} else {
			ProxyThread proxyThread = new ProxyThread(proxyDao, proxyInfo, Comm.Mysql_Update);
	        Thread thread = new Thread(proxyThread,"数据库操作：update");
	        thread.start();
		}
		return isOffer;
	}
	
}