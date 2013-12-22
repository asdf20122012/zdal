package com.alipay.zdal.client.jdbc.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alipay.zdal.client.jdbc.ZdalStatement;



public class SimpleTResultSet extends BaseTResultSet {
	
	private boolean inited;

	public SimpleTResultSet(ZdalStatement statementProxy,
			List<ResultSet> resultSets) {
		super(statementProxy, resultSets);
	}

	@Override
	protected boolean internNext() throws SQLException {
		if(!inited) {
			inited = true;
			reducer();
		}
		for (int i = currentIndex; i < actualResultSets.size(); ++i) {
			if (actualResultSets.get(i).next()) {
				currentIndex = i;
				limitTo--;
				return true;
			}
		}
		
		return false;
	}
	
	protected ResultSet reducer() throws SQLException {
		if(actualResultSets.size() == 0) {
			throw new RuntimeException("This should not happen!!");
		}
		if(actualResultSets.size() == 1) {
			return actualResultSets.get(0);
		}
		for(int i = 0; i < limitFrom; i++) {
			next();
		}
		return null;
	}

}
