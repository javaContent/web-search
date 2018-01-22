package com.wd.module.http;

/**
 * 正常下载但是访问权限异常,访问被限制
 * @author Shenfu
 *
 */
public class ForbiddenException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ForbiddenException(){
		super();
	}
	
	public ForbiddenException(String msg){
		super(msg);
	}
	
	public ForbiddenException(Throwable t){
		super(t);
	}
	
	public ForbiddenException(String msg,Throwable t){
		super(msg,t);
	}

}
