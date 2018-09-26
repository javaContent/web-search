package com.wd.email.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wd.bo.Hosts;
import com.wd.email.MailModuleI;
import com.wd.email.bo.MainInfo;
import com.wd.email.bo.MyAuthenticator;

@Service("mailModule")
public class MailModuleImpl implements MailModuleI {
	
	@Override
	public boolean sendSimpleMail(Hosts hosts) {
		MainInfo mailInfo = sendEmail(hosts);
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			throw new RuntimeException("邮件发送失败!", ex);
		}
	}
	
	
	@Value("${host}")
	private String host;
	
	@Value("${user}")
	private String user;
	
	@Value("${pwd}")
	private String pwd;
	
	@Value("${address}")
	private String address;
	
	private MainInfo sendEmail(Hosts hosts) {
		String subject = "谷歌接口ip拨号失败提醒！";
		String content = "vps:" + hosts.getName() + "长时间拨号失败，请注意！";
		MainInfo mailInfo = new MainInfo();
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setMailServerHost(host);
		mailInfo.setUserName(user);
		mailInfo.setPassword(pwd);// 您的邮箱密码
		mailInfo.setFromAddress(user);
		mailInfo.setToAddress(address);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		return mailInfo;
	}
}
