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
@Feature("事务限流:多时间间隔内，sql执行次数大于限流次数，应被限")
public class VV950140 extends ValveBasicTest{
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
			changeMap.put(Parameter.TX_VALVE, "-1,-1");
			dataSource.flush(changeMap);
			
			dataSource.destroy();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Subject("事务限流(BASIC1628)：连续变更参数，被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950141() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "1,5");
		changeMap.put(Parameter.TX_VALVE, "4,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
			clearRequestTime();
			for (int i = 0; i < 5; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				assertTrue(statement.execute("select * from test1"));
				connection.commit();
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			Logger.info("***********" + getRequestTime());
			assertTrue("OutstripValveException", true);
			assertTrue("不是第5次才限流", getRequestTime() == 5);
			Logger.info(e.getMessage());
		} catch (SQLException e) {
			assertTrue("SQLException", false);
			Logger.error(e.getMessage());
		} catch (Exception e) {
			assertTrue("Exception", false);
			Logger.error(e.getMessage());
		}
	}
	
	@Subject("事务限流(BASIC1579)：连续变更参数，被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950142() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "3,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentTime = new Date().getTime();

		Step("执行事务");	
		//事务次数小于限流上限，不限流 
		try {
			for(long i=0;i<2;i++){
				connection.setAutoCommit(false);
				statement.execute("delete from test1 where clum=1");
				connection.commit();
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}finally{
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//等待一个周期，事务次数等于限流上限，不限流 
		try {
			sleep(6000);
			for(long i=0;i<3;i++){
				connection.setAutoCommit(false);
				statement.execute("delete from test1 where clum=1");
				connection.commit();
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}finally{
			try {
				sleep(6000);
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//等待一个周期，事务更新次数大于限流上限，限流
		try {
			sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("delete from test1 where clum=1");
				connection.commit();
			}
			fail("执行第" + getRequestTime() + "次事务操作，未抛出限流异常");
		} catch (OutstripValveException e) {
			try {
				sleep(6000);
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			assertTrue("不是第4次请求开始限流" + getRequestTime(),getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}finally{
			try {
				sleep(6000);
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//等待一个周期，事务更新次数大于限流上限，setAutoCommit为true，不限流
		try {
			sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				connection.setAutoCommit(true);
				statement.execute("delete from test1 where clum=1");
			}
		} catch (OutstripValveException e) {
			assertTrue("请求次数不是4次",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
}
