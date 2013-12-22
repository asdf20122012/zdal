/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import junit.framework.Assert;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: XfireClientTest.java, v 0.1 2012-11-14 ÏÂÎç06:42:04 xiaoqing.zhouxq Exp $
 */
public class XfireClientTest {

    private static final String serverUrl  = "http://zdataconsole.stable.alipay.net:8080";
    private static final String appName    = "xts";
    private static final String appDsName  = "charge";
    private static final String dbmode     = "dev";
    private static final String idcName    = "gz00";
    private static final String configPath = "./config";

    public static void main(String[] args) {
        for (int j = 0; j < 1; j++) {
            final String threadName = "thread-" + j;
            Thread thead = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 1; i++) {
                        ZdalConfig config = null;
                        Assert.assertEquals(appName, config.getAppName());
                        Assert.assertEquals(appDsName, config.getAppDsName());
                        Assert.assertEquals(dbmode, config.getDbmode());
                        Assert.assertEquals(idcName, config.getIdcName());
                        Assert.assertEquals(ZoneError.EXCEPTOIN, config.getZoneError());

                    }
                }
            }, threadName);
            thead.start();
        }
    }
}
