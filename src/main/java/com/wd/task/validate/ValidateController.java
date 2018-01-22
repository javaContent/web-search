package com.wd.task.validate;

import java.util.List;
import java.util.concurrent.CountDownLatch;

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

	public ValidateController(ValidateIP validate,ProxyDaoI proxyDao,int type) {
		this.validate = validate;
		this.proxyDao = proxyDao;
		this.type = type;
	}

	public ProxyDaoI proxyDao;
	
	public ValidateIP validate;
	
	private int type;
	
	public void start() throws InterruptedException{
		List<ProxyInfo> list = null;
		list = proxyDao.getProxyPass();
		int count = list.size()/50;
		// 创建一个初始值为count的倒数计数器  
        CountDownLatch countDownLatch = new CountDownLatch(count); 
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
			Thread thread = new Thread(valid,"验证线程"+i);
			thread.start();
		}
		try {  
			// 阻塞当前线程，直到倒数计数器倒数到0  
			countDownLatch.await();  
	    } catch (InterruptedException e) {  
	    	e.printStackTrace();  
	    } 
	 }
	 
	@Override
	public void run() {
		try {
			this.start();
		} catch (InterruptedException e) {
		}
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		ValidateController.getInstance().start();
//	}

}
