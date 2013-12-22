/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: RWThread.java, v 0.1 2012-11-26 ÉÏÎç10:27:46 Exp $
 */
public class ShardRWThread extends Thread {
    private static final Logger logger       = Logger.getLogger(ShardRWThread.class);

    private static final String WRITE_SQL    = "insert into user(user_id,name,address) values(?,?,?)";

    private static final String READ_SQL     = "select user_id,name,address from user where user_id in( ?)";

    public static final int     INSERT_COUNT = 10;

    private DataSource          dataSource;

    public ShardRWThread(DataSource dataSource, String name) {
        this.dataSource = dataSource;
        this.setName(name);
    }

    /** 
     * @see java.lang.Thread#run()
     */
    public void run() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            for (int i = 1; i <= INSERT_COUNT; i++) {
                //                ps = conn.prepareStatement(WRITE_SQL);
                //                ps.setInt(1, i);
                //                ps.setString(2, "name" + i);
                //                ps.setString(3, "address" + i);
                //                ps.execute();

                ps = conn.prepareStatement(READ_SQL);
                ps.setInt(1, 1);
                //                ps.setInt(2, 2);
                rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getInt(1));
                    System.out.println(rs.getString(2));
                    System.out.println(rs.getString(3));
                }
                rs.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
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
    }
}
