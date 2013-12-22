/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.tair;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalTairDataSourceTest.java, v 0.1 2013-2-19 ÉÏÎç11:45:39 Exp $
 */
public class ZdalTairDataSourceTest {

    private static final String APPNAME         = "zdal1";
    private static final String DBMODE          = "dev";
    private static final String ZONE            = "gzone";

    private static final String ZDATACONSOLEURL = "http://zdataconsole.p9.alipay.net:8080";
    private static final String CONFIGPATH      = "./config";

    private static final int    NAMESPACE       = 123;

    @Test
    public void test() {
        ZdalTairDataSource dataSource = new ZdalTairDataSource();
        dataSource.setAppName(APPNAME);
        dataSource.setAppDsName("tair-test");
        dataSource.setDbmode(DBMODE);
        dataSource.setZone(ZONE);
        dataSource.setZdataconsoleUrl(ZDATACONSOLEURL);
        dataSource.setConfigPath(CONFIGPATH);
        dataSource.init();

        String key = "zdal-tair-test";
        String value = "dksjdofuoiuoiweue";
        ResultCode resultCode = dataSource.put(NAMESPACE, key, value);
        Assert.assertEquals(resultCode.getCode(), ResultCode.SUCCESS.getCode());

        Result<DataEntry> result = dataSource.get(NAMESPACE, key);
        Assert.assertEquals(result.getValue().getValue().toString(), value);
    }
}
