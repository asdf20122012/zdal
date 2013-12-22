package com.alipay.zdal;

import javax.sql.DataSource;

import org.junit.Test;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;

public class ZdsInit {
    @Test
    public void right() throws Exception {
        LocalTxDataSourceDO dsDo = new LocalTxDataSourceDO();
        dsDo.setDsName("test");
        dsDo
            .setConnectionURL("jdbc:mysql://mypay83307.devdb.alipay.net:3307/zds_switch?useUnicode=true&amp;characterEncoding=gbk");
        dsDo.setUserName("zds_switch");
        dsDo.setPassWord("ali88");
        dsDo.setDriverClass("com.mysql.jdbc.Driver");
        dsDo.setMinPoolSize(0);
        dsDo.setMaxPoolSize(5);
        dsDo
            .setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLExceptionSorter");
        dsDo.setPreparedStatementCacheSize(0);
        DataSource dataSource = new ZDataSource(dsDo);

    }

    @Test(expected = IllegalArgumentException.class)
    public void wrong() throws Exception {
        LocalTxDataSourceDO dsDo = new LocalTxDataSourceDO();
        dsDo.setDsName("test");
        dsDo
            .setConnectionURL("jdbc:mysql://mypay83307.devdb.alipay.net:3307/zds_switch?useUnicode=true&amp;characterEncoding=gbk");
        dsDo.setUserName("zds_switch");
        dsDo.setPassWord("ali88");
        dsDo.setDriverClass("com.mysql.jdbc.Driver");
        dsDo.setMinPoolSize(0);
        dsDo.setMaxPoolSize(5);
        //        dsDo
        //            .setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLExceptionSorter");
        dsDo.setPreparedStatementCacheSize(0);
        DataSource dataSource = new ZDataSource(dsDo);

    }
}
