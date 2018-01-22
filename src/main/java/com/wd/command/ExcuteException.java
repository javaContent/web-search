package com.wd.command;

/**
 * Command执行异常
 * @author Administrator
 *
 */
public class ExcuteException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ExcuteException(){
		super();
	}
	
	public ExcuteException(String msg){
		super(msg);
	}
	
	public ExcuteException(Throwable t){
		super(t);
	}
	
	public ExcuteException(String msg,Throwable t){
		super(msg,t);
	}

}
