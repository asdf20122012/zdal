package com.alipay.zdal.valve.test.cases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;import static com.alipay.ats.internal.domain.ATS.*;

import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.util.Parameter;
import com.alipay.zdal.valve.Valve;
import com.alipay.zdal.valve.test.utils.ValveBasicTest;
import com.alipay.zdal.valve.util.OutstripValveException;

@RunWith(ATSJUnitRunner.class)
@Feature("变更限流配置")
public class VV950340 extends ValveBasicTest{
	Map<String, String> changeMap = new HashMap<String, String>();    
	
	@Before
	public void onSetUp() {
		Step("onSetUp");
		initLocalTxDsDoMap();
		tbName="test1";

		try {
			dataSource = new ZDataSource(LocalTxDsDoMap.get("ds-mysql"));
			connection = dataSource.getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void onTearDown() throws Exception {
		Step("onTearDown");
		try {
			statement.close();
			connection.close();

			changeMap.clear();
			changeMap.put(Parameter.SQL_VALVE, "-1,-1");
			changeMap.put(Parameter.TX_VALVE, "-1,-1");
			changeMap.put(Parameter.TABLE_VALVE, tbName+",-1,-1");
			dataSource.flush(changeMap);

			dataSource.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Subject("变更限流配置")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950341() {
		Step("设置限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "5,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<6;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第6次请求开始限流",getRequestTime() == 6);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}		
		
		Step("变更限流参数为表限流");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbName+",2,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("验证参数");
		Valve valve2 = dataSource.getValve();
		Assert.isTrue(valve2.getSqlValve().getPeriod()==3l && valve2.getSqlValve().getThreshold()==5l, "验证sql-valve参数");
		Assert.isTrue(valve2.getTXValve().getPeriod()==-1l && valve2.getTXValve().getThreshold()==-1l, "验证tx-valve参数");
		Assert.isTrue(valve2.getTableValve(tbName).getPeriod()==3l && valve2.getTableValve(tbName).getThreshold()==2l, "验证table-valve参数");
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<3;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是表限流",e.getMessage().contains("表" + tbName.trim() + "限流执行超过指定阈值"));
			assertTrue("第"+getRequestTime() +"次请求开始限流",getRequestTime() == 3);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("变更限流参数增加事务限流");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "2,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("验证参数");
		Valve valve3 = dataSource.getValve();
		Assert.isTrue(valve3.getSqlValve().getPeriod()==3l && valve3.getSqlValve().getThreshold()==5l, "验证sql-valve参数");
		Assert.isTrue(valve3.getTXValve().getPeriod()==3l && valve3.getTXValve().getThreshold()==2l, "验证tx-valve参数");
		Assert.isTrue(valve3.getTableValve(tbName).getPeriod()==5l && valve3.getTableValve(tbName).getThreshold()==3l, "验证table-valve参数");
		
		Step("变更限流参数为空");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "");
		changeMap.put(Parameter.TABLE_VALVE, "");
		changeMap.put(Parameter.TX_VALVE, "");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
}
