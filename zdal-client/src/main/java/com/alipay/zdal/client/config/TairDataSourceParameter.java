/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

/**
 * Tair数据源的配置项.
 * @author 伯牙
 * @version $Id: TairDataSourceParameter.java, v 0.1 2013-1-13 上午10:55:34 Exp $
 */
public class TairDataSourceParameter {
    /** Tair的configserver的masterUrl */
    private String masterUrl;

    /** Tair的configServer的slaveUrl */
    private String slaveUrl;

    /** Tair的groupName */
    private String groupName;

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
    }

    public String getSlaveUrl() {
        return slaveUrl;
    }

    public void setSlaveUrl(String slaveUrl) {
        this.slaveUrl = slaveUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
