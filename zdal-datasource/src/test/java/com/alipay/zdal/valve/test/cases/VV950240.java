package com.alipay.zdal.valve.test.cases;

import static org.junit.Assert.*;

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
import com.alipay.zdal.valve.test.utils.ValveBasicTest;
import com.alipay.zdal.valve.util.OutstripValveException;

@RunWith(ATSJUnitRunner.class)
@Feature("table限流:多时间间隔内，执行次数大于限流次数，应被限")
public class VV950240 extends ValveBasicTest{
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
			changeMap.put(Parameter.TABLE_VALVE, tbName+",-1,-1");
			dataSource.flush(changeMap);
			
			dataSource.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Subject("表限流(BASIC1581)：多时间间隔，被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950241() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbName+",3,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行sql:表操作次数小于限流值不限流");
		try {
			for(long i=0;i<2;i++){
				statement.execute("select * from " + tbName);
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
			Logger.error(e.getMessage());
		} catch (Exception e) {
			assertTrue("Exception",false);
			Logger.error(e.getMessage());
		}
		
		Step("执行sql:表操作次数等于限流值不限流");
		try {
			sleep(4000);
			for(long i=0;i<3;i++){
				statement.execute("select * from " + tbName);
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
			Logger.error(e.getMessage());
		} catch (Exception e) {
			assertTrue("Exception",false);
			Logger.error(e.getMessage());
		}
		
		Step("执行sql:表操作次数大于限流值不限流");
		try {
			sleep(4000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			assertTrue("不是第4次请求开始限流,getRequestTime()=" + getRequestTime(), getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
			Logger.info(e.getMessage());
		} catch (Exception e) {
			assertTrue("Exception",false);
			Logger.error(e.getMessage());
		}
	}
	
	@Subject("表限流(BASIC1631)：连续变更配置，被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950242() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbName+",1,5");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",4,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行sql");
		try {
			clearRequestTime();
			for(int i=0;i<5;i++){
				incremRequestTime();
				assertTrue(statement.execute("SELECT * from `" + tbName + "`"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("不是第5次才限流",super.getRequestTime() == 5);
			assertTrue("OutstripValveException",true);
			Logger.info(e.getMessage());
		} catch (SQLException e) {
			assertTrue("SQLException",false);
			Logger.error(e.getMessage());
		} catch (Exception e) {
			assertTrue("Exception",false);
			Logger.error(e.getMessage());
		}
	}
}
