/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.config.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.xfire.aegis.type.mtom.DataSourceType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.alipay.zdal.client.config.DataSourceConfigType;
import com.alipay.zdal.client.config.DataSourceParameter;
import com.alipay.zdal.client.config.ShardTableRule;
import com.alipay.zdal.client.config.TairDataSourceParameter;
import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZoneError;
import com.alipay.zdal.client.config.exceptions.ZdalConfigException;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.lang.StringUtil;

/**
 * 加载本地xml配置文件中的配置信息.
 * @author 伯牙
 * @version $Id: ParseXmlConfigUtils.java, v 0.1 2012-11-17 下午4:06:21 Exp $
 */
public class ZdalConfigParserUtils {
    private static final String FAILOVER_MASTER            = "master";

    private static final String FAILOVER_FAILOVER          = "failover";

    private static final String NAME                       = "name";
    private static final String ZONE_ERROR                 = "zoneError";
    private static final String ZONE_DS                    = "zoneDs";
    private static final String DATABASE_TYPE              = "dataBaseType";
    private static final String DATASOURCE_TYPE            = "dataSourceType";
    private static final String DOCUMENT                   = "document";
    private static final String HOTSPOT                    = "hotspot";
    private static final String VERSION                    = "version";

    private static final String APP                        = "App";
    private static final String APPDATASOURCE              = "AppDataSource";
    private static final String DATASOURCE                 = "datasource";

    /** 分库分表相关的标签 */
    private static final String SHARD                      = "shard";
    private static final String SHARD_TABLENAME            = "tableName";
    private static final String SHARD_DBRULES              = "dbRules";
    private static final String SHARD_DBRULE               = "dbRule";
    private static final String SHARD_TABLERULES           = "tableRules";
    private static final String SHARD_TABLERULE            = "tableRule";
    private static final String SHARD_TABLESUFFIX          = "tableSuffix";
    private static final String SHARD_DBINDEX              = "dbIndex";

    private static final String FAILOVER_RULE              = "failoverRule";
    private static final String READWRITE_RULE             = "readwriteRule";

    /** Physic的标签. */
    private static final String JDBCURL                    = "jdbcUrl";
    private static final String USERNAME                   = "userName";
    private static final String PASSWORD                   = "password";
    private static final String DRIVERCLASS                = "driverClass";
    private static final String MAXCONN                    = "maxConn";
    public static final String  MINCONN                    = "minConn";
    private static final String BLOCKINGTIMEOUTMILLIS      = "blockingTimeoutMillis";
    private static final String IDLETIMEOUTMINUTES         = "idleTimeoutMinutes";
    private static final String PREPAREDSTATEMENTCACHESIZE = "preparedStatementCacheSize";
    private static final String QUERYTIMEOUT               = "queryTimeout";
    private static final String SQLVALVE                   = "sqlValve";
    private static final String TABLEVALVE                 = "tableValve";
    private static final String TRANSACTIONVALVE           = "transactionValve";

    /** Tair的标签 */
    private static final String MASTERURL                  = "masterUrl";
    private static final String SLAVEURL                   = "slaveUrl";
    private static final String GROUPNAME                  = "groupName";

