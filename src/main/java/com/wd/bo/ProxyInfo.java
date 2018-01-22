package com.wd.bo;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyInfo extends Authenticator {
	
	private int id;
	/**
	 * 代理ip
	 */
	private String ip;
	/**
	 * 代理端口
	 */
	private String port;
	
	/**
	 * 优先级
	 */
	private int level;
	
	private int successCount;
	
	private int errCount;
	
	/*访问时长*/
	private long speed;

	public ProxyInfo() {
	}
	
	public ProxyInfo(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * 根据succesCount、errCount、speed重写优先级
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

//	@Override
//	protected PasswordAuthentication getPasswordAuthentication() {
//		return new PasswordAuthentication(this.account, this.pwd.toCharArray());
//	}
	
	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount += successCount;
		if(successCount <= 0) {
			this.successCount = 0;
		}
	}

	public int getErrCount() {
		return errCount;
	}

	public void setErrCount(int errCount) {
		this.errCount += errCount;
		if(errCount <= 0) {
			this.errCount = 0;
		}
		
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return this.ip + ":" + this.port;
	}
}
