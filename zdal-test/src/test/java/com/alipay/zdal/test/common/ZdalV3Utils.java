/**
 * 
 */
package com.alipay.zdal.test.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alipay.zdal.client.config.DataSourceConfigType;
import com.alipay.zdal.client.config.DataSourceParameter;
import com.alipay.zdal.client.config.ShardTableRule;
import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZdalConfigAdapter;
import com.alipay.zdal.client.config.utils.ZdalConfigParserUtils;
import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.rule.DocumentShardingRule;
import com.alipay.zdal.client.util.TableSuffixGenerator;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.FailoverDBRuleKey;
import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.OracleExceptionSorter;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.rule.config.beans.AppRule;
import com.alipay.zdal.rule.config.beans.ShardRule;
import com.alipay.zdal.rule.config.beans.TableRule;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ZdalV3Utils {

    private static final String PHYSICAL_DS_MAP                    = "physicalDsMap";

    private static final String IDC_NAME2                          = "idcName";

    private static final String DBMODE                             = "dbmode";

    private static final String APP_NAME2                          = "appName";

    private static final String APP_DATA_SOURCE_LIST               = "appDataSourceList";

    private static final String SPRING_ZDAL_DS_TEMPLATE_FTL        = "spring-zdal-ds-template.ftl";

    private static final String SPRING_ZDAL_RULE_TEMPLATE_FTL      = "spring-zdal-rule-template.ftl";

    private static final String MASTER_LOGIC_PHYSICS_DS_NAME_MAP   = "masterLogicPhysicsDsNameMap";

    private static final String FAILOVER_LOGIC_PHYSICS_DS_NAME_MAP = "failoverLogicPhysicsDsNameMap";

    private static final String TEMPLATE_DIR                       = "./src/test/resources/config";

    public static String        zdalV2FilePath                     = "config/trade-dev-rz00a-ds.xml";

    public static String        zdalV3DsFilePath                   = "";

    public static String        zdalV3RuleFilePath                 = "";

    public static String        ZDAL_DS_TEMPALTE                   = "classpath:/config/spring-zdal-ds-template.ftl";

    public static String        ZDAL_RULE_TEMPALTE                 = "classpath:/config/spring-zdal-rule-template.ftl";

    public static final String  APP_NAME                           = "trade";

    public static final String  APP_DS_NAME                        = "";

    public static final String  DB_MODE                            = "";

    public static final String  IDC_NAME                           = "";

    public static final String  AND_SYMBOL                         = "&";
    public static final String  AND_SYMBOL_XML                     = "&amp;";

    /** 逻辑表名master的后缀. */
    private static final String MASTER_RULE                        = "_masterRule";

    /** 逻辑表名的slave的后缀. */
    private static final String SLAVE_RULE                         = "_slaveRule";

    public static void generateV3Configurations(String[] springXmlPath, String v2ConfigPath,
                                                String v3ConfigPath) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(springXmlPath);
        Map<String, ZdalDataSource> dataSourceMap = context.getBeansOfType(ZdalDataSource.class);
        assertNotNull(dataSourceMap);
        ZdalDataSource dataSource = null;
        Map<AppInfo, Map<String, Object>> totalShardRuleMap = new HashMap<AppInfo, Map<String, Object>>();
        Map<String, Object> shardRuleMap = null, dsSettingMap = null;
        Map<String, Map<String, Object>> allDsSettingMap = new HashMap<String, Map<String, Object>>();
        AppInfo appInfo = null;
        for (ZdalDataSource dataSourceV2 : dataSourceMap.values()) {
            dataSource = dataSourceV2;
            appInfo = new AppInfo(dataSource.getAppName(), dataSource.getDbmode(),
                dataSource.getZone());
            if (null == totalShardRuleMap.get(appInfo)) {
                shardRuleMap = new HashMap<String, Object>();
                totalShardRuleMap.put(appInfo, shardRuleMap);
            } else {
                shardRuleMap = totalShardRuleMap.get(appInfo);
            }
            if (null != dataSource) {
                if (null != dataSource.getShardingRules()
                    && !dataSource.getShardingRules().isEmpty()) {
                    shardRuleMap.putAll(dataSource.getShardingRules());
                }
            }
            dsSettingMap = allDsSettingMap.get(dataSource.getAppName());
            if (null == dsSettingMap) {
                dsSettingMap = new HashMap<String, Object>();
                allDsSettingMap.put(dataSource.getAppDsName(), dsSettingMap);
            }
            dsSettingMap.put(ZdalV3Utils.PREFILL, dataSource.isPrefill());
            dsSettingMap
                .put(ZdalV3Utils.DIFF_MASTER_SLAVE_RULE, dataSource.isDiffMasterSlaveRule());
        }
        for (Entry<AppInfo, Map<String, Object>> entry : totalShardRuleMap.entrySet()) {
            ZdalV3Utils.transformConfigurationFromV2ToV3(entry.getKey().appName,
                entry.getKey().dbMode, entry.getKey().zone, entry.getValue(), allDsSettingMap,
                v2ConfigPath, v3ConfigPath);
        }

    }

    public static void generateV3ConfigurationsWithoutInit(String[] springXmlPath, String v2ConfigPath,
                                                String v3ConfigPath) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(springXmlPath);
        Map<String, ZdalDataSource> dataSourceMap = context.getBeansOfType(ZdalDataSource.class);
        assertNotNull(dataSourceMap);
        Map<AppInfo, Map<String, Object>> totalShardRuleMap = new HashMap<AppInfo, Map<String, Object>>();
        Map<String, Object> shardRuleMap = null, dsSettingMap = null;
        Map<String, Map<String, Object>> allDsSettingMap = new HashMap<String, Map<String, Object>>();
        AppInfo appInfo = null;
        
        for (ZdalDataSource dataSource : dataSourceMap.values()) {
            ZdalConfig zdalConfig = ZdalConfigAdapter.getConfig(dataSource.getAppName(), dataSource.getAppDsName(),
                dataSource.getDbmode(), dataSource.getZone(), "", v2ConfigPath, true);
            initDataSources(dataSource, zdalConfig);
            appInfo = new AppInfo(dataSource.getAppName(), dataSource.getDbmode(),
                dataSource.getZone());
            if (null == totalShardRuleMap.get(appInfo)) {
                shardRuleMap = new HashMap<String, Object>();
                totalShardRuleMap.put(appInfo, shardRuleMap);
            } else {
                shardRuleMap = totalShardRuleMap.get(appInfo);
            }
            if (null != dataSource) {
                if (null != dataSource.getShardingRules()
                    && !dataSource.getShardingRules().isEmpty()) {
                    shardRuleMap.putAll(dataSource.getShardingRules());
                }
            }
            dsSettingMap = allDsSettingMap.get(dataSource.getAppName());
            if (null == dsSettingMap) {
                dsSettingMap = new HashMap<String, Object>();
                allDsSettingMap.put(dataSource.getAppDsName(), dsSettingMap);
            }
            dsSettingMap.put(ZdalV3Utils.PREFILL, dataSource.isPrefill());
            dsSettingMap
                .put(ZdalV3Utils.DIFF_MASTER_SLAVE_RULE, dataSource.isDiffMasterSlaveRule());
        }
        for (Entry<AppInfo, Map<String, Object>> entry : totalShardRuleMap.entrySet()) {
            ZdalV3Utils.transformConfigurationFromV2ToV3(entry.getKey().appName,
                entry.getKey().dbMode, entry.getKey().zone, entry.getValue(), allDsSettingMap,
                v2ConfigPath, v3ConfigPath);
        }

    }
    
    /**
     * Generate V3 configurations from V2 configuration file. Currently, it has not supported sharding rule yet.
     * @param appName
     * @param dbMode
     * @param zone
     * @param v2ConfigPath
     * @param v3ConfigPath
     */
    public static void generateV3FilesWithV2DsFile(String appName, String dbMode, String zone,
                                                   String v2ConfigPath, String v3ConfigPath) {
        Map<String, ZdalConfig> configMap = ZdalConfigAdapter.adapterConfig(appName, dbMode, zone,
            "", v2ConfigPath, true);
        if (null != configMap) {
            generateV3FilesWithZdalConfigMap(appName, dbMode, zone, configMap, v2ConfigPath,
                v3ConfigPath);
        }
        configMap = null;
    }

    private static void generateV3FilesWithZdalConfigMap(String appName, String dbMode,
                                                         String zone,
                                                         Map<String, ZdalConfig> configMap,
                                                         String v2ConfigPath, String v3ConfigPath) {
        Map<AppInfo, Map<String, Object>> totalShardRuleMap = new HashMap<AppInfo, Map<String, Object>>();
        Map<String, Object> shardRuleMap = null, dsSettingMap = null;
        Map<String, Map<String, Object>> allDsSettingMap = new HashMap<String, Map<String, Object>>();
        AppInfo appInfo = null;
        for (Entry<String, ZdalConfig> configEntry : configMap.entrySet()) {
            appInfo = new AppInfo(appName, dbMode, zone);
            if (null == totalShardRuleMap.get(appInfo)) {
                shardRuleMap = new HashMap<String, Object>();
                totalShardRuleMap.put(appInfo, shardRuleMap);
            } else {
                shardRuleMap = totalShardRuleMap.get(appInfo);
            }
            /*if( null != configEntry.getValue() ){
            	if(null != configEntry.getValue().getShardTableRules() && !configEntry.getValue().getShardTableRules().isEmpty()){
            		shardRuleMap.putAll(configEntry.getValue().getShardTableRules());
            	}
            }*/
        }
        for (Entry<AppInfo, Map<String, Object>> entry : totalShardRuleMap.entrySet()) {
            ZdalV3Utils.transformConfigurationFromV2ToV3(entry.getKey().appName,
                entry.getKey().dbMode, entry.getKey().zone, entry.getValue(), allDsSettingMap,
                v2ConfigPath, v3ConfigPath);
        }
    }

    public static void transformConfigurationFromV2ToV3(String appName,
                                                        String dbMode,
                                                        String zone,
                                                        Map<String, Object> shardRuleMap,
                                                        Map<String, Map<String, Object>> allDsSettingMap,
                                                        String fileDir, String v3FileDir) {

        InputStream configStream = null;
        Map<String, ZdalConfig> zdalV2ConfigMap = null;

        //        File configFile = new File(zdalV2FilePath);
        try {
            //Step 1. Load Zdal 2.0 configuration up
            String fileName = MessageFormat.format(Constants.LOCAL_CONFIG_FILENAME_SUFFIX, appName,
                dbMode, zone);
            configStream = new FileInputStream(new File(fileDir, fileName));
            assertNotNull(configStream);
            zdalV2ConfigMap = ZdalConfigParserUtils.parseConfig(configStream, APP_NAME, DB_MODE,
                IDC_NAME);
            assertNotNull(zdalV2ConfigMap);
            //Step 2. Put configuration into a map
            Map<String, Object> valueMap = populateDsMap(appName, dbMode, zone, zdalV2ConfigMap,
                allDsSettingMap);

            Map<String, Object> ruleValueMap = populateRuleMap(allDsSettingMap, shardRuleMap,
                zdalV2ConfigMap);

            generateFiles(appName, dbMode, zone, valueMap, ruleValueMap, v3FileDir);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        }
    }

    public static Map<String, Object> populateDsMap(String appName, String dbMode, String zone,
                                                    Map<String, ZdalConfig> zdalV2ConfigMap,
                                                    Map<String, Map<String, Object>> allDsSettingMap) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(APP_DATA_SOURCE_LIST, zdalV2ConfigMap.values());
        valueMap.put(APP_NAME2, appName);
        valueMap.put(DBMODE, dbMode);
        valueMap.put(IDC_NAME2, zone);
        //FOR DS
        Map<String, List<String>> failoverLogicPhysicsDsNameMap = new HashMap<String, List<String>>();
        Map<String, List<String>> masterLogicPhysicsDsNameMap = new HashMap<String, List<String>>();
        List<String> logDsNameList = null;
        valueMap.put(FAILOVER_LOGIC_PHYSICS_DS_NAME_MAP, failoverLogicPhysicsDsNameMap);
        valueMap.put(MASTER_LOGIC_PHYSICS_DS_NAME_MAP, masterLogicPhysicsDsNameMap);

        valueMap.put(PHYSICAL_DS_MAP, new HashSet<DataSourceParameter>());
        boolean prefill = false;
        for (Entry<String, ZdalConfig> configSet : zdalV2ConfigMap.entrySet()) {
            if (null != configSet.getValue().getFailoverLogicPhysicsDsNames()
                && !configSet.getValue().getFailoverLogicPhysicsDsNames().isEmpty()) {
                for (Entry<String, String> dsEntry : configSet.getValue()
                    .getFailoverLogicPhysicsDsNames().entrySet()) {
                    logDsNameList = failoverLogicPhysicsDsNameMap.get(dsEntry.getValue());
                    if (null == logDsNameList) {
                        logDsNameList = new ArrayList<String>();
                        failoverLogicPhysicsDsNameMap.put(dsEntry.getValue(), logDsNameList);
                    }
                    logDsNameList.add(dsEntry.getKey());
                }
            }
            if (null != configSet.getValue().getLogicPhysicsDsNames()
                && !configSet.getValue().getLogicPhysicsDsNames().isEmpty()) {
                for (Entry<String, String> dsEntry : configSet.getValue()
                    .getLogicPhysicsDsNames().entrySet()) {
                    logDsNameList = masterLogicPhysicsDsNameMap.get(dsEntry.getValue());
                    if (null == logDsNameList) {
                        logDsNameList = new ArrayList<String>();
                        masterLogicPhysicsDsNameMap.put(dsEntry.getValue(), logDsNameList);
                    }
                    logDsNameList.add(dsEntry.getKey());
                }
            }
            //Set prefill parameter in then render it in Zdal configuration file
            if (null != allDsSettingMap && !allDsSettingMap.isEmpty()) {
                prefill = allDsSettingMap.get(configSet.getKey()).get(PREFILL) == Boolean.TRUE;
            }
            for (DataSourceParameter parameter : configSet.getValue().getDataSourceParameters()
                .values()) {
                parameter.setJdbcUrl(parameter.getJdbcUrl().replace(AND_SYMBOL, AND_SYMBOL_XML));
                if (prefill) {
                    parameter.setPrefill(true);
                }
                System.out.println("Key: " + configSet.getKey() + "; Prefill: "
                                   + parameter.getPrefill());
            }
        }
        return valueMap;
    }

    public static Map<String, Object> populateRuleMap(Map<String, Map<String, Object>> allDsSettingMap,
                                                      Map<String, Object> shardRuleMap,
                                                      Map<String, ZdalConfig> zdalV2ConfigMap) {
        //FOR RULE
        Map<String, Object> ruleValueMap = new HashMap<String, Object>();
        Map<String, Set<ShardTableRule>> allRuleMap = new HashMap<String, Set<ShardTableRule>>();
        Map<String, Collection<ShardTableRule>> masterRuleMap = new HashMap<String, Collection<ShardTableRule>>();
        Map<String, Collection<ShardTableRule>> slaveRuleMap = new HashMap<String, Collection<ShardTableRule>>();
        Map<String, Collection<ShardTableRule>> readwriteRuleMap = new HashMap<String, Collection<ShardTableRule>>();
        Map<String, DBType> dbTypeMap = new HashMap<String, DBType>();
        Map<String, String> tbSuffixPaddingMap = new HashMap<String, String>();
        Map<String, String> tbNumForEachDbMap = new HashMap<String, String>();
        List<String> appDsNameList = new ArrayList<String>();
        ruleValueMap.put("masterRuleMap", masterRuleMap);
        ruleValueMap.put("slaveRuleMap", slaveRuleMap);
        ruleValueMap.put("readwriteRuleMap", readwriteRuleMap);
        ruleValueMap.put("tbSuffixPaddingMap", tbSuffixPaddingMap);
        ruleValueMap.put("tbNumForEachDbMap", tbNumForEachDbMap);
        ruleValueMap.put("appDsNameList", appDsNameList);
        ruleValueMap.put("dbTypeMap", dbTypeMap);
        ruleValueMap.put("allRuleMap", allRuleMap);
        ruleValueMap.put("shardRuleMap", shardRuleMap);

        Collection<ShardTableRule> shardRuleList = null;
        Collection<ShardTableRule> masterShardRuleList = null;
        Collection<ShardTableRule> failoverShardRuleList = null;
        Set<ShardTableRule> shardRuleSet = null;
        boolean differentMasterSlaveRule = false;
        for (Entry<String, ZdalConfig> configSet : zdalV2ConfigMap.entrySet()) {
            if (!MapUtils.isEmpty(configSet.getValue().getShardTableRules())) {
                appDsNameList.add(configSet.getKey());
                if (null != configSet.getValue().getDataSourceConfigType()) {
                    shardRuleList = configSet.getValue().getShardTableRules().values();
                    if (configSet.getValue().getDataSourceConfigType() == DataSourceConfigType.SHARD_FAILOVER) {
                        //To resolve the configuration compromise in V2, we have to go through all table again
                        //in order to find out which one is failover or master. Its sucks.
                        if (null != allDsSettingMap && !allDsSettingMap.isEmpty()) {
                            differentMasterSlaveRule = allDsSettingMap.get(configSet.getKey()).get(
                                DIFF_MASTER_SLAVE_RULE) == Boolean.TRUE;
                        }
                        if (differentMasterSlaveRule) {
                            for (ShardTableRule tableRule : shardRuleList) {
                                masterShardRuleList = masterRuleMap.get(StringUtil.substringBefore(
                                    tableRule.getLogicTableName(), MASTER_RULE));
                                if (null == masterShardRuleList)
                                    masterShardRuleList = new ArrayList<ShardTableRule>();
                                failoverShardRuleList = masterRuleMap.get(StringUtil
                                    .substringBefore(tableRule.getLogicTableName(), SLAVE_RULE));
                                if (null == failoverShardRuleList)
                                    failoverShardRuleList = new ArrayList<ShardTableRule>();
                                if (tableRule.getLogicTableName().endsWith(MASTER_RULE)) {
                                    masterRuleMap.put(StringUtil.substringBefore(
                                        tableRule.getLogicTableName(), MASTER_RULE),
                                        masterShardRuleList);
                                } else if (tableRule.getLogicTableName().endsWith(SLAVE_RULE)) {
                                    slaveRuleMap.put(StringUtil.substringBefore(
                                        tableRule.getLogicTableName(), SLAVE_RULE),
                                        failoverShardRuleList);
                                }
                            }
                        } else {
                            masterRuleMap.put(configSet.getKey(), shardRuleList);
                            //							slaveRuleMap.put(configSet.getKey(), shardRuleList);
                        }
                    } else if (configSet.getValue().getDataSourceConfigType() == DataSourceConfigType.SHARD_GROUP) {
                        readwriteRuleMap.put(configSet.getKey(), shardRuleList);
                    }
                    shardRuleSet = allRuleMap.get(configSet.getKey());
                    if (null == shardRuleSet) {
                        shardRuleSet = new HashSet<ShardTableRule>();
                        allRuleMap.put(configSet.getKey(), shardRuleSet);
                    }
                    dbTypeMap.put(configSet.getKey(), configSet.getValue().getDbType());
                    for (ShardTableRule rule : shardRuleList) {
                        shardRuleSet.add(rule);
                    }
                }
            }
        }
        boolean diffMasterSlaveRule = false;
        if (diffMasterSlaveRule) {

        }
        return ruleValueMap;
    }

    public static void generateFiles(String appName, String dbMode, String zone,
                                     Map<String, Object> valueMap,
                                     Map<String, Object> ruleValueMap, String fileDir)
                                                                                      throws IOException {
        File templateDir = new File(TEMPLATE_DIR);
        Configuration templateCfg = new Configuration();
        templateCfg.setDirectoryForTemplateLoading(templateDir);
        Template dsTemplate = templateCfg.getTemplate(SPRING_ZDAL_DS_TEMPLATE_FTL);
        Template ruleTemplate = templateCfg.getTemplate(SPRING_ZDAL_RULE_TEMPLATE_FTL);
        String fileName = MessageFormat.format(Constants.LOCAL_CONFIG_FILENAME_SUFFIX, appName,
            dbMode, zone);
        //Step 4. flush out
        File dsFile = new File(fileDir, fileName);
        if (dsFile.exists()) {
            dsFile.delete();
        }
        fileName = MessageFormat.format(Constants.LOCAL_RULE_CONFIG_FILENAME_SUFFIX, appName,
            dbMode, zone);
        File ruleFile = new File(fileDir, fileName);
        if (ruleFile.exists()) {
            ruleFile.delete();
        }
        if (dsFile.createNewFile()) {
            FileWriter dsWriter = new FileWriter(dsFile);
            try {
                dsTemplate.process(valueMap, dsWriter);
                dsWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != dsWriter)
                    dsWriter.close();
            }
        }
        if (ruleFile.createNewFile()) {
            FileWriter ruleWriter = new FileWriter(ruleFile);
            try {
                ruleTemplate.process(ruleValueMap, ruleWriter);
                ruleWriter.flush();
            } catch (Exception e) {

            } finally {
                if (null != ruleWriter)
                    ruleWriter.close();
            }
        }
    }

    public static final String DIFF_MASTER_SLAVE_RULE = "diffMasterSlaveRule";

    public static final String PREFILL                = "prefill";

    public static void initDataSources(ZdalDataSource zdalDataSource, ZdalConfig zdalConfig) {
        if (zdalConfig.getDataSourceParameters() == null
            || zdalConfig.getDataSourceParameters().isEmpty()) {
            throw new ZdalClientException("ERROR ## the datasource parameter is empty");
        }
        ZDataSource zDataSource = null;
        for (Entry<String, DataSourceParameter> entry : zdalConfig.getDataSourceParameters()
            .entrySet()) {
            try {
                zDataSource = new ZDataSource(createDataSourceDO(entry.getValue(),
                    zdalConfig.getDbType(), zdalDataSource.getAppDsName() + "." + entry.getKey()));
                zdalDataSource.getDataSourcesMap().put(entry.getKey(), zDataSource);
            } catch (Exception e) {
                throw new ZdalClientException("ERROR ## create dsName = " + entry.getKey()
                                              + " dataSource failured", e);
            }
        }
        if (zdalConfig.getDataSourceConfigType() == DataSourceConfigType.SHARD_FAILOVER) {
            zdalDataSource.setDataSourcePoolConfig(getFailoverDataSourcePoolConfig(zdalDataSource,
                zdalConfig.getLogicPhysicsDsNames(),
                zdalConfig.getFailoverLogicPhysicsDsNames()));
            if (zdalDataSource.getKeyWeightConfig() == null || zdalDataSource.getKeyWeightConfig().isEmpty()) {
                zdalDataSource.setKeyWeightConfig(getFailoverRules(
                    zdalConfig.getLogicPhysicsDsNames(),
                    zdalConfig.getFailoverLogicPhysicsDsNames()));
            } 
            zdalDataSource.setAppRule(getAppRule(zdalDataSource, zdalConfig.getDbType(),
                convertTableRule(zdalDataSource, zdalConfig.getShardTableRules()), false));
        } else if (zdalConfig.getDataSourceConfigType() == DataSourceConfigType.SHARD_GROUP) {
            zdalDataSource.setRwDataSourcePoolConfig(zdalConfig.getGroupRules());
            zdalDataSource.setAppRule(getAppRule(zdalDataSource, zdalConfig.getDbType(),
                convertTableRule(zdalDataSource, zdalConfig.getShardTableRules()), false));
            if (zdalConfig.isDocument()) {
                DocumentShardingRule.setHotspot(zdalConfig.getHotspots());
            }
        } else if (zdalConfig.getDataSourceConfigType() == DataSourceConfigType.GROUP) {
            zdalDataSource.setRwDataSourcePoolConfig(zdalConfig.getGroupRules());
            
        } else {
            throw new ZdalClientException("ERROR ## the dbConfigType is invalidate");
        }
    }

    private static AppRule getAppRule(ZdalDataSource zdalDataSource, DBType dbType,
                               Map<String, TableRule> zdalTableRule, boolean isRWMode) {
        if (zdalTableRule == null || zdalTableRule.size() == 0) {
            throw new IllegalArgumentException("The zdalTableRule can't null or empty!");
        }
        if (zdalDataSource.isDiffMasterSlaveRule()) {
            ShardRule masterRule = new ShardRule();
            masterRule.setDbtype(dbType.value());
            ShardRule slaveRule = new ShardRule();
            slaveRule.setDbtype(dbType.value());
            Map<String, TableRule> masterTableRules = new HashMap<String, TableRule>();
            Map<String, TableRule> slaveTableRules = new HashMap<String, TableRule>();
            for (Map.Entry<String, TableRule> entry : zdalTableRule.entrySet()) {
                TableRule tableRule = entry.getValue();
                try {
                    tableRule.init();
                } catch (Exception e) {
                    throw new ZdalClientException("Init the tableRule error, the tableName is"
                                                  + entry.getKey());
                }
                if (entry.getKey().endsWith(MASTER_RULE)) {
                    masterTableRules.put(StringUtil.substringBefore(entry.getKey(), MASTER_RULE),
                        tableRule);
                } else if (entry.getKey().endsWith(SLAVE_RULE)) {
                    slaveTableRules.put(StringUtil.substringBefore(entry.getKey(), SLAVE_RULE),
                        tableRule);
                }
            }
            masterRule.setTableRules(masterTableRules);
            slaveRule.setTableRules(slaveTableRules);

            AppRule appRule = new AppRule();
            appRule.setMasterRule(masterRule);
            appRule.setSlaveRule(slaveRule);
            appRule.init();
            return appRule;
        } else {
            ShardRule readwriteRule = new ShardRule();
            readwriteRule.setDbtype(dbType.value());

            for (Map.Entry<String, TableRule> entry : zdalTableRule.entrySet()) {
                TableRule tableRule = entry.getValue();
                try {
                    tableRule.init();
                } catch (Exception e) {
                    throw new ZdalClientException("Init the tableRule error, the tableName is"
                                                  + entry.getKey());
                }
            }
            readwriteRule.setTableRules(zdalTableRule);
            AppRule appRule = new AppRule();
            if (isRWMode) {
                appRule.setReadwriteRule(readwriteRule);
            } else {
                appRule.setMasterRule(readwriteRule);
                appRule.setSlaveRule(readwriteRule);
            }
            appRule.init();
            return appRule;
        }
    }
    
    private static LocalTxDataSourceDO createDataSourceDO(DataSourceParameter parameter,
                                                          DBType dbType, String dsName)
                                                                                       throws Exception {
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
        if (dbType.isMysql()) {
            dsDo.setExceptionSorterClassName(MySQLExceptionSorter.class.getName());
        } else if (dbType.isOracle()) {
            dsDo.setExceptionSorterClassName(OracleExceptionSorter.class.getName());
        } else {
            throw new ZdalClientException("ERROR ## the DbType must be mysql/oracle.");
        }
        return dsDo;
    }

    private static Map<String, String> getFailoverRules(Map<String, String> masterDBMap,
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

    private static Map<String, DataSource> getFailoverDataSourcePoolConfig(ZdalDataSource zdalDataSource,
                                                                    Map<String, String> masterDBMap,
                                                                    Map<String, String> failoverDBMap) {
        Map<String, DataSource> logicDataSourcesMap = new HashMap<String, DataSource>();
        for (Map.Entry<String, String> mEntry : masterDBMap.entrySet()) {
            String key = mEntry.getKey().trim();
            String value = mEntry.getValue().trim();
            logicDataSourcesMap.put(key, zdalDataSource.getDataSourcesMap().get(value));
        }
        for (Map.Entry<String, String> fEntry : failoverDBMap.entrySet()) {
            String key = fEntry.getKey().trim();
            String value = fEntry.getValue().trim();
            logicDataSourcesMap.put(key, zdalDataSource.getDataSourcesMap().get(value));
        }
        return logicDataSourcesMap;
    }

    private static Map<String, TableRule> convertTableRule(ZdalDataSource zdalDataSource, Map<String, ShardTableRule> tableRules) {
        Map<String, TableRule> results = new HashMap<String, TableRule>();
        for (Map.Entry<String, ShardTableRule> entry : tableRules.entrySet()) {
            ShardTableRule tableRule = entry.getValue();
            String dbIndexes = tableRule.getDbIndex();
            List<String> dbRuleArray = tableRule.getDbRules();
            List<String> tbRuleArray = tableRule.getTableRules();
            String tbSuffix = tableRule.getTableSuffix();
            TableRule tableRuleObj = new TableRule();
            tableRuleObj.setDbIndexes(dbIndexes);
            tableRuleObj.setDbRuleArray(dbRuleArray);
            if (!tbRuleArray.isEmpty()) {
                tableRuleObj.setTbRuleArray(tbRuleArray);
            }
            tableRuleObj.setTbSuffix(tbSuffix);
            if (zdalDataSource.getShardingRules().containsKey(entry.getKey())) {//设置全活策略下的表路由规则.
                tableRuleObj.getShardingRules().addAll(zdalDataSource.getShardingRules().get(entry.getKey()));
            }
            if (zdalDataSource.getTbNumForEachDb().containsKey(entry.getKey())) {
                tableRuleObj
                    .setTbNumForEachDb(Integer.parseInt(zdalDataSource.getTbNumForEachDb().get(entry.getKey())));
            }
            if (zdalDataSource.getTbSuffixPadding().containsKey(entry.getKey())) {
                tableRuleObj.setTbSuffixPadding(zdalDataSource.getTbSuffixPadding().get(entry.getKey()));
            }
            results.put(entry.getKey(), tableRuleObj);
        }
        return results;
    }
}

class AppInfo {
    String appName;
    String dbMode;
    String zone;

    public AppInfo(String appName2, String dbmode2, String zone2) {
        this.appName = appName2;
        this.dbMode = dbmode2;
        this.zone = zone2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appName == null) ? 0 : appName.hashCode());
        result = prime * result + ((dbMode == null) ? 0 : dbMode.hashCode());
        result = prime * result + ((zone == null) ? 0 : zone.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppInfo other = (AppInfo) obj;
        if (appName == null) {
            if (other.appName != null)
                return false;
        } else if (!appName.equals(other.appName))
            return false;
        if (dbMode == null) {
            if (other.dbMode != null)
                return false;
        } else if (!dbMode.equals(other.dbMode))
            return false;
        if (zone == null) {
            if (other.zone != null)
                return false;
        } else if (!zone.equals(other.zone))
            return false;
        return true;
    }

}