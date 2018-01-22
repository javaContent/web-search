package com.wd.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface SearchForWebServiceI {

	public String search(@WebParam(name = "param") String requestParam);

	/**
	 * 清除缓存
	 * @param validateCode 验证码
	 * @return
	 */
	public Boolean cleanCache(@WebParam(name = "param") String validateCode);
	
	/**
	 * 测试网络连通性
	 */
	public String test(@WebParam(name = "param") String requestParam);
	
}
