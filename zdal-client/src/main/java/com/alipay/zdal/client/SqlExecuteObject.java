package com.alipay.zdal.client;

import java.util.List;

/**
 * 用于执行SqlExecuto时传递sql语句和属性对象的Bean
 * @author shenxun
 *
 *
 */
public class SqlExecuteObject {
	private String sql;

	private List<Object> params;

	public String getSql() {
		return sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

}
