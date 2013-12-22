/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.zdal.common.DBType;
import com.alipay.zdal.rule.config.beans.AppRule;

/**
 * Tdatasource/Zdatasource初始化时传入的参数,以及配置发生变更后，记录变化的规则和属性.
 * @author 伯牙
 * @version $Id: ZdalConfig.java, v 0.1 2012-11-17 下午4:07:01 Exp $
 */
public class ZdalConfig {
    private String                               appName;

    private String                               appDsName;

    private String                               dbmode;

    private String                               idcName;

    private ZoneError                            zoneError                   = ZoneError.EXCEPTOIN;

    /** 是否是document. */
    private boolean                              document;

    /** 如果是document，存放热点表的对应关系. */
    private Map<Integer, Integer>                hotspots                    = new HashMap<Integer, Integer>();

    /** zone对应的数据源:
     * 在shard+failover时候是master_00,failover_00,master_01,failover_01  ...
     * 在shard+rw时候是group_00,group_01.... */
    private Set<String>                          zoneDs                      = new HashSet<String>();

    private int                                  version;

    private DBType                               dbType                      = DBType.MYSQL;

    /** key=dsName;value=DataSourceParameter 第一次初始化时的所有物理数据源的配置项 */
    private Map<String, DataSourceParameter>     dataSourceParameters        = new ConcurrentHashMap<String, DataSourceParameter>();

    /**key=logicTableName ; value=TableRule  表的分库规则*/
    private Map<String, ShardTableRule>          shardTableRules             = new ConcurrentHashMap<String, ShardTableRule>();

    /** 主库逻辑数据源和物理数据源的对应关系:key=logicDsName,value=physicDsName */
    private Map<String, String>                  masterLogicPhysicsDsNames   = new ConcurrentHashMap<String, String>();

    /** failover库逻辑数据源和物理数据源的对应关系:key=logicDsName,value=physicDsName */
    private Map<String, String>                  failoverLogicPhysicsDsNames = new ConcurrentHashMap<String, String>();

    /** key=dsName;value=readwriteRule */
    private Map<String, String>                  readWriteRules              = new ConcurrentHashMap<String, String>();

    /** tair的配置信息. */
    private Map<String, TairDataSourceParameter> tairDataSourceParameters    = new HashMap<String, TairDataSourceParameter>();
    
    private AppRule appRootRule;
    
    private DataSourceConfigType dataSourceConfigType;

    public Map<String, DataSourceParameter> getDataSourceParameters() {
        return dataSourceParameters;
    }

    public void setDataSourceParameters(Map<String, DataSourceParameter> dataSources) {
        this.dataSourceParameters = dataSources;
    }

    public Map<String, ShardTableRule> getShardTableRules() {

        return shardTableRules;
    }

    public void setShardTableRules(Map<String, ShardTableRule> shardTableRules) {
        this.shardTableRules = shardTableRules;
    }

    public Map<String, String> getReadWriteRules() {
        return readWriteRules;
    }

    public void setReadWriteRules(Map<String, String> readWriteRules) {
        this.readWriteRules = readWriteRules;
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
    }

    public String getAppDsName() {
        return appDsName;
    }

    public void setAppDsName(String appDsName) {
        this.appDsName = appDsName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ZoneError getZoneError() {
        return zoneError;
    }

    public void setZoneError(ZoneError zoneError) {
        this.zoneError = zoneError;
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

    public Map<String, TairDataSourceParameter> getTairDataSourceParameters() {
        return tairDataSourceParameters;
    }

    public void setTairDataSourceParameters(
                                            Map<String, TairDataSourceParameter> tairDataSourceParameters) {
        this.tairDataSourceParameters = tairDataSourceParameters;
    }

    public Map<String, String> getMasterLogicPhysicsDsNames() {
        return masterLogicPhysicsDsNames;
    }

    public void setMasterLogicPhysicsDsNames(Map<String, String> logicPhysicsDsNames) {
        this.masterLogicPhysicsDsNames = logicPhysicsDsNames;
    }

    public Map<String, String> getFailoverLogicPhysicsDsNames() {
        return failoverLogicPhysicsDsNames;
    }

    public void setFailoverLogicPhysicsDsNames(Map<String, String> failoverLogicPhysicsDsNames) {
        this.failoverLogicPhysicsDsNames = failoverLogicPhysicsDsNames;
    }

    public Set<String> getZoneDs() {
        return zoneDs;
    }

    public void setZoneDs(Set<String> zoneDs) {
        this.zoneDs = zoneDs;
    }

    public boolean isDocument() {
        return document;
    }

    public void setDocument(boolean document) {
        this.document = document;
    }

    public Map<Integer, Integer> getHotspots() {
        return hotspots;
    }

    public void setHotspots(Map<Integer, Integer> hotspots) {
        this.hotspots = hotspots;
    }

	public AppRule getAppRootRule() {
		return appRootRule;
	}

	public void setAppRootRule(AppRule appRootRule) {
		this.appRootRule = appRootRule;
	}

	public DataSourceConfigType getDataSourceConfigType() {
		return dataSourceConfigType;
	}

	public void setDataSourceConfigType(DataSourceConfigType dataSourceConfigType) {
		this.dataSourceConfigType = dataSourceConfigType;
	}

}
