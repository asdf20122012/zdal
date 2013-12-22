/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alipay.zdal.common.DBType;



/**
 * 
 * @author zhaofeng.wang
 * @version $Id: FutureTaskTest.java, v 0.1 2013-3-22 上午10:40:19 zhaofeng.wang Exp $
 */
public class FutureTaskTest {
    public static final Log logger = LogFactory.getLog(FutureTaskTest.class);
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private BasicDataSource bc     = new BasicDataSource();
    JdbcTemplate            jdbcTemplate = new JdbcTemplate();
    private Boolean         isUseFutureTask = true;

    public FutureTaskTest(Boolean isUseFutureTask) {
        bc.setDriverClassName("com.mysql.jdbc.Driver");
        bc.setUrl("jdbc:mysql://mysql-1-2.bjl.alipay.net:3306/tddl_0");
        bc.setUsername("mysql");
        bc.setPassword("mysql");
        bc.setInitialSize(2);
        bc.setMinIdle(5);
        bc.setMaxActive(10);
        jdbcTemplate.setDataSource(bc);
        this.setIsUseFutureTask(isUseFutureTask);
        //insertMaster(this.jdbcTemplate, 100);
    }
    public String isAvailableDB1(final String dbKey) {
        Future<String> future = es.submit(new Callable<String>() {
            public String call() throws Exception {
                try {
                    Thread.sleep(110000L);
                    return "&" + dbKey + "&";
                } catch (Exception e) {
                    logger.error("Check db status failed,", e);
                    return "-" + dbKey + "-";
                }
            }
        });
        try {
            return future.get(100L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("获取数据库状态失败", e);
            System.out.println(dbKey + "=" + future.cancel(true));
            return "@" + dbKey + "@";
        }
    }
    
    public boolean isAvailableDB2(final String dbKey) {
        if (!this.isUseFutureTask) {
            return CheckDBAvailalbeStatusUtil(dbKey);
        }
        Future<Boolean> future = es.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                try {
                    //Thread.sleep(110000L);
                   return CheckDBAvailalbeStatusUtil(dbKey);
                } catch (Exception e) {
                    logger.error("Check db status failed,", e);
                    return false;
                }
            }
        });
        try {
            return future.get(200L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("获取数据库状态失败", e);
            System.out.println(dbKey + "=" + future.cancel(true));
            return false;
        }
    }
    
    public boolean CheckDBAvailalbeStatusUtil(final String dbKey) {
        boolean isAvailable = true;
        DBType dbType = DBType.MYSQL;
        String sql = null;
        switch (dbType) {
            case MYSQL:
                sql = "select 'x' ";
                break;
            case ORACLE:
                sql = "select * from dual";
                break;
            default:
                throw new IllegalArgumentException("数据库类型出错误，请检查配置！");
        }
        try {
            jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.warn("连接该db出错，sql=" + sql + ",dbKey=" + dbKey, e);
            isAvailable = false;
        }
        return isAvailable;
    }
    private static void insertMaster(JdbcTemplate tddlJT, int number) throws DataAccessException {
        String sql = "insert into users_0 (user_id,name,address) values (?,?,?)";
        Object[] arguments = new Object[] { number, "wangzhaofeng", "shandong" };
        tddlJT.update(sql, arguments);

    }
        /**
     * Setter method for property <tt>isUseFutureTask</tt>.
     * 
     * @param isUseFutureTask value to be assigned to property isUseFutureTask
     */
    public void setIsUseFutureTask(Boolean isUseFutureTask) {
        this.isUseFutureTask = isUseFutureTask;
    }

    /**
     * Getter method for property <tt>isUseFutureTask</tt>.
     * 
     * @return property value of isUseFutureTask
     */
    public Boolean getIsUseFutureTask() {
        return isUseFutureTask;
    }
    public static void main(String args[]) {
        if (args.length != 2) {
            throw new IllegalArgumentException("The args can not  be null");
        }
        int number = Integer.valueOf(args[0]);
        boolean isUseFutureTask = Boolean.valueOf(args[1]);
        
        System.out.println("#############################");
        FutureTaskTest test = new FutureTaskTest(isUseFutureTask);
        for (int i = 0; i < number; i++) {
           System.out.println("db-" + i + " Result =" + test.isAvailableDB2("db-" + i));
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + i);
        }
        test.es.toString();
        try {
            //Thread.sleep(1000000);
        } catch (Exception e) {

        }
        System.exit(0);
    }
}
