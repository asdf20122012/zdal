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
import com.alipay.zdal.valve.test.utils.ValveBasicTest;
import com.alipay.zdal.valve.util.OutstripValveException;

@RunWith(ATSJUnitRunner.class)
@Feature("事务限流:单时间间隔内，事务执行次数大于限流次数，应被限")
public class VV950110  extends ValveBasicTest{
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
	
	@Subject("事务限流(BASIC1618)：参数{1,5}，第2次执行被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950111() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "1,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		clearRequestTime();
		try {
			for (int i = 0; i < 2; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				assertTrue(statement.execute("select * from test1"));
				connection.commit();
			}
			fail("没限住");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("OutstripValveException", true);
			assertTrue("第" + getRequestTime() + "次抛异常", getRequestTime() == 2);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			assertTrue("SQLException", false);
		} catch (Exception e) {
			Logger.error(e.getMessage());
			assertTrue("Exception", false);
		}
	}

	@Subject("事务限流：参数{0,5}，全部被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950112() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "0,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
			clearRequestTime();
			for (long i = 0; i < 1; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from test1");
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("第" + getRequestTime() + "1次请求开始限流",
					getRequestTime() == 1);
			assertTrue("抛出OutstripValveException", true);
		} catch (Exception e) {
			assertTrue("Exception", false);
		}
	}
		
	@Subject("事务限流(BASIC1684)：参数{2,60}，第3次执行被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950113() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "2,60");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
			clearRequestTime();
			for (long i = 0; i < 3; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from test1");
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("第"+getRequestTime()+"次请求开始限流", getRequestTime() == 3);
			assertTrue("抛出OutstripValveException", true);
		} catch (Exception e) {
			assertTrue("Exception", false);
		}
	}

	@Subject("事务限流：参数{2,1}，第3次执行被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950114() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "2,1");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
			clearRequestTime();
			for (long i = 0; i < 3; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from test1");
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("第"+getRequestTime()+"次请求开始限流", getRequestTime() == 3);
			assertTrue("抛出OutstripValveException", true);
		} catch (Exception e) {
			assertTrue("Exception", false);
		}
	}
	
	@Subject("事务限流：参数{60,60}，第61次执行被限")
	@Priority(PriorityLevel.NORMAL)
	@Tester("riqiu")
	@Test
	public void TC950115() {
		Step("设置事务限流参数");
		changeMap.clear();
		changeMap.put(Parameter.TX_VALVE, "60,60");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Step("执行事务");
		try {
			clearRequestTime();
			for (long i = 0; i < 61; i++) {
				incremRequestTime();
				connection.setAutoCommit(false);
				statement.execute("select * from test1");
				connection.commit();
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			Logger.info(e.getMessage());
			assertTrue("第"+getRequestTime()+"次请求开始限流", getRequestTime() == 61);
			assertTrue("抛出OutstripValveException", true);
		} catch (Exception e) {
			assertTrue("Exception", false);
		}
	}
}
