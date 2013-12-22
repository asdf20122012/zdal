/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import java.util.Map;
import java.util.Set;

import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * Zdal client启动时，需要初始化DataSource,根据DRM的版本信号从zdataconsole中获取app的数据源配置信息，并且初始化DataSource<br>
 * 在app运行时，如果配置信息发生变化，需要触发zdal client重新重zdataconsole中获取配置信息，并且比对各个属性，动态调整数据源的行为.
 * @author 伯牙
 * @version $Id: ZdalConfigListener.java, v 0.1 2012-11-17 下午4:29:22 Exp $
 */
public interface ZdalConfigListener {

    /**
     * 通过drm推送切换信息.
     * @param keyWeights 推送的值.
     */
    void resetWeight(Map<String, String> keyWeights);

    /**
     * 通过drm推送动态调整本众能访问的逻辑数据源.
     * @param zoneDs
     */
    void resetZoneDs(Set<String> zoneDs);

    /**
     * 通过drm推送路由到非本zone的数据源抛出异常还是记录日志，如果throwException=true 就是抛出异常，如果throwException=false就是记录日志.
     * @param throwException
     */
    void resetZoneDsThreadException(boolean throwException);

    /**
     * Dealing with DRM pushed operation of reset data source bindings between logical data source
     * and physical datasource
     * @throws DataSourceBindingChangeException
     */
    void resetDataSourceBinding(String physicalDbId, String logicalDbId, LocalTxDataSourceDO dataSourceSetting) 
    		throws DataSourceBindingChangeException;
    
    /**
     * Dealing with DRM pushed operation of reset data source connection pool size
     * @throws ScaleConnectionPoolException
     */
    void resetConnectionPoolSize(String physicalDbId, int minSize, int maxSize) throws ScaleConnectionPoolException;
}
