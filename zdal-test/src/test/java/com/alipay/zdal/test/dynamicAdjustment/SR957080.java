package com.alipay.zdal.test.dynamicAdjustment;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.alipay.ats.internal.domain.ATS.Step;
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



@RunWith(ATSJUnitRunner.class)
@Feature("动态调整连接池数,同时调整minConn，maxConn值")
public class SR957080 {
	TestAssertion Assert = new TestAssertion();
	DynamicAdjustmentCommon dynamicMinConn=new DynamicAdjustmentCommon();
	ExecutorService exec = Executors.newCachedThreadPool();
	ZScalableDataSource zz = null;
	ZdalDataSource zs = null;
	SqlMapClient sqlMap = null;
	TransactionTemplate tt = null;
	
	@Before
	public void beforeTestCase() {
		// 初始化数据源
		String[] springXmlPath = { "./dynamicAdjustment/spring-dynamicAdjustment-ds.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext(
				springXmlPath);			
		try {
			zs = (ZdalDataSource) context.getBean("zdalRwDSMysql11");
			sqlMap = (SqlMapClient) context.getBean("zdalRwdynamicAdjustmentMin");
			tt = (TransactionTemplate) context
			.getBean("zdalRwdynamicAdjustmentTmpMin");
			zz = (ZScalableDataSource) zs.getDataSourcesMap().get("ds0");
			zz.getLocalTxDataSource().getDatasource().getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@After
	public void afterTestCase(){
		dynamicMinConn.deleteDateZds2();
	}
	
	
	@Subject("动态调整minConn和maxConn值。现有6个等待线程访问，将minConn调低，maxConn调高。然后4等待线程访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC957081(){
		dynamicMinConn.startNewThread2(6, tt, sqlMap);
		dynamicMinConn.testsleep(1000);
		dynamicMinConn.resetMinMaxConnection(2,10,zz);
		dynamicMinConn.testsleep(1000);
		for (int i = 0; i < 3; i++) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("num", 100+i);
			exec.submit(new TestThread01(tt, sqlMap, params,3000));
		}
		dynamicMinConn.testsleep(90000);
		PoolCondition pc=zz.getPoolCondition();
		System.out.println("========:"+pc.toString());
		Assert.areEqual(2, dynamicMinConn.checkPoint(pc), "销毁重建连接，managed变为2");
		Step("期望结果：minConn为2,manage为2。9个线程全部完成任务");
		Assert.areEqual(9,dynamicMinConn.selectCountZds2(),"验证纪录条数");
	}
	

}
