/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.parser.result;

import com.alipay.zdal.parser.exceptions.SqlParserException;
import com.alipay.zdal.parser.visitor.ZdalMySqlSchemaStatVisitor;
import com.alipay.zdal.parser.visitor.ZdalOracleSchemaStatVisitor;
import com.alipay.zdal.parser.visitor.ZdalSchemaStatVisitor;

/**
 * 创建sqlparserresult的工程类.
 * @author xiaoqing.zhouxq
 * @version $Id: SqlParserResultFactory.java, v 0.1 2012-5-21 下午03:18:34 xiaoqing.zhouxq Exp $
 */
public class SqlParserResultFactory {

    public static SqlParserResult createSqlParserResult(ZdalSchemaStatVisitor visitor,
                                                        boolean isMysql) {
        if (isMysql == true) {
            if (!(visitor instanceof ZdalMySqlSchemaStatVisitor)) {
                throw new SqlParserException("ERROR ## the visitor is not MysqlSchemaStatVisitor");
            }
            return new MysqlSqlParserResult(visitor);
        } else {
            if (!(visitor instanceof ZdalOracleSchemaStatVisitor)) {
                throw new SqlParserException("ERROR ## the visitor is not OracleSchemaStatVisitor");
            }
            return new OracleSqlParserResult(visitor);
        }
    }

}
