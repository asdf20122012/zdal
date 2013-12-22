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
public class SR957030 {
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


	@Subject("动态调整minConn值,调高。将最小连接数由原来的4调整到5")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957031() {
		Step("1、min为4，开始创建连接数，managed还未达到4");
		PoolCondition pc = zz.getPoolCondition();
		Assert.isTrue(dynamicMinConn.checkPoint(pc) < 4, "min为4，开始创建连接数，managed还未达到4,"+dynamicMinConn.checkPoint(pc));
		Step("2、min变为5,继续创建连接数，managed 还未达到5");
		dynamicMinConn.resetMinConnection(5,zz);
		pc = zz.getPoolCondition();
		Assert.isTrue(dynamicMinConn.checkPoint(pc) < 5, "min变为5,继续创建连接数，managed还未达到5,"+dynamicMinConn.checkPoint(pc));
		Step("3、min为5,连接数创建完成，managed为5");
		dynamicMinConn.testsleep(30000);
		pc = zz.getPoolCondition();
		Assert.areEqual(5, dynamicMinConn.checkPoint(pc), "min为5,连接数创建完成，managed为5");
		Step("4、min值为6,连接数消毁重建,managed变为6");
		dynamicMinConn.resetMinConnection(6,zz);
		dynamicMinConn.testsleep(60000);
		pc = zz.getPoolCondition();
		Assert.areEqual(6, dynamicMinConn.checkPoint(pc), "min值为6,连接数消毁重建,managed变为6");
	}

	
}
