/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.oceanbase;

import javax.sql.DataSource;

import com.alipay.cloudengine.kernel.spi.work.ApplicationWorkingAreaAware;
import com.alipay.sofa.common.conf.Configration;
import com.alipay.sofa.service.api.client.ApplicationConfigrationAware;
import com.alipay.zdal.common.Closable;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalOceanbaseDataSource.java, v 0.1 2013-2-19 ÏÂÎç05:04:22 Exp $
 */
public final class ZdalOceanbaseDataSource extends AbstractZdalOceanbaseDataSource
                                                                                  implements
                                                                                  Closable,
                                                                                  DataSource,
                                                                                  ApplicationWorkingAreaAware,
                                                                                  ApplicationConfigrationAware {

    @Override
    public void setWorkingArea(String workingArea) {
    }

    @Override
    public void setConfigration(Configration configration) {
    }

}
