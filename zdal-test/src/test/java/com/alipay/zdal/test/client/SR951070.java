package com.alipay.zdal.test.client;

import org.junit.Test;
import static com.alipay.ats.internal.domain.ATS.Step;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;

@RunWith(ATSJUnitRunner.class)
@Feature("预热,zoneDs不为''")
public class SR951070 {
	public TestAssertion Assert = new TestAssertion();
	
	@Subject("测试 prefill,shard+rw")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951071() throws InterruptedException{
		Step("预热数据");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardrw");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		ZScalableDataSource zz3 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds2");
		ZScalableDataSource zz4 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds3");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill()||zz3.getLocalTxDataSource().getPrefill()||zz4.getLocalTxDataSource().getPrefill();
		Assert.isTrue(bl, "shard+rw is prefill");
		//日志如何得到，作为断言
	    //现在是sleep后看日志
		Step("日志检查");
	}
	
	@Subject("预热,shard+failover数据源，zoneDs不为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951072() throws InterruptedException{
		Step("预热,shard+failover数据源，zoneDs不为''");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardfailover");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill();		
		Step("断言日志");
		Assert.isTrue(bl, "shard+failover is prefill");
	}
	
	@Subject("预热,shard+oracle 数据源，zoneDs不为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951073() throws InterruptedException{
		Step("预热,shard+oracle 数据源，zoneDs不为''");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardOracle");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("master0");
		boolean bl=zz.getLocalTxDataSource().getPrefill();
		Step("日志检查");
		Assert.isTrue(bl, "shard+oracle is prefill");
	}
	
	@Subject("预热,rw数据源，zoneDs不为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951074() throws InterruptedException{
		Step("预热,rw数据源，zoneDs不为''");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillrw");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill();
		Step("日志检查");
		Assert.isTrue(bl, "rw is prefill");
	}
		
	

}
