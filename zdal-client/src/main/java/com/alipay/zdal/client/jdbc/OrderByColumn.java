package com.alipay.zdal.client.jdbc;

/**
 * @author nianbing
 */
public class OrderByColumn {
	private String columnName;
	private boolean asc = true;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
}
