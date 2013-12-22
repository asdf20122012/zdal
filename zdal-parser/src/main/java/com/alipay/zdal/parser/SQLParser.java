/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.parser;

import com.alipay.zdal.parser.result.SqlParserResult;

/**
 * SQL解析器基类
 * 
 * @author xiaoqing.zhouxq
 * @version $Id: SQLParser.java, v 0.1 2012-5-22 上午09:59:15 xiaoqing.zhouxq Exp $
 */
public interface SQLParser {
    /**
     * 解析sql语句，包括mysql和oracle的sql语句.
     * @param sql 
     * @param isMySQL true=mysql,false=oracle.
     * @return
     */
    SqlParserResult parse(String sql, boolean isMySQL);
}
