/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.alipay.cif.core.dal.tddl;

import com.alibaba.common.lang.StringUtil;

/**
 * TDDL规则解析器
 * 
 * @author jia.hej
 *
 * @version $Id: RuleParser.java, v 0.1 2010-11-3 下午06:55:18 jia.hej Exp $
 */
public class RuleParser {

    /** 默认的字符串分隔符 */
    private static String DEFAULT_SEPERATOR = "_";

    /** 特殊的字符串分隔符 */
    private static String ZERO_STR          = "0";

    //~~~~~~~~~~~~~~~address分库分表规则~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 根据用户userId解析分库索引
     * <li>tableName=cs_deliver_address
     * 
     * @param userId    用户id
     * @return
     */
    public static int parseDBIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            int index = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;

            if (index >= 0 && index <= 4) {
                return 0;
            } else if (index >= 5 && index <= 9) {
                return 1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            //外层会打印error
            return -1;
        }
    }

    /**
     * 根据用户userId解析分表索引
     * <li>tableName=cs_deliver_address
     * 
     * @param userId    用户id
     * @return
     */
    public static int parseTableIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            int dbIndex = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;
            int tableIndex = (Integer
                .valueOf(StringUtil.substring(userId, 14, 15).toCharArray()[0]) % 10 + 2) % 10;
            return dbIndex * 10 + tableIndex;
        } catch (Exception e) {
            return -1;
        }
    }

    //~~~~~~~~~~~~~~~userext分库分表规则~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 根据用户userId解析分库索引
     * <li>tableName=CS_ACTION_LOG
     * <li>tableName=CS_USER_MEMO
     * 
     * @param userId    用户id
     * @return
     */
    public static int parseUserextDBIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            //userId的倒数第3位分库
            int index = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;

            if (index >= 0 && index <= 9) {
                return index;
            } else {
                return -1;
            }
        } catch (Exception e) {
            //外层会打印error
            return -1;
        }
    }

    /**
     * 根据用户userId解析分表索引
     * <li>tableName=CS_USER_MEMO
     * 
     * @param userId    用户id
     * @return
     */
    public static int parseUserMemoTableIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            //userId的倒数第3位
            int dbIndex = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;
            //userId的倒数第2位
            int tableIndex = (Integer
                .valueOf(StringUtil.substring(userId, 14, 15).toCharArray()[0]) % 10 + 2) % 10;
            return dbIndex * 10 + tableIndex;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 根据用户id、gmt时间解析分表索引
     * 如："2088111122221039" month=6 返回：03_05
     * 如："2088111122221179" month=6 返回：17_05
     *  
     * @param userId    用户id
     * @param calendar  当前日期
     * @return
     */
    public static String parseActionLogTableIndex(String userId, int month) {
        if (StringUtil.isBlank(userId) || month < 0) {
            return null;
        }
        try {
            //userId的倒数第3位
            int dbIndex = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;
            //userId的倒数第2位
            int reSecondIndex = (Integer
                .valueOf(StringUtil.substring(userId, 14, 15).toCharArray()[0]) % 10 + 2) % 10;

            int tableIndex = dbIndex * 10 + reSecondIndex;
            return StringUtil.alignRight(String.valueOf(tableIndex), 2, ZERO_STR)
                   + DEFAULT_SEPERATOR + StringUtil.alignRight(String.valueOf(month), 2, ZERO_STR);
        } catch (Exception e) {
            return null;
        }
    }

    //~~~~~~~~~~~~~~~cifprofile分库分表规则~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 根据用户userId解析分库索引
     * <li>tableName=cs_secure_info
     * <li>tableName=cs_user_profile
     * 
     * @param userId    用户id
     * @return
     */
    public static int parseCifprofileDBIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            //userId的倒数第3位分库
            int index = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;

            if (index >= 0 && index <= 9) {
                return index;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 根据用户userId解析分表索引
     * <li>tableName=cs_secure_info
     * <li>tableName=cs_user_profile
     * @param userId    用户id
     * @return
     */
    public static int parseCifprofileTableIndex(String userId) {
        if (StringUtil.isBlank(userId)) {
            return -1;
        }

        try {
            //userId的倒数第3位
            int dbIndex = (Integer.valueOf(StringUtil.substring(userId, 13, 14).toCharArray()[0]) % 10 + 2) % 10;
            //userId的倒数第2位
            int tableIndex = (Integer
                .valueOf(StringUtil.substring(userId, 14, 15).toCharArray()[0]) % 10 + 2) % 10;
            return dbIndex * 10 + tableIndex;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("java.home"));
    }
}
