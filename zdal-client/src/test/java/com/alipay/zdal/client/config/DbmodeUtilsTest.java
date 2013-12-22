/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.utils.DbmodeUtils;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: DbmodeUtilsTest.java, v 0.1 2013-2-5 ÏÂÎç01:55:48 Exp $
 */
public class DbmodeUtilsTest {

    @Test
    public void test() {
        String dbmode = null;
        System.setProperty(DbmodeUtils.DBMODE_NAME, "test");
        Assert.assertEquals("test", DbmodeUtils.getDbmode(dbmode));

        dbmode = "";
        System.setProperty(DbmodeUtils.DBMODE_NAME, "test");
        Assert.assertEquals("test", DbmodeUtils.getDbmode(dbmode));

        dbmode = "test";
        System.clearProperty(DbmodeUtils.DBMODE_NAME);
        Assert.assertEquals("test", DbmodeUtils.getDbmode(dbmode));

        dbmode = null;
        System.clearProperty(DbmodeUtils.DBMODE_NAME);
        try {
            String tmpDbmode = DbmodeUtils.getDbmode(dbmode);
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

}
