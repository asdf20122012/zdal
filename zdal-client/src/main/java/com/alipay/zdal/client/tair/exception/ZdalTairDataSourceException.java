/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.tair.exception;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalTairDataSourceException.java, v 0.1 2013-1-29 ÏÂÎç02:40:22 Exp $
 */
public class ZdalTairDataSourceException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ZdalTairDataSourceException(String cause) {
        super(cause);
    }

    public ZdalTairDataSourceException(Throwable t) {
        super(t);
    }

    public ZdalTairDataSourceException(String cause, Throwable t) {
        super(cause, t);
    }
}
