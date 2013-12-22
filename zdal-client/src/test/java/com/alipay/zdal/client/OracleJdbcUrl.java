/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: OracleJdbcUrl.java, v 0.1 2012-11-22 ÉÏÎç10:40:41 Exp $
 */
public class OracleJdbcUrl {

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName("oracle.jdbc.OracleDriver");
    }

    @Test
    public void test1() throws Exception {
        String jdbcUrl = "jdbc:oracle:oci:@" + "(" + "DESCRIPTION =" + "(ADDRESS_LIST="
                         + "(ADDRESS=(PROTOCOL=TCP)(HOST=cifdb.devdb.alipay.net)(PORT=1521)))"
                         + "(CONNECT_DATA =(SERVICE_NAME = cifdb))" + ")";
        String userName = "sharedata";
        String password = "ali88";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            pst = conn.prepareStatement("select id,name from meta_idc");
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getInt(1));
                System.out.println("name = " + rs.getString(2));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    @Test
    public void test2() throws Exception {
        String jdbcUrl = "jdbc:oracle:oci:@//cifdb.devdb.alipay.net:1521:cifdb";
        String userName = "sharedata";
        String password = "ali88";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            pst = conn.prepareStatement("select id,name from meta_idc");
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getInt(1));
                System.out.println("name = " + rs.getString(2));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    @Test
    public void test3() throws Exception {
        String jdbcUrl = "jdbc:oracle:oci:@cifdb.devdb.alipay.net:1521/cifdb";
        String userName = "sharedata";
        String password = "ali88";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            pst = conn.prepareStatement("select id,name from meta_idc");
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getInt(1));
                System.out.println("name = " + rs.getString(2));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    @Test
    public void test4() throws Exception {
        String jdbcUrl = "jdbc:oracle:oci:@cifdb.devdb.alipay.net:1521:cifdb";
        String userName = "sharedata";
        String password = "ali88";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            pst = conn.prepareStatement("select id,name from meta_idc");
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getInt(1));
                System.out.println("name = " + rs.getString(2));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
