/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.alipay.sofa.service.api.drm.DRMClient;
import com.alipay.zdal.client.config.DataSourceConfigType;
import com.alipay.zdal.client.config.DataSourceParameter;
import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.client.config.ZdalDataSourceConfig;
import com.alipay.zdal.client.config.ZoneError;
import com.alipay.zdal.client.config.drm.ZdalLdcSignalResource;
import com.alipay.zdal.client.config.drm.ZdalSignalResource;
import com.alipay.zdal.client.controller.SpringBasedDispatcherImpl;
import com.alipay.zdal.client.datasource.keyweight.GetDataSourceSequenceRules;
import com.alipay.zdal.client.datasource.keyweight.ZdalDataSourceKeyWeightRandom;
import com.alipay.zdal.client.datasource.keyweight.ZdalDataSourceKeyWeightRumtime;
import com.alipay.zdal.client.dispatcher.SqlDispatcher;
import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.client.jdbc.dbselector.EquityDbManager;
import com.alipay.zdal.client.jdbc.dbselector.OneDBSelector;
import com.alipay.zdal.client.jdbc.dbselector.PriorityDbGroupSelector;
import com.alipay.zdal.client.util.PreFillConnection;
import com.alipay.zdal.client.util.TableSuffixGenerator;
import com.alipay.zdal.common.Closable;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.FailoverDBRuleKey;
import com.alipay.zdal.common.Monitor;
import com.alipay.zdal.common.RuntimeConfigHolder;
import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.OracleExceptionSorter;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.common.util.TableSuffixTypeEnum;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.parser.DefaultSQLParser;
import com.alipay.zdal.parser.SQLParser;
import com.alipay.zdal.rule.bean.GroovyTableDatabaseMapProvider;
import com.alipay.zdal.rule.bean.LogicTable;
import com.alipay.zdal.rule.bean.SimpleLogicTable;
import com.alipay.zdal.rule.bean.SimpleTableDatabaseMapProvider;
import com.alipay.zdal.rule.bean.SimpleTableMapProvider;
import com.alipay.zdal.rule.bean.SimpleTableTwoColumnsMapProvider;
import com.alipay.zdal.rule.bean.ZdalRoot;
import com.alipay.zdal.rule.config.beans.AppRule;
import com.alipay.zdal.rule.config.beans.ShardRule;
import com.alipay.zdal.rule.config.beans.Suffix;
import com.alipay.zdal.rule.config.beans.SuffixManager;
import com.alipay.zdal.rule.config.beans.TableRule;
import com.alipay.zdal.rule.config.beans.TableRule.ParseException;
import com.alipay.zdal.rule.ruleengine.entities.abstractentities.ListSharedElement.DEFAULT_LIST_RESULT_STRAGETY;

/**
 * 
 * @author 伯牙
 * @version $Id: AbstractZdalDataSource.java, v 0.1 2013-1-30 上午09:56:01 Exp $
 */
