package com.wd.util;


/**
 * 常用代码的提取，如非空验证的代码
 * 
 * @author pan
 * 
 */
public class SimpleUtil {

	public static boolean strNotNull(String testStr) {

		if (null == testStr || "".equals(testStr.trim())) {
			return false;
		}
		return true;
	}

}
