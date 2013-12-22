package com.alipay.zdal.test.dynamicAdjustment;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;
import com.ibatis.sqlmap.client.SqlMapClient;
import static com.alipay.ats.internal.domain.ATS.Step;
@RunWith(ATSJUnitRunner.class)
@Feature("动态调整连接池数,调整maxConn值")
public class SR957010 {
	ExecutorService exec = Executors.newCachedThreadPool();
	TestAssertion Assert = new TestAssertion();
	DynamicAdjustmentCommon dynamicCommon=new DynamicAdjustmentCommon();
	ZScalableDataSource zz = null;
	ZdalDataSource zs = null;
	SqlMapClient sqlMap = null;
	TransactionTemplate tt = null;
	

	@Before
	public void beforeTestCase() {
		Step("初始化数据源");
		String[] springXmlPath = { "./dynamicAdjustment/spring-dynamicAdjustment-ds.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext(
				springXmlPath);

		zs = (ZdalDataSource) context.getBean("zdalRwDSMysql10");
		sqlMap = (SqlMapClient) context.getBean("zdalRwdynamicAdjustment");
		tt = (TransactionTemplate) context
				.getBean("zdalRwdynamicAdjustmentTmp");
		zz = (ZScalableDataSource) zs.getDataSourcesMap().get("ds0");
		
	}

	@After
	public void afterTestCase() {
		Step("如下为了防止主线程关掉后，其它线程被关掉");
		try {
			Thread.sleep(6000);
			resetMaxConnection(5);
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Step("清除数据");
		dynamicCommon.deleteDateZds1();
			
	}

	@Subject("不调整maxConn值，先启动5个线程，该5个线程占有连接，再启3个线程")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957011() {
		
		startNewThread1(5);
		
		dynamicCommon.testsleep(1000);
		
		startNewThread2(3);
		Step("期望结果：前5个线程插入数据，后3个线程报异常");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(5, dynamicCommon.selectCountZds1(), "前5个线程插入数据，后3个线程报异常");
	}

	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占有连接。现启3个线程。再调大maxConn值为10")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957012() {
		
		startNewThread1(5);
		
		dynamicCommon.testsleep(1000);
		
		startNewThread2(3);
		
		dynamicCommon.testsleep(1000);		
		resetMaxConnection(10);
		Step("期望结果：5个线程完成任务，新启动的3个线程报异常");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(5, dynamicCommon.selectCountZds1(), "5个线程完成任务，新启动的3个线程报异常");
	}
	
	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占用连接。现调大maxConn值为10。再启3个线程")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957013() {
		
		startNewThread1(5);
		
		dynamicCommon.testsleep(1000);

		resetMaxConnection(10);

