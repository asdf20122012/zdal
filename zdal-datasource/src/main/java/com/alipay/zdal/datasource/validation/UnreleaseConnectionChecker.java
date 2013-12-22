/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.alipay.zdal.datasource.validation;

/**
 *
 * @author fengqi.lin@alipay.com
 * @version $Id: UnreleaseConnectionChecker.java, v 0.1 2010-8-12 下午01:18:42 fengqi.lin Exp $
 */
public interface UnreleaseConnectionChecker {
    
    /**
     * 对没有释放的数据库连接进行检查
     */
    void connectionCheck();

}
