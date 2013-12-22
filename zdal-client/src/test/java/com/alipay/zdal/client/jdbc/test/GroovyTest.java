/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.List;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: GroovyTest.java, v 0.1 2013-2-28 ÉÏÎç09:22:32 Exp $
 */
public class GroovyTest {

    public static void main(String[] args) {
        String groovy = "return com.alipay.zdal.client.jdbc.test.Groovy.cal();";
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.evaluate(groovy);
        if (result instanceof List) {
            List<String> rr = (List<String>) result;
            for (String integer : rr) {
                System.out.println(integer);
            }
        }
    }
}
