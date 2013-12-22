/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.SQLException;

import com.alipay.zdal.client.jdbc.ZdalDataSource;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: ZdalDataSourceTest.java, v 0.1 2012-8-7 ÏÂÎç02:15:09 xiaoqing.zhouxq Exp $
 */
public class ZdalDataSourceWithShardRwTest {

    /**
     * 
     * @param args
     * @throws InterruptedException 
     * @throws SQLException 
     */
    public static void main(String[] args) throws InterruptedException, SQLException {
        ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName("zdal");
        dataSource.setAppDsName("test");
        dataSource.setDbmode("dev");
        dataSource.setZone("gzone");
        dataSource.setZdataconsoleUrl("http://zdataconsole.p9.alipay.net:8080");
        dataSource.setConfigPath("./config");
        dataSource.init();

        for (int i = 0; i < 1; i++) {
//            ShardRWThread writeSqlThread = new ShardRWThread(dataSource, "ShardRWThread-" + i);
//            writeSqlThread.start();
        }

    }
}
