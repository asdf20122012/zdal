/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.tair;

import java.io.File;
import java.io.Serializable;

import com.alipay.cloudengine.kernel.spi.work.ApplicationWorkingAreaAware;
import com.alipay.sofa.common.conf.Configration;
import com.alipay.sofa.service.api.client.ApplicationConfigrationAware;
import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.common.Closable;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.lang.StringUtil;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.TairManager;

/**
 * Zdal 对外公布的数据源,用于访问Tair,不支持动态调整数据源的权重,适用于sofa3.<br>
 * 注意：
 * 1,使用前请务必先设置appName,appDsName的值，并且调用init方法进行初始化,configPath,dbmode,zdataconsoleUrl 由sofa3会自动设置;
 * 2,优先从zdataconsole中获取配置信息，如果获取不到，再从configPath获取配置信息.
 * <bean id="testZdalDataSource" class="com.alipay.zdal.client.tair.ZdalTairDataSource" init-method="init" destroy-method="close">
 *  <property name="appDsName" value="appDsName"/>
 * </bean>
 * 3,zdal数据源需要感知zone，这个参数是通过环境变量中设置的，需要app的deploy.sh、jbossctl.sh脚本设置.
 * @author 伯牙
 * @version $Id: ZdalTairDataSource.java, v 0.1 2012-11-17 下午4:08:43 Exp $
 */
public final class ZdalTairDataSource extends AbstractZdalTairDataSource
                                                                        implements
                                                                        Closable,
                                                                        TairManager,
                                                                        ApplicationWorkingAreaAware,
                                                                        ApplicationConfigrationAware {
    public final void init() {
        if (super.inited.get() == true) {
            throw new ZdalClientException("ERROR ## init twice");
        }
        super.checkParameters();
        if (StringUtil.isBlank(zdataconsoleUrl)) {
            throw new IllegalArgumentException("ERROR ## the zdataconsoleUrl is null");
        }
        CONFIG_LOGGER.warn("WARN ## the zdataconsoleUrl = " + zdataconsoleUrl);
        super.initZdalDataSource();
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
    }
    
    public Result<DataEntry> get(int namespace, Serializable key){
    	
    	return null;
    }

	@Override
	public Result<DataEntry> get(int namespace, Serializable key, int expireTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
