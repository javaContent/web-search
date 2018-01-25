package com.wd.mail;

import org.pzy.module.mail.MailModuleI;
import org.pzy.module.mail.bo.MainInfo;
import org.pzy.module.mail.impl.MailModuleImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wd.task.thread.ValidateThread;

@Component("sendMail")
public class SendMail {

	private MailModuleI mailModule = new MailModuleImpl();

	@Value("${mail_user}")
	private String account;
	
	@Value("${mail_pwd}")
	private String pwd;
	
	@Value("${mail_server_host}")
	private String smtpHost;

	public SendMail() {
	}

	public void execute(String title,String content,String email) {
		MainInfo mailInfo = new MainInfo();
		mailInfo.setMailServerHost(smtpHost);
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName(account);
		mailInfo.setPassword(pwd);// 您的邮箱密码
		mailInfo.setFromAddress(account);
		mailInfo.setToAddress(email);
		mailInfo.setSubject(title);
		mailInfo.setContent(content);	
//		mailModule.sendSimpleMail(mailInfo);
		
		SendMailThread valid = new SendMailThread(mailModule,mailInfo);
		Thread thread = new Thread(valid,"发送邮件");
		thread.start();
	}
}
