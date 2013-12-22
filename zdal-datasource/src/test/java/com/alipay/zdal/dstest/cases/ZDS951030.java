package com.alipay.zdal.dstest.cases;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;import static com.alipay.ats.internal.domain.ATS.*;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.dstest.utils.ZDSTest;

/**
 *ZDataSource.checkParam：LocalTxDataSourceDO各参数为空 
 * @author yin.meng
 *
 */
@RunWith(ATSJUnitRunner.class)
@Feature("ZDataSource.checkParam：LocalTxDataSourceDO任一参数为空")
public class ZDS951030  extends ZDSTest{
	
    @Subject("未设置dsName")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951031() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
			Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("DsName is null"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置connection url")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951032() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("connection URL is null"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置userName")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951033() {		
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("username is null"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置driverClass")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951034() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("driverClass is null"),"验证异常信息");
		}
	}	

	
    @Subject("未设置pwd和 encPwd")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951035() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;

		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("both pwd and encPwd are null"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置minSize")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951036() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("minSize is unset"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置maxSize")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951037() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("maxSize is unset"),"验证异常信息");
		}
	}
	
	
    @Subject("未设置preparedStatementCacheSize")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951038() {
    	Step("创建数据源");
		LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("preparedStatementCacheSize is unset"),"验证异常信息");
		}
	}
	
    @Subject("未设置ExceptionSorterClassName")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void testTC951039() {
    	Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@10.253.94.6:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
		try {
			localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setPreparedStatementCacheSize(100);
		localTxDSDo.setUserName("ACM");
		localTxDSDo.setDsName("ds");

		try {
			zDataSource = new ZDataSource(localTxDSDo);
			fail();
		} catch (Exception e) {
	    	Step("验证");
			Logger.error(e.getMessage());
			Assert.isTrue(e.getClass().equals(IllegalArgumentException.class),"验证异常类");
			Assert.isTrue(e.getMessage().equalsIgnoreCase("ExceptionSorterClassName is null"),"验证异常信息");
		}
	}
}
