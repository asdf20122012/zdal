/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.scalable;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;

/**
 * 定义支持数据源之间绑定关系改变的接口<p>
 * 由于数据源在支付宝的场景下，为了更好的支撑业务的快速Scale up，将数据源分为逻辑数据源和物理数据源从而支持
 * 当应用在数据集变大后，数据库扩容能更高效透明，逻辑数据源会多对一的方式绑定物理数据源。
 * 当数据扩容后，需要改变原有的绑定关系(原有的绑定的关系定义在应用对用的app-dbmode-zone-ds.xml)中。
 * @author xiang.yangx
 *
 */
public interface IDataSourceBindingChangeSupport {

    /**
     * 重新绑定逻辑数据源和物理数据源之间的关系
     * @param logicalDbName 逻辑数据源你的唯一名称，必须是定义在DS配置文件中的名称
     * @param phyicalDbName 物理数据源你的唯一名称，必须是定义在DS配置文件中的名称
     * @param phyicalDbParameters 物理数据源的配置参数，支持推送新的数据源
     * @throws DataSourceBindingChangeException
     */
    void reBindingDataSource(String phyicalDbName, String logicalDbName,
                             LocalTxDataSourceDO phyicalDbParameters)
                                                                     throws DataSourceBindingChangeException;
}
