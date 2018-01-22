package com.wd.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wd.bo.ProxyInfo;
import com.wd.dao.ProxyDaoI;
import com.wd.util.MySQLUtil;

@Service
public class ProxyDao extends MySQLUtil implements ProxyDaoI  {

	@Override
	public List<ProxyInfo> getProxy() {
		String sql="select id,ip,port,cookie,success_times as successTimes, err_count as errCount from ips";
		Connection connection = null;
		Statement state=null;
		ResultSet rs=null;
		List<ProxyInfo> list = new ArrayList();
		try{
			connection = getConnection();
			state=connection.createStatement();
			rs=state.executeQuery(sql);
			while(rs.next()){
				ProxyInfo proxyInfo=new ProxyInfo();
				proxyInfo.setIp(rs.getString("ip"));
				proxyInfo.setPort(rs.getString("port"));
				proxyInfo.setSuccessCount(rs.getInt("successTimes"));
				proxyInfo.setErrCount(rs.getInt("errCount"));
				proxyInfo.setLevel(1);			//优先级（暂时默认）
				list.add(proxyInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeAll(connection, state, rs);
		}
		return list;
	}
	
	@Override
	public List<ProxyInfo> getProxyPass() {
		String sql="select id,ip,port,success_times as successTimes, err_count as errCount from ips where validate=1 and success_times > 0 ORDER BY id DESC";
		Connection connection = null;
		Statement state=null;
		ResultSet rs=null;
		List<ProxyInfo> list = new ArrayList();
		try{
			connection = getConnection();
			state=connection.createStatement();
			rs=state.executeQuery(sql);
			while(rs.next()){
				ProxyInfo proxyInfo=new ProxyInfo();
				proxyInfo.setId(rs.getInt("id"));
				proxyInfo.setIp(rs.getString("ip"));
				proxyInfo.setPort(rs.getString("port"));
				proxyInfo.setSuccessCount(rs.getInt("successTimes"));
				proxyInfo.setErrCount(rs.getInt("errCount"));
				proxyInfo.setLevel(1);			//优先级（暂时默认）
				list.add(proxyInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeAll(connection, state, rs);
		}
		return list;
	}
	
	@Override
	public List<ProxyInfo> getProxyNo() {
		String sql="select id,ip,port,success_times as successCount, err_count as errCount from ips where validate=0 and success_times is null";
		Connection connection = null;
		Statement state=null;
		ResultSet rs=null;
		List<ProxyInfo> list = new ArrayList();
		try{
			connection = getConnection();
			state=connection.createStatement();
			rs=state.executeQuery(sql);
			while(rs.next()){
				ProxyInfo proxyInfo=new ProxyInfo();
				proxyInfo.setId(rs.getInt("id"));
				proxyInfo.setIp(rs.getString("ip"));
				proxyInfo.setPort(rs.getString("port"));
				proxyInfo.setLevel(1);			//优先级（暂时默认）
				proxyInfo.setSuccessCount(rs.getInt("successCount"));
				proxyInfo.setErrCount(rs.getInt("errCount"));
				list.add(proxyInfo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeAll(connection, state, rs);
		}
		return list;
	}
	/**
	 * 暂时默认修改出现验证码的（以后添加优先级）
	 */
	@Override
	public void updateProxy(ProxyInfo proxyInfo) {
		Connection con = null;
        PreparedStatement pstm = null;
		ResultSet rs = null;
		String sql="update ips set validate=?, success_times = ? ,err_count = ? , speed = ? where id= ?";
		try {
			con = getConnection();
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, 1);
			pstm.setInt(2, proxyInfo.getSuccessCount());
			pstm.setInt(3, proxyInfo.getErrCount());
			pstm.setLong(4, proxyInfo.getSpeed());
			pstm.setInt(5, proxyInfo.getId());
			pstm.execute();    
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstm, rs);
		}
	}

	@Override
	public void deleteProxy(ProxyInfo proxyInfo) {
		String sql = "delete from ips where id = ? ";
		Connection connection = null;
		PreparedStatement state=null;
		ResultSet rs=null;
		int result = 0;
		try{
			connection =getConnection();
			state = connection.prepareStatement(sql);
			state.setInt(1, proxyInfo.getId());
			result = state.executeUpdate(sql);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeAll(connection, state, rs);
		}
	}

}
