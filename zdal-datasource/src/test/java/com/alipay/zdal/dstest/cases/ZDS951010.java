package com.alipay.zdal.dstest.cases;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import static com.alipay.ats.internal.domain.ATS.*;

import com.alipay.zdal.dstest.utils.ZDSTest;


/**
 * mysql/oracle执行sql
 * @author yin.meng
 *
 */
@RunWith(ATSJUnitRunner.class)
@Feature("mysql/oracle执行sql")
public class ZDS951010 extends ZDSTest{	
	@Before
	public void zdsSetUp() throws Exception{
		dataSourceBeans[0] = getDataSourceBean(2);
		dataSourceBeans[1] = getDataSourceBean(1);
	}
	
	@After
	public void zdsTearDown() {
		try {
			statements[0].executeUpdate(getSql(6));
			statements[1].executeUpdate(getSql(9));
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}	
		
	}

    @Subject("mysql执行sql")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
	@Test
	public void TC951011(){
		Step("执行insert操作");
		int result_1 = 0;
		try {
			result_1 = statements[0].executeUpdate(getSql(4));
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}
		
		Step("验证insert结果");
		Logger.info("insert操作结果" + result_1);
		Assert.isTrue(result_1 == 1,"插入操作验证");

		Step("执行select操作");
		ResultSet result_2;
		String res_2 = null;
		try {
			result_2 = statements[0].executeQuery(getSql(5));
			result_2.next();
			res_2 = result_2.getString(1);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}

		Step("验证select结果");
		Logger.info("select操作结果" + res_2);
		Assert.isTrue(res_2.equalsIgnoreCase("hello"),"插入操作验证");

		
		Step("执行delete操作");
		int result_3 = 0;
		try {
			result_3 = statements[0].executeUpdate(getSql(6));
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}
		
		Step("验证delete结果");
		Logger.info("delete操作结果" + result_3);
		Assert.isTrue(result_3 == 1,"删除操作验证");
	}
	
    @Subject("oracle执行sql")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
	@Test
	public void TC951012(){
		Step("执行insert操作");
		int result_1 = 0;
		try {
			result_1 = statements[1].executeUpdate(getSql(7));
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}
		
		Step("验证insert结果");
		Logger.info("insert操作结果" + result_1);
		Assert.isTrue(result_1 == 1,"插入操作验证");

		Step("执行select操作");
		ResultSet result_2 = null;
		String res_2 = null;
		try {
			result_2 = statements[1].executeQuery(getSql(8));
			result_2.next();
			res_2 = result_2.getString(1);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				result_2.close();
			} catch (SQLException e1) {
				Logger.error(e1.getMessage());
			}
		}
		
		Step("验证select结果");
		Logger.info("select操作结果" + res_2);
		Assert.isTrue(res_2.equalsIgnoreCase("hello"),"插入操作验证");

		Step("执行delete操作");
		int result_3 = 0;
		try {
			result_3 = statements[1].executeUpdate(getSql(9));
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}
		
