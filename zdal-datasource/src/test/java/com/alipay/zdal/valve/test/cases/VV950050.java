package com.alipay.zdal.valve.test.cases;

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
import com.alipay.zdal.valve.test.utils.ValveClient;
import com.alipay.zdal.valve.test.utils.ValveBasicTest;
import static org.junit.Assert.*;

@RunWith(ATSJUnitRunner.class)
@Feature("sql限流:以启动valve客户端形式")
public class VV950050 extends ValveBasicTest {
	Map<String, String> changeMap=new HashMap<String, String>();
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
	
    @Subject("sql限流(BASIC1558)：参数{5,5}")
    @Priority(PriorityLevel.NORMAL)
    @Tester("riqiu")
    @Test
    public void TC950051() {
		changeMap.clear();
		changeMap.put(Parameter.SQL_VALVE, "5,5");
		dataSource.flush(changeMap);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
    	int threadNum = 10;
        int requestNum = 10;
        try {
            ValveClient valveClient = new ValveClient(statement, "select * from test1", requestNum);
            for (int i = 0; i < threadNum; i++) {
                new Thread(valveClient).start();
            }
            while (valveClient.getStopNum() != threadNum) {

            }
            Logger.info("SQL请求的总数量是" + valveClient.getQeuuestNum());
            Logger.info("开始限流的请求次数是" + valveClient.getExceptionNum());
        } catch (Exception e) {
            assertTrue("Exception", false);
        }
    }
}
