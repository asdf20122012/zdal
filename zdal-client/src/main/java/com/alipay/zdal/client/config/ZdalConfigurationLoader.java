/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alipay.zdal.client.config.bean.AppDataSourceBean;
import com.alipay.zdal.client.config.bean.PhysicalDataSourceBean;
import com.alipay.zdal.client.config.bean.ZdalAppBean;
import com.alipay.zdal.client.config.exceptions.ZdalConfigException;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.lang.StringUtil;

/**
 * <p>
 * A single loader to load Zdal configurations via Web Service or local
 * directory in order to initialize Zdal data source context. Afterward, it
 * holds all zdal configurations across applications distinguish by unique id
 * consist of appName + dbMode + zone
 * </p>
 * 
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 * 
 */
public class ZdalConfigurationLoader {

    private static final String                  CONFIGURATION_RULE = "RULE";

    private static final String                  CONFIGURATION_DS   = "DS";

    private static final Logger                  log                = Logger
                                                                        .getLogger(Constants.CONFIG_LOG_NAME_LOGNAME);

    private static final ZdalConfigurationLoader instance           = new ZdalConfigurationLoader();

    private Map<String, ZdalAppBean>             appBeanMap         = new HashMap<String, ZdalAppBean>();

    private Map<String, ZdalConfig>              zdalConfigMap      = null;

    public static String                         localConfigDir;

    static {
        localConfigDir = System.getProperty("user.home");
        if (!localConfigDir.endsWith(File.separator)) {// 如果不是以文件的分隔符结尾，就补充文件分隔符.
            localConfigDir += File.separator;
        }
        if (!localConfigDir.startsWith(File.separator)) {
            localConfigDir = File.separator + localConfigDir;
        }
        localConfigDir += "conf" + File.separator + "zdal";
        log.warn("Local configuration directory at " + localConfigDir);
    }

    public static ZdalConfigurationLoader getInstance() {
        return instance;
    }

