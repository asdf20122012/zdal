package com.alipay.zdal.client.jdbc.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParameterHandler {
	void setParameter(PreparedStatement stmt, Object[] args) throws SQLException;
}
