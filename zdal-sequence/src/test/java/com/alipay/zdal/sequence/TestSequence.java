/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.sequence;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.sequence.impl.MultipleSequenceDao;
import com.alipay.zdal.sequence.impl.MultipleSequenceFactory;

/**
 * 
 * @author 伯牙
 * @version $Id: Test.java, v 0.1 2013-4-3 上午11:17:07 Exp $
 */
public class TestSequence {

    private static final String  URL_0                   = "jdbc:mysql://mypay5.devdb.alipay.net:3306/finext00";
    private static final String  USERNAME_0              = "finext";
    private static final String  PASSWORD_0              = "ali88";

    private static final String  URL_1                   = "jdbc:mysql://mypay5.devdb.alipay.net:3306/finext01";
    private static final String  USERNAME_1              = "finext";
    private static final String  PASSWORD_1              = "ali88";

    private static final String  TABLENAME               = "fin_sequence";
    private static final String  SEQNAME_COLUMNNAME      = "SEQ_NAME";
    private static final String  SEQCURRENT_COLUMNNAME   = "SEQ_CURRENT";
    private static final String  MIN_VALUE_COLUMNNAME    = "MIN_VALUE";
    private static final String  MAX_VALUE_COLUMNNAME    = "MAX_VALUE";
    private static final String  STEP_COLUMNNAME         = "STEP";
    private static final String  GMT_CREATE_COLUMNNAME   = "GMT_CREATE";
    private static final String  GMT_MODIFIED_COLUMNNAME = "GMT_MODIFIED";

    private static final String  SEQUENCENAME            = "zdal-test-sequence1";
    private static final boolean ADJUST                  = true;

    public static void main(String[] args) throws Exception {
        ZDataSource dataSource1 = new ZDataSource(createLocalTxDataSourceDO(URL_0, USERNAME_0,
            PASSWORD_0));
        ZDataSource dataSource2 = new ZDataSource(createLocalTxDataSourceDO(URL_1, USERNAME_1,
            PASSWORD_1));

        List<DataSource> dataSources = new ArrayList<DataSource>();
        dataSources.add(dataSource1);
        dataSources.add(dataSource2);

        //        String appName = "supergw";
        //        String appDsName = "supergwSequenceDataSource";
        //        String dbmode = "dev";
        //        String zone = "gz00";
        //        String configPath = "./config";
        //        ZdalDataSource dataSource = new ZdalDataSource();
        //        dataSource.setAppDsName(appDsName);
        //        dataSource.setAppName(appName);
        //        dataSource.setDbmode(dbmode);
        //        dataSource.setZone(zone);
        //        dataSource.setZdataconsoleUrl("http://zdataconsole.stable.alipay.net:8080");
        //        dataSource.setConfigPath(configPath);
        //        dataSource.init();

        MultipleSequenceDao sequenceDao = new MultipleSequenceDao();
        //        sequenceDao.setZdalDataSource(dataSource);
        sequenceDao.setDataSourceList(dataSources);
        sequenceDao.setAdjust(ADJUST);
        sequenceDao.setTableName("fin_sequence");
        sequenceDao.setNameColumnName(SEQNAME_COLUMNNAME);
        sequenceDao.setValueColumnName(SEQCURRENT_COLUMNNAME);
        sequenceDao.setMinValueColumnName(MIN_VALUE_COLUMNNAME);
        sequenceDao.setMaxValueColumnName(MAX_VALUE_COLUMNNAME);
        sequenceDao.setInnerStepColumnName(STEP_COLUMNNAME);
        sequenceDao.setGmtCreateColumnName(GMT_CREATE_COLUMNNAME);
        sequenceDao.setGmtModifiedColumnName(GMT_MODIFIED_COLUMNNAME);
        sequenceDao.init();

        MultipleSequenceFactory factory = new MultipleSequenceFactory();
        factory.setMultipleSequenceDao(sequenceDao);
        factory.init();
        for (;;) {
            long seq = factory.getNextValue("SEQ_SERIAL_WZCB_S01_18");
            System.out.println(seq);
        }

        //        MultipleSequence sequence = new MultipleSequence();
        //        sequence.setSequenceDao(sequenceDao);
        //        sequence.setSequenceName(SEQUENCENAME);
        //        sequence.init();
        //
        //        Set<SequenceRange> ranges = new HashSet<SequenceRange>();
        //        for (int i = 0; i < 100; i++) {
        //            SequenceRange range = sequenceDao.nextRange(SEQUENCENAME, 0, Long.MAX_VALUE, 1000);
        //            System.out.println("start = " + range.getMin() + "- end = " + range.getMax());
        //            ranges.add(range);
        //        }
        //
        //        System.out.println("the range size = " + ranges.size());
    }

    private static LocalTxDataSourceDO createLocalTxDataSourceDO(String url, String userName,
                                                                 String password) {
        LocalTxDataSourceDO dataSourceDO = new LocalTxDataSourceDO();
        dataSourceDO.setDsName(url);
        dataSourceDO.setUserName(userName);
        dataSourceDO.setConnectionURL(url);
        dataSourceDO.setPassWord(password);
        dataSourceDO.setDriverClass("com.mysql.jdbc.Driver");
        dataSourceDO.setExceptionSorterClassName(MySQLExceptionSorter.class.getName());
        dataSourceDO.setMinPoolSize(1);
        dataSourceDO.setMaxPoolSize(1);//最大，最小连接数，根据应用需要的sequence来确定，有几个sequence就建立几个连接数，最大=最小.
        return dataSourceDO;
    }
}
