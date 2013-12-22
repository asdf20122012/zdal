/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import com.alipay.zdal.client.jdbc.ZdalDataSource;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: XtsZdalDataSourceTest.java, v 0.1 2012-9-5 ÏÂÎç04:29:55 xiaoqing.zhouxq Exp $
 */
public class ZdalDataSourceTest {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        String appName = "pcredittrans";
        String appDsName = "pcttransShard";
        String dbmode = "dev";
        String idcName = "gz00";
        String zdataconsoleUrl = "http://zdataconsole.stable.alipay.net:8080";
        String configPath = "./config";
        ZdalDataSource dataSource = new ZdalDataSource();
        dataSource.setDiffMasterSlaveRule(true);
        dataSource.setAppName(appName);
        dataSource.setAppDsName(appDsName);
        dataSource.setDbmode(dbmode);
        dataSource.setZone(idcName);

        //        dataSource.setZdalConfigLocal(true);
        dataSource.setZdataconsoleUrl(zdataconsoleUrl);
        dataSource.setConfigPath(configPath);
        //        dataSource.setPrefill(true);
        dataSource.init();

        Thread.sleep(1000L);

        for (int i = 0; i < 1; i++) {
            new ThreadTask(dataSource).start();
        }

    }

    private static class ThreadTask extends Thread {
        private DataSource dataSource;

        public ThreadTask(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public void run() {
            for (int i = 0; i < 1; i++) {
                Connection conn = null;
                PreparedStatement pst1 = null;
                ResultSet rs = null;
                try {
                    conn = dataSource.getConnection();
                    pst1 = conn
                        .prepareStatement("select /*MS-PCREDITTRANS-ACCOUNT-SELECT-BY-ACCOUNT-NO*/ account_no, alias_name, prod_id, prin_inst_id, ATTRIBUTE_INST_ID, status, PAY_STATUS, asset_category, account_type, balance_direction, currency, credit_limit, prin_balance, SYS_BU_AMOUNT, SYS_RF_AMOUNT, prev_prin_balance, prev_trans_date, memo, gmt_last_trans, gmt_created, gmt_modified, operator, last_operator from pct_account where (account_no = ?)  ");
                    pst1.setString(1, "50019201013087640156");

                    //                    pst1.setInt(2, 1000);
                    rs = pst1.executeQuery();
                    int j = 0;
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + "----" + j);
                        j++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (pst1 != null) {
                            pst1.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