		startNewThread2(3);
		Step("期望结果：8个线程都完成任务");
		dynamicCommon.testsleep(5000);	
		Assert.areEqual(8, dynamicCommon.selectCountZds1(), "8个线程都完成任务");
	}
	
	
	@Subject("动态调整maxConn值。已有8个线程拿到连接，占用连接，现调大maxConn值为10。")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957014() {
		
		startNewThread1(8);	
        
		resetMaxConnection(10);

		Step("期望结果：至少5个线程都完成任务");
		dynamicCommon.testsleep(5000);
		Assert.isTrue(dynamicCommon.selectCountZds1()>=5, "至少5个线程都完成任务");
	}
	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占用连接。现调大maxConn值为10。再启6个非等待线程")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957015() {
		
		startNewThread1(5);

		resetMaxConnection(10);

		startNewThread2(6);
		Step("期望结果：原5个线程完成任务，新启的6个线程中的5个当即拿到连接，完成任务，另外1个线程等该5个线程释放连接。");
		Step("所以至少完成的任务数为10个");
		dynamicCommon.testsleep(5000);
		Assert.isTrue(dynamicCommon.selectCountZds1()>=10, "至少10个线程都完成任务");
	}
	
	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占用连接。现调大maxConn值为10。再启6个等待线程")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957016() {
		
		startNewThread1(5);

		resetMaxConnection(10);

		for (int i = 0; i < 6; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 100+i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
		Step("期望结果：原5个线程完成任务，新启的6个线程中的5个拿到连接完成任务，另外1个报异常。");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(10, dynamicCommon.selectCountZds1(), "10个线程都完成任务");
	}
	
	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占用连接。再启3个非等待线程。调整maxConn值为6")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957017() {
		
		startNewThread1(5);
		
		dynamicCommon.testsleep(1000);
		
		startNewThread2(3);
		
		dynamicCommon.testsleep(1000);
		resetMaxConnection(6);
		
		Step("期望结果：5个线程完成任务，新启动的3个线程报异常（因为线程在重置之前启动，是拿的之前的线程连接）");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(5, dynamicCommon.selectCountZds1(), "5个线程都完成任务,新启动的3个线程报异常");
	}
		
	
	@Subject("动态调整maxConn值。已有5个线程拿到连接，该5个线程占用连接。再启3个等待线程。调整maxConn值为6")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957018() {
		
		startNewThread1(5);
		
		dynamicCommon.testsleep(1000);
		
		for (int i = 0; i < 3; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 100+i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
		
		dynamicCommon.testsleep(1000);
		resetMaxConnection(6);
		
		Step("期望结果：5个线程完成任务,后启动的3个报异常");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(5, dynamicCommon.selectCountZds1(), "5个线程完成任务,后启动的3个报异常");
	}
		
	@Subject("动态调整maxConn值。已有3个线程拿到连接，该3个线程占用连接。再启5个非等待线程。调整maxConn值为10")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957019(){
		
		startNewThread1(3);
		
		dynamicCommon.testsleep(1000);
		
		startNewThread2(5);
		
		dynamicCommon.testsleep(1000);
		resetMaxConnection(10);
		
		//期望结果：3个线程完成任务，新启的5个线程其中的2个当即拿到连接，另外3个线程要等该2个线程释放连接。
		//所以，至少要5个线程完成任务。
		dynamicCommon.testsleep(5000);
		Assert.isTrue(dynamicCommon.selectCountZds1()>=5, "至少5个线程都完成任务");
	}
	
	
	
	@Subject("动态调整maxConn值。已有3个线程拿到连接，该3个线程占用连接。再启5个等待线程。调整maxConn值为10")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC95701a(){
		
		startNewThread1(3);
		
		dynamicCommon.testsleep(1000);
		
		for (int i = 0; i < 5; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 100+i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
		
		dynamicCommon.testsleep(1000);
		resetMaxConnection(10);
		
		//期望结果：3个线程完成任务，新启的5个线程其中的2个当即拿到连接，其中的另外3个抛异常
		dynamicCommon.testsleep(5000);
		Assert.areEqual(5, dynamicCommon.selectCountZds1(), "3个线程完成任务，新启的5个线程其中的2个当即拿到连接，其中的另外3个抛异常");
		
	}
	

			
	@Subject("动态调整maxConn值，不调小。原maxConn为5，现在4个线程请求")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC95701b(){
		
		startNewThread1(4);
		
		Step("期望结果：4个线程拿到连接访问db");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(4, dynamicCommon.selectCountZds1(), "4个线程拿到连接访问db");
	}
	
	@Subject("动态调整maxConn值，调小。将maxConn由5调整为3,然后4个线程请求")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC95701c(){
		resetMaxConnection(3);
		
		startNewThread1(4);
		
		Step("期望结果：其中3个线程拿到连接访问db，另外1个异常");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(3, dynamicCommon.selectCountZds1(), "其中3个线程拿到连接访问db，另外1个异常");
	}
		
	
	@Subject("动态调整maxConn值，调小。现有4个线程请求，拿到连接。现将maxConn由5调整为3")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC95701e(){
		startNewThread1(4);
		
		dynamicCommon.testsleep(1000);
		resetMaxConnection(3);
				
		Step("期望结果：原4个线程，都拿到连接，完成任务");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(4, dynamicCommon.selectCountZds1(), "原4个线程，都拿到连接，完成任务");
	}
	
	
	@Subject("动态调整maxConn值，调小。现有4个线程请求，拿到连接。现将maxConn由5调整为3。再启动5个等待线程")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC95701f(){
		startNewThread1(4);
		
		dynamicCommon.testsleep(3000);
		resetMaxConnection(3);
		dynamicCommon.testsleep(1000);
		
		for (int i = 0; i < 5; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 100+i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
				
		Step("期望结果：原4个线程，都拿到连接，完成任务.后启动的5个线程，其中3个拿到连接完成任务，其它2个报异常。");
		dynamicCommon.testsleep(5000);
		Assert.areEqual(7, dynamicCommon.selectCountZds1(), "原4个线程，都拿到连接，完成任务.后启动的5个线程，其中3个拿到连接完成任务，其它2个报异常。");
	
	}

	
	/**
	 * 新启线程1
	 * @param z 线程个数
	 */
	private void startNewThread1(int z){
		// 启动5个线程，来占用所有db连接
		for (int i = 0; i < z; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
	}
	
	/**
	 * 新启线程2
	 * @param z
	 */
	private void startNewThread2(int z){
		// 再启动3个线程,用来等待被占用的db连接
		for (int j = 0; j < z; j++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 10 + j);
			exec.submit(new TestThread01(tt, sqlMap, params,0));
		}
	}
	
	/**
	 * 重设置maxConn的数量
	 * @param z
	 */
	private void resetMaxConnection(int z){
		try {
			// 重置最大连接数('0'表示不调整）
			zz.resetConnectionPoolSize(0,z);
		} catch (ScaleConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
