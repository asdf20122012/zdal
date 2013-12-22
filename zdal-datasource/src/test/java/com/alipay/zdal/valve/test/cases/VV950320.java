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
@Feature("设置sql、tx、table限流，则采用sql限流")
public class VV950320 extends ValveBasicTest{
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
	
	@Subject("(BASIC1585)：设置sql、tx、table限流，sql阀值最小，执行非事务，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950321() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "6,3");
		changeMap.put(Parameter.SQL_VALVE, "4,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",5,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<7;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第5次请求开始限流",getRequestTime() == 5);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}


	@Subject("设置sql、tx、table限流，sql阀值最小，执行事务，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950322() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "6,3");
		changeMap.put(Parameter.SQL_VALVE, "4,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",5,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<7;i++){
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from " + tbName);
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第5次请求开始限流",getRequestTime() == 5);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}finally{
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Subject("设置sql、tx、table限流，sql阀值等于table阀值，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950323() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "6,3");
		changeMap.put(Parameter.SQL_VALVE, "4,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",4,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<7;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第5次请求开始限流",getRequestTime() == 5);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
	
	@Subject("BASIC1588：设置sql、tx限流，sql阀值最小，执行非事务，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950324(){
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "6,3");
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
			for(long i=0;i<7;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第4次请求开始限流",getRequestTime() == 6);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
	
	@Subject("BASIC1588：设置sql、tx限流，sql阀值最小，执行事务，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950325(){
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "6,3");
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
			for(long i=0;i<7;i++){
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from " + tbName);
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第4次请求开始限流",getRequestTime() == 6);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}finally{
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Subject("设置sql、table限流，sql阀值最小，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950326() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",4,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<5;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第4次请求开始限流",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}	
	}
	
	@Subject("设置sql、table限流，sql阀值等于table阀值，则采用sql限流")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950327() {
		Step("设置表限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,3");
		changeMap.put(Parameter.TABLE_VALVE, tbName+",3,3");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");	
		try {
			clearRequestTime();
			for(long i=0;i<5;i++){
				incremRequestTime();
				statement.execute("select * from " + tbName);
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("不是SQL限流",e.getMessage().contains("SQL执行超过指定阈值"));
			assertTrue("不是第4次请求开始限流",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}	
	}
}
