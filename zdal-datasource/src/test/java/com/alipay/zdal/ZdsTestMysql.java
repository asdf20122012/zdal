package com.alipay.zdal;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;

/**
 * mysql db 基本使用
 * @author sicong.shou
 * @version $Id: ZdsTest1.java, v 0.1 2012-11-21 下午04:53:45 sicong.shou Exp $
 */
public class ZdsTestMysql {
    protected static DataSource   dataSource = null;
    protected static JdbcTemplate jt         = null;

    @BeforeClass
    public static void setUp() throws Exception {
        LocalTxDataSourceDO dsDo = new LocalTxDataSourceDO();
        dsDo.setDsName("test");
        dsDo
            .setConnectionURL("jdbc:mysql://localhost:3306/binlog?useUnicode=true&amp;characterEncoding=gbk");
        dsDo.setUserName("root");
        dsDo.setPassWord("root");
        dsDo.setDriverClass("com.mysql.jdbc.Driver");
        dsDo.setMinPoolSize(0);
        dsDo.setMaxPoolSize(5);
        dsDo.setExceptionSorterClassName(MySQLExceptionSorter.class.getName());
        dsDo.setPreparedStatementCacheSize(0);
        dataSource = new ZDataSource(dsDo);
        jt = new JdbcTemplate(dataSource);
    }

    @Test
    public void test1() {
        System.out.println(jt.update("insert into test1(id,name) values(1,'zhouxiaoqing')"));
        System.out.println(jt.update("insert into test1(id,name) values(1,'zhouxiaoqing')"));
        //        System.out.println(jt.queryForInt("select id from zdstest where name=?",
        //            new Object[] { "sb" }));
        //        System.out.println(jt.queryForList("select * from zdstest where name=?",
        //            new Object[] { "nb" }));
    }

    /**
     * delete
     */
    //    @Test
    public void test2() {
        System.out.print(jt.update("delete from zdstest where name=?", new Object[] { "hello" }));
    }

    /**
     * insert
     */
    //    @Test
    public void test3() {
        System.out.println(jt.update("insert into zdstest (id,name) values (?,?)", new Object[] {
                998877, "YYY" }));
        System.out.println(jt.queryForInt("select id from zdstest where name=?",
            new Object[] { "YYY" }));
    }

    /**
     * update
     */
    //    @Test
    public void test4() {
        System.out.println(jt.update("update zdstest set name=? where id= ?", new Object[] { "XXX",
                998877 }));
        System.out.println(jt.queryForObject("select name from zdstest where id=?",
            new Object[] { "998877" }, String.class));
    }
}