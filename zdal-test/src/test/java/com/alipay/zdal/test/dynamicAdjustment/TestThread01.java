package com.alipay.zdal.test.dynamicAdjustment;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ibatis.sqlmap.client.SqlMapClient;

public class TestThread01 implements Callable<Object> {

	SqlMapClient sqlMap;
	Map<String, Object> params;
	TransactionTemplate tt;
	long sleepTime;

	public TestThread01(TransactionTemplate tt, SqlMapClient sqlMap,
			Map<String, Object> params,long sleepTime) {
		this.sqlMap = sqlMap;
		this.params = params;
		this.tt = tt;
		this.sleepTime=sleepTime;
	}

	/**
	 * 休息线程
	 */
	public Object call() {
		try {			
			testTransactionInsertSelect();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 事务（休息）
	 */
	private void testTransactionInsertSelect() {
		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {

					try {
						sqlMap.insert("insertRwSql2", params);
						Thread.sleep(sleepTime);
						sqlMap.queryForList("queryRwSql2");
						Date date = new Date();
						System.out.println(date + " this is thread " + Thread.currentThread().getId() + " finished job.");		
					} catch (Exception e) {
						status.setRollbackOnly();
						System.out.println("this is thread " + Thread.currentThread().getId() + " had an exception due to ");	
//						e.printStackTrace();
					}
					return null;
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
