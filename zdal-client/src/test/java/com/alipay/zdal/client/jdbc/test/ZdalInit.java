/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.client.jdbc.test;

/**
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: ZdalInit.java, v 0.1 2012-5-15 上午09:18:16 xiaoqing.zhouxq Exp $
 */
public interface ZdalInit {

    /**
     * 初始化分库分表信息.
     * @param dbInfo[] 所有分库的连接信息.
     * @param tableCount 每个分库新建几张表.
     * @param tableSuffixWidth 表名后缀的长度.
     */
    public void initDatabase(DbInfo[] dbInfos, int tableCount, int tableSuffixWidth);
}
