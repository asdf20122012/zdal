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
@Feature("sql限流:未配置或者配置不合法字符，不限流")
public class VV950030 extends ValveBasicTest{
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
	
    @Subject("sql限流(BASIC1421)：参数{A,1}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950031(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "A,1");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			Assert.isTrue(false,"抛出OutstripValveException");
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流：参数{1,A}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950032(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,A");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流：参数{-1,1}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950033(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,-1");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流(BASIC1422)：参数{1,-1}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950034(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,-1");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流(BASIC1423)：参数{1,0}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950035(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,0");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    
    @Subject("sql限流(BASIC1425)：先配置合法再配置不合法，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950036(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "-1,1");
		changeMap.put(Parameter.SQL_VALVE, "1,A");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流(BASIC1428)：配置{1,61}，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950037(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "1,61");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
    
    @Subject("sql限流：不配置，不限流")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950038(){
    	Step("设置sql限流参数");
    	changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "");
		dataSource.flush(changeMap);		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		Step("执行sql");
		try {
			for(int i=0;i<2;i++){
				assertTrue(statement.execute("select * from test1"));
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		
		Step("验证sql限流参数");
		long threshold=dataSource.getValve().getSqlValve().getThreshold();		
		long period=dataSource.getValve().getSqlValve().getPeriod();
		Assert.areEqual(-1L,threshold,"threshol是否为-1");
		Assert.areEqual(-1L,period,"period是否为-1");
	}
}