    /**
     * 从inputStream中解析配置信息.
     * @param inputStream
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ZdalConfig> parseConfig(InputStream inputStream, String appName,
                                                      String dbmode, String idcName)
                                                                                    throws ZdalConfigException {
        Map<String, ZdalConfig> configs = new HashMap<String, ZdalConfig>();
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document document = saxBuilder.build(inputStream);
            Element appElement = document.getRootElement();
            if (!appElement.getName().equals(APP)) {
                throw new ZdalConfigException(
                    "ERROR ## the rootElement is invalidate,must App Element");
            }
            String zoneError = appElement.getAttributeValue(ZONE_ERROR);
            if (zoneError == null || zoneError.trim().length() == 0) {
                throw new ZdalConfigException("ERROR ## the zoneError is null");
            }
            List<Element> appDataSourceElements = appElement.getChildren(APPDATASOURCE);
            if (appDataSourceElements == null || appDataSourceElements.isEmpty()) {
                throw new ZdalConfigException("ERROR ## the appDataSource-element is empty");
            }
            for (Element appDataSourceElement : appDataSourceElements) {
                ZdalConfig config = new ZdalConfig();
                config.setAppName(appName);
                config.setDbmode(dbmode);
                config.setIdcName(idcName);
                config.setZoneError(ZoneError.convert(zoneError));
                parseAppDataSourceElement(appDataSourceElement, config);

                List<Element> dataSourceElements = appDataSourceElement.getChildren(DATASOURCE);
                if (dataSourceElements == null || dataSourceElements.isEmpty()) {
                    throw new ZdalConfigException("ERROR ## the appDsName = " + appName
                                                  + " has dataSource-element");
                }
                if (dataSourceElements.size() != 1) {
                    throw new ZdalConfigException(
                        "ERROR ## the first level of dataSource-element must have only one");
                }
                Element dataSourceElement = dataSourceElements.get(0);
                String dataSourceType = dataSourceElement.getAttributeValue(DATASOURCE_TYPE).trim();
                if (dataSourceType.equalsIgnoreCase(DataSourceConfigType.SHARD.toString())) {
                    config.setDataSourceConfigType(DataSourceConfigType.SHARD);
                    parseShardDataSources(dataSourceElement, config);
                } else if (dataSourceType.equalsIgnoreCase("RW")) {
                    config.setDataSourceConfigType(DataSourceConfigType.GROUP);
                    parseRwDataSources(dataSourceElement, config);
                } else if (dataSourceType.equalsIgnoreCase("TAIR")) {
                    config.setDataSourceConfigType(DataSourceConfigType.TAIR);
                    parseTairDataSource(dataSourceElement, config);
                } else {
                    throw new ZdalConfigException("ERROR ## the datasource's type = "
                                                  + dataSourceType
                                                  + " is invalidate on the first level");
                }
                configs.put(config.getAppDsName(), config);
            }

            return configs;
        } catch (Exception e) {
            throw new ZdalConfigException(e);
        }
    }

    /**
     * 解析appDataSource-element.
     * @param appDataSourceElement
     * @param zdalConfig
     */
    private static void parseAppDataSourceElement(Element appDataSourceElement,
                                                  ZdalConfig zdalConfig) {
        String appDsName = appDataSourceElement.getAttributeValue(NAME);
        if (appDsName == null || appDsName.trim().length() == 0) {
            throw new ZdalConfigException("ERROR ## the appDsName is null");
        }
        zdalConfig.setAppDsName(appDsName);

        String versionString = appDataSourceElement.getAttributeValue(VERSION);
        if (versionString == null || versionString.trim().length() == 0) {
            throw new ZdalConfigException("ERROR ## the version is null");
        }
        int version = 0;
        try {
            version = Integer.parseInt(versionString);
        } catch (Exception e) {
            throw new ZdalConfigException("ERROR ## the version = " + versionString
                                          + " is not a string", e);
        }
        zdalConfig.setVersion(version);

        String databaseType = appDataSourceElement.getAttributeValue(DATABASE_TYPE);
        if (databaseType == null || databaseType.trim().length() == 0) {
            throw new ZdalConfigException("ERROR ## the databaseType is null");
        }
        zdalConfig.setDbType(DBType.convert(databaseType));

        String zoneDs = appDataSourceElement.getAttributeValue(ZONE_DS);
        if (zoneDs != null && zoneDs.trim().length() > 0) {
            String[] zoneDses = zoneDs.split(",");
            for (String tmp : zoneDses) {
                zdalConfig.getZoneDs().add(tmp);
            }
        }
        String document = appDataSourceElement.getAttributeValue(DOCUMENT);
        if (document != null && document.trim().length() > 0) {
            try {
                zdalConfig.setDocument(Boolean.parseBoolean(document.trim()));
            } catch (Exception e) {
                zdalConfig.setDocument(false);
            }
        }
        String hotspot = appDataSourceElement.getAttributeValue(HOTSPOT);
        if (StringUtil.isNotBlank(hotspot)) {
            try {
                String[] cacheArray = hotspot.split(",");
                for (int i = 0; i < cacheArray.length; i++) {
                    String item[] = cacheArray[i].trim().split(":");
                    String tableRuleIndex = item[0];
                    String dbRuleIndex = item[1];
                    zdalConfig.getHotspots().put(Integer.parseInt(tableRuleIndex),
                        Integer.parseInt(dbRuleIndex));
                }
            } catch (Exception e) {
                throw new ZdalConfigException("ERROR ## the hotspot is invalidate,the hotspot = "
                                              + hotspot);
            }
        }
    }

