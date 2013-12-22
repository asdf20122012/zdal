/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import com.alipay.zdal.client.tair.ZdalTairDataSource;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalTairDataSourceTest.java, v 0.1 2013-1-31 ÏÂÎç04:15:30 Exp $
 */
public class ZdalTairDataSourceTest {

    public static void main(String[] args) {
        String appName = "zdal1";
        String appDsName = "tair-test";
        String dbmode = "dev";
        String idcName = "gzone";
        String zdataconsoleUrl = "http://zdataconsole.p9.alipay.net:8080";
        String configPath = "./config";
        ZdalTairDataSource dataSource = new ZdalTairDataSource();
        dataSource.setAppName(appName);
        dataSource.setAppDsName(appDsName);
        dataSource.setDbmode(dbmode);
        dataSource.setZone(idcName);
        dataSource.setZdataconsoleUrl(zdataconsoleUrl);
        dataSource.setConfigPath(configPath);
        dataSource.init();
    }
}
