package com.alipay.zdal.test.ut.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.util.PoolCondition;

public class ScalaUpConnectionsTests {

	public static boolean isServerStarted = false;
	
	public static ZDataSource dataSource = null;
	
	public ConnectionMonitor monitor = null;
	
	@BeforeClass
	public static void startHqlServer(){
        try {
//        	mySQLDataSource = new ZDataSource(localTxDataSourceDO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static LocalTxDataSourceDO createMySQLConfiguration() {
		LocalTxDataSourceDO localTxDataSourceDO = new LocalTxDataSourceDO();
    	localTxDataSourceDO.setBackgroundValidation(false);
    	localTxDataSourceDO.setBackgroundValidationMinutes(10);
    	localTxDataSourceDO.setBlockingTimeoutMillis(2000);
    	localTxDataSourceDO.setCheckValidConnectionSQL("SELECT 1 from dual");
    	localTxDataSourceDO.setConnectionURL("jdbc:mysql://mysql-1-2.bjl.alipay.net:3306/zds1");
    	localTxDataSourceDO.setDriverClass("com.mysql.jdbc.Driver");
        try {
        	localTxDataSourceDO.setEncPassword("-76079f94c1e11c89");
		} catch (Exception e) {
			e.printStackTrace();
		}
		localTxDataSourceDO.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLExceptionSorter");
		localTxDataSourceDO.setIdleTimeoutMinutes(10);
		localTxDataSourceDO.setMaxPoolSize(12);
		localTxDataSourceDO.setMinPoolSize(6);
		localTxDataSourceDO.setNewConnectionSQL("SELECT 1 from dual");
		localTxDataSourceDO.setPrefill(false);
		localTxDataSourceDO.setPreparedStatementCacheSize(100);
		localTxDataSourceDO.setQueryTimeout(180);
        localTxDataSourceDO.setSharePreparedStatements(false);
        localTxDataSourceDO.setTxQueryTimeout(false);
        localTxDataSourceDO.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDataSourceDO.setUserName("mysql");
        localTxDataSourceDO.setUseFastFail(false);
        localTxDataSourceDO.setValidateOnMatch(false);
        localTxDataSourceDO.setValidConnectionCheckerClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.MySQLValidConnectionChecker");
        localTxDataSourceDO.setDsName("ds-mysql");
		return localTxDataSourceDO;
	}
	

	private static LocalTxDataSourceDO createOracleConfiguration() {
		LocalTxDataSourceDO localTxDataSourceDO = new LocalTxDataSourceDO();
    	localTxDataSourceDO.setBackgroundValidation(false);
    	localTxDataSourceDO.setBackgroundValidationMinutes(10);
    	localTxDataSourceDO.setBlockingTimeoutMillis(500);
    	localTxDataSourceDO.setConnectionURL("jdbc:oracle:oci://app0.dbaapptest.alipay.net:1521/public1");
    	localTxDataSourceDO.setDriverClass("oracle.jdbc.OracleDriver");
        try {
        	localTxDataSourceDO.setPassWord("1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		localTxDataSourceDO.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.OracleExceptionSorterTest");
		localTxDataSourceDO.setIdleTimeoutMinutes(10);
		localTxDataSourceDO.setMaxPoolSize(3);
		localTxDataSourceDO.setMinPoolSize(2);
		localTxDataSourceDO.setPrefill(false);
		localTxDataSourceDO.setPreparedStatementCacheSize(0);
		localTxDataSourceDO.setQueryTimeout(180);
        localTxDataSourceDO.setSharePreparedStatements(false);
        localTxDataSourceDO.setTxQueryTimeout(false);
        localTxDataSourceDO.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDataSourceDO.setUserName("zdaldev");
        localTxDataSourceDO.setUseFastFail(false);
        localTxDataSourceDO.setValidateOnMatch(false);
        localTxDataSourceDO.setValidConnectionCheckerClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.OracleValidConnectionChecker");
        localTxDataSourceDO.setDsName("ds-oracle");
		return localTxDataSourceDO;
	}

	@Before
	public void setUp() throws Exception {
		dataSource = new ZDataSource(createMySQLConfiguration());
		monitor = new ConnectionMonitor(dataSource, true);
		
		
		monitor.start();
	}

	@After
	public void tearDown() throws Exception {
		if( null != dataSource ){
			try {
				dataSource.destroy();
				monitor.stopMonitor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testConsumerConnection() throws SQLException{
		Connection c1 = null;
		Connection c2 = null;
		try {
			
			c1 = dataSource.getConnection();
			Thread.sleep(1000);
//			dataSource.getLocalTxDataSource().getPoolCondition()
			List<HashMap<String, ?>> stats = monitor.getConnectionPoolStat();
			for(HashMap<String, ?> stat : stats ){
				for( Entry<String, ?> entry : stat.entrySet()){
					System.out.println(entry.getKey() + ": " + entry.getValue());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			if( null != c1 && !c1.isClosed() ){
				System.out.println("Close connection no.1.");
				c1.close();
			}
			if( null != c2 && !c2.isClosed() ){
				System.out.println("Close connection no.2.");
				c2.close();
			}
		}
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
	public void testScaleUpConnectionPoolSize() throws SQLException{
		Connection c1 = null;
		Connection c2 = null;
		try {
			c1 = dataSource.getConnection();
			System.out.println("-----------------------");
//			c2 = oracleDataSource.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if( null != c1 && !c1.isClosed() ){
				System.out.println("Close connection no.1.");
				c1.close();
			}
			if( null != c2 && !c2.isClosed() ){
				System.out.println("Close connection no.2.");
				c2.close();
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
	public void testScaleUpConnectionPoolSizeUnderException() throws SQLException{
		Connection c1 = null;
		Connection c2 = null;
		try {
			c1 = dataSource.getConnection();
			System.out.println("-----------------------");
//			c2 = oracleDataSource.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if( null != c1 && !c1.isClosed() ){
				System.out.println("Close connection no.1.");
				c1.close();
			}
			if( null != c2 && !c2.isClosed() ){
				System.out.println("Close connection no.2.");
				c2.close();
			}
		}
	}
	
	/**
	 * This test is designed to test expectation of after connection pool size has been reseted
	 * without reboot connection pool under a case of when out of connections occurred.
	 * Preparation : 
	 * 1. Build a connection pool with size of 8.
	 * 2. Prepare 20 threads to execute SQL in update, select, delete
	 * 3. When the holding connection time getting shorter, there are connections are idle.
	 * 3. Prepare 1 thread to reset connection pool size into 4 in middle of execution of 20 threads
	 * 4. Keep print statistics of connection pool
	 * Expectation:
	 * 1. The connections number scale down to 4
	 * 2. All SQLs have been executed successfully.
	 * 3. No more connection exceptions happen.
	 * @throws SQLException
	 */
	@Test
	public void testScaleDownConnectionPoolSizeWithoutException() throws SQLException{
		Connection c1 = null;
		Connection c2 = null;
		try {
			c1 = dataSource.getConnection();
			System.out.println("-----------------------");
//			c2 = oracleDataSource.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if( null != c1 && !c1.isClosed() ){
				System.out.println("Close connection no.1.");
				c1.close();
			}
			if( null != c2 && !c2.isClosed() ){
				System.out.println("Close connection no.2.");
				c2.close();
			}
		}
	}
	
	@AfterClass
	public static void shutdownServer(){
		
	}
}

class ConnectionMonitor extends Thread{

	private ZDataSource dataSource;
	
	private List<HashMap<String, ?>> connectionPoolStats;

	private boolean printFootPrint;

	private boolean running = true;
	
	public static final String STAT_TIME = "Stat Time";
	
	public static final String ACC = "AvailableConnectionCount";
	
	public static final String CC = "ConnectionCount";
	
	public static final String CCC = "ConnectionCreatedCount";
	
	public static final String CDC = "ConnectionDestroyedCount";
	
	public static final String IUCC = "InUseConnectionCount";
	
	public static final String MCIUC = "MaxConnectionsInUseCount";
	
	public static final String MIN_SIZE = "Min Size";
	
	public static final String MAX_SIZE = "Max Size";
	
	public ConnectionMonitor(ZDataSource dataSource, boolean printFootPrint){
		this.dataSource = dataSource;
		connectionPoolStats = new ArrayList<HashMap<String, ?>>();
	}
	
	@Override
	public void run() {
		while( running  ){
			try {
				//Check the pool condition in 5s
				Thread.sleep(500);
				PoolCondition condition = dataSource.getLocalTxDataSource().getPoolCondition();
				if( hasResetedPool(condition) ){
					HashMap<String, Object> connectionPoolStat = new HashMap<String, Object>();	
					connectionPoolStat.put(STAT_TIME, new Date());
					connectionPoolStat.put(ACC, condition.getAvailableConnectionCount());
					connectionPoolStat.put(CC, condition.getConnectionCount());
					connectionPoolStat.put(CCC, condition.getConnectionCreatedCount());
					
					connectionPoolStat.put(CDC, condition.getConnectionDestroyedCount());
					connectionPoolStat.put(IUCC, condition.getInUseConnectionCount());
					connectionPoolStat.put(MCIUC, condition.getMaxConnectionsInUseCount());
					connectionPoolStat.put(CCC, condition.getMinSize());
					connectionPoolStat.put(CCC, condition.getMaxSize());
					connectionPoolStats.add(connectionPoolStat);
				}
				if( printFootPrint ){
					System.out.println();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean hasResetedPool(PoolCondition condition) {
		if( connectionPoolStats.isEmpty() ){
			return true;
		}else{
			HashMap<String, ?> connectionPoolStat = connectionPoolStats.get(connectionPoolStats.size() - 1 );
			if( null == connectionPoolStat.get(MIN_SIZE) || null == connectionPoolStat.get(MAX_SIZE) ){
				return true;
			}
			return condition.getMinSize() != (Integer)connectionPoolStat.get(MIN_SIZE) || (Integer)condition.getMaxSize() != connectionPoolStat.get(MAX_SIZE);
		}
	}

	public List<HashMap<String, ?>> getConnectionPoolStat(){
		return connectionPoolStats;
	}
	
	public void stopMonitor(){
		running = false;
	}
}
