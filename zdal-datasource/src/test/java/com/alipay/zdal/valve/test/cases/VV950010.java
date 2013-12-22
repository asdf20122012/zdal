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
@Feature("sql限流:单时间间隔内，sql执行次数大于限流次数，应被限")
public class VV950010 extends ValveBasicTest{
	Map<String, String> changeMap=new HashMap<String, String>();
	
	@Before
	public void onSetUp(){
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
	public void onTearDown() throws Exception{
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
	
    @Subject("sql限流(BASIC1426)：参数{1,59}，接近时间间隔结束第2次执行被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950011(){
		Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,59");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行sql");
		try {
			clearRequestTime();
			for (int i = 0; i < 2; i++) {
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
				sleep(50000);
			}
			fail("未抛出限流异常");
		} catch (Exception e) {
			Logger.info("OutstripValveException:"+e.getMessage());			
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Assert.areEqual(2L,getRequestTime(),"在第" + getRequestTime() + "次sql操作抛出限流异常");
		}
	}
    
    @Subject("sql限流(BASIC1417)：参数{1,5}，第2次执行被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950012(){
    	Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			clearRequestTime();
			for(int i=0;i<2;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (Exception e) {
			Logger.info("OutstripValveException:"+e.getMessage());
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Assert.areEqual(2L,getRequestTime(),"在第" + getRequestTime() + "次sql操作抛出限流异常");						
		}
	}
    
    @Subject("sql限流(BASIC1416)：参数{0,5}，sql执行次数最小值0，全部被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950013(){		
    	Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "0,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 

		Step("执行sql");
    	try {
			for(int i=0;i<1;i++){
				assertTrue(statement.execute("select * from test1"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			Step("验证限流抛出的异常");
			Logger.info("OutstripValveException:"+e.getMessage());	
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}   
    
    
    @Subject("sql限流(BASIC1420)：参数{9223372036854775807l,60}，sql执行次数最大值，第92233720368547748071l次执行被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
//    @Test
	public void TC950014(){
    	Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "9223372036854775807l,60");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
    	try {
			for(long i=0l;i<9223372036854774807l;i++){
				assertTrue(statement.execute("select * from test1"));
			}
			assertTrue(statement.execute("select * from test1"));
			assertTrue(statement.execute("select * from test1"));
			fail("没限住");
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1418)：参数{300,60}，时间间隔最大值60s，第301次执行被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950015(){
    	Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "300,60");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
    	
		Step("执行sql");
    	try {
			clearRequestTime();
			for(int i=0;i<301;i++){
				Logger.info("sql调用次数" + i);
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			Logger.info("OutstripValveException:"+e.getMessage());		
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Assert.areEqual(301L,getRequestTime(),"在第" + getRequestTime() + "次sql操作抛出限流异常");
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1424)：参数{1,1}，时间间隔最小值1s，第2次执行被限")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950016(){
    	Step("设置sql限流参数");
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,1");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
    	
		Step("执行sql");
		clearRequestTime();
		try {
			for(int i=0;i<2;i++){
				incremRequestTime();
				assertTrue(statement.execute("select * from test1"));				
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Assert.areEqual(2L,getRequestTime(),"在第" + getRequestTime() + "次sql操作抛出限流异常");
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
  
    @Subject("sql限流(BASIC1537)：配置参数{1,5}，测试消耗时间")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950017(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
    	try {
			for(long i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Long countTime = new Date().getTime() - currentTime;
			Assert.isTrue(countTime <= 100,"耗费时间超过100ms");
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
    }
    
    @Subject("sql限流(BASIC1539)：配置参数{5,5}，测试消耗时间")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950018(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "5,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行sql");
		try {
			for(long i=0;i<5;i++){
				assertTrue(statement.execute("select * from test1"));
				//logger.info("请求次数" + i);
			}
			Thread.sleep(4700);
			assertTrue(statement.execute("select * from test1"));
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Assert.areEqual(OutstripValveException.class,e.getClass(),"验证异常类");
			Long countTime = new Date().getTime() - currentTime;
			Logger.info("从规则生效到限流耗费时间ms" + countTime);
			Assert.isTrue(countTime >= 4700,"耗费时间![4800,5000)");
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
}
