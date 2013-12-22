/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.scalable;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;

/**
 * @author xiang.yangx
 *
 */
public class DataSourceBindingChangeException extends Exception {

    /**
     * 
     */
    private static final long   serialVersionUID = -5617365047233764658L;

    private String              message;

    private String              logicalDbName;

    private String              physicalDbName;

    private LocalTxDataSourceDO phyicalDbParameters;

    public DataSourceBindingChangeException(String logicalDbName, String phyicalDbName,
                                            LocalTxDataSourceDO phyicalDbParameters, String message) {
        this.message = message;
        this.logicalDbName = logicalDbName;
        this.physicalDbName = phyicalDbName;
        this.phyicalDbParameters = phyicalDbParameters;
    }

    public String getMessage() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Rebinding logical DB ").append(logicalDbName);
        strBuilder.append(" to physical DB ").append(physicalDbName);
        if (null != phyicalDbParameters) {
            strBuilder.append(" with properties ").append(phyicalDbParameters);
        }
        strBuilder.append(" failed due to ").append(message);
        return strBuilder.toString();
    }
}
