package com.alipay.zdal.client;

public class ThreadLocalString {
    public static final String ROUTE_CONDITION     = "ROUTE_CONDITION";
    /**
    * added by fanzeng,即选择某个读库来执行某条sql
    */
    public static final String DATABASE_INDEX      = "DATABASE_INDEX";
    /**
     * added by fanzeng,以支持cif提出的需求，即选择读库还是写库来执行某条sql
     */
    public static final String SELECT_DATABASE     = "SELECT_DATABASE";
    /**
     * added by fanzeng, 支持cif以及消息本地事务模式提出的需求，即最后的sql是在哪个库执行的，以及该库的标识id
     */
    public static final String GET_ID_AND_DATABASE = "GET_ID_AND_DATABASE";

}
