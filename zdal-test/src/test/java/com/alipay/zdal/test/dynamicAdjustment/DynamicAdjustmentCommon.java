package com.alipay.zdal.test.dynamicAdjustment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.transaction.support.TransactionTemplate;

import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;
import com.alipay.zdal.datasource.util.PoolCondition;
import com.alipay.zdal.test.common.ConstantsTest;
import com.alipay.zdal.test.common.ZdalTestCommon;
import com.ibatis.sqlmap.client.SqlMapClient;

public class DynamicAdjustmentCommon {
	ExecutorService exec = Executors.newCachedThreadPool();

	/**
	 * 休息一会
	 * 
	 * @param n
	 */
	public void testsleep(long n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 调整minConnection值
	 * 
	 * @param z
	 */
	public void resetMinConnection(int z,ZScalableDataSource zz) {
		try {
			// 重置最大连接数
			zz.resetConnectionPoolSize(z, 8);
		} catch (ScaleConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 调整minConnection和maxConnection值
	 * @param z
	 */
	public void resetMinMaxConnection(int min,int max,ZScalableDataSource zz){
		try {
			// 重置最小连接数，最大连接数
			zz.resetConnectionPoolSize(min,max);
		} catch (ScaleConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

     /**
      * 启动线程
      * @param z
      * @param tt
      * @param sqlMap
      */
	public void startNewThread1(int z,TransactionTemplate tt,SqlMapClient sqlMap) {
		// 启动5个线程，来占用所有db连接
		for (int i = 0; i < z; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", i);
			exec.submit(new TestThread01(tt, sqlMap, params, 40000));
		}
	}
	
	/**
	 * 新启线程1
	 * @param z 线程个数
	 */
	public void startNewThread2(int z,TransactionTemplate tt,SqlMapClient sqlMap){
		// 启动5个线程，来占用所有db连接
		for (int i = 0; i < z; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
	}

	/**
	 * 获取managed线程数量
	 * 
	 * @param pConditon
	 * @return
	 */
	public int checkPoint(PoolCondition pConditon) {
		String pConditonStr = pConditon.toString();
		String[] strArr = pConditonStr.split(";");
		String[] managedArr = strArr[3].split(":");
		return Integer.parseInt(managedArr[1]);
	}
	/**
	 * 删除zds2的test2表
	 */
	public void deleteDateZds2(){
		try {
			String url1=ConstantsTest.mysql12UrlZds2;
			String user=ConstantsTest.mysq112User;
			String psd=ConstantsTest.mysq112Psd;
			String sqldel="delete from test2";
			ZdalTestCommon.dataUpdateJDBC(sqldel, url1, psd, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除zds1的test2表
	 */
	public void deleteDateZds1(){
		try {
			String url1=ConstantsTest.mysql12UrlZds1;
			String user=ConstantsTest.mysq112User;
			String psd=ConstantsTest.mysq112Psd;
			String sqldel="delete from test2";
			ZdalTestCommon.dataUpdateJDBC(sqldel, url1, psd, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取zds2的test2的记录条数
	 * @return
	 */
	public int  selectCountZds2(){
		String url1=ConstantsTest.mysql12UrlZds2;
		String user=ConstantsTest.mysq112User;
		String psd=ConstantsTest.mysq112Psd;
		String selectSql="select count(*) from test2";
		int count=0;
		ResultSet rs=ZdalTestCommon.dataCheckFromJDBC(selectSql, url1, psd, user);
		try {
			rs.next();
			count=rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 获取zds1的test2的记录条数
	 * @return
	 */
	public int  selectCountZds1(){
		String url1=ConstantsTest.mysql12UrlZds1;
		String user=ConstantsTest.mysq112User;
		String psd=ConstantsTest.mysq112Psd;
		String selectSql="select count(*) from test2";
		int count=0;
		ResultSet rs=ZdalTestCommon.dataCheckFromJDBC(selectSql, url1, psd, user);
		try {
			rs.next();
			count=rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

}
