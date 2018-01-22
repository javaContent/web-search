package com.wd.module.http;

/**
 * 下载异常
 * @author Shenfu
 *
 */
public class DownloadException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DownloadException(){
		super();
	}
	
	public DownloadException(String msg){
		super(msg);
	}
	
	public DownloadException(Throwable t){
		super(t);
	}
	
	public DownloadException(String msg,Throwable t){
		super(msg,t);
	}

}