    /**
     * 解析sharding数据源的配置信息.
     * sharding数据源没有分机房属性.
     * @param element
     * @param zdalConfig
     */
    @SuppressWarnings("unchecked")
    private static void parseShardDataSources(Element element, ZdalConfig zdalConfig) {
        List<Element> shardElements = element.getChildren(SHARD);
        for (Element shardElement : shardElements) {
            ShardTableRule shardTableRule = new ShardTableRule();
            shardTableRule.setLogicTableName(shardElement.getChildText(SHARD_TABLENAME).trim());
            String tableSuffix = shardElement.getChildText(SHARD_TABLESUFFIX).trim();
            if (tableSuffix != null && tableSuffix.length() > 0 && !tableSuffix.equals("null")) {//只有在tableSuffix!="null"时才设置
                shardTableRule.setTableSuffix(tableSuffix);
            }
            String dbIndex = shardElement.getChildText(SHARD_DBINDEX).trim();
            if (dbIndex != null && dbIndex.length() > 0 && !dbIndex.equals("null")) {
                shardTableRule.setDbIndex(dbIndex);////只有在dbIndex!="null"时才设置.
            }
            Element dbRulesElement = shardElement.getChild(SHARD_DBRULES);
            List<Element> dbRuleElements = dbRulesElement.getChildren(SHARD_DBRULE);
            for (Element dbRule : dbRuleElements) {
                String dbRuleString = dbRule.getValue().trim();
                if (dbRuleString != null && dbRuleString.length() > 0
                    && !dbRuleString.equals("null")) {//只有在dbRule!="null"时才设置.
                    shardTableRule.getDbRules().add(dbRuleString);
                }
            }

            Element tableRulesElement = shardElement.getChild(SHARD_TABLERULES);
            List<Element> tableRuleElements = tableRulesElement.getChildren(SHARD_TABLERULE);
            for (Element tableRule : tableRuleElements) {
                String tableRuleString = tableRule.getValue().trim();
                if (tableRuleString != null && tableRuleString.length() > 0
                    && !tableRuleString.equals("null")) {//只有在tableRule!="null"时才设置.
                    shardTableRule.getTableRules().add(tableRuleString);
                }
            }
            zdalConfig.getShardTableRules().put(shardTableRule.getLogicTableName(), shardTableRule);
        }
        List<Element> children = element.getChildren(DATASOURCE);
        for (Element child : children) {
            String dataSourceType = child.getAttributeValue(DATASOURCE_TYPE).trim();
            if ("FAILOVER".equalsIgnoreCase(dataSourceType)) {
                parseFailoverElement(child, zdalConfig);
            } else if ("RW".equalsIgnoreCase(dataSourceType)) {
                parseRwDataSources(child, zdalConfig);
            } else {
                throw new ZdalConfigException(
                    "ERROR ## the child of shard must failover/rw datasource");
            }
        }
    }

    /**
     * 解析读写分离的数据源配置信息.
     * @param element
     * @param zdalConfig
     */
    @SuppressWarnings("unchecked")
    private static void parseRwDataSources(Element element, ZdalConfig zdalConfig) {
        String dsName = element.getAttributeValue(NAME).trim();
        String rwRule = element.getAttributeValue(READWRITE_RULE).trim();
        zdalConfig.getReadWriteRules().put(dsName, rwRule);
        List<Element> children = element.getChildren(DATASOURCE);
        for (Element child : children) {
            parsePhysicElement(child, zdalConfig, true, null);
        }
    }

    /**
     * 解析tair的数据源配置信息.
     * @param element
     * @param zdalConfig
     */
    private static void parseTairDataSource(Element element, ZdalConfig zdalConfig) {
        String dsName = element.getAttributeValue(NAME).trim();
        String masterUrl = element.getChildText(MASTERURL).trim();
        String slaveUrl = element.getChildText(SLAVEURL).trim();
        String groupName = element.getChildText(GROUPNAME).trim();
        TairDataSourceParameter dataSourceParameter = new TairDataSourceParameter();
        dataSourceParameter.setMasterUrl(masterUrl);
        dataSourceParameter.setSlaveUrl(slaveUrl);
        dataSourceParameter.setGroupName(groupName);
        zdalConfig.getTairDataSourceParameters().put(dsName, dataSourceParameter);
    }

