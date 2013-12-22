/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: DbInfo.java, v 0.1 2012-5-15 ÉÏÎç10:06:18 xiaoqing.zhouxq Exp $
 */
public class DbInfo {
    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    private String dbName;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}
