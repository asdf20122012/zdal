/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2007 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable;

import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.scalable.impl.DataSourceBindingChangeException;

/**
 * 
 * Define an interface to handle the binding changed between logical data source and physical data source.
 * <p> In our design, the DataSource maintains connections between an application server with a database server, 
 * to support redirect connection with a new database server without reboot the application server. We design a 
 * DataSource implements this interface to response once a new physical data base change has been received. 
 *   
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public interface DataSourceBindingChangeSupport {

	/**
	 * 
	 * @param dataSourceConfigurationDO
	 * @throws DataSourceBindingChangeException
	 */
	void onDataSourceBindingChanged(LocalTxDataSourceDO dataSourceConfigurationDO) throws DataSourceBindingChangeException;
}
