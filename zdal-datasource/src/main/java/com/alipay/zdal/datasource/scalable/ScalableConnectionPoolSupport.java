/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable;

import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;


/**
 * <p>Defined an interface to support perform resize the connection pool maximum size.
 * 
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public interface ScalableConnectionPoolSupport {

	/**
	 * To handle the changes of connection pool max size by the push service.
	 * @param poolMinSize 0 indicates no change with poolMinSize
	 * @param poolMaxSize 0 indicates no change with poolMaxSize
	 */
	void resetConnectionPoolSize(int poolMinSize, int poolMaxSize) throws ScaleConnectionPoolException;
	 
}
