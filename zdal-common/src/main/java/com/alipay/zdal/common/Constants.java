/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.common;

import java.io.File;

/**
 * 
 * @author 伯牙
 * @version $Id: Constants.java, v 0.1 2013-1-10 下午03:31:24 Exp $
 */
public interface Constants {
    /** config信息变更时，记录的log名称. */
    public static final String CONFIG_LOG_NAME_LOGNAME           = "zdal-client-config";

    /** 打印zdatasource连接池状态的log名称. */
    public static final String ZDAL_DATASOURCE_POOL_LOGNAME      = "zdal-datasource-pool";

    /** 打印zdal内部执行的状态log. */
    public static final String ZDAL_MONITOR_LOGNAME              = "zdal-monitor";

    /** zdal-datasource的drm资源dataId：com.alipay.zdal.signal.{标示} */
    public static final String ZDALDATASOURCE_DRM_DATAID         = "com.alipay.zdal.signal.{0}";

    /**zdal-document-datasource 的热点表动态迁移的drm资源dataId:com.alipay.zdal.hotspot.{标示}  */
    public static final String ZDALDATASOURCE_HOTSPOT_DRM_DATAID = "com.alipay.zdal.document.{0}";

    /** zdal-datasource ldc的drm资源dataId */
    public static final String ZDALDATASOURCE_LDC_DRM_DATAID     = "com.alipay.zdal.ldc.{0}";

    /** zdataconsole的webservice-url后缀. */
    public static final String WERBSERVICE_URL_SUFFIX            = "/services/zdalConfigService";

    /** 从zdataconsole获取配置的编码. */
    public static final String CONFIG_ENCODE                     = "gbk";

    /**  本地配置文件的 类型， DS OR　RULE*/
    public static final int LOCAL_CONFIG_DS      				 = 0;
    
    public static final int LOCAL_CONFIG_RULE      				 = 1;
    
    /**  本地配置文件的名称格式：appName-dbmode-zone-ds.xml*/
    public static final String LOCAL_CONFIG_FILENAME_SUFFIX      = "{0}-{1}-{2}-ds.xml";
    
    /**  本地配置文件的名称格式：appName-dbmode-zone-rule.xml*/
    public static final String LOCAL_RULE_CONFIG_FILENAME_SUFFIX = "{0}-{1}-{2}-rule.xml";

    /** 在sofa3中，数据源配置文件存放working地址. */
    public static final String WORKING_PATH_SUFFIX               = "config" + File.separator + "db";

    public static final String DBINDEX_DSKEY_CONN_CHAR           = "_";

    public static final String DBINDEX_DS_GROUP_KEY_PREFIX       = "group_";

    /** dbmode 的antx配置项. */
    public static final String DBMODE                            = "dbmode";

    /**zdataconsole的webservice的url,对应的antx配置项的名称是:zdataconsole.service.url  */
    public static final String ZDATACONSOLE_SERVICE_URL          = "zdataconsole_service_url";

}
