package com.alipay.zdal.test.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.config.drm.ZdalLdcSignalResource;
import com.alipay.zdal.test.common.ConstantsTest;
import com.alipay.zdal.test.common.ZdalTestBase;
import com.alipay.zdal.test.common.ZdalTestCommon;
import static com.alipay.ats.internal.domain.ATS.Step;

@RunWith(ATSJUnitRunner.class)
@Feature("动态修改  zoneDs ")
public class SR951040 extends ZdalTestBase {
	private String dburl;
	private String dbpsd;
	private String dbuser;
	private String dburl2;

	@Before
	public void beginTestCase() {
		dburl = ConstantsTest.mysq112UrlTddl0;
		dbpsd = ConstantsTest.mysq112Psd;
		dbuser = ConstantsTest.mysq112User;
		dburl2 = ConstantsTest.mysq112UrlTddl1;

		localFile = "./config/client";
		zdalDataSource.setConfigPath(localFile);
		zdalDataSource.setAppName("zdalClientupdateZoneDsZoneError");
		zdalDataSource.setAppDsName("zdalClientZoneDsZoneErrorRight");
		zdalDataSource.setLdcDsDrm(ConstantsTest.zone);

	}

	@After
	public void endTestCase() {		
		String delSql = "delete from users_0 where user_id=10";
		String delSql2 = "delete from users_1 where user_id=11";
		ZdalTestCommon.dataUpdateJDBC(delSql, dburl, dbpsd, dbuser);
		ZdalTestCommon.dataUpdateJDBC(delSql2, dburl2, dbpsd, dbuser);
		try {
			zdalDataSource.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zdalDataSource=null;
	}

	@Subject("得到zoneDs为 master_0,修改成 '',then 可以访问[之前不能访问]")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951041() throws SQLException {
		zdalDataSource.initV3();
		Set<String> zoneDs = zdalDataSource.getZdalConfig().getZoneDs();
		for (String str : zoneDs) {
			Assert.areEqual("master_0", str, "get zoneDs,is 'master_0'");
		}
		Step("得到zoneDs为 master_0,修改成 '',then 可以访问[之前不能访问]");
		updateZoneDsThenInsert("");

	}

	@Subject("得到zoneDs为 master_0,修改成 'master_1',then 可以访问[之前不能访问]")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951042() throws SQLException {
		zdalDataSource.initV3();
		Set<String> zoneDs = zdalDataSource.getZdalConfig().getZoneDs();
		for (String str : zoneDs) {
			Assert.areEqual("master_0", str, "get zoneDs,is 'master_0'");
		}
		Step("得到zoneDs为 master_0,修改成 'master_1',then 可以访问[之前不能访问]");
		updateZoneDsThenInsert("master_1");
	}
	
	@Subject("得到zoneDs为 master_0,修改成 'master_0,master_1',then 可以访问")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951043() throws SQLException {
		
		zdalDataSource.initV3();	
		Step("得到zoneDs为 master_0,修改成 'master_0,master_1',then 可以访问");
		updateZoneDsThenInsert("master_0,master_1");
	}

	/**
	 * update zoneDs to toZoneDs,then insert SQL
	 * @param toZoneDs
	 * @throws SQLException 
	 */
	public void updateZoneDsThenInsert(String toZoneDs) throws SQLException {
		
		ZdalLdcSignalResource ldcSignalResource = zdalDataSource
				.getLdcSignalResource();
		String key = "zoneDs";
		ldcSignalResource.updateResource(key, toZoneDs);

		String insertSql = "insert into users (user_id,name,address) values (?,?,?)";
		String querySql = "select user_id,name,address from users_1 where user_id = 11";
		try {
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

}
