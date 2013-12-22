/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.bean;

import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.rule.config.beans.AppRule;

/**
 * Mapping with Zdal Data Source configuration
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class AppDataSourceBean extends DataSourceBean {

    private String              appDataSourceName;
    private String              dataBaseType;
    private String              configType;
    private String              zoneError;
    private Set<String>         zoneDSSet;

    private Map<String, String> groupDataSourceRuleMap;

    private Map<String, String> failOverGroupRuleMap;

    private String              ruleBeanId;

    private AppRule             appRule;

    private boolean             diffMasterSlaveRule;

    /**
     * @return the appRule
     */
    public AppRule getAppRule() {
        return appRule;
    }

    /**
     * @param appRule the appRule to set
     */
    public void setAppRule(AppRule appRule) {
        this.appRule = appRule;
    }

    /**
     * @return the ruleBeanId
     */
    public String getRuleBeanId() {
        return ruleBeanId;
    }

    /**
     * @param ruleBeanId the ruleBeanId to set
     */
    public void setRuleBeanId(String ruleBeanId) {
        this.ruleBeanId = ruleBeanId;
    }

    public String getAppDataSourceName() {
        return appDataSourceName;
    }

    public void setAppDataSourceName(String appDataSourceName) {
        this.appDataSourceName = appDataSourceName;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getZoneError() {
        return zoneError;
    }

    public void setZoneError(String zoneError) {
        this.zoneError = zoneError;
    }

    public Set<String> getZoneDSSet() {
        return zoneDSSet;
    }

    public void setZoneDSSet(Set<String> zoneDSs) {
        this.zoneDSSet = zoneDSs;
    }

    /**
     * @return the groupDataSourceRuleMap
     */
    public Map<String, String> getGroupDataSourceRuleMap() {
        return groupDataSourceRuleMap;
    }

    /**
     * @param groupDataSourceRuleMap the groupDataSourceRuleMap to set
     */
    public void setGroupDataSourceRuleMap(Map<String, String> groupDataSourceRuleMap) {
        this.groupDataSourceRuleMap = groupDataSourceRuleMap;
    }

    /**
     * @return the failOverGroupRuleMap
     */
    public Map<String, String> getFailOverGroupRuleMap() {
        return failOverGroupRuleMap;
    }

    /**
     * @param failOverGroupRuleMap the failOverGroupRuleMap to set
     */
    public void setFailOverGroupRuleMap(Map<String, String> failOverGroupRuleMap) {
        this.failOverGroupRuleMap = failOverGroupRuleMap;
    }

    public boolean isDiffMasterSlaveRule() {
        return diffMasterSlaveRule;
    }

    public void setDiffMasterSlaveRule(boolean diffMasterSlaveRule) {
        this.diffMasterSlaveRule = diffMasterSlaveRule;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(!StringUtil.isEmpty(appDataSourceName));
        Assert.isTrue(!StringUtil.isEmpty(configType));
    }

}
