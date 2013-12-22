package com.alipay.zdal.test.ut.datasource;


import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;import static com.alipay.ats.internal.domain.ATS.*;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.util.PoolCondition;
/**
 * ZDataSource.getPoolCondition():打印数据库连接
 * @author yin.meng
 *
 */
 @RunWith(ATSJUnitRunner.class)
 @Feature("打印数据库连接")
public class ZDS951020 extends ZDSTest{	

    @Subject("print connection:oracle")
    @Priority(PriorityLevel.NORMAL)
    @Test
	public void TC951021(){
		Step("创建oracle数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		Connection connection = null;
		Statement statement = null;
		
		localTxDSDo.setBackgroundValidation(false);
		localTxDSDo.setBackgroundValidationMinutes(10);
		localTxDSDo.setBlockingTimeoutMillis(2000);
		localTxDSDo.setCheckValidConnectionSQL("SELECT 1 from dual");
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@perf6.lab.alipay.net:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
        try {
        	localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
        localTxDSDo.setIdleTimeoutMinutes(10);
        localTxDSDo.setMaxPoolSize(12);
        localTxDSDo.setMinPoolSize(6);
        localTxDSDo.setNewConnectionSQL("SELECT 1 from dual");
        localTxDSDo.setPrefill(false);
        localTxDSDo.setPreparedStatementCacheSize(100);
        localTxDSDo.setQueryTimeout(180);
        localTxDSDo.setSharePreparedStatements(false);
        localTxDSDo.setTxQueryTimeout(false);
        localTxDSDo.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDSDo.setUserName("ACM");
        localTxDSDo.setUseFastFail(false);
        localTxDSDo.setValidateOnMatch(false);
        localTxDSDo.setValidConnectionCheckerClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.OracleValidConnectionChecker");
        localTxDSDo.setDsName("ds");
        try {
        	zDataSource = new ZDataSource(localTxDSDo);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		Step("获取数据源连接池数据");
		PoolCondition poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition\n");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()==12,"验证可用连接数");
		Assert.isTrue(poolCon.getConnectionCreatedCount()==0,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==0,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==0,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==0,"验证被使用过的最大的连接数");

		Step("创建数据库连接");
		try {
			connection = zDataSource.getConnection();
			statement = connection.createStatement();
			statement.executeQuery(getSql(8));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}		
		sleep(30000);
		
		Step("获取数据源连接池数据");
		poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()>0,"验证可用连接数");//
		Assert.isTrue(poolCon.getConnectionCreatedCount()==6,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==6,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==1,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==1,"验证被使用过的最大的连接数");
		
		Step("关闭连接");
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			connection=null;
		}		
		sleep(30000);
		
		Step("获取数据源连接池数据");
		poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()>0,"验证可用连接数");
		Assert.isTrue(poolCon.getConnectionCreatedCount()==6,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==6,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==0,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==1,"验证被使用过的最大的连接数");
		
		Step("销毁数据源");
		try {
			zDataSource.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    @Subject("print connection:mysql")
    @Priority(PriorityLevel.NORMAL)
    @Test
	public void TC951022(){
		Step("创建mysql数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		Connection connection = null;
		Statement statement = null;
		
		localTxDSDo.setBackgroundValidation(false);
		localTxDSDo.setBackgroundValidationMinutes(10);
		localTxDSDo.setBlockingTimeoutMillis(2000);
		localTxDSDo.setCheckValidConnectionSQL("SELECT 1 from dual");
		localTxDSDo.setConnectionURL("jdbc:mysql://arch-3.alipay.net:3306/zdatasource1");
		localTxDSDo.setDriverClass("com.mysql.jdbc.Driver");
        try {
        	localTxDSDo.setEncPassword("-76079f94c1e11c89");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLExceptionSorter");
		localTxDSDo.setIdleTimeoutMinutes(10);
		localTxDSDo.setMaxPoolSize(12);
		localTxDSDo.setMinPoolSize(6);
		localTxDSDo.setNewConnectionSQL("SELECT 1 from dual");
		localTxDSDo.setPrefill(false);
		localTxDSDo.setPreparedStatementCacheSize(100);
        localTxDSDo.setQueryTimeout(180);
        localTxDSDo.setSharePreparedStatements(false);
        localTxDSDo.setTxQueryTimeout(false);
        localTxDSDo.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDSDo.setUserName("root");
        localTxDSDo.setUseFastFail(false);
        localTxDSDo.setValidateOnMatch(false);
        localTxDSDo.setValidConnectionCheckerClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLValidConnectionChecker");
        localTxDSDo.setDsName("ds");
        try {
        	zDataSource = new ZDataSource(localTxDSDo);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		Step("获取数据源连接池数据");
		PoolCondition poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition\n");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()==12,"验证可用连接数");
		Assert.isTrue(poolCon.getConnectionCreatedCount()==0,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==0,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==0,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==0,"验证被使用过的最大的连接数");

		Step("创建数据库连接");
		try {
			connection = zDataSource.getConnection();
			statement = connection.createStatement();
			statement.executeQuery(getSql(5));
	
		} catch (SQLException e) {
			e.printStackTrace();
//			Fail();
		}
		
		Step("获取数据源连接池数据");
		poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()>0,"验证可用连接数");//
		Assert.isTrue(poolCon.getConnectionCreatedCount()==1,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==1,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==1,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==1,"验证被使用过的最大的连接数");
		
		Step("关闭连接");
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			connection=null;
		}
		
		Step("获取数据源连接池数据");
		poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getMinSize()==6,"验证最小连接数");
		Assert.isTrue(poolCon.getMaxSize()==12,"验证最大连接数");
		Assert.isTrue(poolCon.getAvailableConnectionCount()>0,"验证可用连接数");
		Assert.isTrue(poolCon.getConnectionCreatedCount()==1,"验证创建连接数");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==0,"验证销毁连接数");
		Assert.isTrue(poolCon.getConnectionCount()==1,"验证当前数据源管理的连接数");
		Assert.isTrue(poolCon.getInUseConnectionCount()==0,"验证当前在使用中的连接数");
		Assert.isTrue(poolCon.getMaxConnectionsInUseCount()==1,"验证被使用过的最大的连接数");
		
		try {
			zDataSource.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Subject("print destroyed connection")
    @Priority(PriorityLevel.NORMAL)
    @Test
	public void TC951023(){
		Step("创建数据源");
    	LocalTxDataSourceDO localTxDSDo = new LocalTxDataSourceDO();
		ZDataSource zDataSource = null;
		Connection connection = null;
		Statement statement = null;
		
		localTxDSDo.setBackgroundValidation(false);
		localTxDSDo.setBackgroundValidationMinutes(10);
		localTxDSDo.setBlockingTimeoutMillis(2000);
		localTxDSDo.setCheckValidConnectionSQL("SELECT 1 from dual");
		localTxDSDo.setConnectionURL("jdbc:oracle:thin:@perf6.lab.alipay.net:1521:perfdb6");
		localTxDSDo.setDriverClass("oracle.jdbc.OracleDriver");
        try {
        	localTxDSDo.setEncPassword("-7cda29b2eef25d0e");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDSDo.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.OracleExceptionSorter");
        localTxDSDo.setIdleTimeoutMinutes(1);
        localTxDSDo.setMaxPoolSize(12);
        localTxDSDo.setMinPoolSize(6);
        localTxDSDo.setNewConnectionSQL("SELECT 1 from dual");
        localTxDSDo.setPrefill(false);
        localTxDSDo.setPreparedStatementCacheSize(100);
        localTxDSDo.setQueryTimeout(180);
        localTxDSDo.setSharePreparedStatements(false);
        localTxDSDo.setTxQueryTimeout(false);
        localTxDSDo.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDSDo.setUserName("ACM");
        localTxDSDo.setUseFastFail(false);
        localTxDSDo.setValidateOnMatch(false);
        localTxDSDo.setValidConnectionCheckerClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.OracleValidConnectionChecker");
        localTxDSDo.setDsName("ds");
        try {
        	zDataSource = new ZDataSource(localTxDSDo);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}		
		
		Step("创建数据库连接");
		try {
			connection = zDataSource.getConnection();
			statement = connection.createStatement();
			statement.executeQuery(getSql(8));
			sleep(100000);
		} catch (SQLException e) {
			e.printStackTrace();
//			fail();
		}
		
		Step("获取数据源连接池数据");
		PoolCondition poolCon = zDataSource.getPoolCondition();
		Logger.info("PoolCondition");
		printPoolCondition(poolCon);
		
		Step("验证连接池数据");
		Assert.isTrue(poolCon.getConnectionDestroyedCount()==5,"验证销毁连接数");
		
		Step("关闭连接");
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			connection=null;
		}		
		
		try {
			zDataSource.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public  void printPoolCondition(PoolCondition con) {
    	Logger.info(" min : " + con.getMinSize() + " \n max : " + con.getMaxSize()
                           + "\n AvailableConnectionCount : " + con.getAvailableConnectionCount()
                           + "\n ConnectionCount : " + con.getConnectionCount()
                           + "\n ConnectionCreatedCount : " + con.getConnectionCreatedCount()
                           + "\n ConnectionDestroyedCount : " + con.getConnectionDestroyedCount()
                           + "\n InUseConnectionCount : " + con.getInUseConnectionCount()
                           + "\n MaxConnectionsInUseCoun : " + con.getMaxConnectionsInUseCount());
    }
}
