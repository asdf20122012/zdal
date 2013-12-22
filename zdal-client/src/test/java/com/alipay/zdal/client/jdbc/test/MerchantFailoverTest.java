/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import com.alipay.zdal.client.config.utils.ZdalV3Utils;
import com.alipay.zdal.client.jdbc.ZdalDataSource;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: MerchantFailoverTest.java, v 0.1 2013-6-24 ÉÏÎç08:57:23 Exp $
 */
public class MerchantFailoverTest {

    private static final String APP_DS_NAME = "merchantDataSource";
	private static final String APP_NAME = "merchant";
	private static final String CONFIG_PATH = "./config";
    private static final String CONFIG_V3_PATH = "./config/v3";

	public static void main(String[] args) throws Throwable {
		ZdalDataSource dataSourceV2 = null, dataSourceV3 = null;
    	try{
//	    	dataSourceV2 = createActivedZdalDataSourceV2();
//	        ZdalV3Utils.transformConfigurationFromV2ToV3(dataSourceV2, CONFIG_PATH, CONFIG_V3_PATH);
	        //        for (int i = 0; i < 1; i++) {
	        //            ShardThread writeSqlThread = new ShardThread(dataSource, "ShardThread-" + i);
	        //            writeSqlThread.start();
	        //        }
    		dataSourceV3 = createActivedZdalDataSourceV3();
    	}finally{
    		if( null != dataSourceV2) dataSourceV2.close();
    		if( null != dataSourceV3) dataSourceV3.close();
    	}
    }

	protected static ZdalDataSource createActivedZdalDataSourceV2() {
		String appName = APP_NAME;
        String appDsName = APP_DS_NAME;
        String dbmode = "dev";
        String idcName = "gz00";
        String zdataconsoleUrl = "http://zdataconsole.stable.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName(appName);
        dataSource.setAppDsName(appDsName);
        dataSource.setDbmode(dbmode);
        dataSource.setZone(idcName);
        dataSource.setZdataconsoleUrl(zdataconsoleUrl);
        dataSource.setConfigPath(configPath);
        dataSource.setDrm("app_merchant");
        dataSource.init();
        return dataSource;
	}
	
	protected static ZdalDataSource createActivedZdalDataSourceV3() {
		String appName = APP_NAME;
        String appDsName = APP_DS_NAME;
        String dbmode = "dev";
        String idcName = "gz00";
        String zdataconsoleUrl = "http://zdataconsolev3.stable.alipay.net:8080";
        String configPath = CONFIG_V3_PATH;
        ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setAppName(appName);
        dataSource.setAppDsName(appDsName);
        dataSource.setDbmode(dbmode);
        dataSource.setZone(idcName);
        dataSource.setZdataconsoleUrl(zdataconsoleUrl);
        dataSource.setConfigPath(configPath);
        dataSource.setDrm("app_merchant");
        dataSource.init();
        return dataSource;
	}
}
