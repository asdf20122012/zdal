package com.alipay.zdal.test.client;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.config.drm.ZdalLdcSignalResource;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import static com.alipay.ats.internal.domain.ATS.Step;

@RunWith(ATSJUnitRunner.class)
@Feature("update  zoneDs")
public class SR951030 {
	public TestAssertion Assert = new TestAssertion();
	ZdalDataSource zd;
	Set<String> zoneDs;

	@Before
	public void beforeTestCase() {

	}

	@After
	public void afterTestCase() {
		try {
			// zd.close();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// zd = null;
		}
	}

	@Subject("原zoneDs为 '',修改成 ''")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951031() {
		zd = (ZdalDataSource) ZdalClientSuite.context.getBean("zdalUpdateZoneDs");
		zoneDs = zd.getZdalConfig().getZoneDs();
		Assert.isTrue(zoneDs.isEmpty(), "zoneDS is ''");

		ZdalLdcSignalResource ldcSignalResource = zd.getLdcSignalResource();
		String key = "zoneDs";
		ldcSignalResource.updateResource(key, "");
        Step("原zoneDs为 '',修改成 ''");
		Set<String> zoneDs2 = zd.getZdalConfig().getZoneDs();
		Assert.isTrue(zoneDs2.isEmpty(), "zoneDs is ''");

	}

	@Subject("原zoneDs为 '',修改成'group_0_r'")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951032() {
		zd = (ZdalDataSource) ZdalClientSuite.context.getBean("zdalUpdateZoneDs");
		zoneDs = zd.getZdalConfig().getZoneDs();
		ZdalLdcSignalResource ldcSignalResource = zd.getLdcSignalResource();
		String key = "zoneDs";
		ldcSignalResource.updateResource(key, "group_0_r");
		Set<String> zoneDs2 = zd.getZdalConfig().getZoneDs();
		Assert.isTrue(!zoneDs2.isEmpty(), "zoneDs is not ''");
		Step("原zoneDs为 '',修改成'group_0_r'");
		for (String str : zoneDs2) {
			Assert.areEqual("group_0_r", str, "zoneDs is 'group_0_r'");
		}
	}

	@Subject("原zoneDS为'',修改成 'master_0,failover_0'")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951033() {
		zd = (ZdalDataSource) ZdalClientSuite.context.getBean("zdalLdcDSDrm");
		zoneDs = zd.getZdalConfig().getZoneDs();
		for (String str : zoneDs) {
			Assert.areEqual("master_0", str, "the zoneDs is 'master_0'");
		}
		ZdalLdcSignalResource ldcSignalResource = zd.getLdcSignalResource();
		String key = "zoneDs";
		ldcSignalResource.updateResource(key, "master_0,failover_0");
		Set<String> zoneDs2 = zd.getZdalConfig().getZoneDs();
		Assert.isTrue(!zoneDs2.isEmpty(), "zoneDs is not ''");
		
		Step("原zoneDS为'',修改成 'master_0,failover_0'");
		String st=zoneDs2.toString();
		int a=st.indexOf("failover_0");
		int b=st.indexOf("master_0");
		Assert.isTrue(a>=0&&b>=0, "zoneDs is 'master_0,failover_0'");
	}

}
