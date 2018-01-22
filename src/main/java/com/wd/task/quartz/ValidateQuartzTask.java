package com.wd.task.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyQueue;
import com.wd.dao.ProxyDaoI;
import com.wd.task.validate.ValidateController;
import com.wd.task.validate.ValidateIP;
import com.wd.util.Comm;

/**
 * 定时验证可用ip
 * @author Administrator
 *
 */
@Component
@Configuration
@EnableAsync
@EnableScheduling
public class ValidateQuartzTask {
	
	@Autowired
	public ValidateIP validate;
	
	@Autowired
	public ProxyDaoI proxyDao;
	
	@Scheduled(cron="0 30 0/6 * * ?")
    public void doSomeWork(){
		ValidateController validPass = new ValidateController(validate,proxyDao,Comm.Type_Queue);
        Thread threadPass = new Thread(validPass,"初始化：验证已可用ip");
        threadPass.start();
    }

}
