package com.alipay.zdal.test.client;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import static com.alipay.ats.internal.domain.ATS.Step;

@RunWith(ATSJUnitRunner.class)
@Feature("预热,当zoneDs为''")
public class SR951060 {

	public TestAssertion Assert = new TestAssertion();

	@Subject("预热,shard+rw数据源,zoneDs为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951061() throws InterruptedException {
		Step("预热");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
				.getBean("zdalClientPrefillshardrwzoneDsIsNull");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		ZScalableDataSource zz3 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds2");
		ZScalableDataSource zz4 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds3");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill()||zz3.getLocalTxDataSource().getPrefill()||zz4.getLocalTxDataSource().getPrefill();
		Assert.isTrue(bl, "shard+rw is prefill");
		// 日志如何得到，作为断言
		// 现在是sleep后看日志
		Step("对日志检查");
	}
	
	@Subject("预热,shard+failover数据源,zoneDs为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951062() throws InterruptedException{
		Step("预热,shard+failover数据源,zoneDs为''");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardfailoverzoneDsIsNull");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds2");
		ZScalableDataSource zz3 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds4");
		ZScalableDataSource zz4 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds6");
		ZScalableDataSource zz5 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds8");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill()||zz3.getLocalTxDataSource().getPrefill()||zz4.getLocalTxDataSource().getPrefill()||zz5.getLocalTxDataSource().getPrefill();
        Step("断言是否预热");
		Assert.isTrue(bl, "shard+failover is prefill");
	}
	
	@Subject("预热,shard+oracle数据源,zoneDs为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951063() throws InterruptedException{
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardOraclezoneDsIsNull");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("master0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("master1");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill();
		Assert.isTrue(bl, "shard+failover is prefill");
	}
	
	@Subject("预热,rw数据源,zoneDs为''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951064() throws InterruptedException{
		Step("预热,rw数据源,zoneDs为''");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardRwzoneDsIsNull");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		ZScalableDataSource zz3 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds2");
		ZScalableDataSource zz4 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds3");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill()||zz3.getLocalTxDataSource().getPrefill()||zz4.getLocalTxDataSource().getPrefill();
		Step("断言是否预热");
		Assert.isTrue(bl, "shard+failover is prefill");
	}
}
