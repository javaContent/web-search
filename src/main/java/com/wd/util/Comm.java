package com.wd.util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comm {
	
	public final static int MAX_TOTAL_CONNECTIONS = 50; 
	public final static int MAX_ROUTE_CONNECTIONS = 50; 
	
	public final static int REQYEST_TIMEOUT = 10000;
	public final static int CONNECT_TIMEOUT = 10000;		//等待时间
	public final static int READ_TIMEOUT = 10000; 			//超时时间
	
	/*初始化，启动时添加队列里的ip*/
	public static final int Type_Init = 0;
	
	/*（定时验证）验证队列里的ip*/
	public static final int Type_Queue = 1;
	
	/*验证已可用ip*/
	public static final int Type_Pass = 2;
	
	/*验证ip*/
	public static final int Type_No = 3;
	
	public static final int Mysql_Update = 1;
	
	public static final int Mysql_Delete = 2;
	
}
