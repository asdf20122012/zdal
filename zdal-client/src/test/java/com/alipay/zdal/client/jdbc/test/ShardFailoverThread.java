/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: WriteSqlThread.java, v 0.1 2012-8-23 ÏÂÎç01:11:59 xiaoqing.zhouxq Exp $
 */
public class ShardFailoverThread extends Thread {

    private static final Logger logger       = Logger.getLogger(ShardFailoverThread.class);

    private static final String WRITE_SQL    = "insert into test_failover(user_id,name,address) values(?,?,?)";
    private static final String DELETE_SQL   = "delete from  test_failover where user_id=?";

    public static final int     INSERT_COUNT = 4;

    private DataSource          dataSource;

    public ShardFailoverThread(DataSource dataSource, String name) {
        this.dataSource = dataSource;
        this.setName(name);
    }

    /** 
     * @see java.lang.Thread#run()
     */
    public void run() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            for (int i = 1; i <= INSERT_COUNT; i++) {
                ps = conn.prepareStatement(WRITE_SQL);
                ps.setInt(1, i);
                ps.setString(2, "name" + i);
                ps.setString(3, "address" + i);
                ps.execute();
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
