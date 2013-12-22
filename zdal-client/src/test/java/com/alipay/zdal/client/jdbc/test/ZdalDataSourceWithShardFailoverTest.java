/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author zhaofeng.wang  ·¶Ôö
 * @version $Id: ZdalDataSourceTest.java, v 0.1 2012-8-7 ÏÂÎç02:15:09 zhaofeng.wang Exp $
 */
public class ZdalDataSourceWithShardFailoverTest extends ZdalBaseTest {
    private String write_sql  = "insert into test_failover(user_id,name,address) values(?,?,?)";
    private String select_sql = "select * from test_failover where user_id=?";
    private String delete_sql = "delete from  test_failover where user_id=?";
    private String update_sql = "update test_failover set name=? where user_id=?";

    @Before
    public void setUp() throws Exception {
        this.setAppName("xts");
        this.setAppDsName("remote");
        this.setDbmode("dev");
        this.setIdcName("gzone");
        this.setServerUrl("http://zdataconsole.p9.alipay.net:8080");
        this.setLocalConfigPath("./config");
        super.setUp();
    }

    @Test
    public void testDelete() throws Exception {
        Object objects[] = new Object[] { 100 };
        super.insertByZdalDataSourceTemplate(delete_sql, objects);
    }

    @Test
    public void testInsert() throws Exception {
        Object objects[] = new Object[] { 100, "zdal_failover", "sbd" };
        super.insertByZdalDataSourceTemplate(write_sql, objects);
    }

    @Test
    public void testSelect() throws Exception {
        Object objects[] = new Object[] { 100 };
        Map<?, ?> map = super.selectMapByZdalDataSourceTemplate(select_sql, objects);
        assertEquals(map.get("user_id"), 100);
        assertEquals(map.get("name"), "zdal_failover");
    }

    @Test
    public void testUpate() throws Exception {
        Object objects[] = new Object[] { "zdal_failover2", 100 };
        super.updateByZdalDataSourceTemplate(update_sql, objects);
        objects = new Object[] { 100 };
        Map<?, ?> map = super.selectMapByZdalDataSourceTemplate(select_sql, objects);
        assertEquals(map.get("name"), "zdal_failover2");
    }

    @After
    public void tearDown() throws Throwable {
        super.tearDown();
    }

}
