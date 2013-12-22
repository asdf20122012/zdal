/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.config;


/**
 * 
 * @author 伯牙
 * @version $Id: DataSourceType.java, v 0.1 2013-1-10 下午01:44:10 Exp $
 */
public enum DataSourceType {
    /** 读写分离的数据源 */
    RW,
    /** sharding的数据源 */
    SHARD,
    /** failover的数据源 */
    FAILOVER,
    /** 物理数据源 */
    PHYSICS,
    /** tair的数据源 */
    TAIR,
    /**逻辑数据源.  */
    LOGIC,
    ATOM, // Atom data source
    GROUP,
    SHARDGROUP; //Sharding + Group

    public boolean isRW() {
        return this.equals(DataSourceType.RW);
    }

    public static boolean isRW(String name) {
        return DataSourceType.RW.toString().equalsIgnoreCase(name);
    }

    public boolean isShard() {
        return this.equals(DataSourceType.SHARD);
    }

    public static boolean isShard(String name) {
        return DataSourceType.SHARD.toString().equalsIgnoreCase(name);
    }

    public boolean isFailover() {
        return this.equals(DataSourceType.FAILOVER);
    }

    public static boolean isFailover(String name) {
        return DataSourceType.FAILOVER.toString().equalsIgnoreCase(name);
    }

    public boolean isPhysics() {
        return this.equals(DataSourceType.PHYSICS);
    }

    public static boolean isPhysics(String name) {
        return DataSourceType.PHYSICS.toString().equalsIgnoreCase(name);
    }

    public boolean isTair() {
        return this.equals(DataSourceType.TAIR);
    }

    public static boolean isTair(String name) {
        return DataSourceType.TAIR.toString().equalsIgnoreCase(name);
    }

    public boolean isLogic() {
        return this.equals(DataSourceType.LOGIC);
    }
    
    public boolean isAtom() {
        return this.equals(DataSourceType.ATOM);
    }
    
    public boolean isGroup() {
        return this.equals(DataSourceType.GROUP);
    }
    
    public boolean isShardGroup() {
        return this.equals(DataSourceType.SHARDGROUP);
    }

    public static boolean isLogic(String name) {
        return DataSourceType.LOGIC.toString().equalsIgnoreCase(name);
    }

    public static boolean isAtom(String name) {
        return DataSourceType.ATOM.toString().equalsIgnoreCase(name);
    }
    
    public static boolean isGroup(String name) {
        return DataSourceType.GROUP.toString().equalsIgnoreCase(name);
    }
    
    public static boolean isShardGroup(String name) {
        return DataSourceType.SHARDGROUP.toString().equalsIgnoreCase(name);
    }
    
    public static DataSourceType convert(String name) {
        for (DataSourceType type : values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("ERROR ## the name = " + name
                                           + " is not a validate DataSourceType");
    }
}
