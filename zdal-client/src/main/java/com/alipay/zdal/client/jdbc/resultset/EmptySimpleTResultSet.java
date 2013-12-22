package com.alipay.zdal.client.jdbc.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alipay.zdal.client.jdbc.ZdalStatement;



/**
 * 调用rs.next永远返回false的空结果集。
 * 主要用于一些特殊的情况
 * 
 * @author shenxun
 *
 */
public class EmptySimpleTResultSet extends AbstractTResultSet{

	public EmptySimpleTResultSet(ZdalStatement statementProxy,
			List<ResultSet> resultSets) {
		super(statementProxy, resultSets);
	}
	
	@Override
	public boolean next() throws SQLException {
		return false;
	}
}
