/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.zdal.client.datasource.keyweight;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.zdal.client.ThreadLocalString;
import com.alipay.zdal.client.util.TableSuffixGenerator;
import com.alipay.zdal.client.util.ThreadLocalMap;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.RuntimeConfigHolder;

/**
 * 根据数据源的配置在主库与failover库之间选择一个库，业务系统将在该库上获取sequence 
 * @author zhaofeng.wang
 * @version $Id: GetDataSourceSequenceRules.java, v 0.1 2011-9-24 下午12:08:59 zhaofeng.wang Exp $
 */
public class GetDataSourceSequenceRules {
    private static Log                                                       logger                      = LogFactory
                                                                                                             .getLog(GetDataSourceSequenceRules.class);

    /**
     * 运行时变量,主要存放数据源的分组标识以及对应的组内各个数据源的权重信息等
     */
    private static final RuntimeConfigHolder<ZdalDataSourceKeyWeightRumtime> keyWeightRuntimeConfigHoder = new RuntimeConfigHolder<ZdalDataSourceKeyWeightRumtime>();

    public GetDataSourceSequenceRules() {

    }

    /**
     * 注意：这个方法只提供给trade,tradecore,tradequery的failover场景使用.
     * 采用两种策略来选择数据源，
     * 1. 第一种是只允许访问主库，
     * 2. 第二种是根据权重在主库和failover库之间随机选择一个库,其中第二种策略又包含两种可能:
     *    (1)业务系统正常情况下只使用主库，只有在主库故障的情况下，才去使用failover库，即在发现主库故障后，通过
     *     configserver推送权重的机制人工的切换到failover库,但是在没切换之前，系统还是会返回主库，业务会一直报错
     *     直到切换成功;
     *    (2)正常情况下,业务系统就支持在主库和failover库之间根据权重随机选择一个使用
     * 目前采取的策略是2(1).
     * @param groupNum 多组数据源中某一组的组号
     * @return
     */
    @SuppressWarnings("unchecked")
    public static int getDataSourceKeyOrderNum(int groupNum) {
        int orderNum = 0;

        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = keyWeightRuntimeConfigHoder
            .get().getKeyWeightMapHolder();
        if (groupNum >= keyWeightMapHolder.size() || groupNum < 0) {
            throw new IllegalArgumentException("The groupNum is " + groupNum
                                               + ", but the biggest number is "
                                               + (keyWeightMapHolder.size() - 1));
        }
        //String groupNumKey = StringUtil.alignRight(String.valueOf(groupNum), 2, '0');
        //TODO V3
        int convertGrouNum = TableSuffixGenerator.trade50ConvertGroupNum(groupNum);
        String groupNumKey = TableSuffixGenerator.getTableSuffix(convertGrouNum, keyWeightMapHolder
            .size());

        ZdalDataSourceKeyWeightRandom keyWeightRandom = keyWeightMapHolder
            .get(Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey);
        if (keyWeightRandom == null) {
            throw new IllegalArgumentException("The group_" + groupNumKey
                                               + " is not in the keyWeightMapHolder!");
        }
        //获取缓存的threadlocal变量autoCommit的值
        boolean autoCommit = (Boolean) ThreadLocalMap
            .get(ThreadLocalString.GET_AUTOCOMMIT_PROPERTY);

        int orderInGroup = -1;
        //如果autoCommit属性为true，则表示未在事务中；
        if (autoCommit) {
            orderInGroup = keyWeightRandom.select();
        } else {
            //在事务中分为两种情况，第一次计算结果并缓存，第二次直接返回；
            Map<String, Integer> map = (Map<String, Integer>) ThreadLocalMap
                .get(ThreadLocalString.GET_DB_ORDER_IN_GROUP);
            if (map == null) {
                orderInGroup = keyWeightRandom.select();
                Map<String, Integer> groupAndOrder = new HashMap<String, Integer>();
                groupAndOrder.put("groupNum", groupNum);
                groupAndOrder.put("orderNum", orderInGroup);
                ThreadLocalMap.put(ThreadLocalString.GET_DB_ORDER_IN_GROUP, groupAndOrder);
            } else {
                //如果是在同一个组并且缓存了结果就直接返回
                if (map.get("groupNum") != groupNum) {
                    throw new IllegalArgumentException(
                        "The groupNum is different from the last one in the transaction,the groupNum="
                                + groupNum + ",the last one is " + map.get("groupNum") + ".");
                }
                orderInGroup = map.get("orderNum");
                if (logger.isDebugEnabled()) {
                    logger.debug("Use the orderInGroup=" + orderInGroup + " in the threadLocal!");
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger
                .debug("Select the " + orderInGroup + "th datasource in the group_" + groupNumKey);
        }

        if (orderInGroup < 0 || orderInGroup >= keyWeightRandom.getDataSourceNumberInGroup()) {
            throw new IllegalArgumentException("The order number in group_" + groupNumKey + " is "
                                               + orderInGroup + ", but the biggest number is "
                                               + (keyWeightRandom.getDataSourceNumberInGroup() - 1));
        }

        //统计0~groupNum-1这些组内的数据源的总的个数
        for (int i = 0; i < groupNum; i++) {
            int tempGroupNum = TableSuffixGenerator.trade50ConvertGroupNum(i);
            String groupNumKey2 = TableSuffixGenerator.getTableSuffix(tempGroupNum,
                keyWeightMapHolder.size());
            if (keyWeightMapHolder.get(Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey2) != null) {

                int number = keyWeightMapHolder.get(
                    Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey2)
                    .getDataSourceNumberInGroup();
                if (number <= 0) {
                    throw new IllegalArgumentException("The datasource number in the group_"
                                                       + groupNumKey2 + " is illegal, it is"
                                                       + number);
                }
                orderNum += number;
            } else {
                throw new IllegalArgumentException("The key of group_" + groupNumKey2
                                                   + "does not exist in the keyWeightMapHolder!");
            }
        }
        return orderNum + orderInGroup;
    }

    /**
     * 注意：这个方法只提供给trade,tradecore,tradequery的failover场景使用.
     * 修复trade系统failover遗留问题，交易咨询中对于主库完全挂掉的情况，咨询全部会失败。
     * 两次随机结果可以不一样，只是利用当前的权重配置随机选择一个dbIndex返回给业务方
     * @param groupNum
     * @return
     */
    public static int getDbIndexOrderNum(int groupNum) {
        int orderNum = 0;
        Map<String, ZdalDataSourceKeyWeightRandom> keyWeightMapHolder = keyWeightRuntimeConfigHoder
            .get().getKeyWeightMapHolder();
        if (groupNum >= keyWeightMapHolder.size() || groupNum < 0) {
            throw new IllegalArgumentException("The groupNum is " + groupNum
                                               + ", but the biggest number is "
                                               + (keyWeightMapHolder.size() - 1));
        }
        int convertGrouNum = TableSuffixGenerator.trade50ConvertGroupNum(groupNum);
        String groupNumKey = TableSuffixGenerator.getTableSuffix(convertGrouNum, keyWeightMapHolder
            .size());
        ZdalDataSourceKeyWeightRandom keyWeightRandom = keyWeightMapHolder
            .get(Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey);
        if (keyWeightRandom == null) {
            throw new IllegalArgumentException("The group_" + groupNumKey
                                               + "is not in the keyWeightMapHolder!");
        }

        int orderInGroup = -1;
        orderInGroup = keyWeightRandom.select();

        if (logger.isDebugEnabled()) {
            logger
                .debug("Select the " + orderInGroup + "th datasource in the group_" + groupNumKey);
        }

        if (orderInGroup < 0 || orderInGroup >= keyWeightRandom.getDataSourceNumberInGroup()) {
            throw new IllegalArgumentException("The order number in group_" + groupNumKey + " is "
                                               + orderInGroup + ", but the biggest number is "
                                               + (keyWeightRandom.getDataSourceNumberInGroup() - 1));
        }
        //统计0~groupNum-1这些组内的数据源的总的个数
        for (int i = 0; i < groupNum; i++) {
            int tempGroupNum = TableSuffixGenerator.trade50ConvertGroupNum(i);
            String groupNumKey2 = TableSuffixGenerator.getTableSuffix(tempGroupNum,
                keyWeightMapHolder.size());
            if (keyWeightMapHolder.get(Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey2) != null) {

                int number = keyWeightMapHolder.get(
                    Constants.DBINDEX_DS_GROUP_KEY_PREFIX + groupNumKey2)
                    .getDataSourceNumberInGroup();
                if (number <= 0) {
                    throw new IllegalArgumentException("The datasource number in the group_"
                                                       + groupNumKey2 + " is illegal, it is"
                                                       + number);
                }
                orderNum += number;
            } else {
                throw new IllegalArgumentException("The key of group_" + groupNumKey2
                                                   + "does not exist in the keyWeightMapHolder!");
            }
        }
        return orderNum + orderInGroup;
    }

    /**
     * 返回运行时变量
     * 
     * @return
     */
    public static RuntimeConfigHolder<ZdalDataSourceKeyWeightRumtime> getKeyWeightRuntimeConfigHoder() {
        return keyWeightRuntimeConfigHoder;
    }

}
