/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.drm;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.common.DBType;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalSignalResourceTest.java, v 0.1 2013-1-16 ÏÂÎç06:57:28 Exp $
 */
public class ZdalSignalResourceTest {

    @Test
    public void test() {
        ZdalConfigListener configListener = new ZdalConfigListener() {

            public void resetWeight(Map<String, String> keyWeights) {
                Assert.assertEquals(2, keyWeights.size());
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
        };
        ZdalSignalResource resource = new ZdalSignalResource(configListener, "trade10phy",
            DBType.ORACLE);
    }
}
