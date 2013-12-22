/**
 * 
 */
package com.alipay.zdal.client.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alipay.zdal.client.jdbc.ZdalDataSource;

/**
 * 验证同库支持事务的batch
 * 
 * @author xiaoju.luo
 * @version $Id: TDDLBatchTest.java,v 0.1 2012-5-22 上午07:38:50 xiaoju.luo Exp $
 */
public class BatchTest {
	private static Log logger = LogFactory.getLog("BatchTest.class");
	private static String address = "mysql-1-2.bjl.alipay.net:3306";
	ZdalDataSource        ts;
	String tableName;
	// JDBC查询的db
	String dbName = "tddl_transation_0";
	// JDBC查询的tb
	String tbName = "user_0";
	Connection con;
	String user = "mysql";
	String psd = "mysql";

	@Before
	public void setUp() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "introspector/batch-context-ds.xml" });
		ts = (ZdalDataSource) context.getBean("tddl_ds");
	}

	@Test
	public void testBatch() {
		try {
			con = ts.getConnection();
			String iSql = "insert into user(user_id,age,name)values(?,?,?)";

			con.setAutoCommit(false);
			PreparedStatement psInsert = con.prepareStatement(iSql);

			for (int i = 0; i < 2; i++) {
				psInsert.setInt(1, i);
				psInsert.setInt(2, 4 * i);
				psInsert.setString(3, "test");
				psInsert.addBatch();
			}

			int[] a = psInsert.executeBatch();
			Assert.assertTrue(a.length >= 0);
			System.out.println("插入返回：" + a.length);
			con.commit();

		} catch (Exception e) {
			Assert.fail();
		}
		// jdbc查询数据入库情况
		dataCheckJDBC(dbName, tbName);

	}

	@Test
	// 不在同库的写事务失败，batch异常用例
	public void testBatchException() {
		try {
			con = ts.getConnection();
			String iSql = "insert into user(user_id,age,name)values(?,?,?)";
			con.setAutoCommit(false);
			PreparedStatement psInsert = con.prepareStatement(iSql);

			for (int i = 0; i < 2; i++) {
				psInsert.setString(1, "abc" + i);
				// 插入操作不属于同 库
				psInsert.setInt(2, 3 * i);
				psInsert.setString(3, "test");
				psInsert.addBatch();
			}
			psInsert.executeBatch();
			con.commit();
		} catch (SQLException e) {
			Assert
					.assertEquals(
							"batch操作只支持单库的事务,当前dbSelectorID=dataSource_0_w,缓存的dbId=dataSource_1_1_w",
							e.getMessage());
		} catch (Exception e) {
			Assert.fail();
		}

	}

	// jdbc清理数据 加上验证数据
	private void dataCheckJDBC(String dbName, String tbName) {
		Connection jdbcCon = null;
		try {
			String url = "jdbc:mysql://" + address + "/" + dbName;
			String clearSql = "delete from " + tbName + " where name='test'";
			jdbcCon = TddlCommon.getConnection(url, user, psd);
			PreparedStatement stateNormal = jdbcCon.prepareStatement(clearSql);
			int a = stateNormal.executeUpdate();
			System.out.println(a);
			Assert.assertEquals(2, a);
			logger.warn(dbName + "JDBC清理数据 " + a + "条");
		} catch (SQLException e) {
			Assert.fail(dbName + "JDBC清理数据失败！因为" + e);
		} catch (ClassNotFoundException e) {
			Assert.fail(dbName + "JDBC清理数据失败失败！因为" + e);
		} finally {
			try {
				jdbcCon.close();
			} catch (SQLException e) {
				Assert.fail("sql异常" + e);
			}
		}
	}

	@After
	public void tearDown() {
		try {
			con.close();
		} catch (SQLException e) {
			Assert.fail("连接关闭异常");
		}

	}
}
