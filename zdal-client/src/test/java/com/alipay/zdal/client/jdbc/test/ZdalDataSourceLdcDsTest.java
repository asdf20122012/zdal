/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import com.alipay.zdal.client.jdbc.ZdalDataSource;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: XtsZdalDataSourceTest.java, v 0.1 2012-9-5 ÏÂÎç04:29:55 xiaoqing.zhouxq Exp $
 */
public class ZdalDataSourceLdcDsTest {

    //    @Test
    public void testTradeGz00Prefill() {
        String appName = "trade";
        String appDsName = "trade50";
        String dbmode = "dev";
        String zone = "gz00";
        String zdataconsoleUrl = "http://zdataconsole.stable.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource zdalDataSource = new ZdalDataSource();
        zdalDataSource.setAppName(appName);
        zdalDataSource.setAppDsName(appDsName);
        zdalDataSource.setDbmode(dbmode);
        zdalDataSource.setZone(zone);
        zdalDataSource.setZdataconsoleUrl(zdataconsoleUrl);
        zdalDataSource.setConfigPath(configPath);
        zdalDataSource.setZdalConfigLocal(true);
        zdalDataSource.setPrefill(true);
        zdalDataSource.init();
    }

    //    @Test
    public void testTradeGz00NotPrefill() {
        String appName = "trade";
        String appDsName = "trade50";
        String dbmode = "dev";
        String zone = "gz00";
        String zdataconsoleUrl = "http://zdataconsole.stable.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource zdalDataSource = new ZdalDataSource();
        zdalDataSource.setAppName(appName);
        zdalDataSource.setAppDsName(appDsName);
        zdalDataSource.setDbmode(dbmode);
        zdalDataSource.setZone(zone);
        zdalDataSource.setZdataconsoleUrl(zdataconsoleUrl);
        zdalDataSource.setConfigPath(configPath);
        zdalDataSource.setZdalConfigLocal(true);
        zdalDataSource.setPrefill(false);
        zdalDataSource.init();
    }

    //    @Test
    public void testTradeRz00Prefill() {
        String appName = "trade";
        String appDsName = "trade50";
        String dbmode = "dev";
        String zone = "rz00";
        String zdataconsoleUrl = "http://zdataconsole.stable.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource zdalDataSource = new ZdalDataSource();
        zdalDataSource.setAppName(appName);
        zdalDataSource.setAppDsName(appDsName);
        zdalDataSource.setDbmode(dbmode);
        zdalDataSource.setZone(zone);
        zdalDataSource.setZdataconsoleUrl(zdataconsoleUrl);
        zdalDataSource.setConfigPath(configPath);
        zdalDataSource.setZdalConfigLocal(true);
        zdalDataSource.setPrefill(true);
        zdalDataSource.init();
    }

    public static void main(String[] args) {

        String appName = "timeout";
        String appDsName = "trade50";
        String dbmode = "test";
        String zone = "rz00";
        String zdataconsoleUrl = "http://zdataconsole-pool.test.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource zdalDataSource = new ZdalDataSource();
        zdalDataSource.setAppName(appName);
        zdalDataSource.setAppDsName(appDsName);
        zdalDataSource.setDbmode(dbmode);
        zdalDataSource.setZone(zone);
        zdalDataSource.setZdataconsoleUrl(zdataconsoleUrl);
        zdalDataSource.setConfigPath(configPath);
        //        zdalDataSource.setZdalConfigLocal(true);
        zdalDataSource.setPrefill(true);
        zdalDataSource.setLdcDsDrm("app_trade_test");
        zdalDataSource.init();
    }

}
