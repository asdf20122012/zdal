/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.oceanbase.exceptions;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: ZdalOceanBaseDataSourceException.java, v 0.1 2013-4-2 ÏÂÎç01:46:40 Exp $
 */
public class ZdalOceanBaseDataSourceException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ZdalOceanBaseDataSourceException(String cause) {
        super(cause);
    }

    public ZdalOceanBaseDataSourceException(Throwable t) {
        super(t);
    }

    public ZdalOceanBaseDataSourceException(String cause, Throwable t) {
        super(cause, t);
    }
}
