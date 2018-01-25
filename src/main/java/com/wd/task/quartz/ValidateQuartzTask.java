package com.wd.task.quartz;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wd.bo.ProxyQueue;
import com.wd.dao.ProxyDaoI;
import com.wd.mail.SendMail;
import com.wd.task.validate.ValidateController;
import com.wd.task.validate.ValidateIP;
import com.wd.util.Comm;

/**
 * 定时验证可用ip
 * @author Administrator
 *
 */
//@Component("validateQuartzTask")
public class ValidateQuartzTask {
	
	private static final Logger log=Logger.getLogger(ValidateQuartzTask.class);
	
	@Autowired
	public ValidateIP validate;
	
	@Autowired
	public ProxyDaoI proxyDao;
	
	@Autowired
	private SendMail sendMail;
	
    public void execute(){
		ValidateController validPass = new ValidateController(validate,proxyDao,Comm.Type_Queue);
        Thread threadPass = new Thread(validPass,"定时验证验证已可用ip");
        threadPass.start();
        log.info("定时验证验证已可用ip");
    }
	
	public void init(){
		ValidateController validPass = new ValidateController(validate,proxyDao,Comm.Type_No);
        Thread threadPass = new Thread(validPass,"持续验证采集ip");
        threadPass.start();
        log.info("持续验证采集ip");
    }

}
