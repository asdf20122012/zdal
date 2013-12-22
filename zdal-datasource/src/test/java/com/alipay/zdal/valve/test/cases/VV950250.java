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
@Feature("table限流:多表限流")
public class VV950250 extends ValveBasicTest{
	Map<String, String> changeMap = new HashMap<String, String>();    
	
	@Before
	public void onSetUp() {
		Step("onSetUp");
		initLocalTxDsDoMap();
		tbNames[0]="test1";
		tbNames[1]="test2";

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
			changeMap.put(Parameter.TABLE_VALVE, tbNames[0] + ",-1,-1;" + tbNames[1] + ",-1,-1");
			dataSource.flush(changeMap);
			
			dataSource.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Subject("表限流(BASIC1582)：只限流一个表{tbNames[0],3,3}")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950241() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbNames[0]+",3,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("未配置限流的表执行sql");
		try {
			for(long i=0;i<4;i++){
				statement.execute("select * from " + tbNames[1]);
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("已配置限流的表执行sql");
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("select * from " + tbNames[0]);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			assertTrue("不是第4次请求开始限流",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
	
	@Subject("表限流(BASIC1583)：限流两个表{tbNames[0],3,3;tbNames[1],5,3}")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950242() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbNames[0] + ",3,3;" + tbNames[1] + ",5,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("配置限流的表A执行sql");
		try {
			super.clearRequestTime();
			for(long i=0;i<6;i++){
				super.incremRequestTime();
				statement.execute("select * from " + tbNames[1]);
			}
			fail("限流失败");
		} catch (OutstripValveException e) {
			assertTrue("不是第6次限流",super.getRequestTime() == 6);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("配置限流的表B执行sql");
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("select * from " + tbNames[0]);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			assertTrue("不是第4次请求开始限流"+getRequestTime(),getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		}
	
	@Subject("表限流(BASIC1621)：限流两个表{tbNames[0],1,5;tbNames[1],2,5}")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950243(){
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TABLE_VALVE, tbNames[0] + ",1,5;" + tbNames[1] + ",2,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("配置限流的表A执行sql");
		try {
			for(int i=0;i<3;i++){
				assertTrue(statement.execute("SELECT * from `" + tbNames[1] + "`"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("OutstripValveException",true);
		} catch (SQLException e) {
			assertTrue("SQLException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("配置限流的表B执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("SELECT * from `" + tbNames[0] + "`"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("OutstripValveException",true);
		} catch (SQLException e) {
			assertTrue("SQLException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}		
	}	
}
