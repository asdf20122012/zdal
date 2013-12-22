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
@Feature("不预热")
public class SR951080 {
	public TestAssertion Assert = new TestAssertion();
	
	@Subject("rw数据源，不预热")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951081() {
		Step("rw数据源，不预热");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
				.getBean("zdalUpdateZoneDs");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds1");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill();
		Step("检查是否预热");
		Assert.isTrue(!bl, "rw is not prefill");
	}
	
	@Subject("shard+failover数据源，不预热")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951082() {
		Step("shard+failover数据源，不预热");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
				.getBean("zdalLdcDSDrm");
		ZScalableDataSource zz = (ZScalableDataSource) zd.getDataSourcesMap().get("ds0");
		ZScalableDataSource zz2 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds2");
		ZScalableDataSource zz3 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds4");
		ZScalableDataSource zz4 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds6");
		ZScalableDataSource zz5 = (ZScalableDataSource) zd.getDataSourcesMap().get("ds8");
		boolean bl=zz.getLocalTxDataSource().getPrefill()||zz2.getLocalTxDataSource().getPrefill()||zz3.getLocalTxDataSource().getPrefill()||zz4.getLocalTxDataSource().getPrefill()||zz5.getLocalTxDataSource().getPrefill();
		Step("检查是否预热");
		Assert.isTrue(!bl, "rw is not prefill");
	}
	

}
