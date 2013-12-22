/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.config.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZoneError;
import com.alipay.zdal.common.DBType;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: ParseXmlConfigUtilsTest.java, v 0.1 2012-8-21 ÉÏÎç09:41:59 xiaoqing.zhouxq Exp $
 */
public class ParseXmlConfigUtilsTest {
    private static final String CONFIG_FILE = "./config/zdal-test-dev-gzone-ds.xml";
    private static final String APPNAME     = "zdal-test";
    private static final String DBMODE      = "dev";
    private static final String IDCNAME     = "gzone";

    @Test
    public void testParseConfigOfShard() throws Exception {
        File configFile = new File(CONFIG_FILE);
        String appDsName = "zdal-ds-1";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            Map<String, ZdalConfig> zdalConfigs = ZdalConfigParserUtils.parseConfig(inputStream,
                APPNAME, DBMODE, IDCNAME);
            for (ZdalConfig zdalConfig : zdalConfigs.values()) {
                Assert.assertEquals(APPNAME, zdalConfig.getAppName());
                Assert.assertEquals(appDsName, zdalConfig.getAppDsName());
                Assert.assertEquals(DBMODE, zdalConfig.getDbmode());
                Assert.assertEquals(IDCNAME, zdalConfig.getIdcName());
                Assert.assertEquals(ZoneError.EXCEPTOIN, zdalConfig.getZoneError());
                Assert.assertEquals(1, zdalConfig.getVersion());
                Assert.assertEquals(DBType.MYSQL, zdalConfig.getDbType());
//                Assert.assertEquals(DataSourceType.SHARD, zdalConfig.getDataSourceType());
                Assert.assertEquals(2, zdalConfig.getShardTableRules().size());
                Assert.assertEquals(4, zdalConfig.getDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getTairDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getReadWriteRules().size());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testParseConfigOfShardRW() throws Exception {
        File configFile = new File(CONFIG_FILE);
        String appDsName = "zdal-ds-2";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            Map<String, ZdalConfig> zdalConfigs = ZdalConfigParserUtils.parseConfig(inputStream,
                APPNAME, DBMODE, IDCNAME);
            for (ZdalConfig zdalConfig : zdalConfigs.values()) {
                Assert.assertEquals(APPNAME, zdalConfig.getAppName());
                Assert.assertEquals(appDsName, zdalConfig.getAppDsName());
                Assert.assertEquals(DBMODE, zdalConfig.getDbmode());
                Assert.assertEquals(IDCNAME, zdalConfig.getIdcName());
                Assert.assertEquals(ZoneError.EXCEPTOIN, zdalConfig.getZoneError());
                Assert.assertEquals(1, zdalConfig.getVersion());
                Assert.assertEquals(DBType.MYSQL, zdalConfig.getDbType());
//                Assert.assertEquals(DataSourceType.SHARD, zdalConfig.getDataSourceType());
                Assert.assertEquals(2, zdalConfig.getShardTableRules().size());
                Assert.assertEquals(4, zdalConfig.getDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getTairDataSourceParameters().size());
                Assert.assertEquals(2, zdalConfig.getReadWriteRules().size());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testParseConfigShardFailover() throws Exception {
        File configFile = new File(CONFIG_FILE);
        String appDsName = "zdal-ds-3";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            Map<String, ZdalConfig> zdalConfigs = ZdalConfigParserUtils.parseConfig(inputStream,
                APPNAME, DBMODE, IDCNAME);
            for (ZdalConfig zdalConfig : zdalConfigs.values()) {
                Assert.assertEquals(APPNAME, zdalConfig.getAppName());
                Assert.assertEquals(appDsName, zdalConfig.getAppDsName());
                Assert.assertEquals(DBMODE, zdalConfig.getDbmode());
                Assert.assertEquals(IDCNAME, zdalConfig.getIdcName());
                Assert.assertEquals(ZoneError.EXCEPTOIN, zdalConfig.getZoneError());
                Assert.assertEquals(1, zdalConfig.getVersion());
                Assert.assertEquals(DBType.MYSQL, zdalConfig.getDbType());
//                Assert.assertEquals(DataSourceType.SHARD, zdalConfig.getDataSourceType());
                Assert.assertEquals(2, zdalConfig.getShardTableRules().size());
                Assert.assertEquals(4, zdalConfig.getDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getTairDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getReadWriteRules().size());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testParseConfigRW() throws Exception {
        File configFile = new File(CONFIG_FILE);
        String appDsName = "zdal-ds-4";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            Map<String, ZdalConfig> zdalConfigs = ZdalConfigParserUtils.parseConfig(inputStream,
                APPNAME, DBMODE, IDCNAME);
            for (ZdalConfig zdalConfig : zdalConfigs.values()) {
                Assert.assertEquals(APPNAME, zdalConfig.getAppName());
                Assert.assertEquals(appDsName, zdalConfig.getAppDsName());
                Assert.assertEquals(DBMODE, zdalConfig.getDbmode());
                Assert.assertEquals(IDCNAME, zdalConfig.getIdcName());
                Assert.assertEquals(ZoneError.EXCEPTOIN, zdalConfig.getZoneError());
                Assert.assertEquals(1, zdalConfig.getVersion());
                Assert.assertEquals(DBType.MYSQL, zdalConfig.getDbType());
//                Assert.assertEquals(DataSourceType.RW, zdalConfig.getDataSourceType());
                Assert.assertEquals(0, zdalConfig.getShardTableRules().size());
                Assert.assertEquals(2, zdalConfig.getDataSourceParameters().size());
                Assert.assertEquals(0, zdalConfig.getTairDataSourceParameters().size());
                Assert.assertEquals(1, zdalConfig.getReadWriteRules().size());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File configFile = new File("./config/xts-dev-gzone-ds.xml");
        String appName = "xts";
        String dbmode = "dev";
        String idcName = "gzone";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            Map<String, ZdalConfig> zdalConfigs = ZdalConfigParserUtils.parseConfig(inputStream,
                appName, dbmode, idcName);
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
