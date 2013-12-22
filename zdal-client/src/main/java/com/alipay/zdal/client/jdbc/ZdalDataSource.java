/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc;

import java.io.File;

import javax.sql.DataSource;

import com.alipay.cloudengine.kernel.spi.work.ApplicationWorkingAreaAware;
import com.alipay.sofa.common.conf.Configration;
import com.alipay.sofa.service.api.client.ApplicationConfigrationAware;
import com.alipay.zdal.client.config.ZdalConfigurationLoader;
import com.alipay.zdal.client.config.drm.ZdalDrmPushListener;
import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.client.scalable.IDataSourceBindingChangeSupport;
import com.alipay.zdal.common.Closable;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.scalable.ZScalableDataSource;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * Zdal 对外公布的数据源,支持动态调整数据源的配置信息，切换等功能,适用于sofa3.<br>
 * 注意：
 * 1,使用前请务必先设置appName,appDsName的值，并且调用init方法进行初始化,configPath,dbmode,zdataconsoleUrl 由sofa3会自动设置;
 * 2,优先从zdataconsole中获取配置信息，如果获取不到，再从configPath获取配置信息.
 * <bean id="testZdalDataSource" class="com.alipay.zdal.client.jdbc.ZdalDataSource" init-method="init" destroy-method="close">
 *  <property name="appDsName" value="appDsName"/>
 *  <property name="drm" value="drm"/>
 * </bean>
 * 3,zdal数据源需要感知zone，这个参数是通过环境变量中设置的，需要app的deploy.sh、jbossctl.sh脚本设置.
 * 4,drm配置项如果需要推送切换数据源的权重，需要配置上对应的drm标示.
 * @author 伯牙
 * @version $Id: ZdalDataSource.java, v 0.1 2012-11-17 下午4:08:43 Exp $
 */
