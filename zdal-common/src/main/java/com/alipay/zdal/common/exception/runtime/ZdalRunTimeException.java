/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.common.exception.runtime;

public class ZdalRunTimeException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2139691156552402165L;

    public ZdalRunTimeException(String arg) {
        super(arg);
    }

    public ZdalRunTimeException() {
        super();
    }

    public ZdalRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZdalRunTimeException(Throwable throwable) {
        super(throwable);
    }
}
