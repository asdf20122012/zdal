/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable;

import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.ZDataSourceFactory;
import com.alipay.zdal.datasource.resource.adapter.jdbc.local.LocalTxDataSource;
import com.alipay.zdal.datasource.scalable.impl.DataSourceBindingChangeException;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * Implemented DataSourceBindingChangeSupport to carry out split a data source into two data sources
 * <p> after the binding change has been changed.
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ZScalableDataSource extends ZDataSource implements DataSourceBindingChangeSupport,
                                                    ScalableConnectionPoolSupport {

    protected LocalTxDataSource deputyLocalTxDataSource = null;

    /**
     * <p>
     * If the binding between logical data source and physical data source still as same as startup.
     * Then mode still on the master mode which means application will get data source from the 
     * master JBoss LocalTxDataSource. Once the binding changes, we keep shift the mode from master
     * to deputy, deputy to master. If any one of them has no connection, it will be destroyed. 
     */
    protected AtomicBoolean     deputyMode              = new AtomicBoolean(false);

    protected AtomicBoolean     deputyDestroyed         = new AtomicBoolean(false);

    protected AtomicBoolean     masterDestroyed         = new AtomicBoolean(false);

    /**
     * Utilize a thread pool to destroy LocalTxDataSource in case of multiple threads to invoke destroy method 
     * concurrently. Besides, it avoid invoking thread being blocked. 
     */
    private ThreadPoolExecutor  threadPool              = new ThreadPoolExecutor(1, 1, 100,
                                                            TimeUnit.MILLISECONDS,
                                                            new LinkedBlockingQueue<Runnable>(1024));

    public ZScalableDataSource(LocalTxDataSourceDO dataSourceDO) throws Exception {
        super(dataSourceDO);
    }

    @Override
    protected DataSource getDatasource() throws SQLException {
        return getLocalTxDataSource().getDatasource();
    }

    public LocalTxDataSource getLocalTxDataSource() {
        if (!switching.get()) {
            //Choose a DataSource to return bases on current mode 
            if (deputyMode.get()) {
                if (isDataSourceNeedToDestory(localTxDataSource, masterDestroyed)) {
                    doDestroyDataSource(localTxDataSource, masterDestroyed);
                    if (masterDestroyed.get())
                        localTxDataSource = null;
                }
                return deputyLocalTxDataSource;
            } else {
                if (isDataSourceNeedToDestory(deputyLocalTxDataSource, deputyDestroyed)) {
                    doDestroyDataSource(deputyLocalTxDataSource, deputyDestroyed);
                    if (deputyDestroyed.get())
                        deputyLocalTxDataSource = null;
                }
                return localTxDataSource;
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e);
            }
            return getLocalTxDataSource();
        }
    }

    protected void doDestroyDataSource(final LocalTxDataSource dataSource,
                                       final AtomicBoolean isDestroyed) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isDestroyed.get()) {
                        return;
                    }
                    dataSource.destroy();
                    logger.warn("已经销毁数据源 ： " + dataSource.getBeanName());
                    isDestroyed.set(true);
                } catch (Exception e) {
                    logger.error("Zdal failed to destory a data source "
                                 + localTxDataSource.getZdataosource().getDsName(), e);
                }
            }
        });
    }

    /**
     * Check when the obsoleted data source is ready to destroy. Once none thread use the data source, we will destroy.
     * @param localTxDataSource
     * @return
     */
    protected boolean isDataSourceNeedToDestory(LocalTxDataSource dataSource,
                                                AtomicBoolean isDestroyed) {
        if (isDestroyed.get())
            return false; //If it has been destroyed by another thread, no need to do it again.
        if (null != dataSource) {
            return dataSource.getPoolCondition().getInUseConnectionCount() == 0;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.alipay.zdal.datasource.scalable.DataSourceBindingChangeSupport#onDataSourceBindingChanged(com.alipay.zdal.datasource.LocalTxDataSourceDO)
     */
    @Override
    public void onDataSourceBindingChanged(LocalTxDataSourceDO dataSourceConfigurationDO)
                                                                                         throws DataSourceBindingChangeException {
        //validate first
        validateConfiguration(dataSourceConfigurationDO);
        //Initialize the new pool
        this.dsName = dataSourceConfigurationDO.getDsName();
        try {
            if (deputyMode.get() && null == localTxDataSource) { //Deputy mode means the binding has been change before
                localTxDataSource = ZDataSourceFactory.createLocalTxDataSource(
                    dataSourceConfigurationDO, this);
                masterDestroyed.set(true);
            } else if (null == deputyLocalTxDataSource) {
                deputyLocalTxDataSource = ZDataSourceFactory.createLocalTxDataSource(
                    dataSourceConfigurationDO, this);
                deputyDestroyed.set(true);
            }
            //update mode
            deputyMode.set(!deputyMode.get());
        } catch (Exception e) {
            throw new DataSourceBindingChangeException(e, dataSourceConfigurationDO);
        }
    }

    private void validateConfiguration(LocalTxDataSourceDO dataSourceConfigurationDO)
                                                                                     throws DataSourceBindingChangeException {
        boolean isValid = true;
        isValid = !StringUtil.isEmpty(dataSourceConfigurationDO.getDsName())
                  && !StringUtil.isEmpty(dataSourceConfigurationDO.getConnectionURL())
                  && !StringUtil.isEmpty(dataSourceConfigurationDO.getUserName())
                  && (!StringUtil.isEmpty(dataSourceConfigurationDO.getPassWord()) || !StringUtil
                      .isEmpty(dataSourceConfigurationDO.getEncPassword()));
        if (!isValid) {
            throw new DataSourceBindingChangeException(new Exception(
                "Invalid dataSourceConfigurationDO"), dataSourceConfigurationDO);
        }
        if (deputyMode.get()) {
            if (null != localTxDataSource)
                throw new DataSourceBindingChangeException(
                    new Exception(
                        "They master LocalTxDatasource still alive, Zdal unable to initialize it currently."),
                    dataSourceConfigurationDO);
        } else {
            if (null != deputyLocalTxDataSource)
                throw new DataSourceBindingChangeException(
                    new Exception(
                        "They deputy LocalTxDatasource still alive, Zdal unable to initialize it currently."),
                    dataSourceConfigurationDO);
        }
    }

    @Override
    public void resetConnectionPoolSize(int poolMinSize, int poolMaxSize)
                                                                         throws ScaleConnectionPoolException {
        try {
            getLocalTxDataSource().resetConnectionPoolSize(poolMinSize, poolMaxSize);
        } catch (Exception e) {
            logger.error("Failed resetConnection pool size due to ", e);
        }
    }

    public void destroy() throws Exception {
        super.destroy();
        threadPool.shutdown();
    }
}
