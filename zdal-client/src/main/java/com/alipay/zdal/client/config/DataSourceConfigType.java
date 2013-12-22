/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.zdal.client.config;

/***
 * Unified all of Zdal DataSource Configuration Type in this enumeration
 */
public enum DataSourceConfigType {
    ATOM, GROUP, SHARD, SHARD_GROUP, SHARD_FAILOVER, OB, HBase, Neo4J, TAIR;

    public static DataSourceConfigType typeOf(String type) {
        if (null == type || "".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("The DataSourceConfigType can not be null or empty.");
        }
        if (type.equalsIgnoreCase("ATOM")) {
            return ATOM;
        } else if (type.equalsIgnoreCase("GROUP") || type.equalsIgnoreCase("RW")
                   || type.equalsIgnoreCase("LOAD_BALANCE")) {
            return GROUP;
        } else if (type.equalsIgnoreCase("SHARD")) {
            return SHARD;
        } else if (type.equalsIgnoreCase("SHARD_GROUP") || type.equalsIgnoreCase("SHARD_RW")) {
            return SHARD_GROUP;
        } else if (type.equalsIgnoreCase("SHARD_FAILOVER")) {
            return SHARD_FAILOVER;
        } else if (type.equalsIgnoreCase("OB")) {
            return OB;
        } else if (type.equalsIgnoreCase("HBase")) {
            return HBase;
        } else if (type.equalsIgnoreCase("Neo4J")) {
            return Neo4J;
        } else if (type.equalsIgnoreCase("TAIR")) {
            return TAIR;
        } else {
            throw new IllegalArgumentException("The DataSourceConfigType " + type
                                               + " has not been supported yet.");
        }
    }
}