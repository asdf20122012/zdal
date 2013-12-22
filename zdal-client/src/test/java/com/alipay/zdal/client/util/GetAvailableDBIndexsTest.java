/**
 * 
 */
package com.alipay.zdal.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.util.dispatchanalyzer.ZdalDatasourceIntrospector;

/**
 * @author xiaoju.luo
 * @version $Id: GetAvailableDBIndexsTest.java,v 0.1 2013-4-1 上午10:41:13 xiaoju.luo Exp $
 */
public class GetAvailableDBIndexsTest {

    private static Log                             logger           = LogFactory
                                                                        .getLog("GetAvailableDBIndexsTest.class");

    private static final String                    APPNAME          = "allavailable";
    private static final String                    APPDSNAME        = "test";
    private static final String                    DBMODE           = "dev";
    private static final String                    IDCNAME          = "gzone";
    private static final String                    ZDATACONSOLE_URL = "http://zdataconsole.stable.alipay.net:8080";
    private static final String                    CONFIG_PATH      = "./config";

    private static final Map<String, String>       keyWeightConfig  = new HashMap<String, String>();
    static {
        keyWeightConfig.put("group_0", "master_0:10,master_2:10");
        keyWeightConfig.put("group_1", "master_1:10,master_3:10");
    }

    private static final Map<String, List<String>> shardingRules    = new HashMap<String, List<String>>();
    static {
        List<String> rules = new ArrayList<String>();
        rules.add("Integer.valueOf(#card_no#.substring(9,10),10)%2;");
        shardingRules.put("card_no_month", rules);
    }

    private static final Map<String, String>       tbNumForEachDb   = new HashMap<String, String>();
    static {
        tbNumForEachDb.put("card_no_month", "24");
    }

    private ZdalDataSource                         dataSource       = null;
    ZdalDatasourceIntrospector                        td;
    String                                         tableName;
    List<String>                                   tb               = new ArrayList<String>();
    String                                         db;

    @Before
    public void setUp() {
        dataSource = new ZdalDataSource();
        dataSource.setAppName(APPNAME);
        dataSource.setAppDsName(APPDSNAME);
        dataSource.setDbmode(DBMODE);
        dataSource.setZone(IDCNAME);
        dataSource.setZdataconsoleUrl(ZDATACONSOLE_URL);
        dataSource.setConfigPath(CONFIG_PATH);
        dataSource.setKeyWeightConfig(keyWeightConfig);
        dataSource.setShardingRules(shardingRules);
        dataSource.setTbNumForEachDb(tbNumForEachDb);
        dataSource.init();

        td = new ZdalDatasourceIntrospector();
        td.setTargetDataSource(dataSource);
        td.setCloseDBLimitNumber(0);
        td.init();

    }

    @Test
    //对序号为1的group进行接口调用，db均正常时
    public void testGetAllTableNames0() {
        List<Integer> index = td.getAvailableDBIndexes(1);
        Assert.assertEquals(2, index.size());
        Assert.assertTrue(index.iterator().hasNext());
    }

    @Test
    //对序号为1的group进行接口调用，有个db异常时
    public void testGetAllTableNames1() {
        List<Integer> index = td.getAvailableDBIndexes(0);
        Assert.assertEquals(2, index.size());
    }

    @Test
    //对一个不存在的group进行接口调用
    public void testGetAllTableNamesException() {
        try {
            td.getAvailableDBIndexes(2);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("The groupNum can't be more than 1"));
        } catch (Exception e) {
            Assert.fail("失败异常");
        }
    }

    @After
    public void tearDown() {
        //        try {
        //            dataSource.close();
        //        } catch (Throwable e) {
        //            logger.error("", e);
        //        }
    }
}
