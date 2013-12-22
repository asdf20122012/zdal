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
 * @version $Id: GetAvailableGroupDBParamsTest.java,v 0.1 2013-4-1 下午01:35:16
 *          xiaoju.luo Exp $
 */
public class GetAvailableGroupDBParamsTest {
    private static Log                             logger           = LogFactory
                                                                        .getLog("GetAvailableGroupDBParamsTest.class");

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
    // 参数为false时，正常的用例
    public void testGetAvailableGroupDBParams() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042913");

        for (int i = 0; i < 100; i++) {
            String db = td.getAvailableGroupDB(parameters, false);
            int dbIndex = Integer.parseInt(db);
            boolean bl = td.isDataBaseAvailable(dbIndex, 1);
            List<Integer> avaDB = td.getAvailableDBIndexes(1);
            List<Integer> navaDB = td.getNotAvailableDBIndexes(1);
            // 增加判定生成的db在group 1中是有效的
            Assert.assertTrue(bl);
            // 判定生成的db不在组内不可用的序号中
            Assert.assertFalse(navaDB.contains(dbIndex));
            // 判定生成的db在组内可用的序号中
            Assert.assertTrue(avaDB.contains(dbIndex));

        }

    }

    @Test
    // 参数为true时，检查db状态正常的用例
    public void testGetAvailableGroupDBParams1() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_no", "2012042913");

        for (int i = 0; i < 100; i++) {
            String db = td.getAvailableGroupDB(parameters, true);
            int dbIndex = Integer.parseInt(db);
            boolean bl = td.isDataBaseAvailable(dbIndex, 1);
            List<Integer> avaDB = td.getAvailableDBIndexes(1);
            List<Integer> navaDB = td.getNotAvailableDBIndexes(1);
            // 增加判定生成的db在group 1中是有效的
            Assert.assertTrue(bl);
            // 判定生成的db不在组内不可用的序号中
            Assert.assertFalse(navaDB.contains(dbIndex));
            // 判定生成的db在组内可用的序号中
            Assert.assertTrue(avaDB.contains(dbIndex));

        }

    }

    @Test
    // 参数为false时，错误sharding字段异常的用例
    public void testGetAvailableGroupDBParamsException() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("card_noABC", "2012042913");
        for (int i = 0; i < 10; i++) {
            try {
                td.getAvailableGroupDB(parameters, true);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("请检查规则配置的字段"));
            } catch (Exception e) {
                Assert.fail("非预期的异常");
            }
        }

    }

    @After
    public void tearDown() {

    }
}