    /**
     * Load Zdal configuration context via Spring XmlApplicationContext when the
     * zdal has not been loaded up. If application's Zdal configuration has been
     * loaded up, just fetch the zdal config from the single configuration map.
     * 
     * @param appName
     * @param dbMode
     * @param idcName
     * @param appDsName
     * @param zdataconsoleUrl
     * @param configPath
     * @return
     */
    public synchronized ZdalConfig getZdalConfiguration(String appName, String dbMode,
                                                        String appDsName, String configPath) {
        ZdalConfig zdalConfig = null;
        if (StringUtil.isEmpty(appName) || StringUtil.isEmpty(dbMode)
            || StringUtil.isEmpty(idcName) || StringUtil.isEmpty(appDsName)) {
            return null;
        }
        String appDsUniqueId = generateAppDsUniqueId(appName, dbMode, idcName, appDsName);
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            zdalConfig = zdalConfigMap.get(appDsUniqueId);
            if (null != zdalConfig) {
                return zdalConfig;
            }
        }
        List<String> zdalConfigurationFilePathList = new ArrayList<String>();
        // Pull DS configuration from Zdal Console
        String cfgContent = pullZdalConfigurationFromConsole(appName, dbMode, idcName,
            zdataconsoleUrl, CONFIGURATION_DS);
        String filePath = localizeZdalConfiguration(appName, dbMode, idcName,
            Constants.LOCAL_CONFIG_DS, cfgContent);
        File configurationFile = null;
        if (StringUtil.isEmpty(filePath)) {
            configurationFile = new File(configPath, MessageFormat.format(
                Constants.LOCAL_CONFIG_FILENAME_SUFFIX, appName, dbMode, idcName));
            if (configurationFile.exists() && configurationFile.isFile()) {
                zdalConfigurationFilePathList.add("file:" + configurationFile.getAbsolutePath());
            }
        } else {
            zdalConfigurationFilePathList.add("file:" + filePath);
        }
        configurationFile = null;
        // Pull Rule configuration from Zdal Console
        cfgContent = pullZdalConfigurationFromConsole(appName, dbMode, idcName, zdataconsoleUrl,
            CONFIGURATION_RULE);
        filePath = localizeZdalConfiguration(appName, dbMode, idcName, Constants.LOCAL_CONFIG_RULE,
            cfgContent);
        if (StringUtil.isEmpty(filePath)) {
            configurationFile = new File(configPath, MessageFormat.format(
                Constants.LOCAL_RULE_CONFIG_FILENAME_SUFFIX, appName, dbMode, idcName));
            if (configurationFile.exists() && configurationFile.isFile()) {
                zdalConfigurationFilePathList.add("file:" + configurationFile.getAbsolutePath());
            }
        } else {
            zdalConfigurationFilePathList.add("file:" + filePath);
        }
        configurationFile = null;
        if (zdalConfigurationFilePathList.isEmpty()) {
            throw new ZdalConfigException(
                "ERROR ## There is no local Zdal configuration files for " + appName
                        + " to initialize ZdalDataSource.");
        }
        loadZdalConfigurationContext(zdalConfigurationFilePathList
            .toArray(new String[zdalConfigurationFilePathList.size()]), appName, dbMode, idcName);
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            return zdalConfigMap.get(appDsUniqueId);
        } else {
            return null;
        }
    }

    /**
     * It is pretty much as same as getZdalConfiguration, but it only load
     * configuration from local which given parameter configPath.
     * 
     * @param appName
     * @param dbMode
     * @param idcName
     * @param appDsName
     * @param configPath
     *            Given path to load applications's data source and rule
     *            configuraiton files.
     * @return
     */
    public synchronized ZdalConfig getZdalConfigurationFromLocal(String appName, String dbMode,
                                                                 String idcName, String appDsName,
                                                                 String configPath) {
        ZdalConfig zdalConfig = null;
        if (StringUtil.isEmpty(appName) || StringUtil.isEmpty(dbMode)
            || StringUtil.isEmpty(idcName) || StringUtil.isEmpty(appDsName)) {
            return null;
        }
        String appDsUniqueId = generateAppDsUniqueId(appName, dbMode, idcName, appDsName);
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            zdalConfig = zdalConfigMap.get(appDsUniqueId);
            if (null != zdalConfig) {
                return zdalConfig;
            }
        }
        List<String> zdalConfigurationFilePathList = new ArrayList<String>();
        File configurationFile = new File(configPath, MessageFormat.format(
            Constants.LOCAL_CONFIG_FILENAME_SUFFIX, appName, dbMode, idcName));
        if (configurationFile.exists() && configurationFile.isFile()) {
            zdalConfigurationFilePathList.add(configurationFile.getAbsolutePath());
        }
        configurationFile = new File(configPath, MessageFormat.format(
            Constants.LOCAL_RULE_CONFIG_FILENAME_SUFFIX, appName, dbMode, idcName));
        if (configurationFile.exists() && configurationFile.isFile()) {
            zdalConfigurationFilePathList.add(configurationFile.getAbsolutePath());
        }
        if (zdalConfigurationFilePathList.isEmpty()) {
            throw new ZdalConfigException(
                "ERROR ## There is no local Zdal configuration files for " + appName
                        + " to initialize ZdalDataSource.");
        }
        loadZdalConfigurationContext(zdalConfigurationFilePathList
            .toArray(new String[zdalConfigurationFilePathList.size()]), appName, dbMode, idcName);
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            return zdalConfigMap.get(appDsUniqueId);
        } else {
            return null;
        }
    }

    /**
     * Load Zdal configuration with local Zdal configuration files include a
     * data source definition and a rule definition then return the
     * configurations of an application
     * 
     * @param appName
     * @param dbMode
     * @param idcName
     * @param appDsName
     * @param filePaths
     * @return
     */
    public Map<String, ZdalConfig> getZdalConfiguration(String appName, String dbMode,
                                                        String idcName, String appDsName,
                                                        String[] filePaths) {
        Map<String, ZdalConfig> zdalConfigMap = null;
        if (null != appBeanMap) {
            ZdalAppBean appBean = appBeanMap.get(generateAppUniqueId(appName, dbMode, idcName));
            if (null == appBean || !dbMode.equals(appBean.getDbmode())
                || !idcName.equals(appBean.getIdcName())) {
                throw new ZdalConfigException("ERROR ## The configured dbMode is "
                                              + appBean.getDbmode() + " and idcName "
                                              + appBean.getIdcName()
                                              + " are not match with requested dbMode: " + dbMode
                                              + " and idcName: " + idcName);
            }
        } else {
            loadZdalConfigurationContext(filePaths, appName, dbMode, idcName);
        }
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            return zdalConfigMap;
        }
        return zdalConfigMap;
    }

    /**
     * For testing, we can use this method to load configuration from local
     * files instead of using zdal console.
     * 
     * @param fileNames
     * @param appName
     * @param dbMode
     * @param idcName
     * @return
     */
    public Map<String, ZdalConfig> getZdalConfiguration(String[] fileNames, String appName,
                                                        String dbMode, String idcName) {
        Map<String, ZdalConfig> zdalConfigMap = null;
        if (null != appBeanMap) {
            ZdalAppBean appBean = appBeanMap.get(generateAppUniqueId(appName, dbMode, idcName));
            if (null == appBean || !dbMode.equals(appBean.getDbmode())
                || !idcName.equals(appBean.getIdcName())) {
                throw new ZdalConfigException("ERROR ## The configured dbMode is "
                                              + appBean.getDbmode() + " and idcName "
                                              + appBean.getIdcName()
                                              + " are not match with requested dbMode: " + dbMode
                                              + " and idcName: " + idcName);
            }
        } else {
            loadZdalConfigurationContext(fileNames, appName, dbMode, idcName);
        }
        if (!MapUtils.isEmpty(zdalConfigMap)) {
            return zdalConfigMap;
        }
        return zdalConfigMap;
    }

    /**
     * Localize the fetched Zdal configuration from Zdal Console into local
     * folder. File name as app-dbmode-idcname-ds.xml or
     * app-dbmode-idcname-rule.xml
     * 
     * @param appName
     * @param dbmode
     * @param idcName
     * @param type
     *            defined either DS or Rule
     * @param configContent
     *            configuration pull from zdal console
     * @return
     */
    protected String localizeZdalConfiguration(final String appName, final String dbmode,
                                               final String idcName, int type,
                                               final String configContent) {
        String configFileName = null;
        if (StringUtil.isEmpty(configContent)) {
            log
                .warn("WARN ## synced the latest configuration is empty, Zdal started to utilize local configuration to initialize.");
        }
        if (Constants.LOCAL_CONFIG_DS == type) {
            configFileName = MessageFormat.format(Constants.LOCAL_CONFIG_FILENAME_SUFFIX, appName,
                dbmode, idcName);
        } else {
            configFileName = MessageFormat.format(Constants.LOCAL_RULE_CONFIG_FILENAME_SUFFIX,
                appName, dbmode, idcName);
        }
        File configDir = new File(localConfigDir);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File configFile = new File(localConfigDir, configFileName);
        try {
            if (configFile.exists()) {
                if (StringUtil.isEmpty(configContent)) { // Load local
                    // configuration
                    // file
                    return configFile.getAbsolutePath();
                } else {
                    configFile.delete();// 先删除原来的文件.
                    configFile = null;
                    configFile = new File(localConfigDir, configFileName);
                    configFile.createNewFile();
                }
            } else {
                if (StringUtil.isEmpty(configContent)) { // return null as there
                    // is no file
                    return null;
                }
                configFile.createNewFile();
            }
            Writer writer = null;
            try {
                writer = new FileWriter(configFile);
                writer.write(configContent);
                log.info("WARN ## synced the latest config to local file " + configFile
                         + " succesfully.");
            } catch (Exception e) {
                log.error(
                    "ERROR ## flush config from zdataconsole to local file has an error,the fileName = "
                            + configFileName, e);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (Exception e) {
            log.error("ERROR ## sync the newest config to local file has an error,the appName = "
                      + appName + ",the dbmode = " + dbmode + ",the idcName = " + idcName, e);
        }
        return configFile.getAbsolutePath();
    }

    /**
     * 
     * @param appName
     * @param dbmode
     * @param idcName
     * @param zdataconsoleUrl
     * @param parameter
     * @return
     */
    protected String pullZdalConfigurationFromConsole(final String appName, final String dbmode,
                                                      final String idcName,
                                                      final String zdataconsoleUrl, String parameter) {
        String configContent = null;
        Service service = new ObjectServiceFactory().create(ZdalConfigService.class);
        XFireProxyFactory factory = new XFireProxyFactory();
        ZdalConfigService configService = null;
        try {
            configService = (ZdalConfigService) factory.create(service,
                zdataconsoleUrl + Constants.WERBSERVICE_URL_SUFFIX);
            Client client = null;
            for (int i = 0; i < 3; i++) {// 重试3次（尽管xfire内部已经重试了3次），每次间隔1秒.
                try {
                    client = Client.getInstance(configService);
                    client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, "5000");// 设置socket请求通信的超时时间
                    /*
                     * client.setProperty(CommonsHttpMessageSender.HTTP_CONNECTION_TIMEOUT
                     * ,
                     * CommonsHttpMessageSender.HTTP_CONNECTION_MANAGER_TIMEOUT,
                     * "10000");
                     */
                    configContent = configService.getAppDsConfig(appName, dbmode, idcName);// 默认会重试3次.
                    break;
                } catch (Exception e) {
                    log
                        .warn("WARN ## get the config info from zdataconsole has an error, the appName = "
                              + appName
                              + ", the dbmode = "
                              + dbmode
                              + ", the idcName = "
                              + idcName
                              + e.getMessage());
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e1) {
                        log.warn("WARN ## the thread sleep is interrupted ", e);
                    }
                } finally {
                    if (client != null) {
                        client.close();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("WARN ## init the xfire-ws for zdalconfigService has an error", e);
        }
        return configContent;
    }

    /**
     * 
     * @param fileNames
     * @param appName
     * @param dbMode
     * @param idcName
     */
    protected synchronized void loadZdalConfigurationContext(String[] fileNames, String appName,
                                                             String dbMode, String idcName) {
        // Map<String, ZdalConfig> zdalConfigMap = null;
        try {
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(fileNames);
            if (null == ctx.getBean(appName)) {
                throw new ZdalConfigException("ERROR ## It must has at least one app bean in "
                                              + appName + " Zdal configuration file ");
            }
            ZdalAppBean appBean = (ZdalAppBean) ctx.getBean(appName);
            if (CollectionUtils.isEmpty(appBean.getAppDataSourceList())) {
                throw new ZdalConfigException(
                    "ERROR ## It must has at least one appDataSource bean in " + appName
                            + " ZdalAppBean");
            }
            if (!dbMode.equals(appBean.getDbmode()) || !idcName.equals(appBean.getIdcName())) {
                throw new ZdalConfigException("ERROR ## The configured dbMode is "
                                              + appBean.getDbmode() + " and idcName "
                                              + appBean.getIdcName()
                                              + " are not match with requested dbMode: " + dbMode
                                              + " and idcName: " + idcName);
            }
            if (null == zdalConfigMap) {
                zdalConfigMap = new HashMap<String, ZdalConfig>();
            }
            String appDsUniqueId = null;
            appBeanMap.put(generateAppUniqueId(appName, dbMode, idcName), appBean);
            for (AppDataSourceBean appDataSourceBean : appBean.getAppDataSourceList()) {
                appDsUniqueId = generateAppDsUniqueId(appName, dbMode, idcName, appDataSourceBean
                    .getAppDataSourceName());
                zdalConfigMap.put(appDsUniqueId, populateZdalConfig(appBean, appDataSourceBean));
            }
        } catch (Exception e) {
            StringBuilder stb = new StringBuilder();
            stb.append("Error### Zdal failed to load Zdal datasource and rule context with files ");
            for (String fileName : fileNames) {
                stb.append(fileName).append(", ");
            }
            stb.append(" when Zdal was loading them.");
            log.error(stb.toString(), e);
            throw new ZdalConfigException(e);
        }
    }

    /**
     * 
     * @param appBean
     * @param appDataSourceBean
     * @return
     */
    protected ZdalConfig populateZdalConfig(ZdalAppBean appBean, AppDataSourceBean appDataSourceBean) {
        ZdalConfig config = new ZdalConfig();
        config.setAppName(appBean.getAppName());
        config.setDbmode(appBean.getDbmode());
        config.setIdcName(appBean.getIdcName());
        config.setZoneError(ZoneError.convert(appDataSourceBean.getZoneError()));
        config.setAppDsName(appDataSourceBean.getAppDataSourceName());
        config.setDbType(DBType.convert(appDataSourceBean.getDataBaseType()));
        // Set Rule
        config.setAppRootRule(appDataSourceBean.getAppRule());
        config.setZoneDs(appDataSourceBean.getZoneDSSet());
        Map<String, DataSourceParameter> dataSourceParameterMap = new HashMap<String, DataSourceParameter>();
        for (PhysicalDataSourceBean physicalDataSource : appDataSourceBean
            .getPhysicalDataSourceSet()) {
            dataSourceParameterMap.put(physicalDataSource.getName(), DataSourceParameter
                .valueOf(physicalDataSource));
            if (null != physicalDataSource.getLogicDbNameSet()
                && !physicalDataSource.getLogicDbNameSet().isEmpty()) {
                for (String logicDBName : physicalDataSource.getLogicDbNameSet()) {
                    if (PhysicalDataSourceBean.FAILOVER_MASTER.equals(physicalDataSource
                        .getFailoverRule())) {
                        config.getMasterLogicPhysicsDsNames().put(logicDBName,
                            physicalDataSource.getName());
                    } else {
                        config.getFailoverLogicPhysicsDsNames().put(logicDBName,
                            physicalDataSource.getName());
                    }
                }
            }
        }
        config.setReadWriteRules(appDataSourceBean.getGroupDataSourceRuleMap());
        config.setDataSourceParameters(dataSourceParameterMap);

        DataSourceConfigType dsType = DataSourceConfigType
            .typeOf(appDataSourceBean.getConfigType());
        if (null == dsType) {
            throw new ZdalConfigException("ERROR ## the datasource's type = "
                                          + appDataSourceBean.getDataBaseType()
                                          + " is not one of ATOM, GROUP, SHARD,SHARDGOURP,FAILOVER");
        }
        config.setDataSourceConfigType(dsType);
        switch (dsType) {
            case ATOM:
                break;
            case GROUP:
                populateGroupDataSourceConfiguration(config, appDataSourceBean);
                break;
            case SHARD:
                populateGroupDataSourceConfiguration(config, appDataSourceBean);
                if (null != config.getReadWriteRules() && !config.getReadWriteRules().isEmpty()) {
                    config.setDataSourceConfigType(DataSourceConfigType.SHARD_GROUP);
                } else if (null != config.getMasterLogicPhysicsDsNames()
                           && !config.getMasterLogicPhysicsDsNames().isEmpty()) {
                    config.setDataSourceConfigType(DataSourceConfigType.SHARD_FAILOVER);
                }
                break;
            case SHARD_GROUP:
                populateGroupDataSourceConfiguration(config, appDataSourceBean);
            case SHARD_FAILOVER:
                populateGroupDataSourceConfiguration(config, appDataSourceBean);
                break;
            default:
                break;
        }
        return config;
    }

    private void populateGroupDataSourceConfiguration(ZdalConfig config,
                                                      AppDataSourceBean appDataSourceBean) {
        config.setReadWriteRules(appDataSourceBean.getGroupDataSourceRuleMap());
    }

    /**
     * Generate App unique ID with appName + dbMode + ZoneId
     * 
     * @param names
     * @return
     */
    protected String generateAppUniqueId(String... names) {
        Assert.isTrue(null != names && names.length >= 3);
        StringBuilder stb = new StringBuilder();
        for (String name : names) {
            stb.append(name);
        }
        return stb.toString();
    }

    /**
     * Generate App DataSource Name unique ID with appName + dbMode + ZoneId +
     * appDsName
     * 
     * @param names
     * @return
     */
    protected String generateAppDsUniqueId(String... names) {
        Assert.isTrue(null != names && names.length >= 4);
        StringBuilder stb = new StringBuilder();
        for (String name : names) {
            stb.append(name);
        }
        return stb.toString();
    }
}
