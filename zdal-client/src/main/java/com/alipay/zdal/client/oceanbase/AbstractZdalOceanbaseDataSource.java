/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.oceanbase;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZdalConfigAdapter;
import com.alipay.zdal.client.config.ZdalDataSourceConfig;
import com.alipay.zdal.common.Closable;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: AbstractZdalOceanbaseDataSource.java, v 0.1 2013-2-19 ÏÂÎç05:01:58 Exp $
 */
public abstract class AbstractZdalOceanbaseDataSource extends ZdalDataSourceConfig implements
                                                                                  Closable,
                                                                                  DataSource {

    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDataSources(com.alipay.zdal.client.config.ZdalConfig)
     */
    public final void initDataSources(ZdalConfig zdalConfig) {
    }

    /** 
     * @see com.alipay.zdal.common.Closable#close()
     */
    public final void close() throws Throwable {
        ZdalConfigAdapter.close();
    }

    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDrmListener()
     */
    protected final void initDrmListener() {
    }

    /** 
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return null;
    }

    /** 
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    /** 
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    /** 
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    /** 
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    /** 
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    /** 
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    /** 
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

}
