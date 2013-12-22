/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.zdal.client.config.drm;

import java.util.Map;
import java.util.Set;

import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 * 
 */
public class ZdalDrmPushListener implements ZdalConfigListener {

    public ZdalDataSource zdalDataSource;

    public ZdalDrmPushListener(ZdalDataSource zdalDataSource) {
        this.zdalDataSource = zdalDataSource;
    }

    public void resetWeight(Map<String, String> keyWeights) {
        zdalDataSource.resetZdalDataSource(keyWeights);
    }

    public void resetZoneDs(Set<String> zoneDs) {
        zdalDataSource.resetZoneDs(zoneDs);
    }

    public void resetZoneDsThreadException(boolean throwException) {
        zdalDataSource.resetZoneDsThrowException(throwException);
    }

    @Override
    public void resetConnectionPoolSize(String physicalDbId, int poolMinSize, int poolMaxSize)
                                                                                              throws ScaleConnectionPoolException {
        zdalDataSource.resetConnectionPoolSize(physicalDbId, poolMinSize, poolMaxSize);
    }

    @Override
    public void resetDataSourceBinding(String physicalDbId, String logicalDbId,
                                       LocalTxDataSourceDO dataSourceSetting)
                                                                             throws DataSourceBindingChangeException {
        zdalDataSource.reBindingDataSource(physicalDbId, logicalDbId, dataSourceSetting);
    }
};
