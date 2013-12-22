package com.alipay.zdal.test.ut.client;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.sofa.service.api.drm.DRMClient;
import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.client.config.drm.ZdalSignalResource;
import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

public class ZdalSignalResourceTest {
	

    @Test
    public void test() {
        ZdalConfigListener configListener = new ZdalConfigListener() {

            public void resetWeight(Map<String, String> keyWeights) {
                Assert.assertEquals(2, keyWeights.size());
            }

            public void resetHotspot(Map<Integer, Integer> hotspots) {
            }

            @Override
            public void resetZoneDs(Set<String> zoneDs) {
            }

            @Override
            public void resetZoneDsThreadException(boolean throwException) {
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

        ZdalSignalResource resource = new ZdalSignalResource(configListener, "trade50",
            DBType.ORACLE);
        StringBuilder builder = new StringBuilder();
        builder.append("group_00=master_0:10,failover_0:10").append(";");
        builder.append("group_01=master_1:10,failover_1:10").append(";");
        resource.updateResource("keyWeight", builder.toString());
        //        resource.setKeyWeight(builder.toString());
    }

    public static void main(String[] args) {
        ZdalConfigListener configListener = new ZdalConfigListener() {
            public void resetWeight(Map<String, String> keyWeights) {
                Assert.assertEquals(2, keyWeights.size());
            }

            @Override
            public void resetZoneDs(Set<String> zoneDs) {
            }

            @Override
            public void resetZoneDsThreadException(boolean throwException) {
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
        ZdalSignalResource resource = new ZdalSignalResource(configListener, "trade10phy",
            DBType.ORACLE);
        try {
            DRMClient.getInstance().register(resource, resource.getResourceId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
