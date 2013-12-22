/**
 * 
 */
package com.alipay.zdal.client.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import junit.framework.Assert;

/**
 * 需要通过jdbc查询的公用获得连接类
 * 
 * @author xiaoju.luo
 * 
 */
public class TddlCommon {
	private static Log logger = LogFactory.getLog("TddlCommon.class");
	
	
//	public static Connection getConnection(String url) throws SQLException,
//			java.lang.ClassNotFoundException {
//		// 第一步：加载MySQL的JDBC的驱动
//		Class.forName("com.mysql.jdbc.Driver");
//		// 取得连接的url,能访问MySQL数据库的用户名,密码；数据库名
//
//		String username = "mysql";
//		String password = "mysql";
//
//		// 第二步：创建与MySQL数据库的连接类的实例
//		Connection con = DriverManager.getConnection(url, username, password);
//		return con;
//	}
	
	public static Connection getConnection(String url,String user,String psd) throws SQLException,
	java.lang.ClassNotFoundException {
// 第一步：加载MySQL的JDBC的驱动
Class.forName("com.mysql.jdbc.Driver");
//// 取得连接的url,能访问MySQL数据库的用户名,密码；数据库名
//
//String username = "mysql";
//String password = "mysql";

// 第二步：创建与MySQL数据库的连接类的实例
Connection con = DriverManager.getConnection(url, user, psd);
return con;
}
	public static void dataClear(String url,String clearSql,String user, String psd){
		try {
			Connection jdbcCon;
			jdbcCon = TddlCommon.getConnection(url,user,psd);
			PreparedStatement clearState = jdbcCon.prepareStatement(clearSql);
			int clearCount = clearState.executeUpdate();
			logger.warn("表数据清理成功，数据量为：" + clearCount);
			jdbcCon.close();
		} catch (Exception e) {
			Assert.fail("数据清理失败");
		}
	}
	
	// 测试插入的数据可以从 预期的库表中取出，自动校验tddl分库分表规则的正确性
	public static ResultSet dataCheckJDBC(String url,String querySqlJDBC,String user,String psd) {
		ResultSet result=null;
		try {
			Connection jdbcCon;
			jdbcCon = TddlCommon.getConnection(url,user,psd);
			// 不带参数的处理
			PreparedStatement stateNormal = jdbcCon
					.prepareStatement(querySqlJDBC);
			 result = stateNormal.executeQuery(); 
			
		} catch (Exception e) {
			Assert.fail("用jdbc查询失败");
		}
		return result;
		//logger.warn(result);
		
	}
	
	// jdbc插入
//	public static int dataInsertJDBC(String url,String insertSqlJDBC) {
//		
//		int rNumber=0;
//		try {
//			Connection jdbcCon;
//			jdbcCon = TddlCommon.getConnection(url);
//			// 不带参数的处理
//			PreparedStatement stateNormal = jdbcCon
//					.prepareStatement(insertSqlJDBC);
//			rNumber = stateNormal.executeUpdate();
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail("用jdbc写入失败");
//		}
//		return rNumber;
//		
//	}
	
	// jdbc插入
	public static int dataInsertJDBC(String url,String insertSqlJDBC,String user,String psd) {
		
		int rNumber=0;
		try {
			Connection jdbcCon;
			jdbcCon = TddlCommon.getConnection(url,user,psd);
			// 不带参数的处理
			PreparedStatement stateNormal = jdbcCon
					.prepareStatement(insertSqlJDBC);
			rNumber = stateNormal.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("用jdbc写入失败");
		}
		return rNumber;
		
	}
}
