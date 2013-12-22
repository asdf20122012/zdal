/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.zdal.parser.sql.dialect.mysql.ast.statement;

import com.alipay.zdal.parser.sql.ast.SQLExpr;
import com.alipay.zdal.parser.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * 
 * @author ²®ÑÀ
 * @version $Id: MySqlBinlogStatement.java, v 0.1 2012-11-17 ÏÂÎç3:31:49 Exp $
 */
public class MySqlBinlogStatement extends MySqlStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLExpr           expr;

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
    }
}