public class ZdalDataSource extends AbstractZdalDataSource implements DataSource, Closable,
                                                          ApplicationWorkingAreaAware,
                                                          ApplicationConfigrationAware,
                                                          IDataSourceBindingChangeSupport{

    private static final String ZDAL_CONFIG_LOCAL = "zdalConfigLocal";

    /** 是否从本地获取配置.  */
    private boolean             zdalConfigLocal   = false;

    public void init() {
        if (super.inited.get() == true) {
            throw new ZdalClientException("ERROR ## init twice");
        }
        super.checkParameters();
        if (zdalConfigLocal == false) {//从远程zdataconsole中获取配置，就必须有zdataconsoleUrl.
            if (StringUtil.isBlank(zdataconsoleUrl)) {
                throw new IllegalArgumentException(
                    "ERROR ## the zdalConfigLocal = false,but zdataconsoleUrl is null");
            }
        } else {
            zdataconsoleUrl = null;
        }
        CONFIG_LOGGER.warn("WARN ## the zdataconsoleUrl = " + zdataconsoleUrl);
        try {
            this.zdalConfig = ZdalConfigurationLoader.getInstance().getZdalConfiguration(appName, 
            		dbmode, zone, appDsName, zdataconsoleUrl, configPath);
            dbConfigType = zdalConfig.getDataSourceConfigType();
            if (this.zdalConfig == null) {
                throw new ZdalClientException(
                    "ERROR ## Load "+ appName + " and dbMode " + dbmode + " and Zone " +  zone
                + " config is null, please check the config file is exist or not.");
            }
            defaultDbType = zdalConfig.getDbType();
            initDataSources(zdalConfig);
            setZdalConfigListener(new ZdalDrmPushListener(this));
            initDrmListener();
            CONFIG_LOGGER.info("WARN ## init Zdal with zoneDs " + zdalConfig.getZoneDs()
                               + ", the appDsName = " + appDsName);
            this.inited.set(true);
        } catch (Exception e) {
            CONFIG_LOGGER.error("zdal init fail,config:" + this.toString(), e);
            throw new ZdalClientException(e);
        }
    }
    
    public void initializeFromLocal(){
    	if (super.inited.get() == true) {
            throw new ZdalClientException("ERROR ## init twice");
        }
        super.checkParameters();
        if (zdalConfigLocal == false) {//从远程zdataconsole中获取配置，就必须有zdataconsoleUrl.
            if (StringUtil.isBlank(zdataconsoleUrl)) {
                throw new IllegalArgumentException(
                    "ERROR ## the zdalConfigLocal = false,but zdataconsoleUrl is null");
            }
        } else {
            zdataconsoleUrl = null;
        }
        CONFIG_LOGGER.warn("WARN ## the zdataconsoleUrl = " + zdataconsoleUrl);
        try {
            this.zdalConfig = ZdalConfigurationLoader.getInstance().getZdalConfiguration(appName, 
            		dbmode, zone, appDsName, zdataconsoleUrl, configPath);

            if (this.zdalConfig == null) {
                throw new ZdalClientException(
                    "ERROR ## Load "+ appName + " and dbMode " + dbmode + " and Zone " +  zone
                + " config is null, please check the config file is exist or not.");
            }
            defaultDbType = zdalConfig.getDbType();
            initDataSources(zdalConfig);
            initDrmListener();
            CONFIG_LOGGER.info("WARN ## init Zdal with zoneDs " + zdalConfig.getZoneDs()
                               + ", the appDsName = " + appDsName);
            this.inited.set(true);
        } catch (Exception e) {
            CONFIG_LOGGER.error("zdal init fail,config:" + this.toString(), e);
            throw new ZdalClientException(e);
        }
    }
    
    /**
     * Since some of test cases still reference with initV3; thus, keep initV3 only for test purpose.
     */
    public void initV3(){
    	init();
    }
    
    /**
     * 由业务方调用触发预热.
     */
    public void prefillZdal(ZdalPrefill prefill) {
        if (super.inited.get() == false) {
            throw new ZdalClientException("ERROR ## zdatasource not init");
        }
        CONFIG_LOGGER.warn("WARN ## start to prefill zdal,the appDsName = " + getAppDsName());
        startPrefillZdal();
        try {
            prefill.prefill();
        } catch (Exception e) {
            CONFIG_LOGGER.error("ERROR ## prefill zdal has an error,can be ignore,the zone = "
                                + super.getZone() + " the zoneDs = "
                                + super.getZdalConfig().getZoneDs());
        } finally {
            endPrefillZdal();
        }
    }

    /** 
     * @see com.alipay.cloudengine.kernel.api.work.ApplicationWorkingAreaAware#setWorkingArea(java.lang.String)
     */
    public void setWorkingArea(String workingArea) {
        if (StringUtil.isBlank(workingArea)) {
            throw new IllegalArgumentException("ERROR ## the workingArea is not set by sofa3");
        }
        if (!workingArea.endsWith(File.separator)) {//如果不是以文件的分隔符结尾，就补充文件分隔符.
            workingArea += File.separator;
        }
        workingArea += Constants.WORKING_PATH_SUFFIX;//加上数据源配置文件所在的子目录.
        this.configPath = workingArea;
    }

    /** 
     * @see com.alipay.sofa.service.api.client.ApplicationConfigrationAware#setConfigration(com.alipay.sofa.common.conf.Configration)
     */
    public void setConfigration(Configration configration) {
        this.zdataconsoleUrl = configration.getPropertyValue(Constants.ZDATACONSOLE_SERVICE_URL);
        this.dbmode = configration.getPropertyValue(Constants.DBMODE);
        this.appName = configration.getSysAppName();
        try {
            if (configration.getPropertyValue(ZDAL_CONFIG_LOCAL) != null
                && configration.getPropertyValue(ZDAL_CONFIG_LOCAL).trim().length() > 0) {
                this.zdalConfigLocal = Boolean.parseBoolean(configration
                    .getPropertyValue(ZDAL_CONFIG_LOCAL));
            }
        } catch (Exception e) {
            this.zdalConfigLocal = false;
        }
    }

    public boolean isZdalConfigLocal() {
        return zdalConfigLocal;
    }

    public void setZdalConfigLocal(boolean zdalConfigLocal) {
        this.zdalConfigLocal = zdalConfigLocal;
    }

	@Override
	public void reBindingDataSource(String phyicalDbName, String logicalDbName,
			LocalTxDataSourceDO phyicalDbParameters)
			throws DataSourceBindingChangeException {
		if( null == logicalDbName || "".equalsIgnoreCase(logicalDbName) ){
			CONFIG_LOGGER.error("reBindingDataSource failed with an empty logicalDbName.");
			return;
		}
		if( null == phyicalDbName || "".equalsIgnoreCase(phyicalDbName) ){
			CONFIG_LOGGER.error("reBindingDataSource failed with an empty phyicalDbName.");
			return;
		}
		String existedPhyicalDbName = this.getZdalConfig().getMasterLogicPhysicsDsNames().get(logicalDbName);
		if( null == existedPhyicalDbName ){
			CONFIG_LOGGER.error("reBindingDataSource failed with a not exist logicalDbName " + logicalDbName);
		}
		if( null == getZdalConfig().getDataSourceParameters().get(phyicalDbName) && null == phyicalDbParameters ){
			CONFIG_LOGGER.error("reBindingDataSource failed with an empty phyicalDbParameters for DB " + phyicalDbName);
			return;
		}
		if( null == phyicalDbParameters ){
			getZdalConfig().getMasterLogicPhysicsDsNames().put(logicalDbName, phyicalDbName);
		}else{
			try {
				phyicalDbParameters.setDsName(phyicalDbName);
				ZDataSource zDataSource = new ZScalableDataSource(phyicalDbParameters);
				this.getDataSourcesMap().put(phyicalDbName, zDataSource);
				//Put the dataSource in Map first
				getZdalConfig().getMasterLogicPhysicsDsNames().put(logicalDbName, phyicalDbName);
			} catch (Exception e) {
				throw new DataSourceBindingChangeException(logicalDbName, phyicalDbName, 
						phyicalDbParameters, e.getMessage());
			}
		}
	}

	public void resetConnectionPoolSize(String physicalDbId, int poolMinSize, int poolMaxSize){
		if( null == getDataSourcesMap().get(physicalDbId)){
			CONFIG_LOGGER.error("The physical DB " + physicalDbId + " is not existed in current ZdalDataSource.");
			return ;
		}
		ZDataSource dataSource = getDataSourcesMap().get(physicalDbId);
		if( dataSource instanceof ZScalableDataSource ){
			try {
				((ZScalableDataSource)dataSource).resetConnectionPoolSize(poolMinSize, poolMaxSize);
			} catch (ScaleConnectionPoolException e) {
				CONFIG_LOGGER.error("ZdalDataSource failed to reset connection pool size for physical DB " + physicalDbId, e);
			}
		}
	}
}