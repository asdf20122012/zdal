/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.oci;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZDataSourceOciDriverException.java, v 0.1 2013-1-28 ÏÂÎç02:44:53 Exp $
 */
public class ZDataSourceOciDriverException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ZDataSourceOciDriverException(String cause) {
        super(cause);
    }

    public ZDataSourceOciDriverException(Throwable t) {
        super(t);
    }

    public ZDataSourceOciDriverException(String cause, Throwable t) {
        super(cause, t);
    }
}
