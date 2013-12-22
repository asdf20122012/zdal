/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: TableRule.java, v 0.1 2013-1-10 ÏÂÎç01:48:33 Exp $
 */
public class ShardTableRule {

    private String       logicTableName;

    private List<String> dbRules    = new ArrayList<String>();

    private List<String> tableRules = new ArrayList<String>();

    private String       tableSuffix;

    private String       dbIndex;

	public String getLogicTableName() {
        return logicTableName;
    }

    public void setLogicTableName(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    public List<String> getDbRules() {
        return dbRules;
    }

    public void setDbRules(List<String> dbRules) {
        this.dbRules = dbRules;
    }

    public List<String> getTableRules() {
        return tableRules;
    }

    public void setTableRules(List<String> tableRules) {
        this.tableRules = tableRules;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public String getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(String dbIndex) {
        this.dbIndex = dbIndex;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbIndex == null) ? 0 : dbIndex.hashCode());
        result = prime * result + ((dbRules == null) ? 0 : dbRules.hashCode());
        result = prime * result + ((logicTableName == null) ? 0 : logicTableName.hashCode());
        result = prime * result + ((tableRules == null) ? 0 : tableRules.hashCode());
        result = prime * result + ((tableSuffix == null) ? 0 : tableSuffix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ShardTableRule other = (ShardTableRule) obj;
        if (dbIndex == null) {
            if (other.dbIndex != null)
                return false;
        } else if (!dbIndex.equals(other.dbIndex))
            return false;
        if (dbRules == null) {
            if (other.dbRules != null)
                return false;
        } else if (!dbRules.equals(other.dbRules))
            return false;
        if (logicTableName == null) {
            if (other.logicTableName != null)
                return false;
        } else if (!logicTableName.equals(other.logicTableName))
            return false;
        if (tableRules == null) {
            if (other.tableRules != null)
                return false;
        } else if (!tableRules.equals(other.tableRules))
            return false;
        if (tableSuffix == null) {
            if (other.tableSuffix != null)
                return false;
        } else if (!tableSuffix.equals(other.tableSuffix))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TableRule [dbIndex=" + dbIndex + ", dbRules=" + dbRules + ", logicTableName="
               + logicTableName + ", tableRules=" + tableRules + ", tableSuffix=" + tableSuffix
               + "]";
    }

}
