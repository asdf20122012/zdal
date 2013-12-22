/**
 * 
 */
package com.alipay.zdal.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.util.dispatchanalyzer.ZdalDatasourceIntrospector;

/**
 * @author xiaoju.luo
 * @version $Id: GetShardingResultByTableNameTest.java,v 0.1 2013-4-1 下午01:52:52 xiaoju.luo Exp $
 */
public class GetShardingResultByTableNameTest {
    private static Log                             logger           = LogFactory
                                                                        .getLog("GetShardingResultByTableNameTest.class");
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
    //预期为db_2,在group0中
    public void testGetShardingResultByTableName() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042914");
        for (int i = 0; i < 10; i++) {
            int groupNo = td.getShardingResultByTableName(tableName, parameters);
            Assert.assertEquals(0, groupNo);
        }

    }

    @Test
    //预期为db_3,在group1中
    public void testGetShardingResultByTableName1() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < 10; i++) {
            parameters.put("card_no", "2012042915");
            int groupNo = td.getShardingResultByTableName(tableName, parameters);
            Assert.assertEquals(1, groupNo);
        }
    }

    @Test
    //逻辑表错误的异常用例
    public void testGetShardingResultByTableNameException0() {
        tableName = "card_month";
        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < 10; i++) {
            parameters.put("card_no", "2012042915");
            try {
                td.getShardingResultByTableName(tableName, parameters);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains(
                    "The tableRule object can not be null,tableName=card_month"));
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    @Test
    //sharding字段错误的异常用例
    public void testGetShardingResultByTableNameException1() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < 10; i++) {
            parameters.put("card_no", "2012042915");
            try {
                td.getShardingResultByTableName(tableName, parameters);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains(
                    "The tableRule object can not be null,tableName=card_month"));
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    @After
    public void tearDown() {

    }
}
