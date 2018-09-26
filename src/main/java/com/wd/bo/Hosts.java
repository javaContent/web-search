package com.wd.bo;

public class Hosts {
	
	public Hosts(String name,String ip) {
		this.name = name;
		this.ip = ip;
	}
	
	private String name;
	
	private String ip;
	
	private long count;
	
	private int userCount;//用户同时使用量
	
	private int type = 1;//使用状态（1可使用，2拨号中）
	
	private long dialingTime; //拨号时间（时间戳）
	
	private int dialingCount;//拨号次数()
	
	private int errCount;	//请求错误次数

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getCount() {
		return count;
	}

	public void setCount() {
		this.count++;
	}
	
	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getDialingTime() {
		return dialingTime;
	}

	public void setDialingTime(long dialingTime) {
		this.dialingTime = dialingTime;
	}

	public int getDialingCount() {
		return dialingCount;
	}
	/**
	 * dialingCount自增
	 */
	public void setDialingCount() {
		this.dialingCount++;
	}
	/**
	 * 设置dialingCount=count
	 */
//	public void setDialingCount(int count) {
//		this.dialingCount = 0;
//	}

	/**
	 * count+1；
	 * userCount+1;
	 */
	public synchronized void increaseUserCount() {
		this.userCount++;
		this.count++;
	}
	/**
	 * userCount-1；
	 */
	public synchronized void decreaseUserCount() {
		this.userCount--;
	}
	
	public int getErrCount() {
		return errCount;
	}

	public void setErrCount(int errCount) {
		this.errCount++;
		if(errCount == 0) {
			this.errCount = errCount;
		}
	}

	public boolean equals(Hosts hosts) {
		if(hosts.getName().equals(this.name)) {
			return true;
		} else {
			return false;
		}
	}

}
