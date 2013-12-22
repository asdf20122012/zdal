package com.alipay.zdal.client.rule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放zdal-document的分库分表规则.
 * 线程不安全的，需要调用的地方进行并发控制.
 * @author 伯牙
 * @version $Id: DocumentShardingRule.java, v 0.1 2013-1-16 下午03:59:35 Exp $
 */
public class DocumentShardingRule {

    /** key=tableIndex,value=dbIndex */
    private volatile static ConcurrentHashMap<Integer, Integer> cacheRuleMap = new ConcurrentHashMap<Integer, Integer>();

    /**
     * 设置document中热点表的对应关系.
     * tableIndex:dbIndex,tableIndex:dbIndex,tableIndex:dbIndex
     * @param cacheString
     */
    public static void setHotspot(Map<Integer, Integer> hotspots) {
        cacheRuleMap.clear();//先清空原来的热点表对应关系.
        if (hotspots == null || hotspots.isEmpty()) {//如果cacheString为空，就直接返回.
            return;
        }
        cacheRuleMap.putAll(hotspots);
    }

    /**
     * 计算 分表的规则
     * @param key      分库key
     * @param dbCount  库的个数
     * @param tbCount  表的个数
     * @return   分表值
     */
    public static int getShardingDBNumber(String key, int dbCount, int tbCount) {
        //先算分表
        int tbNumber = getShardingTableNumber(key, dbCount, tbCount);
        //访问cache,如果缓存里有，就直接返回
        if (cacheRuleMap.get(tbNumber) != null) {
            return cacheRuleMap.get(tbNumber);
        }

        //继续按照分库规则算分库
        int keyHashValue = getKeyHashValue(key);
        int dbNumber = (keyHashValue % tbCount) / (tbCount / dbCount);
        return dbNumber;
    }

    /**
     * 计算 分表的规则
     * @param key      分库key
     * @param dbCount  库的个数
     * @param tbCount  表的个数
     * @return   分表值
     */
    public static int getShardingTableNumber(String key, int dbCount, int tbCount) {
        int keyHashValue = getKeyHashValue(key);
        int tbNumber = (keyHashValue % tbCount) % (tbCount / dbCount);
        return tbNumber;
    }

    /**
     * 返回key的hashcode，确保是>=0的整数.
     * @param key
     * @return
     */
    private static int getKeyHashValue(String key) {
        int keyHashValue = key.hashCode();
        if (keyHashValue < 0) {
            keyHashValue = keyHashValue & 0x7FFFFFFF;
        }
        return keyHashValue;
    }
}
