package com.alipay.zdal.client;

public class ThreadLocalString {
    public static final String ROUTE_CONDITION = "ROUTE_CONDITION",
            IS_EXIST_QUITE = "IS_EXIST_QUITE";
    public static final String TABLE_MERGE_SORT_TABLENAME = "TABLE_MERGE_SORT_TABLENAME",
            TABLE_MERGE_SORT_POOL = "TABLE_MERGE_SORT_POOL",
            TABLE_MERGE_SORT_VIRTUAL_TABLE_NAME = "TABLE_MERGE_SORT_VIRTUAL_TABLE_NAME";
    /**
    * added by fanzeng,即选择某个读库来执行某条sql
    */
    public static final String DATABASE_INDEX             = "DATABASE_INDEX";

    /** 在指定库读时，只选择写库，不进行failover. */
    public static final String WRITE_DATABASE_READ_RETRY  = "WRITE_DATABASE_READ_RETRY";
    /**
     * added by fanzeng,以支持cif提出的需求，即选择读库还是写库来执行某条sql
     */
    public static final String SELECT_DATABASE            = "SELECT_DATABASE";
    /**
     * added by fanzeng, 支持cif以及消息本地事务模式提出的需求，即最后的sql是在哪个库执行的，以及该库的标识id
     */
    public static final String GET_ID_AND_DATABASE        = "GET_ID_AND_DATABASE";
    /**
     * added by fanzeng, 支持 trade系统在按数据源key分组随机选择db时，根据autocommit属性来决定，以保证事务；
     */
    public static final String GET_DB_ORDER_IN_GROUP      = "GET_DB_ORDER_IN_GROUP";
    public static final String GET_AUTOCOMMIT_PROPERTY    = "GET_AUTOCOMMIT_PROPERTY";

    /**
     * added by fanzeng, 设置后，一组sql的执行db将会是同一个，如果发生了故障，就去找一个可用的。 
     */
    public static final String SET_GROUP_SQL_DATABASE     = "SET_GROUP_SQL_DATABASE";
    public static final String DB_NAME_USED_BY_GROUP_SQL  = "DB_NAME_USED_BY_GROUP_SQL";

}
