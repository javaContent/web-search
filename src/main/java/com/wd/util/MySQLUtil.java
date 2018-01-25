package com.wd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUtil {
	
//	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/crawlerdb?useUnicode=true&characterEncoding=utf8";
//	private static final String DB_USER = "root";
//	private static final String DB_PWD = "root";
	//阿里云
	private static final String DB_URL = "jdbc:mysql://220.168.42.19:3308/alibaba?useUnicode=true&characterEncoding=utf8";
	//韩国
//	private static final String DB_URL = "jdbc:mysql://220.168.42.19:3308/googleip?useUnicode=true&characterEncoding=utf8";
	private static final String DB_USER = "bse";
	private static final String DB_PWD = "bse321";
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	
	/**
	 * @return
	 */
	public Connection getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection con =DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
			return con;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  void closeConnection(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeStatement(Statement state){
		if(state!=null){
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeResultSet(ResultSet set){
		if(set!=null){
			try {
				set.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeAll(Connection con, Statement stat, ResultSet rs) {
		try {
			if (rs != null) rs.close();
			if (stat !=null) stat.close();
			if (con != null && !con.isClosed()) con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
