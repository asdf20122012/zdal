package com.alipay.zdal.test.dynamicAdjustment;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ibatis.sqlmap.client.SqlMapClient;


public class TestThread02 implements Callable<Object> {
	
	SqlMapClient sqlMap;
	Map<String, Object> params;
	TransactionTemplate tt;
	
	public  TestThread02(TransactionTemplate tt,SqlMapClient sqlMap,Map<String, Object> params){
		this.sqlMap=sqlMap;
		this.params=params;
		this.tt=tt;
	}
	
	/**
	 * 非休息线程
	 */
	public   Object call(){		
		try {
			System.out.println("this is thread--thread2--beging--------"+params.toString());
			testTransactionInsertSelect2();
			System.out.println("this is thread--thread2--end--------"+params.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return null;		
	}
	
	/**
	 * 事务(非休息）
	 */
	private  void testTransactionInsertSelect2(){
		try {
			tt.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {

					try {
						sqlMap.insert("insertRwSql2",params);
						sqlMap.queryForList("queryRwSql2");				
					} catch (Exception e) {
						status.setRollbackOnly();
						e.printStackTrace();
					}
					return null;
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