    @SuppressWarnings("unchecked")
    private static void parseFailoverElement(Element element, ZdalConfig zdalConfig) {
        String failoverRule = element.getAttributeValue(FAILOVER_RULE).trim();
        if (FAILOVER_MASTER.equals(failoverRule)) {
            List<Element> childs = element.getChildren(DATASOURCE);
            for (Element child : childs) {
                parsePhysicElement(child, zdalConfig, false, zdalConfig
                    .getMasterLogicPhysicsDsNames());
            }
        } else if (FAILOVER_FAILOVER.equals(failoverRule)) {
            List<Element> childs = element.getChildren(DATASOURCE);
            for (Element child : childs) {
                parsePhysicElement(child, zdalConfig, false, zdalConfig
                    .getFailoverLogicPhysicsDsNames());
            }
        } else {
            throw new ZdalConfigException(
                "ERROR ## the failoverRule is invalidate,the failoverRule = " + failoverRule);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parsePhysicElement(Element childElement, ZdalConfig zdalConfig, boolean rw,
                                           Map<String, String> map) {
        String dataSourceType = childElement.getAttributeValue(DATASOURCE_TYPE).trim();
        if (dataSourceType == null || dataSourceType.length() == 0) {
            throw new ZdalConfigException("ERROR ## this not physics datasource");
        }
        String dsName = childElement.getAttributeValue(NAME).trim();
        String jdbcUrl = childElement.getChildText(JDBCURL).trim();
        String userName = childElement.getChildText(USERNAME).trim();
        String password = childElement.getChildText(PASSWORD).trim();
        String minConn = childElement.getChildText(MINCONN).trim();
        String maxConn = childElement.getChildText(MAXCONN).trim();
        String driverClass = childElement.getChildText(DRIVERCLASS).trim();
        String blockingTimeoutMillis = childElement.getChildText(BLOCKINGTIMEOUTMILLIS).trim();
        String idleTimeoutMinutes = childElement.getChildText(IDLETIMEOUTMINUTES).trim();
        String preparedStatementCacheSize = childElement.getChildText(PREPAREDSTATEMENTCACHESIZE)
            .trim();
        String queryTimeout = childElement.getChildText(QUERYTIMEOUT).trim();
        String sqlValve = childElement.getChildText(SQLVALVE).trim();
        String transactionValve = childElement.getChildText(TRANSACTIONVALVE).trim();
        String tableValve = childElement.getChildText(TABLEVALVE).trim();
        if (tableValve.equalsIgnoreCase("null")) {//可能从zdataconsole或者本地获取tableValve值为null，这里做一个转化，后续会把valve的功能去掉.
            tableValve = "";
        }
        DataSourceParameter dataSourceParameter = new DataSourceParameter();
        dataSourceParameter.setJdbcUrl(jdbcUrl);
        dataSourceParameter.setUserName(userName);
        dataSourceParameter.setPassword(password);
        try {
            dataSourceParameter.setMaxConn(Integer.parseInt(maxConn));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's minConn from localFile is a invalidate number,the minConn = "
                        + minConn);
        }
        try {
            dataSourceParameter.setMinConn(Integer.parseInt(minConn));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's maxConn from localFile is a invalidate number,the maxConn = "
                        + maxConn);
        }
        dataSourceParameter.setDriverClass(driverClass);
        try {
            dataSourceParameter.setBlockingTimeoutMillis(Integer.parseInt(blockingTimeoutMillis));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's blockingTimeoutMillis from localFile is a invalidate number,the blockingTimeoutMillis = "
                        + blockingTimeoutMillis);
        }
        try {
            dataSourceParameter.setIdleTimeoutMinutes(Integer.parseInt(idleTimeoutMinutes));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's idleTimeoutMinutes from localFile is a invalidate number,the idleTimeoutMinutes = "
                        + idleTimeoutMinutes);
        }
        try {
            dataSourceParameter.setPreparedStatementCacheSize(Integer
                .parseInt(preparedStatementCacheSize));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's preparedStatementCacheSize from localFile is a invalidate number,the preparedStatementCacheSize = "
                        + preparedStatementCacheSize);
        }
        try {
            dataSourceParameter.setQueryTimeout(Integer.parseInt(queryTimeout));
        } catch (Exception e) {
            throw new ZdalConfigException(
                "ERROR ## the datasource's queryTimeout from localFile is a invalidate number,the queryTimeout = "
                        + queryTimeout);
        }
        dataSourceParameter.setSqlValve(sqlValve);
        dataSourceParameter.setTransactionValve(transactionValve);
        dataSourceParameter.setTableValve(tableValve);
        zdalConfig.getDataSourceParameters().put(dsName, dataSourceParameter);
        if (!rw) {
            List<Element> childs = childElement.getChildren(DATASOURCE);
            if (childs.isEmpty()) {
                throw new ZdalConfigException(
                    "ERROR ## the failover-physics must have logic dataSource");
            } else {
                for (Element child : childs) {
                    String dataSourceType1 = child.getAttributeValue(DATASOURCE_TYPE).trim();
                    
                    String name = child.getAttributeValue(NAME).trim();
                    map.put(name, dsName);//逻辑数据源和物理数据源.
                }
            }
        }
    }

}
