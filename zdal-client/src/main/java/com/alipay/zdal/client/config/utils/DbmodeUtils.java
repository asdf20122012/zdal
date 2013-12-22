/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config.utils;

import com.alipay.zdal.common.lang.StringUtil;

/**
 * 获取dbmode的值.
 * @author 伯牙
 * @version $Id: DbmodeUtils.java, v 0.1 2013-2-5 上午11:32:30 Exp $
 */
public class DbmodeUtils {
    /** 从启动参数里面获取dbmode变量名称 */
    public static final String DBMODE_NAME = "dbmode";

    /**
     * 先判断dbmode是否为空,如果为空,就从环境变量中获取,如果还是空,就直接抛出异常,转化成小写字母.
     * @param dbmode
     * @return
     */
    public static String getDbmode(String dbmode) {
        if (StringUtil.isNotBlank(dbmode)) {
            return dbmode.toLowerCase();
        }
        String tmpDbmode = System.getProperty(DBMODE_NAME);
        if (StringUtil.isBlank(tmpDbmode)) {
            throw new IllegalArgumentException("ERROR ## the dbmode is null");
        }
        return tmpDbmode.toLowerCase();
    }

}
