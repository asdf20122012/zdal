/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.bean;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class PhysicalDataSourceBean implements InitializingBean {

    public static final String  FAILOVER_MASTER   = "master";

    public static final String  FAILOVER_FAILOVER = "failover";

    private String              name              = "";

    private Set<String>         logicDbNameSet;

    private String              jdbcUrl           = "";

    private String              userName          = "";

    private String              password          = "";

    /** 连接池中活动的最小连接数 */
    private int                 minConn;

    /** 连接池中活动的最大连接数 */

    private int                 maxConn;

    private String              driverClass       = "";

    private int                 blockingTimeoutMillis;

    private int                 idleTimeoutMinutes;

    private int                 preparedStatementCacheSize;

    private int                 queryTimeout;

    private int                 maxReadThreshold;

    private int                 maxWriteThreshold;

    private String              failoverRule      = FAILOVER_MASTER;

    private Map<String, String> connectionProperties;

    private boolean             prefill;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getLogicDbNameSet() {
        return logicDbNameSet;
    }

    public void setLogicDbNameSet(Set<String> logicDbNameSet) {
        this.logicDbNameSet = logicDbNameSet;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinConn() {
        return minConn;
    }

    public void setMinConn(int minConn) {
        this.minConn = minConn;
    }

    public int getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(int maxConn) {
        this.maxConn = maxConn;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public int getBlockingTimeoutMillis() {
        return blockingTimeoutMillis;
    }

    public void setBlockingTimeoutMillis(int blockingTimeoutMillis) {
        this.blockingTimeoutMillis = blockingTimeoutMillis;
    }

    public int getIdleTimeoutMinutes() {
        return idleTimeoutMinutes;
    }

    public void setIdleTimeoutMinutes(int idleTimeoutMinutes) {
        this.idleTimeoutMinutes = idleTimeoutMinutes;
    }

    public int getPreparedStatementCacheSize() {
        return preparedStatementCacheSize;
    }

    public void setPreparedStatementCacheSize(int preparedStatementCacheSize) {
        this.preparedStatementCacheSize = preparedStatementCacheSize;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getMaxReadThreshold() {
        return maxReadThreshold;
    }

    public void setMaxReadThreshold(int maxReadThreshold) {
        this.maxReadThreshold = maxReadThreshold;
    }

    public int getMaxWriteThreshold() {
        return maxWriteThreshold;
    }

    public void setMaxWriteThreshold(int maxWriteThreshold) {
        this.maxWriteThreshold = maxWriteThreshold;
    }

    public String getFailoverRule() {
        return failoverRule;
    }

    public void setFailoverRule(String failoverRule) {
        this.failoverRule = failoverRule;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(null != jdbcUrl && !"".equalsIgnoreCase(jdbcUrl));
    }

    /**
     * @return the connectionProperties
     */
    public Map<String, String> getConnectionProperties() {
        return connectionProperties;
    }

    /**
     * @param connectionProperties the connectionProperties to set
     */
    public void setConnectionProperties(Map<String, String> connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public boolean isPrefill() {
        return prefill;
    }

    public void setPrefill(boolean prefill) {
        this.prefill = prefill;
    }

}
