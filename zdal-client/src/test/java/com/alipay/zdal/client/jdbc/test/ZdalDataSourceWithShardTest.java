/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map.Entry;

import com.alipay.zdal.client.config.utils.ZdalV3Utils;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.datasource.ZDataSource;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: ZdalDataSourceTest.java, v 0.1 2012-8-7 ÏÂÎç02:15:09 xiaoqing.zhouxq Exp $
 */
public class ZdalDataSourceWithShardTest {

    private static final String CONFIG_PATH = "./config";
    private static final String CONFIG_V3_PATH = "./config/v3";

	/**
     * 
     * @param args
	 * @throws Throwable 
     */
    public static void main(String[] args) throws Throwable {
    	ZdalDataSource dataSourceV2 = null, dataSourceV3 = null;
    	try{
	    	dataSourceV2 = initZdalV2();
	        ZdalV3Utils.transformConfigurationFromV2ToV3(dataSourceV2, CONFIG_PATH, CONFIG_V3_PATH);
	        //        for (int i = 0; i < 1; i++) {
	        //            ShardThread writeSqlThread = new ShardThread(dataSource, "ShardThread-" + i);
	        //            writeSqlThread.start();
	        //        }
        	initZdalV3();
    	}finally{
    		if( null != dataSourceV2) dataSourceV2.close();
    		if( null != dataSourceV3) dataSourceV3.close();
    	}
    }

	protected static ZdalDataSource initZdalV2() {
		ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName("zdal");
        dataSource.setAppDsName("test1");
        dataSource.setDbmode("dev");
        dataSource.setZone("gzone");
        dataSource.setZdataconsoleUrl("http://zdataconsole.p9.alipay.net:8080");
        dataSource.setConfigPath(CONFIG_PATH);
        dataSource.init();
        return dataSource;
	}

	protected static ZdalDataSource initZdalV3() {
		ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName("zdal");
        dataSource.setAppDsName("test1");
        dataSource.setDbmode("dev");
        dataSource.setZone("gzone");
        dataSource.setZdataconsoleUrl("http://zdataconsolev3.p9.alipay.net:8080");
        dataSource.setConfigPath("./config/v3");
        dataSource.initV3();
        for(Entry<String, ZDataSource> dataSourceEntry : dataSource.getDataSourcesMap().entrySet()){
        	System.out.println(dataSourceEntry.getValue().getLocalTxDataSource().getPoolCondition());
        }
        return dataSource;
	}
}
