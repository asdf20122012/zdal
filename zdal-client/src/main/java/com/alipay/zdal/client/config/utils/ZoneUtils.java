/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.utils;

import com.alipay.zdal.common.lang.StringUtil;

/**
 * 维护ldc-zone名称与zdataconsole中配置的zone名称的对应关系.
 * @author 伯牙
 * @version $Id: ZoneUtils.java, v 0.1 2013-2-5 上午11:13:10 Exp $
 */
public final class ZoneUtils {

    /** 从启动参数里面获取ldc变量名称 */
    public static final String ZONE_NAME          = "com.alipay.ldc.zone";

    /** 默认的ldc. */
    public static final String DEFAULT_ZONE_VALUE = "gz00a";

    //    /** 现有的ldc的一个映射关系,全部都是小写字母. */
    //    private static final Map<String, String> zoneMap            = new HashMap<String, String>();
    //
    //    static {
    //        //gzone
    //        zoneMap.put("gz00a", "gz00");
    //        zoneMap.put("gz00b", "gz00");
    //        //预发布的配置和gzone是一样的.
    //        zoneMap.put("gz99p", "gz00");
    //        //rzone
    //        zoneMap.put("rz00a", "rz00");//测试环境的zone.
    //        zoneMap.put("rz00b", "rz00");
    //        zoneMap.put("rz01a", "rz01");
    //        zoneMap.put("rz01b", "rz01");
    //        zoneMap.put("rz02a", "rz02");
    //        zoneMap.put("rz02b", "rz02");
    //        zoneMap.put("rz03a", "rz03");
    //        zoneMap.put("rz03b", "rz03");
    //        zoneMap.put("rz04a", "rz04");
    //        zoneMap.put("rz04b", "rz04");
    //        zoneMap.put("drz00a", "drz00a");
    //        zoneMap.put("drz01a", "drz01a");
    //        zoneMap.put("drz02a", "drz02a");
    //    }

    /**
     * 先从环境变量中获取zone，如果没有，就取默认值,转化成小写.
     * @return
     */
    public static String getZone(String zone) {
        if (StringUtil.isNotBlank(zone)) {//zdal的测试代码需要自己设置的时候,直接返回.
            return zone.toLowerCase();
        }
        //        return DEFAULT_ZONE_VALUE.toLowerCase();//zone的概念在zdal中无需关心.
        String tmpZone = System.getProperty(ZONE_NAME);//先从环境变量中获取
        if (StringUtil.isBlank(tmpZone)) {
            return DEFAULT_ZONE_VALUE.toLowerCase();//取默认的zone=gz00a.
        }
        return tmpZone.toLowerCase();
    }
}
