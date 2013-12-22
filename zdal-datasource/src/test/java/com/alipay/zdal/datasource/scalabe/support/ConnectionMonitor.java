/**
 * 
 */
package com.alipay.zdal.datasource.scalabe.support;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.util.PoolCondition;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ConnectionMonitor extends Thread{

	private ZScalableDataSource dataSource;
	
	private ConcurrentLinkedQueue<HashMap<String, Object>> connectionPoolStats;

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
	
	public ConnectionMonitor(ZScalableDataSource dataSource, boolean printFootPrint){
		this.dataSource = dataSource;
		connectionPoolStats = new ConcurrentLinkedQueue<HashMap<String, Object>>();
	}
	
	@Override
	public void run() {
		HashMap<String, Object> connectionPoolStat = null;
		while( running  ){
			try {
				//Check the pool condition in 5s
				Thread.sleep(50);
				PoolCondition condition = dataSource.getLocalTxDataSource().getPoolCondition();
				if( hasResetedPool(condition) ){
					connectionPoolStat = new HashMap<String, Object>();	
					
					connectionPoolStat.put(MIN_SIZE, condition.getMinSize());
					connectionPoolStat.put(MAX_SIZE, condition.getMaxSize());
					connectionPoolStats.add(connectionPoolStat);
				}else{
					connectionPoolStat = connectionPoolStats.peek();
				}
				connectionPoolStat.put(STAT_TIME, new Date());
				connectionPoolStat.put(ACC, condition.getAvailableConnectionCount());
				connectionPoolStat.put(CC, condition.getConnectionCount());
				connectionPoolStat.put(CCC, condition.getConnectionCreatedCount());
				
				connectionPoolStat.put(CDC, condition.getConnectionDestroyedCount());
				connectionPoolStat.put(IUCC, condition.getInUseConnectionCount());
				connectionPoolStat.put(MCIUC, condition.getMaxConnectionsInUseCount());
				if( printFootPrint ){
					System.out.println(connectionPoolStats.toString());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean hasResetedPool(PoolCondition condition) {
		if( null == connectionPoolStats ){
			return true;
		}else{
			HashMap<String, ?> connectionPoolStat = connectionPoolStats.peek();
			if( null == connectionPoolStat ){
				return true;
			}
			if( null == connectionPoolStat.get(MIN_SIZE) || null == connectionPoolStat.get(MAX_SIZE) ){
				return true;
			}
			return condition.getMinSize() != (Integer)connectionPoolStat.get(MIN_SIZE) || (Integer)condition.getMaxSize() != connectionPoolStat.get(MAX_SIZE);
		}
	}

	public ConcurrentLinkedQueue<HashMap<String, Object>> getConnectionPoolStat(){
		return connectionPoolStats;
	}
	
	public void stopRunning(){
		running = false;
	}
}
