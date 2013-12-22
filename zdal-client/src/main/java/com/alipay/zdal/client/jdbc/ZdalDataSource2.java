/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc;

import javax.sql.DataSource;

import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.common.Closable;
import com.alipay.zdal.common.lang.StringUtil;

/**
 * Zdal 对外公布的数据源,支持动态调整数据源的配置信息，切换等功能,适用于sofa2或者独立运行的app.br>
 * 注意：
 * 1,使用前请务必先设置appName,appDsName,dbmode,idcName,zdataconsoleUrl，configPath的值，并且调用init方法进行初始化;
 * 2,优先从zdataconsole中获取配置信息，如果获取不到，再从configPath获取配置信息.
 * <bean id="testZdalDataSource" class="com.alipay.zdal.client.jdbc.ZdalDataSource2" init-method="init" destroy-method="close">
 *  <property name="appName" value="appName"/>
 *  <property name="appDsName" value="appDsName"/>
 *  <property name="dbmode" value="${dbmode}"/>
 *  <property name="zdataconsoleUrl" value="${zdataconsole_service_url}"/>
 *  <property name="configPath" value="/home/admin/appName-run/jboss/deploy"/>
 *  <property name="drm" value="drm"/>
 * </bean>
 * 3,zdal数据源需要感知zone，这个参数是通过环境变量中设置的，需要app的deploy.sh、jbossctl.sh脚本设置.
 * 4,drm配置项如果需要推送切换数据源的权重，需要配置上对应的drm标示.
 * @author 伯牙
 * @version $Id: ZdalDataSource.java, v 0.1 2012-11-17 下午4:08:43 Exp $
 */
public final class ZdalDataSource2 extends AbstractZdalDataSource implements DataSource, Closable {

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

}