		Step("验证delete结果");
		Logger.info("delete操作结果" + result_3);
		Assert.isTrue(result_3 == 1,"删除操作验证");
	}
	
    @Subject("mysql执行sql,preparestatment")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
	@Test
	public void TC951013(){
    	Step("执行insert操作");
		String sql = "insert into test1 values(?,'hello')";
		int result_1 = 0;
		PreparedStatement preparedStatement_1 =null;
		try {
			preparedStatement_1 = connections[0].prepareStatement(sql);
			preparedStatement_1.setInt(1,99);
			result_1 = preparedStatement_1.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				preparedStatement_1.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_1 = null;
			}
		}
		
		Step("验证insert结果");
		Logger.info("insert操作结果" + result_1);
		Assert.isTrue(result_1 == 1,"插入操作验证");

		
		Step("执行select操作");
		sql = "select colu2 from test1 where clum = ?";
		ResultSet result_2 = null;
		String res_2 = null;
		PreparedStatement preparedStatement_2 =null;
		try {
			preparedStatement_2 = connections[0].prepareStatement(sql);
			preparedStatement_2.setInt(1,99);
			result_2 = preparedStatement_2.executeQuery();
			result_2.next();
			res_2 = result_2.getString(1);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				result_2.close();
				preparedStatement_2.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_2 = null;
			}
		}
		
		Step("验证select结果");
		Logger.info("select操作结果" +res_2);
		Assert.isTrue(res_2.equalsIgnoreCase("hello"),"插入操作验证" );

		Step("执行delete操作");
		sql = "delete from test1 where clum = ?";
		int result_3=0;
		PreparedStatement preparedStatement_3 =null;
		try {
			preparedStatement_3 = connections[0].prepareStatement(sql);
			preparedStatement_3.setInt(1,99);
			result_3 = preparedStatement_3.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				preparedStatement_3.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_3 = null;
			}
		}
		
		Step("验证delete结果");
		Logger.info("delete操作结果" + result_3);
		Assert.isTrue(result_3 == 1,"删除操作验证");
	}
	
    @Subject("oracle执行sql,preparestatment")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
	@Test
	public void TC951014(){
    	Step("执行insert操作");
		String sql = "insert into ACM_TARGET_RECORD (id,test_varchar,test_date,int_field_1,int_field_2,var_field_1,var_field_2) values (?,'hello',to_date('2012-06-15 20:46:34','YYYY-MM-DD-HH24:MI:SS'),1,1,'a','b')";
		int result_1 = 0;
		PreparedStatement preparedStatement_1 =null;
		try {
			preparedStatement_1 = connections[1].prepareStatement(sql);
			preparedStatement_1.setInt(1,99);
			result_1 = preparedStatement_1.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				preparedStatement_1.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_1 = null;
			}
		}
		
		Step("验证insert结果");
		Logger.info("insert操作结果" + result_1);
		Assert.isTrue(result_1 == 1,"插入操作验证");

		
		Step("执行select操作");
		sql = "select test_varchar from ACM_TARGET_RECORD where id = ?";
		ResultSet result_2 = null;
		String res_2 = null;
		PreparedStatement preparedStatement_2 =null;
		try {
			preparedStatement_2 = connections[1].prepareStatement(sql);
			preparedStatement_2.setInt(1,99);
			result_2 = preparedStatement_2.executeQuery();
			result_2.next();
			res_2 = result_2.getString(1);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				result_2.close();
				preparedStatement_2.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_2 = null;
			}
		}
		
		Step("验证select结果");
		Logger.info("select操作结果" +res_2);
		Assert.isTrue(res_2.equalsIgnoreCase("hello"),"插入操作验证");

		Step("执行delete操作");
		sql = "delete from ACM_TARGET_RECORD where id = ?";
		int result_3=0;
		PreparedStatement preparedStatement_3 =null;
		try {
			preparedStatement_3 = connections[1].prepareStatement(sql);
			preparedStatement_3.setInt(1,99);
			result_3 = preparedStatement_3.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				preparedStatement_3.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_3 = null;
			}
		}
		
		Step("验证delete结果");
		Logger.info("delete操作结果" + result_3);
		Assert.isTrue(result_3 == 1,"删除操作验证");
	}

    @Subject("mysql执行sql,preparestatment")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
	@Test
	public void TC951016(){
    	Step("执行insert操作");
		String sql = "insert into test1 values(?,'hello')";
		int result_1 = 0;
		PreparedStatement preparedStatement_1 =null;
		try {
			preparedStatement_1 = connections[0].prepareStatement(sql);
			preparedStatement_1.setInt(1,99);
			preparedStatement_1.execute();
			result_1 = preparedStatement_1.getUpdateCount();
			Logger.error("^^^^^^^^^^^"+result_1);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
		}finally{
			try {
				preparedStatement_1.close();
			} catch (SQLException e) {
				Logger.error(e.getMessage());
			}finally{
				preparedStatement_1 = null;
			}
		}
		
		Step("验证insert结果");
		Logger.info("insert操作结果" + result_1);
		Assert.isTrue(result_1 == 1,"插入操作验证");
		}
}