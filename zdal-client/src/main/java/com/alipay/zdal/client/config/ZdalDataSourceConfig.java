package com.alipay.zdal.client.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.alipay.zdal.client.config.utils.DbmodeUtils;
import com.alipay.zdal.client.config.utils.ZoneUtils;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.lang.StringUtil;

/**
 * mysql/oracle/tair各种数据源的配置加载.
 * @author 伯牙
 * @version $Id: ZdalDataSourceConfig.java, v 0.1 2013-1-18 下午03:48:28 Exp $
 */
public abstract class ZdalDataSourceConfig {

    /** 专门打印推送结果的log信息. */
    protected static final Logger CONFIG_LOGGER = Logger
                                                    .getLogger(Constants.CONFIG_LOG_NAME_LOGNAME);

    /**app名称  */
    protected String              appName;

    /** 数据源的名称. */
    protected String              appDsName     = null;

    /** 数据库环境参数,先从bean的参数里面获取,再从System.getProperty中获取,如果都不存在,就抛出异常(都默认转化成小写字母). */
    protected String              dbmode;

    /** 逻辑机房参数,从System.getProperty中获取,如果都不存在,就设置默认的gzone(都默认转化成小写字母). */
    protected String              zone;

    /** zdataconsole的webservice地址. */
    protected String              zdataconsoleUrl;

    /** 本地配置文件存放的路径. */
    protected String              configPath;

    /** 数据源的配置信息. */
    protected ZdalConfig          zdalConfig    = null;

    /** 用于标示ZdalDataSource是否初始化完成.*/
    protected AtomicBoolean       inited        = new AtomicBoolean(false);

    protected DBType              defaultDbType;

    protected DataSourceConfigType      dbConfigType  = null;
    
    /**
    * 检验配置项.
    */
    protected void checkParameters() {
        if (StringUtil.isBlank(appName)) {
            throw new IllegalArgumentException("ERROR ## the appName is null");
        }
        CONFIG_LOGGER.warn("WARN ## the appName = " + this.appName);

        if (StringUtil.isBlank(appDsName)) {
            throw new IllegalArgumentException("ERROR ## the appDsName is null");
        }
        CONFIG_LOGGER.warn("WARN ## the appDsName = " + this.appDsName);

        this.dbmode = DbmodeUtils.getDbmode(this.dbmode);
        CONFIG_LOGGER.warn("WARN ## the dbmode = " + this.dbmode);

        this.zone = ZoneUtils.getZone(this.zone);
        if (StringUtil.isBlank(this.zone)) {
            throw new IllegalArgumentException("ERROR ## the zone is null");
        }
        CONFIG_LOGGER.warn("WARN ## the zone = " + this.zone);

        if (StringUtil.isBlank(configPath)) {
            throw new IllegalArgumentException("ERROR ## the configPath is null");
        }
        CONFIG_LOGGER.warn("WARN ## the configPath = " + this.configPath);
    }

    /**
     * 应用使用时，必须先调用initZdalDataSource方法来初始化.
     */
    protected void initZdalDataSource() {
        long startInit = System.currentTimeMillis();
        initDrmListener();
        this.inited.set(true);
        CONFIG_LOGGER.warn("WARN ## init ZdalDataSource [" + appDsName + "] success,cost "
                           + (System.currentTimeMillis() - startInit) + " ms");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("appName:").append(appName);
        sb.append(" appDsName:").append(appDsName);
        sb.append(" dbmode:").append(dbmode);
        sb.append(" zone:").append(zone);
        sb.append(" zdataconsoleUrl:").append(zdataconsoleUrl);
        sb.append(" configPath:").append(configPath);
        sb.append("}");
        return sb.toString();
    }

    /**
     * 初始化drm资源.
     */
    protected abstract void initDrmListener();

    /**
     * 初始化mysql/oracle/tair的数据源.
     */
    protected abstract void initDataSources(ZdalConfig zdalConfig);

    public ZdalConfig getZdalConfig() {
        return zdalConfig;
    }

    //下面的get/set对应的参数需要在初始化的时候设置.

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDsName() {
        return appDsName;
    }

    public void setAppDsName(String appDsName) {
        this.appDsName = appDsName;
    }

    public String getDbmode() {
        return dbmode;
    }

    public void setDbmode(String dbmode) {
        this.dbmode = dbmode.toLowerCase();
    }

    public String getZdataconsoleUrl() {
        return zdataconsoleUrl;
    }

    public void setZdataconsoleUrl(String zdataconsoleUrl) {
        this.zdataconsoleUrl = zdataconsoleUrl;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone.toLowerCase();
    }

}
