package com.alipay.zdal.test.rw;

import static com.alipay.ats.internal.domain.ATS.Step;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.ThreadLocalString;
import com.alipay.zdal.client.util.ThreadLocalMap;
import com.alipay.zdal.test.common.ZdalTestCommon;
import com.ibatis.sqlmap.client.SqlMapClient;

@RunWith(ATSJUnitRunner.class)
@Feature("粘着模型：ThreadLocalMap指定读sql路由到相同db.粘着模型,只能粘着去读，没有粘着写功能")
public class SR952180 {
	public TestAssertion Assert = new TestAssertion();
	private SqlMapClient sqlMap;
	private int countA = 0;
	private int countB = 0;

	@Before
	public void beforeTestCase() {
		Step("数据准备");
		ZdalTestCommon.dataPrepareForZds();

		sqlMap = (SqlMapClient) ZdalRwSuite.context.getBean("zdalRwMysql1");
	}

	@After
	public void afterTestCase() {
		Step("删除数据");
		ZdalTestCommon.dataDeleteForZds();
	}

	@Subject("连续读，指定路由到相同db")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC952181() {

		Step("开启路由 到同一个库");
		ThreadLocalMap.put(ThreadLocalString.DB_NAME_USED_BY_GROUP_SQL, null);
		ThreadLocalMap.put(ThreadLocalString.SET_GROUP_SQL_DATABASE,
				"SET_GROUP_SQL_DATABASE");
		Step("连续读");
		testReadDb();
		Step("断言，应该是从同一个库中读取的");
		Assert.areEqual(true, 10 == countA || countB == 10, "the count value");

		Step("关闭：指定路由到相同库");
		ThreadLocalMap.put(ThreadLocalString.SET_GROUP_SQL_DATABASE, null);
		ThreadLocalMap.put(ThreadLocalString.DB_NAME_USED_BY_GROUP_SQL, null);

	}

	@Subject("连续读，先打开，后关闭粘着模型")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC952182() {
		Step("先使用粘着模型");
		TC952181();

		Step("已经关闭了粘着模型 ，连接读10次");
		testReadDb();
		Assert.areEqual(true, 10 > countA && countB < 10, "the count value");
	}

	/**
	 * 连续读取db
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void testReadDb() {
		countA = 0;
		countB = 0;
		Step("连续读10次");
		for (int i = 0; i < 10; i++) {
			try {
				List<Object> a = (List<Object>) sqlMap
						.queryForList("queryRwSql");
				for (int j = 0; j < a.size(); j++) {
					HashMap<String, String> hs = (HashMap<String, String>) a
							.get(j);
					if ("DB_A".equalsIgnoreCase((String) hs.get("colu2"))) {
						countA++;
					} else if ("DB_B"
							.equalsIgnoreCase((String) hs.get("colu2"))) {
						countB++;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
