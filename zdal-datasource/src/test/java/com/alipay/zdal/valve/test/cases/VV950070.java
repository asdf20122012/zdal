package com.alipay.zdal.valve.test.cases;

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
import static org.junit.Assert.*;


@RunWith(ATSJUnitRunner.class)
@Feature("sql限流:执行select、delete")
public class VV950070 extends ValveBasicTest{	
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
	
    @Subject("sql限流(BASIC1573)：配置参数{3,5}，多时间间隔限流，执行select")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950071(){
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
    	//查询次数小于限流上限
		try {
			for(long i=0;i<2;i++){
				statement.execute("select * from test1");
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Exception",false);
		}
		//等待一个周期，查询次数等于限流上限，不限流 1574
		try {
			sleep(6000);
			for(long i=0;i<3;i++){
				statement.execute("select * from test1");
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		//等待一个周期，查询次数大于限流上限，限流 1575
		try {
			sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("select * from test1");
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			assertTrue("不是第4次请求开始限流",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
    
    @Subject("sql限流(BASIC1576)：配置参数{3,5}，多时间间隔限流，执行delete")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
	public void TC950072(){
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
    	//更新次数小于限流上限
		try {
			for(long i=0;i<2;i++){
				statement.execute("delete from test1 where clum = 1");
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Exception",false);
		}
		//等待一个周期，更新次数等于限流上限，不限流 1574
		try {
			sleep(6000);
			for(long i=0;i<3;i++){
				statement.execute("delete from test1 where clum = 1");
			}
		} catch (OutstripValveException e) {
			assertTrue("抛出OutstripValveException",false);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
		//等待一个周期，更新次数大于限流上限，限流 1575
		try {
			sleep(6000);
			clearRequestTime();
			for(long i=0;i<4;i++){
				incremRequestTime();
				statement.execute("delete from test1 where clum = 1");
			}
			fail("未抛出限流异常");
		} catch (OutstripValveException e) {
			assertTrue("不是第4次请求开始限流",getRequestTime() == 4);
			assertTrue("抛出OutstripValveException",true);
		} catch (Exception e) {
			assertTrue("Exception",false);
		}
	}
}