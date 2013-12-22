/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.bean;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public abstract class DataSourceBean implements InitializingBean {

    private String                      dataSourceName;

    private Set<PhysicalDataSourceBean> physicalDataSourceSet;

    /**
     * @return the physicalDataSourceSet
     */
    public Set<PhysicalDataSourceBean> getPhysicalDataSourceSet() {
        return physicalDataSourceSet;
    }

    /**
     * @param physicalDataSourceSet the physicalDataSourceSet to set
     */
    public void setPhysicalDataSourceSet(Set<PhysicalDataSourceBean> physicalDataSourceSet) {
        this.physicalDataSourceSet = physicalDataSourceSet;
    }

    /**
     * @return the dataSourceName
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param dataSourceName the dataSourceName to set
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

}
