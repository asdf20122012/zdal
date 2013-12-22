package com.alipay.zdal.test.rw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import static com.alipay.ats.internal.domain.ATS.Step;
import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.ThreadLocalString;
import com.alipay.zdal.client.util.ThreadLocalMap;
import com.alipay.zdal.test.common.ConstantsTest;
import com.alipay.zdal.test.common.ZdalTestCommon;
import com.ibatis.sqlmap.client.SqlMapClient;

@RunWith(ATSJUnitRunner.class)
@Feature("指定数据源，不重试")
public class SR952210 {
	public TestAssertion Assert = new TestAssertion();
	private SqlMapClient sqlMap;
	
	@After
	public void afterTestCase(){
		ZdalTestCommon.dataDeleteForZds();
		ThreadLocalMap.reset();
		ZdalTestCommon.dataDeleteForTddl();
	}
	
	
	@Subject("指定数据源不重试。读库 ds0:r2w1p0,ds1:r1w1p1,ds2:r1w1p2。ds0和ds1不可用,ds2可用。指定ds1")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC952211(){
		Step("指定库，并且说明不重试");
		ThreadLocalMap.put(ThreadLocalString.DATABASE_INDEX, 1);
        ThreadLocalMap.put(ThreadLocalString.WRITE_DATABASE_READ_RETRY,
        "WRITE_DATABASE_READ_RETRY");
        
        ZdalTestCommon.dataPrepareForTddl(); 
        Step("zdal读数据");
		HashMap<String, Integer> mp = readRwMysqlDB("zdalRwRetry1",
				"queryRwSqlTddl");
		int countA = mp.get("countA");
		int countB = mp.get("countB");
		int countC = mp.get("countC");
		Assert.areEqual(true, countA == 0, "the count0 value");
		Assert.areEqual(true, countB == 0, "the count1 value");
		Assert.areEqual(true, countC == 0, "the count2 value");
        
	}
	
	
	@Subject("指定数据源不重试：写库ds0:r10w10,ds1:r10w10,ds2:r1w1，ds0和ds1不可用,ds2可用。指定ds0")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC952212() {
		Step("指定库，并且说明不重试");
		ThreadLocalMap.put(ThreadLocalString.DATABASE_INDEX, 0);
        ThreadLocalMap.put(ThreadLocalString.WRITE_DATABASE_READ_RETRY,
        "WRITE_DATABASE_READ_RETRY");
        
		HashMap<String, Integer> mp = writeRwMysqlDB("zdalRwRetry4",
				"insertRwSqlTddl");
		int count0 = mp.get("count0");
		int count1 = mp.get("count1");
		int count2 = mp.get("count2");
		Assert.areEqual(true, count0 == 0, "the count0 value");
		Assert.areEqual(true, count1 == 0, "the count1 value");
		Assert.areEqual(true, count2 == 0, "the count2 value");
	}
	
	
	/**
	 * 针对rw mysql库的读库的公共函数(tddl库）
	 * 
	 * @param beanName
	 * @param sqlName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> readRwMysqlDB(String beanName,
			String sqlName) {
		HashMap<String, Integer> hp = new HashMap<String, Integer>();
		int countA = 0;
		int countB = 0;
		int countC = 0;
		Step("准备数据");
		ZdalTestCommon.dataPrepareForZds();

		Step("读取数据，并计算从每个库里面读数的次数");
		sqlMap = (SqlMapClient) ZdalRwSuite.context.getBean(beanName);
		String sqlStr1 = sqlName;
		
			try {
				List<Object> a = (List<Object>) sqlMap.queryForList(sqlStr1);
				for (int i = 0; i < a.size(); i++) {
					HashMap<String, String> hs = (HashMap<String, String>) a
							.get(i);
					if ("DB_A".equalsIgnoreCase((String) hs.get("address"))) {
						countA++;
					} else if ("DB_B".equalsIgnoreCase((String) hs
							.get("address"))) {
						countB++;
					} else if ("DB_C".equalsIgnoreCase((String) hs
							.get("address"))) {
						countC++;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		hp.put("countA", countA);
		hp.put("countB", countB);
		hp.put("countC", countC);
		return hp;
	}
	
	/**
	 * 针对rw mysql写库的公共函数（tddl）
	 * 
	 * @param beanName
	 * @param sqlName
	 * @return
	 */
	private HashMap<String, Integer> writeRwMysqlDB(String beanName,
			String sqlName) {
		String url0 = ConstantsTest.mysq112UrlTddl0;
		String url1 = ConstantsTest.mysq112UrlTddl1;
		String url2 = ConstantsTest.mysq112UrlTddl2;
		String user = ConstantsTest.mysq112User;
		String psd = ConstantsTest.mysq112Psd;

		HashMap<String, Integer> mp = new HashMap<String, Integer>();
		sqlMap = (SqlMapClient) ZdalRwSuite.context.getBean(beanName);
		String sqlStr1 = sqlName;

		
			try {
				sqlMap.insert(sqlStr1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Step("进写入的数据count，进行验证");
		String querySql = "select count(*) from users where name = 'DB' and address='DB_G'";
		ResultSet rs0 = ZdalTestCommon.dataCheckFromJDBC(querySql, url0, psd,
				user);
		ResultSet rs1 = ZdalTestCommon.dataCheckFromJDBC(querySql, url1, psd,
				user);
		ResultSet rs2 = ZdalTestCommon.dataCheckFromJDBC(querySql, url2, psd,
				user);
		try {
			Assert.areEqual(true, rs0.next() && rs1.next() && rs2.next(),
					"the value");
			int count0 = rs0.getInt(1);
			int count1 = rs1.getInt(1);
			int count2 = rs2.getInt(1);

			mp.put("count0", count0);
			mp.put("count1", count1);
			mp.put("count2", count2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mp;
	}

}
