package com.alipay.zdal.test.sequence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.junit.Test;


/**
 * 
 * 
 * @author yaojun.yj
 * @version $Id: DbUtilForTest.java, v 0.1 2013-11-1 ÉÏÎç10:18:15 yaojun.yj Exp $
 */
public class DbUtilForTest {
    
    
    @Test
    public void testDrop() throws Exception {
        
        String[] sqllist = {
                            "create database shardseq_created0",
                            "create database shardseq_created1",
                            "create database shardseq_created2",
                            "drop database if exists shardseq_created0",
                            "use shardseq_created2",
                            "create table mytest (id integer, name varchar(10))",
                            };
        
        runinitsqllist("jdbc:mysql://mysql-1-2.bjl.alipay.net:3306/shardseqnormal?characterEncoding=gbk", "mysql", "mysql", sqllist);
    }

    public static void runinitsqllist(String url, String user, String pwd, String[] sqllist) throws Exception {
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, pwd);
        
        runinitsqllist(conn, sqllist);
    }
    
    public static void runinitsqllist(Connection conn, String[] sqllist) throws Exception {
        
        for(String sql: sqllist){
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            
            ps.close();
            
        }
        
        conn.close();
    }

}
