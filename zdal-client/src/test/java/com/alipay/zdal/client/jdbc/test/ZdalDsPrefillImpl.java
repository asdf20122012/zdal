/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
//package com.alipay.tradecore.common.dal.util;

///**
// * 
// * @author zhongxiang.li
// * @version $Id: ZdalDsPrefillImpl.java, v 0.1 2013-8-5 上午11:59:21 zhongxiang.li Exp $
// */
//public class ZdalDsPrefillImpl implements ZdalPrefill, InitializingBean {
//
//    /** sysInitLog */
//    private static final Logger      sysInitLog                = LoggerFactory
//                                                                   .getLogger(LoggerNames.SYSINIT);
//
//    /** 主库10拆50后的日期 */
//    private static final String      TRADE_DATE                = "20140101";
//
//    /**模拟主库交易号(seq范围[0,799999]) */
//    private static final String      FAKE_MAIN_TRADENO_SEQ     = "588888";
//
//    /**模拟failover库交易(8seq范围[800000,999999]) */
//    private static final String      FAKE_FAILOVER_TRADENO_SEQ = "888888";
//
//    /** 交易号尾字符 */
//    private static final String      TRADE_NO_END              = "0";
//
//    /** 交易主库(50个)、交易failover库(10个)对应的虚拟交易号，共60个。 */
//    public static final List<String> fakeTradeNo4Trade         = new ArrayList<String>(60);
//
//    /** TradeBaseDAO */
//    private TradeBaseDAO             tradeBaseDAO;
//
//    /**  */
//    private ZdalDataSource           dataSource;
//
//    /**路由到交易表的模拟交易号 */
//    static {
//
//        //虚拟主库的交易号
//        for (int i = 0, j = 0; i < 50; i++) {
//            j = i * 2;
//            fakeTradeNo4Trade.add(TRADE_DATE + FAKE_MAIN_TRADENO_SEQ
//                                  + (j < 10 ? TRADE_NO_END + j : j));
//        }
//
//        //虚拟failover库的交易号
//        for (int i = 0; i < 10; i++) {
//            fakeTradeNo4Trade.add(TRADE_DATE + FAKE_FAILOVER_TRADENO_SEQ + i + TRADE_NO_END);
//        }
//    };
//
//    /** 
//     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
//     */
//    public void afterPropertiesSet() throws Exception {
//        dataSource.prefillZdal(this);
//    }
//
//    /** 
//     * @see com.alipay.zdal.client.jdbc.ZdalPrefill#prefill()
//     */
//    public void prefill() {
//
//        for (String tradeNo : fakeTradeNo4Trade) {
//
//            // 是否有异常
//            boolean hasError = false;
//
//            long startTime = System.currentTimeMillis();
//
//            try {
//
//                tradeBaseDAO.getByTradeNo(tradeNo);
//
//            } catch (Throwable ex) {
//                hasError = true;
//                sysInitLog.error("初始化系统资源时，发生异常,tradeNo=" + tradeNo, ex);
//            } finally {
//
//                long elapseTime = System.currentTimeMillis() - startTime;
//
//                sysInitLog.warn("[(INIT_DS," + getDsName() + (hasError ? ",N," : ",Y,")
//                                + elapseTime + "ms)]");
//
//            }
//        }
//
//    }
//
//    @SuppressWarnings("unchecked")
//    private String getDsName() {
//
//        Map<String, DataSource> mapDs = (Map<String, DataSource>) ThreadLocalMap
//            .get(ThreadLocalString.GET_ID_AND_DATABASE);
//
//        if (mapDs != null) {
//            for (Map.Entry<String, DataSource> entry : mapDs.entrySet()) {
//                return entry.getKey();
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * Setter method for property <tt>tradeBaseDAO</tt>.
//     * 
//     * @param tradeBaseDAO value to be assigned to property tradeBaseDAO
//     */
//    public void setTradeBaseDAO(TradeBaseDAO tradeBaseDAO) {
//        this.tradeBaseDAO = tradeBaseDAO;
//    }
//
//    /**
//     * Setter method for property <tt>dataSource</tt>.
//     * 
//     * @param dataSource value to be assigned to property dataSource
//     */
//    public void setDataSource(ZdalDataSource dataSource) {
//        this.dataSource = dataSource;
//    }

//}
