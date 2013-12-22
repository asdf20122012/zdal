/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc;

/**
 * 用于预热zdal上下文，如果应用需要预热，自己实现该接口，然后注入ZdalDataSource中，使用如下：
 * <bean id="testZdalDataSource" class="com.alipay.zdal.client.jdbc.ZdalDataSource" init-method="init" destroy-method="close"> 
 *     <property name="appDsName" value=""/> 
 * </bean>
 * 
 * <bean class="com.alipay.tradecore.common.dal.util.ZdalDsPrefillImpl">  这个类实现com.alipay.zdal.client.jdbc.ZdalPrefill接口，然后在内部调用ZdalDataSource.prefillZdal方法.
        <property name="dataSource" ref="tradecore_tddl" />
    </bean>
 * 
 * zdal内部会按照LDC的不同zone预热对应数据源的最小连接数.
 * @author 伯牙
 * @version $Id: ZdalPrefill.java, v 0.1 2013-8-1 下午03:53:42 Exp $
 */
public interface ZdalPrefill {

    /**
     * 业务的预热功能实现：注意，这个预热功能必须同步完成，否则会预热不同zone的数据库连接;
     * 如果预热过程出现问题，不要抛出异常.
     */
    void prefill();

}
