/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.alipay.zdal.client.ThreadLocalString;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.util.ThreadLocalMap;

/**
 * 
 * @author zhaofeng.wang  ·¶Ôö
 * @version $Id: ZdalDataSourceTest.java, v 0.1 2012-8-7 ÏÂÎç02:15:09 zhaofeng.wang Exp $
 */
public class ZdalDataSourceWithOutPutTest extends ZdalBaseTest {
    private static final Logger logger     = Logger.getLogger(ZdalDataSourceWithOutPutTest.class);
    public String               select_sql = "select * from card_month where (card_no =?) and  gmtTime>=? and gmtTime<= ? order by gmtTime limit ?,?";

    @Before
    public void setUp() throws Exception {
        this.setAppName("fangzeng");
        this.setAppDsName("zdal-ds-2");
        this.setDbmode("dev");
        this.setIdcName("gzone");
        this.setServerUrl("http://zdataconsole.p9.alipay.net:8080");
        this.setLocalConfigPath("./config");
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSelect() throws Exception {
        Object objects[] = new Object[] { "2011042910", new Date(111, 0, 1), new Date(111, 1, 28),
                1, 2 };
        List list = super.selectListByZdalDataSourceTemplate(select_sql, objects);
        assertEquals(list.size(), 0);
        LinkedHashMap<?, ?> map0 = (LinkedHashMap<?, ?>) list.get(0);
        assertEquals(map0.get("gmtTime"), "");
        LinkedHashMap<?, ?> map1 = (LinkedHashMap<?, ?>) list.get(0);
        assertEquals(map1.get("gmtTime"), "");
    }

    /**
     * 
     * @param args
     * @throws InterruptedException 
     * @throws SQLException 
     */
    public static void main(String[] args) throws InterruptedException, SQLException {

        ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName("fangzeng");
        dataSource.setAppDsName("zdal-ds-2");
        dataSource.setDbmode("dev");
        dataSource.setZone("dev");
        dataSource.setZdataconsoleUrl("http://zdataconsole.p9.alipay.net:8080");
        dataSource.setConfigPath("./config");
        dataSource.init();
        String sql = "select * from card_month where (card_no =?) and  gmtTime>=? and gmtTime<= ? order by gmtTime limit ?,?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "2011042910");
            ps.setDate(2, new Date(111, 0, 1));
            ps.setDate(3, new Date(111, 1, 28));
            ps.setInt(4, 1);
            ps.setInt(5, 8);
            ThreadLocalMap.put(ThreadLocalString.DATABASE_INDEX, 1);
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
                System.out.println(rs.getDate(2));
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        System.exit(0);

    }

}
