/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alipay.zdal.client.datasource.keyweight.GetDataSourceSequenceRules;
import com.alipay.zdal.client.datasource.keyweight.ZdalDataSourceKeyWeightRandom;
import com.alipay.zdal.client.datasource.keyweight.ZdalDataSourceKeyWeightRumtime;
import com.alipay.zdal.client.exceptions.ZdalClientException;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.rule.config.beans.AppRule;

/**
 * 为了支持paycore-payment中的全活策略下的failover功能.
 * 主要的改动点是：
 * 在内部维护每个group需要进行全活检测的数据源列表，在failover切换以后，把这个列表的dbKey重置.
 * @author 伯牙
 * @version $Id: ZdalDataSourcePayment.java, v 0.1 2013-9-3 下午03:21:13 Exp $
 */
public class ZdalDataSourcePayment extends ZdalDataSource {

    /** 用于存放需要进行全活检测的数据源名称,key=groupName value=dbKeys */
    private Map<String, List<String>> checkDbKeys = new HashMap<String, List<String>>();

    protected void initForAppRule(AppRule appRule) {
        super.initForAppRule(appRule);
        //把各个group中权重>0的dbKey保存到checkDbKeys中，便于全活中进行检测.
        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = super
            .getKeyWeightMapConfig();
        for (Entry<String, ZdalDataSourceKeyWeightRandom> entrySet : keyWeightMapHolder.entrySet()) {
            String groupKey = entrySet.getKey();
            ZdalDataSourceKeyWeightRandom keyWeightRandom = entrySet.getValue();
            Map<String, Integer> cacheKeyWeights = keyWeightRandom.getWeightConfig();
            List<String> canUseDbKeys = new ArrayList<String>();
            for (Entry<String, Integer> weightsEntry : cacheKeyWeights.entrySet()) {
                String dbKey = weightsEntry.getKey();
                Integer weight = weightsEntry.getValue();
                if (weight > 0) {
                    canUseDbKeys.add(dbKey);
                }
            }
            checkDbKeys.put(groupKey, canUseDbKeys);
        }
        CONFIG_LOGGER.warn("WARN ## the PayMentZdalDataSource need to check dataSources = ["
                           + checkDbKeys + "]");
    }

    /**
     * 参数p格式如下
     * group_00=ds0:10,ds1:0
     * group_01=ds2:10,ds3:0
     * group_02=ds4:0,ds5:10
     * 一组只有一个库的时候不用调整其权重，默认为10
     * @param p 推送过来的内容
     *
     * @see com.alipay.zdal.client.jdbc.AbstractZdalDataSource#resetKeyWeightConfig(java.util.Map)
     */
    protected void resetKeyWeightConfig(Map<String, String> p) {
        //        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = GetDataSourceSequenceRules
        //            .getKeyWeightRuntimeConfigHoder().get().getKeyWeightMapHolder();
        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = super
            .getKeyWeightMapConfig();
        for (Entry<String, String> entrySet : p.entrySet()) {
            String groupKey = entrySet.getKey();
            String value = entrySet.getValue();
            if (StringUtil.isBlank(groupKey) || StringUtil.isBlank(value)) {
                throw new ZdalClientException("ERROR ## 数据源groupKey=" + groupKey
                                              + "分组权重配置信息不能为空,value=" + value);
            }
            String[] keyWeightStr = value.split(",");
            String[] weightKeys = new String[keyWeightStr.length];
            int[] weights = new int[keyWeightStr.length];
            List<String> canUseDbKeys = new ArrayList<String>();
            for (int i = 0; i < keyWeightStr.length; i++) {
                if (StringUtil.isBlank(keyWeightStr[i])) {
                    throw new ZdalClientException("ERROR ## 数据源keyWeightStr[" + i
                                                  + "]分组权重配置信息不能为空.");
                }
                String[] keyAndWeight = keyWeightStr[i].split(":");
                if (keyAndWeight.length != 2) {
                    throw new ZdalClientException("ERROR ## 数据源key按组配置权重错误,keyWeightStr[" + i
                                                  + "]=" + keyWeightStr[i] + ".");
                }
                String key = keyAndWeight[0];
                String weightStr = keyAndWeight[1];
                if (StringUtil.isBlank(key) || StringUtil.isBlank(weightStr)) {
                    CONFIG_LOGGER.error("ERROR ## 数据源分组权重配置信息不能为空,key=" + key + ",weightStr="
                                        + weightStr);
                    return;
                }
                weightKeys[i] = key.trim();
                weights[i] = Integer.parseInt(weightStr.trim());
                if (weights[i] > 0) {//把需要全活检测的数据源存起来.
                    canUseDbKeys.add(weightKeys[i]);
                }
            }
            //          根据 groupKey以及对应的keyAndWeightMap去查询
            ZdalDataSourceKeyWeightRandom weightRandom = keyWeightMapHolder.get(groupKey);
            if (weightRandom == null) {
                throw new ZdalClientException("ERROR ## 新推送的按数据源key分组权重配置中的key不对,非法的groupKey="
                                              + groupKey);
            }
            for (String newKey : weightKeys) {
                if (weightRandom.getWeightConfig() == null
                    || !weightRandom.getWeightConfig().containsKey(newKey)) {
                    throw new ZdalClientException("新推送的数据源分组" + groupKey
                                                  + "权重配置中包含不属于该组的数据源标识,key=" + newKey);
                }
            }
            if (weightKeys.length != weightRandom.getDataSourceNumberInGroup()) {
                throw new ZdalClientException("新推送的按数据源key分组权重配置中，分组groupKey=" + groupKey
                                              + "包含的数据源个数不对 ,size=" + weightKeys.length
                                              + ",the size should be "
                                              + weightRandom.getDataSourceNumberInGroup());
            }
            //根据该组的groupKey以及对应的keyAndWeightMap生成TDataSourceKeyWeightRandom
            ZdalDataSourceKeyWeightRandom TDataSourceKeyWeightRandom = new ZdalDataSourceKeyWeightRandom(
                weightKeys, weights);
            keyWeightMapHolder.put(groupKey, TDataSourceKeyWeightRandom);

            CONFIG_LOGGER.warn("WARN ## the groupName = " + groupKey + " check dbKey changed to "
                               + canUseDbKeys);
            checkDbKeys.put(groupKey, canUseDbKeys);
        }
        GetDataSourceSequenceRules.getKeyWeightRuntimeConfigHoder().set(
            new ZdalDataSourceKeyWeightRumtime(keyWeightMapHolder));
        //设置本地的keyWeightMapCofig属性，全活策略会依赖于该配置
        super.setKeyWeightMapConfig(keyWeightMapHolder);
    }

    public Map<String, List<String>> getCheckDbKeys() {
        return checkDbKeys;
    }

    public void setCheckDbKeys(Map<String, List<String>> checkDbKeys) {
        this.checkDbKeys = checkDbKeys;
    }

}
