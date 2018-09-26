package com.wd.email;

import com.wd.bo.Hosts;


public interface MailModuleI {

	/**
	 * 发送简单邮件，邮件内容只能是文本
	 * 
	 * @param mainInfo
	 */
	public boolean sendSimpleMail(Hosts hosts);
}
