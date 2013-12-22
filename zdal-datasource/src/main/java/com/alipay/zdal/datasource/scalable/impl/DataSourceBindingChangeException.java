/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable.impl;

import com.alipay.zdal.common.lang.StringUtil;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;

/**
 * <p>
 * This exception being thrown occurs during data source was trying to create a new connection pool
 * with database.
 * It might be configuration error, or unable to build connection.
 * </p>
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class DataSourceBindingChangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6320226953487844666L;

	private Throwable cause;
	
	private String message;
	
	public DataSourceBindingChangeException(Throwable cause, LocalTxDataSourceDO dataSourceConfigurationDO){
		this.cause = cause;
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("An unexcepected exception orrcured when a datasource binding has been reseted with new setting ");
		if( !StringUtil.isEmpty(dataSourceConfigurationDO.getDsName()) ){
			strBuilder.append("DS name: " + dataSourceConfigurationDO.getDsName());
		}else{
			strBuilder.append("DS name: " + "null");
		}
		if( !StringUtil.isEmpty(dataSourceConfigurationDO.getConnectionURL()) ){
			strBuilder.append(", DS URL: " + dataSourceConfigurationDO.getConnectionURL());
		}else{
			strBuilder.append("DS URL: " + "null");
		}
		if( !StringUtil.isEmpty(dataSourceConfigurationDO.getUserName()) ){
			strBuilder.append(", DS user name: " + dataSourceConfigurationDO.getUserName());
		}else{
			strBuilder.append("DS user name: " + "null");
		}
		if( !StringUtil.isEmpty(dataSourceConfigurationDO.getEncPassword()) ){
			strBuilder.append(", DS enc password: " + dataSourceConfigurationDO.getEncPassword());
		}else{
			strBuilder.append("DS enc password: " + "null");
		}
		message = strBuilder.toString();
	}
	
	/**
	 * 
	 */
	public Throwable getCause(){
		return cause;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage(){
		return message;
	}
}
