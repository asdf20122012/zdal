package com.alipay.zdal.test.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import com.alipay.zdal.client.config.ZoneError;
import com.alipay.zdal.client.config.controller.ZdalLdcSignalResource;
import com.alipay.zdal.client.exceptions.ZdalLdcException;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.test.common.ConstantsTest;
import com.alipay.zdal.test.common.ZdalTestBase;
import com.alipay.zdal.test.common.ZdalTestCommon;
import static com.alipay.ats.internal.domain.ATS.Step;

@RunWith(ATSJUnitRunner.class)
@Feature("修改  zoneError")
public class SR951050 extends ZdalTestBase {
	private String dburl;
	private String dbpsd;
	private String dbuser;
	private String dburl2;

	public TestAssertion Assert = new TestAssertion();

	@Subject("获取error,修改")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951051() {

		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
				.getBean("zdalLdcDSDrm");
		ZoneError zoneError = zd.getZdalConfig().getZoneError();
		Assert.isTrue(zoneError != null && zoneError.isException(),
				"Exception is true");

		ZdalLdcSignalResource ldcSignalResource = zd.getLdcSignalResource();
		String key = "throwException";
		ldcSignalResource.updateResource(key, false);

		ZoneError zoneError1 = zd.getZdalConfig().getZoneError();
		Assert.isTrue(zoneError1 != null && !zoneError1.isException(),
				"Exception is false");

	}

	@Before
	public void beginTestCase() {
		localFile = "./config/client";
		zdalDataSource.setConfigPath(localFile);
		dburl = ConstantsTest.mysq112UrlTddl0;
		dbpsd = ConstantsTest.mysq112Psd;
		dbuser = ConstantsTest.mysq112User;
		dburl2 = ConstantsTest.mysq112UrlTddl1;

	}

	@After
	public void afterTestCase() {
		String delSql = "delete from users_0 where user_id=10";
		String delSql2 = "delete from users_1 where user_id=11";
		ZdalTestCommon.dataUpdateJDBC(delSql, dburl, dbpsd, dbuser);
		ZdalTestCommon.dataUpdateJDBC(delSql2, dburl2, dbpsd, dbuser);
	}

	@Subject("原 zoneError是true,修改成 false.then 可访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951052() throws SQLException {
		Step("原 zoneError是true,修改成 false.then 可访问");
		zdalDataSource.setAppName("zdalClientZoneDsZoneError");
		zdalDataSource.setAppDsName("zdalZoneDsZoneErrorRight1");
		zdalDataSource.setLdcDsDrm(ConstantsTest.zone);
		zdalDataSource.initV3();

		ZdalLdcSignalResource ldcSignalResource = zdalDataSource
				.getLdcSignalResource();
		String key = "throwException";
		ldcSignalResource.updateResource(key, false);
		String insertSql = "insert into users (user_id,name,address) values (?,?,?)";
		String querySql = "select user_id,name,address from users_1 where user_id = 11";
		try {
			Step("sql执行");
			Connection conn = zdalDataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertSql);
			ps.setInt(1, 11);
			ps.setString(2, "test_users");
			ps.setString(3, "test_address");
			ps.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ResultSet rs = ZdalTestCommon.dataCheckFromJDBC(querySql, dburl2,
				dbpsd, dbuser);
		Assert.areEqual(true, rs.next(), "the value");

	}

	@Subject("原 zoneError是false,修改成true, then 不可以访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951053() {
		Step("原 zoneError是false,修改成true, then 不可以访问");
		int isException = 0;
		zdalDataSource.setAppName("zdalClientZoneDsZoneErrorLog");
		zdalDataSource.setAppDsName("zdalZoneDsZoneErrorLogRight");
		zdalDataSource.setLdcDsDrm(ConstantsTest.zone);
		zdalDataSource.initV3();

		ZdalLdcSignalResource ldcSignalResource = zdalDataSource
				.getLdcSignalResource();
		String key = "throwException";
		ldcSignalResource.updateResource(key, true);
		String insertSql = "insert into users (user_id,name,address) values (?,?,?)";
		try {
			Step("sql执行");
			Connection conn = zdalDataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertSql);
			ps.setInt(1, 11);
			ps.setString(2, "test_users");
			ps.setString(3, "test_address");
			ps.execute();
		} catch (Exception ex) {
			isException = 1;
			ex.printStackTrace();
			Assert.areEqual(ZdalLdcException.class, ex.getClass(),
					"get zoneError,is false,update to true can not visit");
		}
		Assert.areEqual(1, isException, "Exception is ok");
	}

	@Subject("原zoneError是 true,修改成 'a'.then 可以访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951054() throws SQLException {
		Step("原zoneError是 true,修改成 'a'.then 可以访问");
		zdalDataSource.setAppName("zdalClientZoneDsZoneError");
		zdalDataSource.setAppDsName("zdalZoneDsZoneErrorRight1");
		zdalDataSource.setLdcDsDrm(ConstantsTest.zone);
		zdalDataSource.initV3();

		ZdalLdcSignalResource ldcSignalResource = zdalDataSource
				.getLdcSignalResource();
		String key = "throwException";
		ldcSignalResource.updateResource(key, "a");
		String insertSql = "insert into users (user_id,name,address) values (?,?,?)";
		String querySql = "select user_id,name,address from users_1 where user_id = 11";
		try {
			Step("获取连接");
			Connection conn = zdalDataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertSql);
			ps.setInt(1, 11);
			ps.setString(2, "test_users");
			ps.setString(3, "test_address");
			Step("sql执行");
			ps.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ResultSet rs = ZdalTestCommon.dataCheckFromJDBC(querySql, dburl2,
				dbpsd, dbuser);
		Assert.areEqual(true, rs.next(), "the value");

	}

}
