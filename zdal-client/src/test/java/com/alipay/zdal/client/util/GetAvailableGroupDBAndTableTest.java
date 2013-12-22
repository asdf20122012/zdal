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
 * 测试根据逻辑表以及参数情况，返回组内的db id以及tb name
 * 
 * @author xiaoju.luo
 * @version $Id: DBAndTableTest.java,v 0.1 2013-4-1 上午04:01:39 xiaoju.luo Exp $
 */
public class GetAvailableGroupDBAndTableTest {

    private static Log                             logger           = LogFactory
                                                                        .getLog("GetAvailableGroupDBAndTableTest.class");
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
    String                                         groupNumber;

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
    // 正常的sharding字段,默认参数false，调用轮寻线程
    public void testGetAvailableGroupDBAndTable0() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042913");
        for (int i = 0; i < 100; i++) {
            String dbAndTable[] = td.getAvailableGroupDBAndTable(tableName, parameters, false);
            System.out
                .println("dbAndTable[0]=" + dbAndTable[0] + ";dbAndTable[1]=" + dbAndTable[1]);
            int dbNo = Integer.parseInt(dbAndTable[0]);
            // 验证根据sharding规则group1中生成db的有效性
            Assert.assertTrue(td.isDataBaseAvailable(dbNo, 1));
        }
    }

    @Test
    // 正常的sharding字段，参数为true，对db进行检查
    public void testGetAvailableGroupDBAndTable1() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042913");
        for (int i = 0; i < 10; i++) {
            String dbAndTable[] = td.getAvailableGroupDBAndTable(tableName, parameters, true);
            System.out
                .println("dbAndTable[0]=" + dbAndTable[0] + ";dbAndTable[1]=" + dbAndTable[1]);
            int dbNo = Integer.parseInt(dbAndTable[0]);
            // 验证根据sharding规则group1中生成db的有效性
            Assert.assertTrue(td.isDataBaseAvailable(dbNo, 1));
        }
    }

    @Test
    // 错误的sharding字段
    public void testGetAvailableGroupDBAndTableException0() {
        tableName = "card_no_month";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_noABC", "2012042913");
        for (int i = 0; i < 10; i++) {
            try {
                td.getAvailableGroupDBAndTable(tableName, parameters, false);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("请检查规则配置的字段"));
            } catch (Exception e) {
                Assert.fail();
            }

        }
    }

    @Test
    // 错误的虚拟表
    public void testGetAvailableGroupDBAndTableException1() {
        tableName = "card_month";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042913");
        for (int i = 0; i < 10; i++) {
            try {
                td.getAvailableGroupDBAndTable(tableName, parameters, false);
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
