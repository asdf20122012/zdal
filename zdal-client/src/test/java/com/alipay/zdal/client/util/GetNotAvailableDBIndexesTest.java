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
 * @version $Id: GetNotAvailableDBIndexesTest.java,v 0.1 2013-4-1 下午02:56:53 xiaoju.luo Exp $
 */
public class GetNotAvailableDBIndexesTest {
    private static Log                             logger           = LogFactory
                                                                        .getLog("GetNotAvailableDBIndexesTest.class");

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
    //db正常下，
    public void testGetNotAvailableDBIndexes() {
        for (int i = 0; i < 1000; i++) {
            List<Integer> list = td.getNotAvailableDBIndexes(0);
            Assert.assertEquals(0, list.size());
        }
    }

    @Test
    //当db的server1停掉后的异常用例，需要 debug跟踪且断库来看
    public void testGetNotAvailableDBIndexesException() {
        for (int i = 0; i < 10; i++) {
            List<Integer> list = td.getNotAvailableDBIndexes(0);
            logger.info("满足预期为2：" + list.size());
            //Assert.assertEquals(2, list.size());
        }
    }

    @After
    public void tearDown() {

    }
}
