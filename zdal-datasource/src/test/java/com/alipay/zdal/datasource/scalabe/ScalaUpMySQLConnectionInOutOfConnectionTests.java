package com.alipay.zdal.datasource.scalabe;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import com.alipay.zdal.datasource.scalabe.support.ConnectionMonitor;
import com.alipay.zdal.datasource.scalabe.support.ExceptionChecker;
import com.alipay.zdal.datasource.scalabe.support.SqlExecutor;
import com.alipay.zdal.datasource.scalabe.support.SqlQueryer;
import com.alipay.zdal.datasource.scalabe.support.SqlUpdater;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

@ContextConfiguration(locations ={
		"/scalable/zdal-mysql-small-pool-context.xml"
})
public class ScalaUpMySQLConnectionInOutOfConnectionTests extends AbstractJUnit4SpringContextTests  {

	private static final Logger             logger               = Logger.getLogger(ScalaUpMySQLConnectionInOutOfConnectionTests.class);
	
	public static final int QUERY_INTERVAL = 10;

	public static boolean isServerStarted = false;
	
	private static final int PERMITS_FIVE = 5;

	private static final int PERMITS_TEN = 10;
	
	private static final int QUERY_THREAD_SIZE = 80;
	
	private static final int UPDATE_THREAD_SIZE = 20;
	
	@Autowired
	@Qualifier("scalableMySQLDataSource")
	public ZScalableDataSource dataSource = null;
	
	public ConnectionMonitor monitor = null;
	
	public ExceptionChecker checker = null;
	
	@Autowired
	@Qualifier("scalableMySQLDataSource")
	public ZScalableDataSource scalableMySQLDataSource;

	@Autowired
	@Qualifier("mySQLTransactionTemplate")
	public TransactionTemplate mySQLTransactionTemplate;
	
	@Autowired
	@Qualifier("mySQLJdbcTemplate")
	public JdbcTemplate mySQLJdbcTemplate;
	
	private ThreadPoolExecutor threadPool;
	
	private List<SqlExecutor> executorList = null; 
	
	@Before
	public void prepareThreadPool(){
		threadPool = new ThreadPoolExecutor(10, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024));
	}
	
	private void validateConnectionStatis(ConnectionMonitor monitor) {
		if( !CollectionUtils.isEmpty(monitor.getConnectionPoolStat() )){
			for(HashMap<String, ?> statMap : monitor.getConnectionPoolStat() ){
				System.out.println("\\\\*************** Pool Statistics ************************//");
				for( Entry<String,?> infoEntry : statMap.entrySet()){
					System.out.println(infoEntry.getKey() + " : " + infoEntry.getValue());
				}
			}
		}		
	}

	/**
	 * This test is designed to test expectation of after connection pool size has been reseted
	 * without reboot connection pool under a case of when out of connections occurred.
	 * Preparation : 
	 * 1. Build a connection pool with size of 4.
	 * 2. Prepare 20 threads to execute SQL in update, select, delete
	 * 3. When the holding connection time get longer then usual, the out of connection exception happens.
	 * 3. Prepare 1 thread to reset connection pool size into 8 in middle of execution of 20 threads
	 * 4. Keep print statistics of connection pool
	 * Expectation:
	 * 1. The connections number scale up but under 8
	 * 2. All SQLs have been executed successfully.
	 * 3. No more connection exceptions happen.
	 * @throws SQLException
	 */
	@Test
	public void testScaleUpConnectionPoolSizeUnderOutOfConnection() throws SQLException{
		logger.setLevel(Level.DEBUG);
		logger.debug("|---------- Start Test ----------|");
		ConcurrentHashMap<Long, List<Exception>> exceptionCollection = new ConcurrentHashMap<Long, List<Exception>>();
		executorList = new ArrayList<SqlExecutor>();
		monitor = new ConnectionMonitor(dataSource, false);
		monitor.start();
		checker = new ExceptionChecker(exceptionCollection, 200, logger);
//		checker.start();
		
		SqlQueryer query = null;
		SqlUpdater updater = null;
		String sql = "select * from users";
		Object[] parameters = {};
		for( int index = 0; index < QUERY_THREAD_SIZE ; index++ ){
			query = new SqlQueryer(mySQLJdbcTemplate, sql, parameters, QUERY_INTERVAL,exceptionCollection);
			executorList.add(query);
			threadPool.execute(query);
		}
		String updateSql = "update users set address = 'zhejiang hangzhou at " + System.currentTimeMillis() + "'";
		for( int index = 0; index < UPDATE_THREAD_SIZE ; index++ ){
			updater = new SqlUpdater(mySQLJdbcTemplate, updateSql, parameters, QUERY_INTERVAL,exceptionCollection);
			threadPool.execute(updater);
			executorList.add(updater);
		}
		int exceptionCount = 0, afterResetExceptionCount = 0;
		try {
			Thread.sleep(5000);
			exceptionCount = countExceptions(exceptionCollection);
			logger.debug("Exception count: " + exceptionCount);
//			validateConnectionStatis(monitor);
			logger.debug("|-------- Reset pool size -------------|");
//			dataSource.getLocalTxDataSource().resetConnectionPoolMaxSize(PERMITS_FIVE);
			Thread.sleep(5000);
//			validateConnectionStatis(monitor);
			afterResetExceptionCount = countExceptions(exceptionCollection);
			logger.debug("Exception count: " + afterResetExceptionCount);
			assertTrue(afterResetExceptionCount < exceptionCount * 2);
			/*
			dataSource.getLocalTxDataSource().resetConnectionPoolMaxSize(PERMITS_TEN);
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolMaxSize(PERMITS_FIVE);
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolMaxSize(PERMITS_TEN);
			Thread.sleep(2000);*/
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}finally{
			shutdown();
		}
		logger.debug("|----------- Test End --------------|");
	}
	
	protected int countExceptions(ConcurrentHashMap<Long, List<Exception>> exceptionCollection){
		int count = 0;
		for( Entry<Long, List<Exception>> excepEntry : exceptionCollection.entrySet() ){
			if( null != excepEntry && null != excepEntry.getValue() && !excepEntry.getValue().isEmpty()){
				logger.debug("Thread " + excepEntry.getKey() + " Exception numbers " + excepEntry.getValue().size());
				count += excepEntry.getValue().size();
			}
		}
		return count;
	}
	
	public void shutdown(){
		if( null != executorList && !executorList.isEmpty()){
			for( SqlExecutor executor : executorList ){
				executor.stopRunning();
			}
		}
		monitor.stopRunning();
		checker.stopRunning();
		if( null != threadPool ){
			threadPool.shutdown();
			threadPool = null;
		}
	}
}