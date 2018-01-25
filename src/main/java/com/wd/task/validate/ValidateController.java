package com.wd.task.validate;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.task.thread.ValidateThread;
import com.wd.util.Comm;

/**
 * IP验证控制器（分初始化、验证已可用ip、验证采集未验证ip）
 * 验证已可用ip（每天定时验证）
 * 验证采集未验证ip（需要一直周期性验证）
 *
 */
public class ValidateController implements Runnable {
	
	private static final Logger log=Logger.getLogger(ValidateController.class);

	public ValidateController(ValidateIP validate,ProxyDaoI proxyDao,int type) {
		this.validate = validate;
		this.proxyDao = proxyDao;
		this.type = type;
	}

	public ProxyDaoI proxyDao;
	
	public ValidateIP validate;
	
	private int type;
	
	public void start() throws InterruptedException{
		do{
			try {
				List<ProxyInfo> list = null;
				if(type == Comm.Type_Init) {
					list = proxyDao.getProxyInit();
				} else if(type == Comm.Type_No) {
					list = proxyDao.getProxyNo();
				} else {
					list = proxyDao.getProxyPass();
				}
				int count = list.size()/50;
				if(list.size()%50 == 0) count--;
				// 创建一个初始值为count的倒数计数器  
		        CountDownLatch countDownLatch = new CountDownLatch(count+1); 
		        log.info("countDownLatch: " + countDownLatch.getCount());
				for (int i = 0; i <= count; i++) {
					List<ProxyInfo> subList = null;
					if(i == count) {
						subList = list.subList(50*i, list.size()-1);
					} else {
						subList = list.subList(50*i, 50*(i+1));
					}
					if(type == Comm.Type_Queue && i != 0) {
						type = Comm.Type_Pass;
					}
					ValidateThread valid = new ValidateThread(validate,subList,countDownLatch,type);
					Thread thread = new Thread(valid,"验证线程:类型："+ type + "  第几个：" + i);
					thread.start();
				}
				try {  
					// 阻塞当前线程，直到倒数计数器倒数到0  
					countDownLatch.await();  
			    } catch (InterruptedException e) {  
			    	e.printStackTrace();  
			    } 
			} catch(Exception e) {
				e.printStackTrace();  
			}
		} while (type == Comm.Type_No);
		log.info("线程结束！type：" + type + "  name:" + Thread.currentThread().getName());
	 }
	 
	@Override
	public void run() {
		try {
			this.start();
		} catch (InterruptedException e) {
		}
	}

}
