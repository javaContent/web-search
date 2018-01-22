package com.wd.command;

import java.util.Map;

public interface ICommand {
	
	/**
	 * 执行求求并返回处理结果
	 * @param params
	 * @return
	 */
	public String excute(Map<String,String> params) throws ExcuteException;

}
