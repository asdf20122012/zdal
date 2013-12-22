package com.alipay.zdal.datasource.resource.adapter.jdbc;

import java.sql.SQLException;

import com.alipay.zdal.common.jdbc.sorter.ExceptionSorter;

/**
 * A GenericExceptionSorter returning true for all exceptions.
 * 
 * @author <a href="mailto:weston.price@jboss.org>Weston Price</a>
 * @version $Revision: 1.1 $
 */
public class GenericExceptionSorter implements ExceptionSorter {

    /** 
    * @see com.alipay.zdal.datasource.resource.adapter.jdbc.ExceptionSorter#isExceptionFatal(java.sql.SQLException)
    */
    public boolean isExceptionFatal(final SQLException e) {
        return true;
    }

}
