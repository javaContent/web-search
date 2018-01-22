package org.pzy.module.mail;

import org.pzy.module.mail.bo.MainInfo;

public interface MailModuleI {

	/**
	 * 发送简单邮件，邮件内容只能是文本
	 * 
	 * @param mainInfo
	 */
	public boolean sendSimpleMail(MainInfo mainInfo);
}
