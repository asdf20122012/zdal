/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.drm;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalLdcSignalResourceTest.java, v 0.1 2013-6-7 ÏÂÎç03:32:14 Exp $
 */
public class ZdalLdcSignalResourceTest {

    @Test
    public void test() {
        ZdalConfigListener configListener = new ZdalConfigListener() {

            @Override
            public void resetZoneDsThreadException(boolean throwException) {
            }

            @Override
            public void resetZoneDs(Set<String> zoneDs) {
                Assert.assertEquals(4, zoneDs.size());
            }

            @Override
            public void resetWeight(Map<String, String> keyWeights) {
            }

			@Override
			public void resetDataSourceBinding(String physicalDbId,
					String logicalDbId, LocalTxDataSourceDO dataSourceSetting)
					throws DataSourceBindingChangeException {
				
			}

			@Override
			public void resetConnectionPoolSize(String physicalDbId,
					int minSize, int maxSize)
					throws ScaleConnectionPoolException {
				
			}
        };
        ZdalLdcSignalResource resource = new ZdalLdcSignalResource(configListener, "testLdcDrm");
        String drmValue = "master_000,failover_000,master_001,failover_001";
        resource.updateResource("zoneDs", drmValue);
    }

    @Test
    public void test1() {
        ZdalConfigListener configListener = new ZdalConfigListener() {

            @Override
            public void resetZoneDsThreadException(boolean throwException) {
            }

            @Override
            public void resetZoneDs(Set<String> zoneDs) {
                Assert.assertEquals(0, zoneDs.size());
            }

            @Override
            public void resetWeight(Map<String, String> keyWeights) {
            }

			@Override
			public void resetDataSourceBinding(String physicalDbId,
					String logicalDbId, LocalTxDataSourceDO dataSourceSetting)
					throws DataSourceBindingChangeException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void resetConnectionPoolSize(String physicalDbId,
					int minSize, int maxSize)
					throws ScaleConnectionPoolException {
				// TODO Auto-generated method stub
				
			}

        };
        ZdalLdcSignalResource resource = new ZdalLdcSignalResource(configListener, "testLdcDrm");
        resource.updateResource("zoneDs", "");
        resource.updateResource("zoneDs", null);
    }

    public static void main(String[] args) {
        StringBuilder pullDrmValue = new StringBuilder();
        for (int i = 88; i < 110; i++) {
            pullDrmValue.append("master_").append(StringUtil.alignRight("" + i, 3, "0"));
            pullDrmValue.append(",");

        }
        for (int i = 88; i < 110; i++) {
            pullDrmValue.append("failover_").append(StringUtil.alignRight("" + i, 3, "0"));
            if (i != 109) {
                pullDrmValue.append(",");
            }
        }
        System.out.println(pullDrmValue.toString());
    }
}
