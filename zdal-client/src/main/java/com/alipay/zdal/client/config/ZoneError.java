/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

/**
 * 
 * @author 伯牙
 * @version $Id: ZoneError.java, v 0.1 2013-1-11 上午11:13:05 Exp $
 */
public enum ZoneError {
    /**zdal组件直接抛出异常.  */
    EXCEPTOIN,
    /** zdal组件记录日志，并且忽略. */
    LOG;
    //        /** zdal组件忽略，继续执行. */
    //        IGNORE;

    public static ZoneError convert(String zoneError) {
        ZoneError[] errors = values();
        for (ZoneError tmp : errors) {
            if (tmp.toString().equalsIgnoreCase(zoneError)) {
                return tmp;
            }
        }
        throw new IllegalArgumentException("ERROR ## the zoneError = " + zoneError
                                           + " is invalidate");
    }

    public boolean isException() {
        return this.equals(EXCEPTOIN);
    }

    public boolean isLog() {
        return this.equals(LOG);
    }
}
