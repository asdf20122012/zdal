package com.alipay.zdal.test.dynamicAdjustment;

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
import com.alipay.zdal.datasource.util.PoolCondition;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.alipay.zdal.test.dynamicAdjustment.DynamicAdjustmentCommon;
import static com.alipay.ats.internal.domain.ATS.Step;
@RunWith(ATSJUnitRunner.class)
@Feature("动态调整连接池数,调整minConn值")
public class SR957060 {
	TestAssertion Assert = new TestAssertion();
	DynamicAdjustmentCommon dynamicMinConn=new DynamicAdjustmentCommon();
	ExecutorService exec = Executors.newCachedThreadPool();
	ZScalableDataSource zz = null;
	ZdalDataSource zs =null;
	SqlMapClient sqlMap =null; 
	TransactionTemplate tt =null; 

	@Before
	public void beforeTestCase() {
		String[] springXmlPath = { 
		"./dynamicAdjustment/spring-dynamicAdjustment-ds.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext(springXmlPath);
		try {
			zs=(ZdalDataSource)context.getBean("zdalRwDSMysql11");
			sqlMap=(SqlMapClient) context.getBean("zdalRwdynamicAdjustmentMin");
			tt = (TransactionTemplate) context.getBean("zdalRwdynamicAdjustmentTmpMin");
			zz = (ZScalableDataSource) zs.getDataSourcesMap().get("ds0");
			zz.getLocalTxDataSource().getDatasource().getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void afterTestcase() {
		dynamicMinConn.deleteDateZds2();
	}


	@Subject("动态调整minConn值,调低。将最小连接数由原来的4调到2,然后再有3个线程访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957061() {
		dynamicMinConn.resetMinConnection(2,zz);
		Step(" 1、minConn变为2,managed更新为2");
		dynamicMinConn.testsleep(30000);
		PoolCondition pc = zz.getPoolCondition();
		Assert.areEqual(2, dynamicMinConn.checkPoint(pc), "minConn变为2,managed更新为2");
		dynamicMinConn.startNewThread1(2,tt,sqlMap);
		Step("2、minConn仍为2,managed为3");
		dynamicMinConn.testsleep(60000);
		pc = zz.getPoolCondition();
		Assert.areEqual(3, dynamicMinConn.checkPoint(pc), "minConn仍为2,managed为3");
		Step(" 3、minConn为2,managed重建后为2");
		dynamicMinConn.testsleep(60000);
		pc = zz.getPoolCondition();
		Assert.areEqual(2, dynamicMinConn.checkPoint(pc), "minConn为2,managed重建后为2");
	}

	
}
