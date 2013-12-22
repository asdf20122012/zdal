/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import com.alipay.zdal.common.lang.StringUtil;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: Test.java, v 0.1 2013-2-22 ÉÏÎç09:14:14 Exp $
 */
public class Test {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int count = 10;
        int length = (int) Math.ceil(Math.log10(count));
        for (int i = 0; i < count; i++) {
            String key = StringUtil.alignRight(String.valueOf(i), length, '0');
            System.out.println(key);
        }
    }

}
