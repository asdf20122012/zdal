/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.utils.ZoneUtils;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZoneUtilsTest.java, v 0.1 2013-2-5 ÏÂÎç01:54:24 Exp $
 */
public class ZoneUtilsTest {

    @Test
    public void test() {
        String zone = "gz00a";
        System.setProperty(ZoneUtils.ZONE_NAME, zone);
        Assert.assertEquals("gz00a", ZoneUtils.getZone(zone));

        zone = "";
        System.setProperty(ZoneUtils.ZONE_NAME, "rz01a");
        Assert.assertEquals("rz01", ZoneUtils.getZone(zone));

        zone = "";
        System.setProperty(ZoneUtils.ZONE_NAME, "gz99p");
        Assert.assertEquals("gz00", ZoneUtils.getZone(zone));

        zone = null;
        System.clearProperty(ZoneUtils.ZONE_NAME);
        Assert.assertEquals(ZoneUtils.DEFAULT_ZONE_VALUE, ZoneUtils.getZone(zone));
    }
}
