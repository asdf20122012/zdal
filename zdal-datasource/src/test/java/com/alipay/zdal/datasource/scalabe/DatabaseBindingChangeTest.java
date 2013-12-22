package com.alipay.zdal.datasource.scalabe;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalabe.support.SqlExecutor;
import com.alipay.zdal.datasource.scalabe.support.SqlQueryer;
import com.alipay.zdal.datasource.scalabe.support.SqlUpdater;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.DataSourceBindingChangeException;

@ContextConfiguration(locations ={
		"/scalable/zdal-mysql-context.xml"
})
public class DatabaseBindingChangeTest extends AbstractJUnit4SpringContextTests{

	public static final int QUERY_THREAD_SIZE = 20;
	
	public static final int UPDATE_THREAD_SIZE = 10;
	
	public static final int QUERY_INTERVAL = 10;
	
	private static final Logger             logger               = Logger
            .getLogger(DatabaseBindingChangeTest.class);
	
	@Autowired
	@Qualifier("scalableMySQLDataSource")
	public ZScalableDataSource scalableMySQLDataSource;
	
	@Autowired
	@Qualifier("newMySQLConfig")
	public LocalTxDataSourceDO newMySQLConfig;
	
	@Autowired
	@Qualifier("mySQLJdbcTemplate")
	public JdbcTemplate mySQLJdbcTemplate;
	
	@Autowired
	@Qualifier("mySQLTransactionTemplate")
	public TransactionTemplate mySQLTransactionTemplate;
	
	private ThreadPoolExecutor threadPool;
	
	private List<SqlExecutor> executorList = null; 
	
	@Before
	public void setUp() throws Exception {
		threadPool = new ThreadPoolExecutor(10, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024));
	}

	@After
	public void tearDown() throws Exception {
		if( null != executorList && !executorList.isEmpty()){
			for( SqlExecutor executor : executorList ){
				executor.stopRunning();
			}
		}
		/*monitor.stopRunning();
		checker.stopRunning();*/
		if( null != threadPool ){
			threadPool.shutdown();
			threadPool = null;
		}
	}

	@Test
	public void test() {
		assertNotNull(newMySQLConfig);
		assertNotNull(mySQLTransactionTemplate);
	}

	@Test
	public void testChangeBindingNormally(){
		logger.setLevel(Level.DEBUG);
		executorList = new ArrayList<SqlExecutor>();
		SqlQueryer query = null;
		SqlUpdater updater = null;
		String sql = "select * from users_0";
		Object[] parameters = {};
		for( int index = 0; index < QUERY_THREAD_SIZE ; index++ ){
			query = new SqlQueryer(mySQLJdbcTemplate, sql, parameters, QUERY_INTERVAL);
			executorList.add(query);
			threadPool.execute(query);
		}
		String updateSql = "update users_0 set address = 'zhejiang hangzhou at " + System.currentTimeMillis() + "'";
		for( int index = 0; index < UPDATE_THREAD_SIZE ; index++ ){
			updater = new SqlUpdater(mySQLJdbcTemplate, updateSql, parameters, QUERY_INTERVAL);
			threadPool.execute(updater);
			executorList.add(updater);
		}
		try {
			System.out.println("|------------------- BEGAIN -------------------|");
			Thread.sleep(2000);
			scalableMySQLDataSource.onDataSourceBindingChanged(newMySQLConfig);
			Thread.sleep(2000);
			System.out.println("|-------------------- END --------------------|");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		} catch (DataSourceBindingChangeException e) {
			e.printStackTrace();
			fail();
		} finally{
			
		}
	}
	
}
