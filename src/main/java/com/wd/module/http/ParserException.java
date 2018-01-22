package com.wd.module.http;

/**
 * 文档解析异常
 * @author Administrator
 *
 */
public class ParserException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ParserException(){
		super();
	}
	
	public ParserException(String msg){
		super(msg);
	}
	
	public ParserException(Throwable t){
		super(t);
	}
	
	public ParserException(String msg,Throwable t){
		super(msg,t);
	}

}
