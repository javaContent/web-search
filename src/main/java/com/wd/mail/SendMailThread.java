package com.wd.mail;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.pzy.module.mail.MailModuleI;
import org.pzy.module.mail.bo.MainInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.task.validate.ValidateController;
import com.wd.task.validate.ValidateIP;
/**
 * 发送邮件线程
 * @author Administrator
 *
 */
public class SendMailThread implements Runnable {
	
	MailModuleI mailModule;
	
	MainInfo mailInfo;
	
	private static final Logger log=Logger.getLogger(SendMailThread.class);

	public SendMailThread(MailModuleI mailModule,MainInfo mailInfo) {
		this.mailModule = mailModule;
		this.mailInfo = mailInfo;
	}

	@Override
	public void run() {
		mailModule.sendSimpleMail(mailInfo);
	}

}
