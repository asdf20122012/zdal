package com.alipay.zdal.test.ut.datasource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

@ContextConfiguration(locations ={
		"/scalable/zdal-mysql-context.xml"
})
public class ScalaUpAndDownMySQLConnectionsTests extends AbstractJUnit4SpringContextTests  {

	private static final Logger             logger               = Logger
            .getLogger(ScalaUpAndDownMySQLConnectionsTests.class);
	
	public static final int QUERY_INTERVAL = 10;

	public static boolean isServerStarted = false;
	
	private static final int PERMITS_FIVE = 5;

	private static final int PERMITS_TEN = 10;
	
	private static final int QUERY_THREAD_SIZE = 20;
	
	private static final int UPDATE_THREAD_SIZE = 20;
	
	@Autowired
	@Qualifier("scalableMySQLDataSource")
	public ZScalableDataSource dataSource = null;
	
	public ConnectionMonitor2 monitor = null;
	
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
	
	@Test
	public void testConsumerConnection() throws SQLException{
		Assert.notNull(dataSource);
		Assert.notNull(scalableMySQLDataSource);
		Assert.notNull(mySQLTransactionTemplate);
	}

	/**
	 * This test is designed to test expectation of after connection pool size has been reseted
	 * without reboot connection pool.
	 * Preparation : 
	 * 1. Build a connection pool with size of 4.
	 * 2. Prepare 20 threads to execute SQL in update, select, delete
	 * 3. Prepare 1 thread to reset connection pool size into 8 in middle of execution of 20 threads
	 * 4. Keep print statistics of connection pool
	 * Expectation:
	 * 1. The connections number scale up but under 8
	 * 2. All SQLs have been executed successfully.
	 * @throws SQLException
	 */
	@Test
	public void testScaleUpAndDownConnectionPoolSize() throws SQLException{
		logger.setLevel(Level.DEBUG);
		executorList = new ArrayList<SqlExecutor>();
		monitor = new ConnectionMonitor2(dataSource, true);
		
		monitor.start();
		SqlQueryer query = null;
		SqlUpdater updater = null;
		String sql = "select * from users";
		Object[] parameters = {};
		for( int index = 0; index < QUERY_THREAD_SIZE ; index++ ){
			query = new SqlQueryer(mySQLJdbcTemplate, sql, parameters, QUERY_INTERVAL);
			executorList.add(query);
			threadPool.execute(query);
		}
		String updateSql = "update users set address = 'zhejiang hangzhou at " + System.currentTimeMillis() + "'";
		for( int index = 0; index < UPDATE_THREAD_SIZE ; index++ ){
			updater = new SqlUpdater(mySQLJdbcTemplate, updateSql, parameters, QUERY_INTERVAL);
			threadPool.execute(updater);
			executorList.add(updater);
		}
		try {
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolSize(0, PERMITS_FIVE);
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolSize(0, PERMITS_TEN);
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolSize(0, PERMITS_FIVE);
			Thread.sleep(2000);
			dataSource.getLocalTxDataSource().resetConnectionPoolSize(0, PERMITS_TEN);
			Thread.sleep(2000);
//			validateConnectionStatis(monitor);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ScaleConnectionPoolException e) {
			e.printStackTrace();
			Assert.isTrue(false);
		}finally{
			shutdown();
		}
	}
	
	private void validateConnectionStatis(ConnectionMonitor2 monitor) {
		if( !CollectionUtils.isEmpty(monitor.getConnectionPoolStat() )){
			for(HashMap<String, ?> statMap : monitor.getConnectionPoolStat() ){
				System.out.println("\\\\*************** Pool Statistics ************************//");
				for( Entry<String,?> infoEntry : statMap.entrySet()){
					System.out.println(infoEntry.getKey() + " : " + infoEntry.getValue());
				}
			}
		}		
	}
	
	public void shutdown(){
		if( null != executorList && !executorList.isEmpty()){
			for( SqlExecutor executor : executorList ){
				executor.stopRunning();
			}
		}
		monitor.stopRunning();
		if( null != threadPool ){
			threadPool.shutdown();
			threadPool = null;
		}
	}
}