/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.alipay.zdal.common.lang.StringUtil;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: DefaultZdalInit.java, v 0.1 2012-5-15 上午09:20:23 xiaoqing.zhouxq Exp $
 */
public class DefaultZdalInit implements ZdalInit {

    private static final Logger logger              = Logger.getLogger(DefaultZdalInit.class);

    public static final String  DATABASE_SUFFIX     = "zdal_";

    private static final String TABLE_SUFFIX        = "user_";

    private static final String DROP_DATABASE_SQL   = " drop database if exists {0} ";

    private static final String CREATE_DATABASE_SQL = " create database if not exists {0} default character set utf8";

    private static final String CREATE_TABLE_SQL    = " create table if not exists {0} ( "
                                                      + " user_id int(11) NOT NULL  ,"
                                                      + " name varchar(20) NOT NULL,"
                                                      + " address varchar(256) DEFAULT NULL"
                                                      + " ) ENGINE=InnoDB DEFAULT CHARSET=utf8";

    public DefaultZdalInit() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("ERROR ## can not find mysql jdbc driver ", e);
        }
    }

    /** 
     * @see com.taobao.ZdalInit.zhouxiaoqing.ZdalInit#initDatabase(int, int, int)
     */
    public void initDatabase(DbInfo[] dbInfos, int tableCount, int tableSuffixWidth) {
        if (dbInfos == null || dbInfos.length == 0) {
            throw new RuntimeException("ERROR ## the dbInfo is null");
        }
        for (int i = 0; i < dbInfos.length; i++) {
            initDatabaseAndTable(dbInfos[i], dbInfos[i].getDbName(), i * tableCount, i * tableCount
                                                                                     + tableCount,
                tableSuffixWidth);
        }
    }

    private void initDatabaseAndTable(DbInfo dbInfo, String dbName, int tableIndexFrom,
                                      int tableIndexTo, int tableSuffixWidth) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = DriverManager.getConnection(dbInfo.getDbUrl(), dbInfo.getDbUsername(), dbInfo
                .getDbPassword());
            st = conn.createStatement();

            String dropDatabaseSql = MessageFormat.format(DROP_DATABASE_SQL, dbName);
            st.executeUpdate(dropDatabaseSql);

            String createDatabaseSql = MessageFormat.format(CREATE_DATABASE_SQL, dbName);
            st.executeUpdate(createDatabaseSql);

            for (int i = tableIndexFrom; i < tableIndexTo; i++) {
                String createTableSql = createTableSql(dbName, i, tableSuffixWidth);
                st.executeUpdate(createTableSql);
            }
        } catch (SQLException e) {
            logger.error("ERROR ## initDatabaseAndTable has an error", e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error("ERROR ## close Statement has an error", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("ERROR ## close Connection has an error", e);
                }
            }
        }
    }

    /**
     * 组装成schemaName.tableName的格式的表名，并且返回对应的sql语句.
     * @param dbName
     * @param tableIndex
     * @param tableSuffixWidth
     * @return
     */
    private String createTableSql(String dbName, int tableIndex, int tableSuffixWidth) {
        String tableNameSuffix = StringUtil.alignRight(String.valueOf(tableIndex),
            tableSuffixWidth, '0');
        String tableName = new StringBuilder().append(dbName).append(".").append(TABLE_SUFFIX)
            .append(tableNameSuffix).toString();
        return MessageFormat.format(CREATE_TABLE_SQL, tableName);
    }

    public static void main(String[] args) {
        DbInfo[] dbInfos = new DbInfo[4];
        for (int i = 0; i < dbInfos.length; i++) {
            DbInfo dbInfo = new DbInfo();
            dbInfo
                .setDbUrl("jdbc:mysql://10.253.98.18:3306/user_char_id?useUnicode=true&characterEncoding=gbk");
            dbInfo.setDbUsername("mysql");
            dbInfo.setDbPassword("mysql");
            dbInfo.setDbName(DATABASE_SUFFIX + "0" + i);
            dbInfos[i] = dbInfo;
        }
        ZdalInit init = new DefaultZdalInit();
        init.initDatabase(dbInfos, 2, 2);
    }
}
