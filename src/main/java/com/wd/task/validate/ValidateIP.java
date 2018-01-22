package com.wd.task.validate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyInfo;
import com.wd.bo.ProxyQueue;
import com.wd.dao.ProxyDaoI;
import com.wd.mail.SendMail;
import com.wd.module.http.DocumentParser;
import com.wd.module.http.HttpConnectionManager;
import com.wd.module.http.QueryResult;
import com.wd.util.Comm;

@Component
public class ValidateIP {
	
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
		if(type == Comm.Type_Queue) {
			for (int i = 0; i < proxyQueueModule.getProxySize(); i++) {
				ProxyInfo proxyInfo = proxyQueueModule.pollProxy();
				boolean isOffer = validate(proxyInfo);
//				if(!isOffer) break;
			}
		}
		for (ProxyInfo proxyInfo : list) {
			boolean isContent = false;
			for (int i = 0; i < proxyQueueModule.getProxySize(); i++) {
				ProxyInfo proxy = proxyQueueModule.peekProxy();
				if(proxy.getId() == proxyInfo.getId()) {
					isContent = true;
					break;
				}
			}
			if(!isContent) {
				boolean isOffer = validate(proxyInfo);
				if(!isOffer) break;
			}
		}
		if(proxyQueueModule.getProxySize() < 10) {
			sendMail.execute("谷歌接口ip数量提醒", "谷歌接口ip数量只剩"+proxyQueueModule.getProxySize()+"！请及时处理！","yangshuaifei@hnwdkj.com");
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
//				HttpClient client = new HttpClient(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort()));
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
		if(proxyInfo.getErrCount() >=5) {
			proxyDao.deleteProxy(proxyInfo);
		} else {
			proxyDao.updateProxy(proxyInfo);
		}
		return isOffer;
	}
	/**
	 * 测试，不进行验证
	 * @param proxyInfo
	 * @return
	 */
	public boolean validateTest(ProxyInfo proxyInfo) {
		proxyQueueModule.returnProxy(proxyInfo);
		return true;
	}
	
}