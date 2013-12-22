/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.util.ArrayList;
import java.util.List;

import com.alipay.zdal.common.lang.StringUtil;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: Groovy.java, v 0.1 2013-2-28 ÏÂÎç02:46:40 Exp $
 */
public class Groovy {

    public static List<String> cal() {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < 10; i++) {
            result.add(StringUtil.alignRight(i + "", 2, '0'));
        }
        for (int i = 0; i < 100; i++) {
            result.add(StringUtil.alignRight(i + "", 3, '0'));
        }
        return result;
    }

}
