package com.alipay.zdal.valve.test.cases;

import static org.junit.Assert.assertTrue;

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
@Feature("事务限流:未配置或者配置不合法字符，不限流")
public class VV950130 extends ValveBasicTest{
	Map<String, String> changeMap = new HashMap<String, String>();

	@Before
	public void onSetUp() {
		Step("onSetUp");
		initLocalTxDsDoMap();

		try {
			dataSource = new ZDataSource(LocalTxDsDoMap.get("ds-mysql"));
			connection = dataSource.getConnection();
			statement = connection.createStatement();
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
	
	@Subject("事务限流：参数{a,1}，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950131() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "a,1");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
	
	@Subject("事务限流：参数{-1,1}，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950132() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "-1,1");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
	
	@Subject("事务限流：参数{1,0}，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950133() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "1,0");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
	
	@Subject("事务限流：参数{1,61}，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950134() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "1,61");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
	
	@Subject("事务限流：参数{1,A}，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950135() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "1,A");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
	@Subject("事务限流：不配置，不限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950136() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
				for(int i=0;i<2;i++){
					connection.setAutoCommit(false);
					assertTrue(statement.execute("select * from test1"));
					connection.commit();
				}
		} catch (OutstripValveException e) {
			Logger.error(e.getMessage());
			assertTrue("OutstripValveException", false);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}
}
