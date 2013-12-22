/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable.impl;

/**
 * 
 * 
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ScaleConnectionPoolException extends Exception {

	public static final String MESSAGE_MASTER_SEMAPHORE_ALIVE = 
			"Both of seamphores still queued threads, Zdal unable to reset permits during reset into master mode.";
	
	public static final String MESSAGE_DEPUTY_SEMAPHORE_ALIVE = 
			"Both of seamphores still queued threads, Zdal unable to reset permits during reset into deputy mode.";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 125133161271557053L;

	private String message;
	
	public ScaleConnectionPoolException(String message){
		this.message = message ; 
	}
	
	public String toString(){
		return message;
	}
}
