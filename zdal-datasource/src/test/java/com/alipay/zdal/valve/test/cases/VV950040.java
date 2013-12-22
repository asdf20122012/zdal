package com.alipay.zdal.valve.test.cases;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Date;
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
@Feature("sql限流:多时间间隔内，sql执行次数大于限流次数，应被限")
public class VV950040 extends ValveBasicTest{	
	Map<String, String> changeMap = new HashMap<String, String>();

	@Before
	public void onSetUp() {
		Step("onSetUp");
		initLocalTxDsDoMap();

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
			dataSource.flush(changeMap);
			
			dataSource.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    @Subject("sql限流(BASIC1427)：配置参数{1,60}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950041(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,60");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Step("执行sql");
    	try {
			for(int i=0;i<1;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		try {
			Thread.sleep(58000);
			for(int i=0;i<1;i++){
				assertTrue(statement.execute("select * from test1"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		try {
			for(int i=0;i<1;i++){
				Thread.sleep(3000);
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1540)：配置参数{3,5}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950042(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			for(long i=0;i<3;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到当前时间ms" + countTime);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		try {
			Thread.sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("耗费时间不超过5000",countTime > 5000);
			assertTrue("限流异常不到4次就抛出",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Exception",false);
		}
    }
    
    @Subject("sql限流(BASIC1542)：配置参数{3,5}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950043(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			for(long i=0;i<3;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到当前时间ms" + countTime);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		try {
			Thread.sleep(6000);
			for(long i=0;i<3;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到当前时间ms" + countTime);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1543)：配置参数{3,5}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950044(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("限流异常不到4次就抛出",getRequestTime() == 4);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		try {
			Thread.sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("耗费时间不超过5000",countTime > 5000);
			assertTrue("限流异常不到4次就抛出",getRequestTime() == 4);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1544)：配置参数{3,5}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950045(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("限流异常不到4次就抛出",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		try {
			Thread.sleep(6000);
			clearRequestTime();
			for(long i=0;i<3;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("耗费时间不超过5000",countTime > 5000);
			assertTrue(getRequestTime() == 3);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1545)：配置参数{3,5}，多时间间隔限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950046(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("限流异常不到4次就抛出",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		try {
			Thread.sleep(6000);
			clearRequestTime();
			for(long i=0;i<2;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			assertTrue("耗费时间不超过5000",countTime > 5000);
			assertTrue(getRequestTime() == 2);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1626)：变更限流参数")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950047(){
    	int loop = 1000;
    	Long timeNormal = 0L;
    	Long timeValve = 0L;
    	Long timeValveAfter = 0L;
    	
    	try {
			for(int i=0;i<loop;i++){
				currentTime = new Date().getTime();
				statement.execute("select * from test1");
				timeNormal += new Date().getTime() - currentTime;
			}
			timeNormal = timeNormal / loop;
		
	    	changeMap.clear();
			changeMap.put(Parameter.SQL_VALVE, "10000,60");
			dataSource.flush(changeMap);
			for(int i=0;i<loop;i++){
				currentTime = new Date().getTime();
				statement.execute("select * from test1");
				timeValve += new Date().getTime() - currentTime;
			}
			timeValve = timeValve / loop;
			
	    	changeMap.clear();
			changeMap.put(Parameter.SQL_VALVE, "-1,1");
			dataSource.flush(changeMap);
			for(int i=0;i<loop;i++){
				currentTime = new Date().getTime();
				statement.execute("select * from test1");
				timeValveAfter += new Date().getTime() - currentTime;
			}
			timeValveAfter = timeValveAfter / loop;
			Logger.info("timeNormal  " + timeNormal);
			Logger.info("timeValve  " + timeValve);
			Logger.info("timeValveAfter  " + timeValveAfter);
			assertTrue("Exception",Math.abs(timeNormal - timeValveAfter) < 5);
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}	
    }
    
    @Subject("sql限流(BASIC1627)：连续变更参数，限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950048(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,5");
		changeMap.put(Parameter.SQL_VALVE, "4,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			clearRequestTime();
			for(int i=0;i<5;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("不是第5次才限流",super.getRequestTime() == 5);
			assertTrue("OutstripValveException",true);
		} catch (SQLException e) {
			assertTrue("SQLException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
}
