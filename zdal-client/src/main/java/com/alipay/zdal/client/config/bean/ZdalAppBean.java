/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.bean;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.alibaba.common.lang.StringUtil;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ZdalAppBean implements InitializingBean {

    private String                  appName;

    private String                  dbmode;
    private String                  idcName;

    private List<AppDataSourceBean> appDataSourceList;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDbmode() {
        return dbmode;
    }

    public void setDbmode(String dbmode) {
        this.dbmode = dbmode;
    }

    public String getIdcName() {
        return idcName;
    }

    public void setIdcName(String idcName) {
        this.idcName = idcName;
    }

    public List<AppDataSourceBean> getAppDataSourceList() {
        return appDataSourceList;
    }

    public void setAppDataSourceList(List<AppDataSourceBean> appDataSourceList) {
        this.appDataSourceList = appDataSourceList;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(null != appName && !appName.equals(""));
        Assert.isTrue(!StringUtil.isEmpty(dbmode));
        Assert.isTrue(!StringUtil.isEmpty(dbmode));
    }

}
