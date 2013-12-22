/**
 * 
 */
package com.alipay.zdal.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
 * 测试可用的db中返回所有tb的列表
 * 
 * @author xiaoju.luo
 * @version $Id: AllTableNamesTest.java,v 0.1 2012-5-22 上午05:40:54 xiaoju.luo
 *          Exp $
 */
public class GetAllTableNamesTest {
    private static Log                             logger           = LogFactory
                                                                        .getLog("GetAllTableNamesTest.class");
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
    //正常的接口调用
    public void testGetAllTableNames() {
        tableName = "card_no_month";
        for (int i = 0; i < 10; i++) {
            Map<String, List<String>> result = td.getAllTableNames(tableName);
            int dbCount = result.size();
            Assert.assertEquals(4, dbCount);
            Iterator iter = result.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String db = (String) entry.getKey();
                List<String> tb = (List) entry.getValue();
                int count = tb.size();
                Assert.assertEquals(24, count);
                logger.warn("db为：" + db + "可用table有" + count + "个" + "\n");
                Collections.sort(tb);
                for (String tbName : tb) {
                    logger.warn("db为" + db + "表名分别为" + tbName + "\n");
                }
            }
        }

    }

    @Test
    //虚拟表不合法的异常
    public void testGetAllTableNamesException() {
        tableName = "card_no_month00";
        for (int i = 0; i < 10; i++) {
            try {
                td.getAllTableNames(tableName);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("逻辑表名未找到"));
            } catch (Exception e) {
                Assert.fail("非预期的异常");
            }
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