public abstract class AbstractZdalDataSource extends ZdalDataSourceConfig implements DataSource,
                                                                         Closable {

    /** rwRule,failoverRule推送的标示. */
    private String                                     drm;

    /** ldc本zone能访问的逻辑数据源 drm推送标示. */
    private String                                     ldcDsDrm;

    /**数据库driver所支持的配置参数.  */
    //TODO
    private Map<String, String>                        connectionProperties          = new HashMap<String, String>();

    /** 支持金融交换全活策略，每张表对应的一个规则. */
    private Map<String, List<String>>                  shardingRules                 = new HashMap<String, List<String>>();

    /** 全活策略，需要设置每张表在每个库里面有多少张表. */
    //TODO
    private Map<String, String>                        tbNumForEachDb                = new HashMap<String, String>();

    /** 读写的分库分表规则是否分开. */
    private boolean                                    diffMasterSlaveRule           = false;

    /** prodtrans表后缀. */
    private Map<String, String>                        tbSuffixPadding               = new HashMap<String, String>();

    /** 是否在初始化连接池的时候,创建最小连接数,默认false. */
    private boolean                                    prefill                       = false;

    /** 预热标示，开始预发需要置为true，预热完成置为false. */
    private boolean                                    prefillZdal                   = false;

    /**  预热时候，根据zoneDs里面的来判断.*/
    private Set<String>                                prefillZoneDs                 = new HashSet<String>();

    private RuntimeConfigHolder<ZdalRuntime>           runtimeConfigHolder           = new RuntimeConfigHolder<ZdalRuntime>();
    private SqlDispatcher                              writeDispatcher;
    private SqlDispatcher                              readDispatcher;
    private Map<String, ? extends Object>              dataSourcePoolConfig;
    /** rwRule */
    private Map<String, ? extends Object>              rwDataSourcePoolConfig;
    /**  全活策略需要根据这个map进行判断，进行可用库的选择.*/
    //TODO
    private Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapConfig;
    /** failoverRule/全活策略设置group. */
    private Map<String, String>                        keyWeightConfig;
    private int                                        timeoutThreshold              = 100;
    private int                                        retryingTimes                 = 4;

    private AppRule                                    appRule;

    /**
     * 数据源集合，key是数据源标识，value 是DataSource对象，目前采用的是 ZdataSource
     */
    protected Map<String, ZDataSource>                 dataSourcesMap                = new HashMap<String, ZDataSource>();

    private ZdalSignalResource                         drmResource                   = null;

    private ZdalLdcSignalResource                      ldcSignalResource             = null;

    /**
     * 权重码以rwp分别代表读权重、写权重、读级别3项。不区分大小和顺序，后面可以跟一个数字。
     * 若字母不出现，对应项的值默认为0；若字母出现数字不出现，对应项的默认值见return说明
     * @param weight 格式 
     * @return int[0] R后面的数字(默认10), int[1] W后面的数字(默认10), int[2] P后面的数字(默认0); 
     * R20W10 --> int[]{20,10,0}
     * rp2w30 --> int[]{10,30,2}
     */
    private static final Pattern                       weightPattern_r               = Pattern
                                                                                         .compile("[Rr](\\d*)");
    private static final Pattern                       weightPattern_w               = Pattern
                                                                                         .compile("[Ww](\\d*)");
    private static final Pattern                       weightPattern_p               = Pattern
                                                                                         .compile("[Pp](\\d*)");
    private static final Pattern                       weightPattern_q               = Pattern
                                                                                         .compile("[Qq](\\d*)");

    private ZdalConfigListener zdalConfigListener = null;
    
    private static enum WeightRWPQEnum {
        rWeight(0), wWeight(1), readPriority(2), writePriority(3);
        private Integer value;

        public Integer value() {
            return value;
        }

        WeightRWPQEnum(Integer value) {
            this.value = value;
        }
    }

    /**
     * 开始预热连接和zdal上下文的groovy.
     */
    protected void startPrefillZdal() {
        prefillZdal = true;
    }

    /**
     * 预热完成以后，需要调用一下.
     */
    protected void endPrefillZdal() {
        prefillZdal = false;
    }

    /**
     * 销毁数据源.
     */
    /** 
     * @see com.alipay.zdal.common.Closable#close()
     */
    public final void close() throws Throwable {
        if (!this.dataSourcesMap.isEmpty()) {
            for (DataSource dataSource : this.dataSourcesMap.values()) {
                try {
                    ((ZDataSource) dataSource).destroy();
                } catch (Throwable e) {
                    CONFIG_LOGGER.error("##Error, ZdalDataSource tried to close datasource occured unexpected exception.",e);
                }
            }
            this.dataSourcesMap.clear();
        }
        if (drmResource != null) {
            DRMClient.getInstance().unregister(drmResource.getResourceId());
        }
        if (ldcSignalResource != null) {
            DRMClient.getInstance().unregister(ldcSignalResource.getResourceId());
        }
    }

    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDataSources(com.alipay.zdal.client.config.ZdalConfig)
     */
    protected final void initDataSources(ZdalConfig zdalConfig) {
        if (zdalConfig.getDataSourceParameters() == null
            || zdalConfig.getDataSourceParameters().isEmpty()) {
            throw new ZdalClientException("ERROR ## the datasource parameter is empty");
        }
        boolean isPrefillByZone = false;
        if( null != zdalConfig.getZoneDs() && !zdalConfig.getZoneDs().isEmpty() 
                && null != getPreFillDataSources(zdalConfig.getZoneDs()) ){
            isPrefillByZone = true;
        }
        for (Entry<String, DataSourceParameter> entry : zdalConfig.getDataSourceParameters()
            .entrySet()) {
            try {
                ZDataSource zDataSource = new ZScalableDataSource(createDataSourceDO(entry.getValue(),
                    zdalConfig.getDbType(), appDsName + "." + entry.getKey(), isPrefillByZone));
                this.dataSourcesMap.put(entry.getKey(), zDataSource);
            } catch (Exception e) {
                throw new ZdalClientException("ERROR ## create dsName = " + entry.getKey()
                                              + " dataSource failured", e);
            }
        }
        this.prefillZoneDs.addAll(zdalConfig.getZoneDs());
        prefillDataSource(zdalConfig.getZoneDs());//预热最小连接数.
        if (dbConfigType == DataSourceConfigType.SHARD_FAILOVER) {
            this.dataSourcePoolConfig = getFailoverDataSourcePoolConfig(zdalConfig
                .getMasterLogicPhysicsDsNames(), zdalConfig.getFailoverLogicPhysicsDsNames());
            if (this.keyWeightConfig == null || this.keyWeightConfig.isEmpty()) {
                this.keyWeightConfig = this.getFailoverRules(zdalConfig
                    .getMasterLogicPhysicsDsNames(), zdalConfig.getFailoverLogicPhysicsDsNames());
            } else {//如果是通过ZdalDataSource的keyWeightConfig参数设置的，就是全活策略.
                CONFIG_LOGGER
                    .warn("WARN ## this shard_failover is allAvailable mode,the appDsName = "
                          + appDsName);
            }
            appRule = zdalConfig.getAppRootRule();
            initForAppRule(appRule);
            CONFIG_LOGGER.warn("WARN ## the shardFailoverWeight of " + appDsName + " is :"
                               + getReceivDataStr(keyWeightConfig));
        } else if (dbConfigType == DataSourceConfigType.SHARD_GROUP) {
            this.rwDataSourcePoolConfig = zdalConfig.getReadWriteRules();
            appRule = zdalConfig.getAppRootRule();
            initForAppRule(appRule);
            CONFIG_LOGGER.warn("WARN ## the shardRWWeight of " + appDsName + " is :"
                               + getReceivDataStr(zdalConfig.getReadWriteRules()));
        } else if (dbConfigType == DataSourceConfigType.GROUP) {
            this.rwDataSourcePoolConfig = zdalConfig.getReadWriteRules();
            this.initForLoadBalance(zdalConfig.getDbType());
            CONFIG_LOGGER.warn("WARN ## the RWWeight of " + appDsName + " is :"
                               + getReceivDataStr(zdalConfig.getReadWriteRules()));
        } 
    }
    
    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDrmListener()
     */
    public void initDrmListener() {
        if (StringUtil.isNotBlank(drm)) {
            drmResource = new ZdalSignalResource(zdalConfigListener, drm, defaultDbType);
            try {
                DRMClient.getInstance().register(drmResource, drmResource.getResourceId());
                CONFIG_LOGGER.warn("WARN ## register failover-drm success,the dataId = "
                                   + drmResource.getResourceId() + " the appName = " + appName
                                   + ",the appDsName = " + appDsName);
            } catch (Exception e) {
                CONFIG_LOGGER.warn("WARN ## register the failover-drm of dataId = "
                                   + drmResource.getResourceId() + " failured,the appName = "
                                   + appName + ",the appDsName = " + appDsName, e);
            }
        }
        if (StringUtil.isNotBlank(ldcDsDrm)) {
            ldcSignalResource = new ZdalLdcSignalResource(zdalConfigListener, ldcDsDrm);
            try {
                DRMClient.getInstance().register(ldcSignalResource,
                    ldcSignalResource.getResourceId());
                CONFIG_LOGGER.warn("WARN ## register the ldc-drm success,the dataId = "
                                   + ldcSignalResource.getResourceId());
            } catch (Exception e) {
                CONFIG_LOGGER.warn("WARN ## register the ldc-drm failured ,the dataId = "
                                   + ldcSignalResource.getResourceId());
            }
        }
    }

    private void initForLoadBalance(DBType dbType) {
        Map<String, DBSelector> dsSelectors = this.buildRwDbSelectors(this.rwDataSourcePoolConfig);
        this.runtimeConfigHolder.set(new ZdalRuntime(dsSelectors));
        this.setDbTypeForDBSelector(dbType);
        Monitor.setAppName(appName);
    }

    /**
     * 获取failover的 主库和failover库的映射规则
     * @param masterDBMap   主库的逻辑库和物理库的映射关系
     * @param failoverDBMap  failover库的逻辑库和物理库的映射关系
     * @return  主库和failover库的分组关系
     */
    private Map<String, String> getFailoverRules(Map<String, String> masterDBMap,
                                                 Map<String, String> failoverDBMap) {
        //验证
        int masterDBSize = masterDBMap.size();
        int failoverDBSize = failoverDBMap.size();
        if (masterDBSize == 0 || failoverDBSize == 0) {
            return null;
        }
        Map<String, String> keyWeightMapConfig = new HashMap<String, String>();
        //int lenth = 2;
        for (int i = 0; i < masterDBSize; i++) {
            // String key = StringUtil.alignRight(String.valueOf(i), lenth, '0');
            String key = TableSuffixGenerator.getTableSuffix(i, masterDBSize);
            String masterKey = FailoverDBRuleKey.MASTER_KEY.getValue() + key;
            String failoverKey = FailoverDBRuleKey.FAILOVER_KEY.getValue() + key;
            if (masterDBMap.get(masterKey) == null || failoverDBMap.get(failoverKey) == null) {
                throw new IllegalArgumentException("The datasource map configure error!masterKey:"
                                                   + masterKey + ",failoverKey:" + failoverKey);
            }
            String value = masterKey + ":10," + failoverKey + ":0";
            keyWeightMapConfig.put(Constants.DBINDEX_DS_GROUP_KEY_PREFIX + key, value);
        }
        return keyWeightMapConfig;
    }

    private Map<String, DataSource> getFailoverDataSourcePoolConfig(
                                                                    Map<String, String> masterDBMap,
                                                                    Map<String, String> failoverDBMap) {
        Map<String, DataSource> logicDataSourcesMap = new HashMap<String, DataSource>();
        for (Map.Entry<String, String> mEntry : masterDBMap.entrySet()) {
            String key = mEntry.getKey().trim();
            String value = mEntry.getValue().trim();
            logicDataSourcesMap.put(key, dataSourcesMap.get(value));
        }
        for (Map.Entry<String, String> fEntry : failoverDBMap.entrySet()) {
            String key = fEntry.getKey().trim();
            String value = fEntry.getValue().trim();
            logicDataSourcesMap.put(key, dataSourcesMap.get(value));
        }
        return logicDataSourcesMap;
    }

    protected void initForAppRule(AppRule appRule) {
        Map<String, DBSelector> dsSelectors = this.rwDataSourcePoolConfig == null ? buildDbSelectors(this.dataSourcePoolConfig)
            : this.buildRwDbSelectors(this.rwDataSourcePoolConfig);
        this.runtimeConfigHolder.set(new ZdalRuntime(dsSelectors));

        //添加按数据源key分组的权重配置属性
        if (keyWeightConfig != null && !keyWeightConfig.isEmpty()) {
            // 解析各个分组内数据源的权重信息
            Map<String, ? extends Object> dataSourceKeyConfig = this.rwDataSourcePoolConfig == null ? this.dataSourcePoolConfig
                : this.rwDataSourcePoolConfig;
            keyWeightMapConfig = ZdalDataSourceKeyWeightRumtime.buildKeyWeightConfig(
                keyWeightConfig, dataSourceKeyConfig);
            if (keyWeightMapConfig == null) {
                throw new IllegalStateException("数据源key按分组权重配置错误,zdal初始化失败！");
            }
            GetDataSourceSequenceRules.getKeyWeightRuntimeConfigHoder().set(
                new ZdalDataSourceKeyWeightRumtime(keyWeightMapConfig));
        }
        this.initForDispatcher(appRule);
        Monitor.setAppName(appName);
    }

    private void initForDispatcher(AppRule appRule) {
        SQLParser parser = new DefaultSQLParser();
        this.writeDispatcher = buildSqlDispatcher(appRule.getMasterRule(), parser);
        this.readDispatcher = buildSqlDispatcher(appRule.getSlaveRule(), parser);
    }

    private SqlDispatcher buildSqlDispatcher(ShardRule shardRule, SQLParser parser) {
        if (shardRule == null)
            return null;

        ZdalRoot zdalRoot = new ZdalRoot();
        zdalRoot.setDBType(shardRule.getDbType());
        Map<String/*key*/, LogicTable> logicTableMap = new HashMap<String, LogicTable>();
        if (shardRule.getTableRules() != null) {
            for (Map.Entry<String/*逻辑表名*/, TableRule> e : shardRule.getTableRules().entrySet()) {
                setDbTypeForDbIndex(shardRule.getDbType(), e.getValue().getDbIndexArray());
                LogicTable logicTable = toLogicTable(e.getValue());
                logicTable.setLogicTableName(e.getKey());
                logicTable.setDBType(shardRule.getDbType());
                //logicTable.init(); //tddlRoot.init()包含了logicTable.init()              
                logicTableMap.put(e.getKey(), logicTable);
            }
        }
        zdalRoot.setLogicTableMap(logicTableMap);
        if (shardRule.getDefaultDbIndex() != null) {
            zdalRoot.setDefaultDBSelectorID(shardRule.getDefaultDbIndex());
        }
        zdalRoot.init(appDsName);
        return buildSqlDispatcher(parser, zdalRoot);
    }

    private void setDbTypeForDbIndex(DBType dbType, String[] dbIndexes) {
        Map<String, DBSelector> dbSelectors = this.runtimeConfigHolder.get().dbSelectors;

        for (String dbIndex : dbIndexes) {
            DBSelector dbs = dbSelectors.get(dbIndex);
            if (dbs == null) {
                throw new IllegalArgumentException("规则配置错误：[" + dbIndex + "]在dataSourcePool中没有配置");
            }
            dbs.setDbType(dbType);
            //bug fixed by fanzeng. 因为tddl默认的dbType 是mysql，而在 按优先级进行选择db的时候，如果连接db出现异常，
            //priorityDbGroupSelector会利用内部包装的对等库的 dbtype去选择 excetptionSorter,如果db类型是oracle的，
            //bug fixed 之前，并未初始化EquityDbManager的dbtype，导致会用默认的 mysql类型去选择；
            if (dbs instanceof PriorityDbGroupSelector) {
                EquityDbManager[] equityDbmanager = ((PriorityDbGroupSelector) dbs)
                    .getPriorityGroups();
                if (equityDbmanager == null) {
                    throw new IllegalArgumentException("优先级的对等库并未初始化，请检查配置！");
                }
                for (int i = 0; i < equityDbmanager.length; i++) {
                    equityDbmanager[i].setDbType(dbType);
                }
            }

        }
    }

    /**
     * 为load balance 设置dbType
     * @param dbType
     */
    private void setDbTypeForDBSelector(DBType dbType) {
        Map<String, DBSelector> dbSelectors = this.runtimeConfigHolder.get().dbSelectors;
        int i = 0;
        String[] dbIndexes = new String[dbSelectors.size()];
        for (Map.Entry<String, DBSelector> dbselector : dbSelectors.entrySet()) {
            dbIndexes[i++] = dbselector.getKey().trim();
        }
        setDbTypeForDbIndex(dbType, dbIndexes);
    }

    /**
     * 
     * @return
     */
    private SimpleTableMapProvider getTableMapProvider(TableRule tableRule) {
        SimpleTableMapProvider simpleTableMapProvider = null;
        SuffixManager suffixManager = tableRule.getSuffixManager();
        Suffix suf = suffixManager.getSuffix(0);
        if (suf.getTbType().equals(TableSuffixTypeEnum.twoColumnForEachDB.getValue())) {
            simpleTableMapProvider = new SimpleTableTwoColumnsMapProvider();
            SimpleTableTwoColumnsMapProvider twoColumns = (SimpleTableTwoColumnsMapProvider) simpleTableMapProvider;
            Suffix suf2 = suffixManager.getSuffix(1);
            twoColumns.setFrom2(suf2.getTbSuffixFrom());
            twoColumns.setTo2(suf2.getTbSuffixTo());
            twoColumns.setWidth2(suf2.getTbSuffixWidth());
            twoColumns.setPadding2(suf2.getTbSuffixPadding());
        } else if (TableSuffixTypeEnum.dbIndexForEachDB.getValue().equals(suf.getTbType())) {
            simpleTableMapProvider = new SimpleTableDatabaseMapProvider();
        } else if (TableSuffixTypeEnum.groovyTableList.getValue().equals(suf.getTbType())
                   || TableSuffixTypeEnum.groovyThroughAllDBTableList.getValue().equals(
                       suf.getTbType())
                   || TableSuffixTypeEnum.groovyAdjustTableList.getValue().equals(suf.getTbType())) {
            simpleTableMapProvider = new GroovyTableDatabaseMapProvider();
            try {
                GroovyTableDatabaseMapProvider groovyTableDatabaseMapProvider = (GroovyTableDatabaseMapProvider) simpleTableMapProvider;
                groovyTableDatabaseMapProvider.setTbType(suf.getTbType());
                groovyTableDatabaseMapProvider.setExpression(suffixManager.getExpression());
                groovyTableDatabaseMapProvider.setTbPreffix(tableRule.getTbPreffix());
                //设定db的个数，在实现groovy的分表均匀分布的时候会用到。
                groovyTableDatabaseMapProvider.setDbNumber(tableRule.getDbIndexCount());
            } catch (ParseException e) {
                throw new ZdalClientException("ERROR ## Tbsuffix的配置有问题！，请检查", e);
            }

        } else {
            simpleTableMapProvider = new SimpleTableMapProvider();
        }
        return simpleTableMapProvider;
    }

    private LogicTable toLogicTable(TableRule tableRule) {
        SimpleLogicTable st = new SimpleLogicTable();
        st.setAllowReverseOutput(tableRule.isAllowReverseOutput());
        st.setDatabases(tableRule.getDbIndexes());
        if (tableRule.getDbRuleArray() != null) {
            List<Object> dbRules = new ArrayList<Object>(tableRule.getDbRuleArray().length);
            for (Object obj : tableRule.getDbRuleArray()) {
                dbRules.add((String) obj);
            }
            st.setDatabaseRuleStringList(dbRules);
        }
        if (tableRule.getTbRuleArray() != null) {
            List<Object> tbRules = new ArrayList<Object>(tableRule.getTbRuleArray().length);
            for (Object obj : tableRule.getTbRuleArray()) {
                tbRules.add((String) obj);
            }
            st.setTableRuleStringList(tbRules);
            //如果是2列的情况就用2列的类，否则按以前的逻辑走
            st.setSimpleTableMapProvider(getTableMapProvider(tableRule));
            SuffixManager suffixManager = tableRule.getSuffixManager();
            Suffix suf = suffixManager.getSuffix(0);

            //分表规则存在，才设置表后缀属性，设置了任何一个属性，就表示用simpleTableMapProvider
            st.setFrom(suf.getTbSuffixFrom());
            st.setTo(suf.getTbSuffixTo());
            st.setWidth(suf.getTbSuffixWidth());
            st.setPadding(suf.getTbSuffixPadding());
            st.setTablesNumberForEachDatabases(suf.getTbNumForEachDb());
        }
        if (tableRule.getUniqueKeyArray() != null) {
            st.setUniqueKeys(Arrays.asList(tableRule.getUniqueKeyArray()));
        }
        if (tableRule.isDisableFullTableScan()) {
            st.setDefaultListResultStragety(DEFAULT_LIST_RESULT_STRAGETY.NONE);
        } else {
            st.setDefaultListResultStragety(DEFAULT_LIST_RESULT_STRAGETY.FULL_TABLE_SCAN);
        }

        return st;
    }

    private SpringBasedDispatcherImpl buildSqlDispatcher(SQLParser parser, ZdalRoot tddlRoot) {
        if (tddlRoot != null) {
            SpringBasedDispatcherImpl dispatcher = new SpringBasedDispatcherImpl();
            dispatcher.setParser(parser);
            dispatcher.setRoot(tddlRoot);
            return dispatcher;
        } else {
            return null;
        }
    }

    private DBSelector buildDbSelector(String dbIndex, DataSource[] dataSourceArray) {
        Map<String, DataSource> map = new HashMap<String, DataSource>(dataSourceArray.length);
        for (int i = 0; i < dataSourceArray.length; i++) {
            map.put(dbIndex + Constants.DBINDEX_DSKEY_CONN_CHAR + i, dataSourceArray[i]);
        }
        EquityDbManager dbSelector = new EquityDbManager(dbIndex, map);
        dbSelector.setAppDsName(appDsName);
        return dbSelector;
    }

    private DBSelector buildDbSelector(String dbIndex, List<DataSource> dataSourceList) {
        Map<String, DataSource> map = new HashMap<String, DataSource>(dataSourceList.size());
        for (int i = 0, n = dataSourceList.size(); i < n; i++) {
            map.put(dbIndex + Constants.DBINDEX_DSKEY_CONN_CHAR + i, dataSourceList.get(i));
        }
        EquityDbManager dbSelector = new EquityDbManager(dbIndex, map);
        dbSelector.setAppDsName(appDsName);
        return dbSelector;
    }

    @SuppressWarnings("unchecked")
    private Map<String, DBSelector> buildDbSelectors(Map<String, ? extends Object> dataSourcePool) {
        Map<String, DBSelector> dsSelectors = new HashMap<String, DBSelector>();
        for (Map.Entry<String, ? extends Object> e : dataSourcePool.entrySet()) {
            if (e.getValue() instanceof DataSource) {
                OneDBSelector selector = new OneDBSelector(e.getKey(), (DataSource) e.getValue());
                selector.setAppDsName(appDsName);
                dsSelectors.put(e.getKey(), selector);
            } else if (e.getValue() instanceof DataSource[]) {
                dsSelectors.put(e.getKey(),
                    buildDbSelector(e.getKey(), (DataSource[]) e.getValue()));
            } else if (e.getValue() instanceof List) {
                dsSelectors.put(e.getKey(), buildDbSelector(e.getKey(), (List<DataSource>) e
                    .getValue()));
            } else if (e.getValue() instanceof DBSelector) {
                dsSelectors.put(e.getKey(), (DBSelector) e.getValue());
            } else if (e.getValue() instanceof String) {
                String[] dbs = ((String) e.getValue()).split(","); //支持以逗号分隔的多个数据源ID
                if (dbs.length == 1) {
                    int index = dbs[0].indexOf(":");
                    String dsbeanId = index == -1 ? dbs[0] : dbs[0].substring(0, index);//单个DS去除不必要的权重
                    DataSource dataSource = getDataSourceObject(dsbeanId);
                    OneDBSelector selector = new OneDBSelector(e.getKey(), dataSource);
                    selector.setAppDsName(appDsName);
                    dsSelectors.put(e.getKey(), selector);
                } else {
                    DataSource[] dsArray = new DataSource[dbs.length];
                    for (int i = 0; i < dbs.length; i++) {
                        dsArray[i] = getDataSourceObject(dbs[i]);
                    }
                    dsSelectors.put(e.getKey(), buildDbSelector(e.getKey(), dsArray));
                }
            }
            dsSelectors.get(e.getKey()).setDbType(this.defaultDbType);
        }
        return dsSelectors;
    }

    /**
     * 获取 数据源
     * @return
     */
    private DataSource getDataSourceObject(String dsName) {
        DataSource dataSource = null;
        if (StringUtil.isBlank(dsName)) {
            throw new IllegalArgumentException("The dsName can't be null!");
        }

        dataSource = this.dataSourcesMap.get(dsName.trim());

        if (dataSource == null) {
            throw new IllegalArgumentException("The dataSource can't be null,dsName=" + dsName);
        }
        return dataSource;
    }

    /**
     * 对老式配置模式的支持：
     * 分库规则定位到的是一个dbgroup，dbgroup内又包含读库和写库，这时规则不分masterRule、slaveRule，只有一个oneRule
     *   
     * 每个key对应dbgroup中，每个库可以有读写属性及权重，格式如下
     *   <entry key="slave_0" value="slaver_db1_a:RW    ,slaver_db1_b:R" />
     *   <entry key="slave_1" value="slaver_db2_a:R10W  ,slaver_db2_b:R20" />
     *   <entry key="slave_2" value="slaver_db3_a:R10W10,slaver_db3_b:R20W0" />
     *   <entry key="slave_3" value="slaver_db4_a:R10W20,slaver_db3_b:R20W10" /> <!-- 主主 -->
     *   <entry key="slave_4" value="slaver_db5_a,slaver_db5_b" /><!-- 主主 -->
     *   <entry key="slave_5" value="slaver_db6" />     * 
     * 对应的权重：
     *   slave_0=R10W10,R10W0
     *   slave_1=R10W10,R20W0
     *   slave_3=R10W20,R20W10
     *   slave_4=R10W10,R10W10
     *   slave_5=RW
     *   
     * 
     * 适配做法是
     * 将oneRule拆分成masterRule和slaveRule；将oneRule中的dbIndex分别在masterRule中加_w后缀，在slaveRule中加_r后缀
     *    tabaleA: <property name="dbIndexes" value="slave_0,slave_1,slave_2,slave_3" />
     *    
     *    master.tabaleA: <property name="dbIndexes" value="slave_0_w,slave_1_w,slave_2_w,slave_3_w" />
     *    slaver.tabaleA: <property name="dbIndexes" value="slave_0_r,slave_1_r,slave_2_r,slave_3_r" />
     * 将dbindex中每个数据源的读写属性，按权重拆分到master_dbindex 和slave_dbindex
     * master和slave的具体数据源列出所有的，只是
     *   <entry key="slave_0_w" value="slaver_db1_a:10,slaver_db1_b:0" />
     *   <entry key="slave_0_r" value="slaver_db1_a:10,slaver_db1_b:10" />
     *   <entry key="slave_1_w" value="slaver_db2_a:10,slaver_db2_b:0" />
     *   <entry key="slave_1_r" value="slaver_db2_a:10,slaver_db2_b:20" />
     *   <entry key="slave_2_w" value="slaver_db3_a:10,slaver_db3_b:0" />
     *   <entry key="slave_2_r" value="slaver_db3_a:10,slaver_db3_b:20" />
     *   <entry key="slave_3_w" value="slaver_db4_a:20,slaver_db3_b:10" /> <!-- 主主 -->
     *   <entry key="slave_3_r" value="slaver_db4_a:10,slaver_db3_b:20" /> <!-- 主主 -->
     *   <entry key="slave_4_w" value="slaver_db5_a:10,slaver_db5_b:10" /><!-- 主主 -->
     *   <entry key="slave_4_r" value="slaver_db5_a:10,slaver_db5_b:10" /><!-- 主主 -->
     *   <entry key="slave_5_w" value="slaver_db6" />
     *   <entry key="slave_5_r" value="slaver_db6" />
     * 
     * 权重推送：
     *   slave_1=R10W10,R20W0  |--> slave_1_w[10,0], slave_1_r[10,20]
     *   变为：
     *   slave_1=R10W10,R0,W0  |--> slave_1_w[10,0], slave_1_r[10,0]
     * 
     * @param dataSourcePool
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, DBSelector> buildRwDbSelectors(Map<String, ? extends Object> dataSourcePool) {
        Map<String, DBSelector> dsSelectors = new HashMap<String, DBSelector>();
        for (Map.Entry<String, ? extends Object> e : dataSourcePool.entrySet()) {
            String rdbIndex = e.getKey() + AppRule.DBINDEX_SUFFIX_READ; //"_r";
            String wdbIndex = e.getKey() + AppRule.DBINDEX_SUFFIX_WRITE;//"_w";
            if (e.getValue() instanceof DataSource) {
                OneDBSelector rSelector = new OneDBSelector(rdbIndex, (DataSource) e.getValue());
                rSelector.setAppDsName(appDsName);
                dsSelectors.put(rdbIndex, rSelector);
                OneDBSelector wSelector = new OneDBSelector(wdbIndex, (DataSource) e.getValue());
                wSelector.setAppDsName(appDsName);
                dsSelectors.put(wdbIndex, wSelector);
            } else if (e.getValue() instanceof DataSource[]) {
                dsSelectors.put(rdbIndex, buildDbSelector(rdbIndex, (DataSource[]) e.getValue()));
                dsSelectors.put(wdbIndex, buildDbSelector(wdbIndex, (DataSource[]) e.getValue()));
            } else if (e.getValue() instanceof List) {
                dsSelectors.put(rdbIndex,
                    buildDbSelector(rdbIndex, (List<DataSource>) e.getValue()));
                dsSelectors.put(wdbIndex,
                    buildDbSelector(wdbIndex, (List<DataSource>) e.getValue()));
            } else if (e.getValue() instanceof DBSelector) {
                dsSelectors.put(rdbIndex, (DBSelector) e.getValue());
                dsSelectors.put(wdbIndex, (DBSelector) e.getValue());
            } else if (e.getValue() instanceof String) {
                parse(dsSelectors, e.getKey(), (String) e.getValue());
            }
            dsSelectors.get(rdbIndex).setDbType(this.defaultDbType);
            dsSelectors.get(wdbIndex).setDbType(this.defaultDbType);
        }
        CONFIG_LOGGER.warn("warn ## \n" + showDbSelectors(dsSelectors, dataSourcePool));
        return dsSelectors;
    }

    private String showDbSelectors(Map<String, DBSelector> dsSelectors,
                                   Map<String, ? extends Object> dataSourcePool) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ? extends Object> e : dataSourcePool.entrySet()) {
            sb.append("[").append(e.getKey()).append("=").append(e.getValue()).append("]");
        }
        sb.append("\nconvert to:\n");
        for (Map.Entry<String, DBSelector> e : dsSelectors.entrySet()) {
            if (e.getValue() instanceof EquityDbManager) {
                EquityDbManager db = (EquityDbManager) e.getValue();
                sb.append(e.getKey()).append("=").append(db.getWeights()).append("\n");
            } else if (e.getValue() instanceof PriorityDbGroupSelector) {
                PriorityDbGroupSelector selector = (PriorityDbGroupSelector) e.getValue();
                sb.append(selector.getId() + ": \n");
                EquityDbManager[] dbs = selector.getPriorityGroups();
                for (EquityDbManager db : dbs) {
                    sb.append(db.getId()).append(db.getWeights()).append("\n");
                }
            }

        }

        return sb.toString();
    }

    /**如果不仅一组，则用优先队列来存储数据源。
     * 同一组内随机选取一个，不同组严格按优先级别来选取，只有高级别出错误才会向下选取低级别的数据源
     *
     * 只写rw时，pr=pw=0默认值，即大家都在同一组内，大家随便选取,只写p 时，pr=pw=p
    * dbs = slaver_db3_a:R10W10p10,slaver_db3_b:R20W0p5 
     * 对读和写都分别分级，pr pw 
    * dbs = slaver_db3_a:R10W10pr10pw2,slaver_db3_b:R20W0pr5pw10
    * @param  databaseSources
    *  @param dbIndex
    *  @param dsSelectors
     */
    @SuppressWarnings("unchecked")
    private void parseDbSelector(String[] databaseSources, String dbIndex,
                                 Map<String, DBSelector> dsSelectors, WeightRWPQEnum rwPriority) {

        Map<Integer, Map<String, DataSource>> initDataSourceGroups = new HashMap<Integer, Map<String, DataSource>>(
            1);
        Map<Integer, Map<String, Integer>> weightGroups = new HashMap<Integer, Map<String, Integer>>(
            1);

        for (int i = 0; i < databaseSources.length; i++) {
            //对于每个DataSource和权重
            String dsKey = dbIndex + Constants.DBINDEX_DSKEY_CONN_CHAR + i;
            String[] beanIdAndWeight = databaseSources[i].split(":"); //dbs[i]=slaver_db3_a:R10W10
            DataSource dataSource = (DataSource) this
                .getDataSourceObject(beanIdAndWeight[0].trim());
            int[] weightRWPQ = parseWeightRW(beanIdAndWeight.length == 2 ? beanIdAndWeight[1]
                : null);

            //获得本级别的数据库组，没有则创建
            Map<String, DataSource> initDataSources = initDataSourceGroups
                .get(weightRWPQ[rwPriority.value()]);
            if (initDataSources == null) {
                initDataSources = new HashMap<String, DataSource>(databaseSources.length);
                initDataSourceGroups.put(weightRWPQ[rwPriority.value()], initDataSources);
            }

            //获得本级别的权重组，没有则创建
            Map<String, Integer> weights = weightGroups.get(weightRWPQ[rwPriority.value()]);
            if (weights == null) {
                weights = new HashMap<String, Integer>(databaseSources.length);
                weightGroups.put(weightRWPQ[rwPriority.value()], weights);
            }

            weights.put(dsKey, weightRWPQ[rwPriority.value() - 2]);
            initDataSources.put(dsKey, dataSource);
        }

        if (initDataSourceGroups.size() == 1) {
            Map<String, DataSource> rInitDataSources = initDataSourceGroups.values().toArray(
                new Map[1])[0];
            Map<String, Integer> rWeights = weightGroups.values().toArray(new Map[1])[0];
            EquityDbManager equityDbManager = new EquityDbManager(dbIndex, rInitDataSources,
                rWeights);
            if (defaultDbType != null)
                equityDbManager.setDbType(defaultDbType);
            equityDbManager.setAppDsName(appDsName);
            dsSelectors.put(dbIndex, equityDbManager);
        } else {
            List<Integer> rpriorityKeys = new ArrayList<Integer>(initDataSourceGroups.size());
            rpriorityKeys.addAll(initDataSourceGroups.keySet());
            Collections.sort(rpriorityKeys);
            EquityDbManager[] rpriorityGroups = new EquityDbManager[rpriorityKeys.size()];

            for (int i = 0; i < rpriorityGroups.length; i++) {
                Integer key = rpriorityKeys.get(i);
                Map<String, DataSource> rInitDataSources = initDataSourceGroups.get(key);
                Map<String, Integer> rWeights = weightGroups.get(key);
                EquityDbManager equityDbManager = new EquityDbManager(dbIndex, rInitDataSources,
                    rWeights);
                if (defaultDbType != null)
                    equityDbManager.setDbType(defaultDbType);
                equityDbManager.setAppDsName(appDsName);
                rpriorityGroups[i] = equityDbManager;

            }
            dsSelectors.put(dbIndex, new PriorityDbGroupSelector(dbIndex, rpriorityGroups));
        }
    }

    //<entry key="slave_2" value="slaver_db3_a:R10W10,slaver_db3_b:R20W0" />
    private void parse(Map<String, DBSelector> dsSelectors, String dbIndex, String commaDbs) {
        String rdbIndex = dbIndex + AppRule.DBINDEX_SUFFIX_READ; //"_r";
        String wdbIndex = dbIndex + AppRule.DBINDEX_SUFFIX_WRITE;//"_w";
        String[] dbs = commaDbs.split(","); //支持以逗号分隔的多个数据源ID
        //如果只有一个DataSource，则用OneDBSelector
        if (dbs.length == 1) {
            int index = dbs[0].indexOf(":");
            String dsbeanId = index == -1 ? dbs[0] : dbs[0].substring(0, index);//单个DS去除不必要的权重
            DataSource ds = this.getDataSourceObject(dsbeanId.trim());
            OneDBSelector selectorRead = new OneDBSelector(rdbIndex, ds);
            selectorRead.setAppDsName(appDsName);
            OneDBSelector selectorWrite = new OneDBSelector(wdbIndex, ds);
            selectorWrite.setAppDsName(appDsName);
            dsSelectors.put(rdbIndex, selectorRead);
            dsSelectors.put(wdbIndex, selectorWrite);
        } else {
            //分别分析写读数据源
            parseDbSelector(dbs, wdbIndex, dsSelectors, WeightRWPQEnum.writePriority);
            parseDbSelector(dbs, rdbIndex, dsSelectors, WeightRWPQEnum.readPriority);
        }
    }

    private int[] parseWeightRW(String weight) {
        if (weight == null) {
            return new int[] { 10, 10, 0, 0 }; //默认读写都打开，读写均为P0级
        }
        int r, w, p, q;
        weight = weight.trim().toLowerCase(); //统计到小写方便后续处理
        if (weight.indexOf('R') == -1 && weight.indexOf('r') == -1) {
            r = 0;
        } else {
            r = parseNumber(weightPattern_r, weight, 10);
        }

        if (weight.indexOf('W') == -1 && weight.indexOf('w') == -1) {
            w = 0;
        } else {
            w = parseNumber(weightPattern_w, weight, 10);
        }
        if (weight.indexOf('P') == -1 && weight.indexOf('p') == -1) {
            p = 0;
        } else {
            p = parseNumber(weightPattern_p, weight, 0);
        }
        if (weight.indexOf('Q') == -1 && weight.indexOf('q') == -1) {
            q = 0;
        } else {
            q = parseNumber(weightPattern_q, weight, 0);
        }

        return new int[] { r, w, p, q };

    }

    private int parseNumber(Pattern p, String weight, int defaultValue) {
        Matcher m = p.matcher(weight);
        if (!m.find()) {//这里用matches()就不行？
            throw new IllegalArgumentException(
                "权重配置不符合正则式[Rr](\\d*)[Ww](\\d*)[Pp](\\d*)[Qq](\\d*)：" + weight);
        }
        if (m.group(1).length() == 0) {
            return defaultValue;
        } else {
            return Integer.parseInt(m.group(1));
        }
    }

    /**
     * reset zdatasource，failoverRule，readWriteRule,目前tair数据源不支持重建.
     * @param zdalConfig
     */
    public void resetZdalDataSource(Map<String, String> keyWeights) {
        try {
            long startReset = System.currentTimeMillis();
            if (keyWeightConfig != null && !keyWeightConfig.isEmpty()) {
                this.resetKeyWeightConfig(keyWeights);
                String resetKeyWeightResults = getReceivDataStr(keyWeights);
                //当所有的权重调整完毕后再打印该日志
                CONFIG_LOGGER.warn("WARN ## resetKeyWeightConfig[" + appDsName + "]:"
                                   + resetKeyWeightResults);
                CONFIG_LOGGER.warn("WARN ## reset the config success,cost "
                                   + (System.currentTimeMillis() - startReset)
                                   + " ms,the appDsName = " + appDsName);
                this.keyWeightConfig = keyWeights;
            } else if (rwDataSourcePoolConfig != null && !rwDataSourcePoolConfig.isEmpty()) {
                this.resetDbWeight(keyWeights);
                String dbWeightConfigs = getReceivDataStr(keyWeights);
                //当所有的权重调整完毕后再打印该日志
                CONFIG_LOGGER.warn("WARN ## resetRwDataSourceConfig[" + appDsName + "]:"
                                   + dbWeightConfigs);
                CONFIG_LOGGER.warn("WARN ## reset the config success,cost "
                                   + (System.currentTimeMillis() - startReset)
                                   + " ms,the appDsName = " + appDsName);
                this.rwDataSourcePoolConfig = keyWeights;
            } else {
                throw new ZdalClientException(
                    "ERROR ## only keyWeightConfig,rwDataSourcePoolConfig can reset weight");
            }

        } catch (Exception e) {
            throw new ZdalClientException("ERROR ## appDsName = " + zdalConfig.getAppDsName()
                                          + " the version = " + zdalConfig.getVersion()
                                          + " reset config failured ", e);
        }
    }

    /**
     * zdal reset rw weight
     * @param p
     */
    private void resetDbWeight(Map<String, String> p) {
        Map<String, DBSelector> dbSelectors = this.runtimeConfigHolder.get().dbSelectors;
        for (Map.Entry<String, String> entrySet : p.entrySet()) {
            String dbIndex = ((String) entrySet.getKey()).trim();
            String commaWeights = ((String) entrySet.getValue()).trim();

            if ( this.rwDataSourcePoolConfig != null
                && this.rwDataSourcePoolConfig.get(dbIndex) != null) {
                //readwriteRule方式的weight
                resetRwDbWeight(dbIndex, dbSelectors, commaWeights);
            } else if (this.dataSourcePoolConfig != null
                       && dataSourcePoolConfig.get(dbIndex) != null) {
                String[] rdwds = commaWeights.split(",");
                int[] weights = new int[rdwds.length];
                for (int i = 0; i < rdwds.length; i++) {
                    weights[i] = Integer.parseInt(rdwds[i]);
                }
                resetDbWeight(dbIndex, dbSelectors, weights);
            }
        }
    }

    /**
     * @param commaWeights: R10W10,R10W0
     * 格式：dskey0=r10w10,r10w0 
     */
    private void resetRwDbWeight(String dbIndex, Map<String, DBSelector> dbSelectors,
                                 String commaWeights) {
        String[] rdwds = commaWeights.split(",");
        int[] rWeights = new int[rdwds.length];
        int[] wWeights = new int[rdwds.length];
        for (int i = 0; i < rdwds.length; i++) {
            int[] weightRW = parseWeightRW(rdwds[i]);
            rWeights[i] = weightRW[0];
            wWeights[i] = weightRW[1];
        }
        resetDbWeight(dbIndex + AppRule.DBINDEX_SUFFIX_READ, dbSelectors, rWeights);
        resetDbWeight(dbIndex + AppRule.DBINDEX_SUFFIX_WRITE, dbSelectors, wWeights);
    }

    private void resetDbWeight(String dbIndex, Map<String, DBSelector> dbSelectors, int[] weights) {
        DBSelector dbSelector = dbSelectors.get(dbIndex);
        if (dbSelector == null) {
            throw new ZdalClientException(
                "ERROR ## Couldn't find dbIndex in current datasoures. dbIndex:" + dbIndex);
        }
        Map<String, Integer> weightMap = new HashMap<String, Integer>(weights.length);
        for (int i = 0; i < weights.length; i++) {
            weightMap.put(dbIndex + Constants.DBINDEX_DSKEY_CONN_CHAR + i, weights[i]);
        }
        dbSelector.setWeight(weightMap);
    }

    /**
     * added by fanzeng.
     * changed by boya.
     * 参数p格式如下
     * group_00=ds0:10,ds1:0
     * group_01=ds2:10,ds3:0
     * group_02=ds4:0,ds5:10
     * 一组只有一个库的时候不用调整其权重，默认为10
     * @param p 推送过来的内容
     */
    protected void resetKeyWeightConfig(Map<String, String> p) {
        //        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = GetDataSourceSequenceRules
        //            .getKeyWeightRuntimeConfigHoder().get().getKeyWeightMapHolder();
        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = keyWeightMapConfig;
        for (Entry<String, String> entrySet : p.entrySet()) {
            String groupKey = entrySet.getKey();
            String value = entrySet.getValue();
            if (StringUtil.isBlank(groupKey) || StringUtil.isBlank(value)) {
                throw new ZdalClientException("ERROR ## 数据源groupKey=" + groupKey
                                              + "分组权重配置信息不能为空,value=" + value);
            }
            String[] keyWeightStr = value.split(",");
            String[] weightKeys = new String[keyWeightStr.length];
            int[] weights = new int[keyWeightStr.length];
            for (int i = 0; i < keyWeightStr.length; i++) {
                if (StringUtil.isBlank(keyWeightStr[i])) {
                    throw new ZdalClientException("ERROR ## 数据源keyWeightStr[" + i
                                                  + "]分组权重配置信息不能为空.");
                }
                String[] keyAndWeight = keyWeightStr[i].split(":");
                if (keyAndWeight.length != 2) {
                    throw new ZdalClientException("ERROR ## 数据源key按组配置权重错误,keyWeightStr[" + i
                                                  + "]=" + keyWeightStr[i] + ".");
                }
                String key = keyAndWeight[0];
                String weightStr = keyAndWeight[1];
                if (StringUtil.isBlank(key) || StringUtil.isBlank(weightStr)) {
                    CONFIG_LOGGER.error("ERROR ## 数据源分组权重配置信息不能为空,key=" + key + ",weightStr="
                                        + weightStr);
                    return;
                }
                weightKeys[i] = key.trim();
                weights[i] = Integer.parseInt(weightStr.trim());
            }
            //          根据 groupKey以及对应的keyAndWeightMap去查询
            ZdalDataSourceKeyWeightRandom weightRandom = keyWeightMapHolder.get(groupKey);
            if (weightRandom == null) {
                throw new ZdalClientException("ERROR ## 新推送的按数据源key分组权重配置中的key不对,非法的groupKey="
                                              + groupKey);
            }
            for (String newKey : weightKeys) {
                if (weightRandom.getWeightConfig() == null
                    || !weightRandom.getWeightConfig().containsKey(newKey)) {
                    throw new ZdalClientException("新推送的数据源分组" + groupKey
                                                  + "权重配置中包含不属于该组的数据源标识,key=" + newKey);
                }
            }
            if (weightKeys.length != weightRandom.getDataSourceNumberInGroup()) {
                throw new ZdalClientException("新推送的按数据源key分组权重配置中，分组groupKey=" + groupKey
                                              + "包含的数据源个数不对 ,size=" + weightKeys.length
                                              + ",the size should be "
                                              + weightRandom.getDataSourceNumberInGroup());
            }
            //根据该组的groupKey以及对应的keyAndWeightMap生成TDataSourceKeyWeightRandom
            ZdalDataSourceKeyWeightRandom TDataSourceKeyWeightRandom = new ZdalDataSourceKeyWeightRandom(
                weightKeys, weights);
            keyWeightMapHolder.put(groupKey, TDataSourceKeyWeightRandom);
        }
        GetDataSourceSequenceRules.getKeyWeightRuntimeConfigHoder().set(
            new ZdalDataSourceKeyWeightRumtime(keyWeightMapHolder));
        //设置本地的keyWeightMapCofig属性，全活策略会依赖于该配置
        this.keyWeightMapConfig = keyWeightMapHolder;
    }

    /**
     * added  for  zdal
     * @param p
     * @return
     */
    private String getReceivDataStr(Map<String, String> p) {
        String str = "";
        if (p != null) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : p.entrySet()) {
                String key = entry.getKey().trim();
                String value = entry.getValue().trim();
                sb.append(key).append("=").append(value).append(";");
            }
            str = sb.toString();
        }
        return str;
    }

    /**
     * zdataconsole中的配置信息转化成ZdataSource的配置信息 .
     * 
     * @param parameter
     * @return
     */
    private LocalTxDataSourceDO createDataSourceDO(DataSourceParameter parameter, DBType dbType,
                                                   String dsName, boolean isPrefillByZone) throws Exception {
        LocalTxDataSourceDO dsDo = new LocalTxDataSourceDO();
        dsDo.setDsName(dsName);
        dsDo.setConnectionURL(parameter.getJdbcUrl());
        dsDo.setUserName(parameter.getUserName());
        dsDo.setEncPassword(parameter.getPassword());
        dsDo.setMinPoolSize(parameter.getMinConn());
        dsDo.setMaxPoolSize(parameter.getMaxConn());
        dsDo.setDriverClass(parameter.getDriverClass());
        dsDo.setBlockingTimeoutMillis(parameter.getBlockingTimeoutMillis());
        dsDo.setIdleTimeoutMinutes(parameter.getIdleTimeoutMinutes());
        dsDo.setPreparedStatementCacheSize(parameter.getPreparedStatementCacheSize());
        dsDo.setQueryTimeout(parameter.getQueryTimeout());
        dsDo.getConnectionProperties().putAll(getConnectionProperties());
        if( !isPrefillByZone ) dsDo.setPrefill(parameter.getPrefill());
        if (dbType.isMysql()) {
            dsDo.setExceptionSorterClassName(MySQLExceptionSorter.class.getName());
        } else if (dbType.isOracle()) {
            dsDo.setExceptionSorterClassName(OracleExceptionSorter.class.getName());
        } else {
            throw new ZdalClientException("ERROR ## the DbType must be mysql/oracle.");
        }
    	dsDo.setConnectionProperties(parameter.getConnectionProperties());
        return dsDo;

    }

    /** 
     * 数据源对connection的封装.
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        if (inited.get() == false) {
            throw new ZdalClientException("ERROR ## the ZdalDataSource has not init");
        }
        ZdalConnection connection = new ZdalConnection();
        this.buildTconnection(connection);
        return connection;
    }

    /** 
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        if (inited.get() == false) {
            throw new ZdalClientException("ERROR ## the ZdalDataSource has not init");
        }
        ZdalConnection connection = new ZdalConnection(username, password);
        this.buildTconnection(connection);
        return connection;
    }

    /**
     * 创建连接，将必要的参数设置到ZdalConnection里去，然后设置给 ZdalStatement 对象
     * @param connection
     */
    private void buildTconnection(ZdalConnection connection) {
        ZdalRuntime rt = this.runtimeConfigHolder.get();
        connection.setDataSourcePool(rt == null ? null : rt.dbSelectors);
        connection.setWriteDispatcher(this.writeDispatcher);
        connection.setReadDispatcher(this.readDispatcher);
        connection.setTimeoutThreshold(this.timeoutThreshold);
        connection.setRetryingTimes(this.retryingTimes);
        connection.setDbConfigType(this.dbConfigType);
        connection.setThrowException(super.getZdalConfig().getZoneError().isException());
        connection.setPrefillZdal(prefillZdal);
        connection.setAppDsName(appDsName);
        if (prefillZdal) {
            connection.setZoneDs(prefillZoneDs);
        } else {
            connection.setZoneDs(super.getZdalConfig().getZoneDs());
        }
    }

    /**
     * 预热最小连接数，策略如下：
     * 1，如果prefill = false,就不预热
     * 2，如果prefill = true，按照下面的逻辑进行预热：
     *  a,如果zoneDs为空，就预热所有的物理数据源的最小连接数；
     *  b，如果zoneDs不为空，就预热zoneDs对应的物理数据源的最小连接数.
     */
    private void prefillDataSource(Set<String> zoneDs) {
        if (prefill == false) {
            CONFIG_LOGGER
                .warn("WARN ## the prefill is false,will not prefill min connection at init-time,the appDsName = "
                      + appDsName);
            return;
        }
        if (zoneDs == null || zoneDs.isEmpty()) {
            PreFillConnection.prefillConnection(dataSourcesMap.values(), defaultDbType);//如果zoneDs为空，就预热所有的物理数据源.
            CONFIG_LOGGER
                .warn("WARN ## will prefill all ZdataSource min connection at init-time,the appDsName = "
                      + appDsName);
            return;
        } else {
            // 根据zoneDs预热对应的物理ds.
            Map<String, ZDataSource> prefillDataSources = getPreFillDataSources(zoneDs);
            PreFillConnection.prefillConnection(prefillDataSources.values(), defaultDbType);
            CONFIG_LOGGER
                .warn("WARN ## appDsName = " + appDsName + " logicDataSource = " + zoneDs
                      + " ,will prefill min connection  of " + prefillDataSources.keySet());
            return;
        }
    }

    /**
     * 重置本众能访问到的逻辑数据源名称.
     * @param zoneDs
     */
    public void resetZoneDs(Set<String> zoneDs) {
        try {
            super.getZdalConfig().getZoneDs().clear();
            super.getZdalConfig().getZoneDs().addAll(zoneDs);
            if (zoneDs.isEmpty()) {
                CONFIG_LOGGER.warn("WARN ## zone = " + zone
                                   + " can use all datasources,the appDsName = " + appDsName);
            } else {
                CONFIG_LOGGER.warn("WARN ## zone = " + zone + " can use these datasources:"
                                   + zoneDs + " ,the appDsName = " + appDsName);
            }
        } catch (Exception e) {
            CONFIG_LOGGER.warn("WARN ## reset ZoneDs has an error:", e);
        }
    }

    /**
     * 根据逻辑数据源返回对应的物理数据源.
     * @param zoneDs
     * @return
     */
    private Map<String, ZDataSource> getPreFillDataSources(Set<String> zoneDs) {
        Map<String, ZDataSource> prefillDataSources = new HashMap<String, ZDataSource>();
        for (String ds : zoneDs) {
            Map<String, String> masters = super.getZdalConfig().getMasterLogicPhysicsDsNames();
            if (!masters.isEmpty() && masters.containsKey(ds)) {//在masters中寻找对应的物理数据源
                if (!prefillDataSources.containsKey(masters.get(ds))) {
                    ZDataSource dataSource = dataSourcesMap.get(masters.get(ds));
                    if (dataSource != null) {
                        prefillDataSources.put(masters.get(ds), dataSource);
                    }
                }
                continue;
            }

            Map<String, String> failovers = super.getZdalConfig().getFailoverLogicPhysicsDsNames();
            if (!failovers.isEmpty() && failovers.containsKey(ds)) {//在failovers中寻找对应的物理数据源
                if (!prefillDataSources.containsKey(failovers.get(ds))) {
                    ZDataSource dataSource = dataSourcesMap.get(failovers.get(ds));
                    if (dataSource != null) {
                        prefillDataSources.put(failovers.get(ds), dataSource);
                    }
                }
                continue;
            }
            Map<String, String> rwDses = super.getZdalConfig().getReadWriteRules();
            if (!rwDses.isEmpty()) {//如果是shard+rw的模式，zoneDs存放的是rw对应的group_0_w,group_0_r等格式的.
                String groupName = null;
                if (ds.contains("_w")) {//获取_w前面的groupName.
                    groupName = StringUtil.substringBefore(ds, "_w");
                }
                if (ds.contains("_r")) {//获取_r前面的groupName.
                    groupName = StringUtil.substringBefore(ds, "_r");
                }
                if (groupName != null && rwDses.containsKey(groupName)) {//ds0:r10w10,ds2:r0w0
                    String rwRule = rwDses.get(groupName);
                    String[] splits = rwRule.split(",");
                    for (int i = 0; i < splits.length; i++) {
                        String[] splits1 = splits[i].split(":");
                        String physicDs = splits1[0];
                        if (!prefillDataSources.containsKey(physicDs)) {//在物理数据源集合中寻找对应的物理数据源
                            ZDataSource dataSource = dataSourcesMap.get(physicDs);
                            if (dataSource != null) {
                                prefillDataSources.put(physicDs, dataSource);
                            }
                        }
                    }
                }
            }
        }
        return prefillDataSources;
    }

    /**
     * 重置非本zone的数据源访问是抛出异常还是记录日志.
     * @param isThrowException
     */
    public void resetZoneDsThrowException(boolean isThrowException) {
        try {
            if (isThrowException == true) {
                super.getZdalConfig().setZoneError(ZoneError.EXCEPTOIN);
            } else {
                super.getZdalConfig().setZoneError(ZoneError.LOG);
            }
            CONFIG_LOGGER.warn("WARN ## reset the zoneDsThrowExceptoin to " + isThrowException
                               + " ,the appDsName = " + appDsName);
        } catch (Exception e) {
            CONFIG_LOGGER.warn("WARN ## reset zoneDsThrowException has an error:", e);
        }
    }

    /** 
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("getLoginTimeout");
    }

    /** 
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    /** 
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("getLogWriter");
    }

    /** 
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    /** 
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("isWrapperFor");
    }

    /** 
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap");
    }

    public Map<String, ZdalDataSourceKeyWeightRandom> getKeyWeightMapConfig() {
        return keyWeightMapConfig;
    }

    public void setKeyWeightMapConfig(Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapConfig) {
        this.keyWeightMapConfig = keyWeightMapConfig;
    }

    public RuntimeConfigHolder<ZdalRuntime> getRuntimeConfigHolder() {
        return runtimeConfigHolder;
    }

    public SqlDispatcher getWriteDispatcher() {
        return writeDispatcher;
    }

    public SqlDispatcher getReadDispatcher() {
        return readDispatcher;
    }

    public AppRule getAppRule() {
        return appRule;
    }

    public Map<String, String> getKeyWeightConfig() {
        return keyWeightConfig;
    }

    public void setKeyWeightConfig(Map<String, String> keyWeightConfig) {
        this.keyWeightConfig = keyWeightConfig;
    }

    public Map<String, ? extends Object> getRwDataSourcePoolConfig() {
        return rwDataSourcePoolConfig;
    }

    public ZdalConfigListener getZdalConfigListener() {
        return zdalConfigListener;
    }
    
    public void setZdalConfigListener(ZdalConfigListener zdalConfigListener) {
        this.zdalConfigListener = zdalConfigListener;
    }

    public Map<String, ZDataSource> getDataSourcesMap() {
        return dataSourcesMap;
    }

    public Map<String, String> getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Map<String, String> connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public Map<String, List<String>> getShardingRules() {
        return shardingRules;
    }

    public void setShardingRules(Map<String, List<String>> shardingRules) {
        this.shardingRules = shardingRules;
    }

    public String getDrm() {
        return drm;
    }

    public void setDrm(String drm) {
        this.drm = drm;
    }

    public String getLdcDsDrm() {
        return ldcDsDrm;
    }

    public void setLdcDsDrm(String ldcDsDrm) {
        this.ldcDsDrm = ldcDsDrm;
    }

    public boolean isPrefill() {
        return prefill;
    }

    public void setPrefill(boolean prefill) {
        this.prefill = prefill;
    }

    public Map<String, String> getTbNumForEachDb() {
        return tbNumForEachDb;
    }

    public void setTbNumForEachDb(Map<String, String> tbNumForEachDb) {
        this.tbNumForEachDb = tbNumForEachDb;
    }

    public Map<String, String> getTbSuffixPadding() {
        return tbSuffixPadding;
    }

    public void setTbSuffixPadding(Map<String, String> tbSuffixPadding) {
        this.tbSuffixPadding = tbSuffixPadding;
    }

    public boolean isDiffMasterSlaveRule() {
        return diffMasterSlaveRule;
    }

    public void setDiffMasterSlaveRule(boolean diffMasterSlaveRule) {
        this.diffMasterSlaveRule = diffMasterSlaveRule;
    }

    public ZdalSignalResource getDrmResource() {
        return drmResource;
    }

    public ZdalLdcSignalResource getLdcSignalResource() {
        return ldcSignalResource;
    }

    public int getRetryingTimes() {
        return retryingTimes;
    }

    public void setRetryingTimes(int retryingTimes) {
        this.retryingTimes = retryingTimes;
    }

    public Map<String, ? extends Object> getDataSourcePoolConfig() {
        return dataSourcePoolConfig;
    }

    public void setDataSourcePoolConfig(Map<String, ? extends Object> dataSourcePoolConfig) {
        this.dataSourcePoolConfig = dataSourcePoolConfig;
    }

    public void setRwDataSourcePoolConfig(Map<String, ? extends Object> rwDataSourcePoolConfig) {
        this.rwDataSourcePoolConfig = rwDataSourcePoolConfig;
    }

    public void setAppRule(AppRule appRule) {
        this.appRule = appRule;
    }

}
