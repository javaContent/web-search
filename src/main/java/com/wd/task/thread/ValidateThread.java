package com.wd.task.thread;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.task.validate.ValidateIP;
/**
 * ip验证线程
 * @author Administrator
 *
 */
public class ValidateThread implements Runnable {

	private ValidateIP validate;
	
	private List<ProxyInfo> list;
	
	private CountDownLatch countDownLatch; 
	
	private int type;
	
	public ValidateThread(ValidateIP validate,List<ProxyInfo> list, CountDownLatch countDownLatch, int type ) {
		this.validate = validate;
		this.list = list;
		this.countDownLatch = countDownLatch;
		this.type = type;
	}

	@Override
	public void run() {
		validate.validateList(list, type);
		// 倒数器减1  
        countDownLatch.countDown();  
	}

}
