package com.alipay.zdal.common.exception.sqlexceptionwrapper;

import java.sql.SQLException;

public class ZdalCommunicationException extends ZdalSQLExceptionWrapper{

	public ZdalCommunicationException(String message,
			SQLException targetSQLESqlException) {
		super(message, targetSQLESqlException);
	}

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3502922457609932248L;

}
