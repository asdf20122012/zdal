package com.alipay.zdal.test.ut.client;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.utils.DbmodeUtils;

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
