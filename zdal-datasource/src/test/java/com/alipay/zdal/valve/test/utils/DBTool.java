package com.alipay.zdal.valve.test.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;


public class DBTool {
	private DataSource dataSource;
	 
	public DBTool(){}
	
	public DBTool(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public void init(){
		Connection connection;
		Statement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
//			statement.executeUpdate("create database testh2");
//			statement.executeUpdate("use testh2");
			statement.executeUpdate("DROP TABLE IF EXISTS `test1`");
			statement.executeUpdate("create table test1(id INT not null primary key," +
					"name VARCHAR(20) not null,address VARCHAR(50) not null,email VARCHAR(20) not null)");
			statement.executeUpdate("insert into test1 values(1,'nike','address','a@a.com')");
			statement.executeUpdate("DROP TABLE IF EXISTS `preparedStatementTable`");
			statement.executeUpdate("create table preparedStatementTable(clum1 numeric(6),clum2 Blob," +
					"clum3 boolean)");
//			statement.executeUpdate("insert into preparedStatementTable (clum1,clum2) values" +
//					"(1,2)");
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		}
	}
	
	/**
	 * 建表
	 * @param createStatement 建表sql语句
	 */
	public void init(String createStatement){
		Connection connection;
		Statement statement;
		String tableName = createStatement.split("\\(")[0].split(" ")[2];
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
			statement.executeUpdate(createStatement);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addDB(){
		Connection connection;
		Statement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("create database testh3");
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		}
	}
	
	public static void main(String [] args){
		//new DBTool().init("create table test1(id INT");
		System.out.println("STRINGDECODE('\ufffd')".equals("STRINGDECODE('\ufffd')"));
	}
}
