/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.alipay.zdal.datasource.resource.adapter.jdbc;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Reference;
import javax.sql.DataSource;

import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.datasource.client.util.ZConstants;
import com.alipay.zdal.datasource.exception.ConnectionNotAvailableException;
import com.alipay.zdal.datasource.exception.DBNotAvailableException;
import com.alipay.zdal.datasource.exception.NestedSQLException;
import com.alipay.zdal.datasource.exception.TimeOutException;
import com.alipay.zdal.datasource.resource.Referenceable;
import com.alipay.zdal.datasource.resource.ResourceException;
import com.alipay.zdal.datasource.resource.spi.ConnectionManager;
import com.alipay.zdal.datasource.resource.spi.ConnectionRequestInfo;
import com.alipay.zdal.datasource.tm.TransactionTimeoutConfiguration;
import com.alipay.zdal.datasource.transaction.RollbackException;

/**
 * WrapperDataSource
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @author <a href="mailto:adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 57189 $
 */
public class WrapperDataSource implements Referenceable, DataSource, Serializable {
    /**  應用看到的數據源*/
    ZDataSource                                       zdatasource      = null;

    static final long                                 serialVersionUID = 3570285419164793501L;

    private final BaseWrapperManagedConnectionFactory mcf;
    private final ConnectionManager                   cm;
    private final String                              dataSourceName;

    private Reference                                 reference;

    /**
     * @param mcf
     * @param cm
     * @param dataSourceName
     * @param zdatasource
     */
    public WrapperDataSource(final BaseWrapperManagedConnectionFactory mcf,
                             final ConnectionManager cm, String dataSourceName,
                             ZDataSource zdatasource) {
        this.mcf = mcf;
        this.cm = cm;
        this.dataSourceName = dataSourceName;
        this.zdatasource = zdatasource;
    }

    public PrintWriter getLogWriter() throws SQLException {
        // TODO: implement this javax.sql.DataSource method
        return null;
    }

    public void setLogWriter(PrintWriter param1) throws SQLException {
        // TODO: implement this javax.sql.DataSource method
    }

    public int getLoginTimeout() throws SQLException {
        // TODO: implement this javax.sql.DataSource method
        return 0;
    }

    public void setLoginTimeout(int param1) throws SQLException {
        // TODO: implement this javax.sql.DataSource method
    }

    public Connection getConnection() throws SQLException {
        try {
            WrappedConnection wc = (WrappedConnection) cm.allocateConnection(mcf, null);
            wc.setDataSource(this);
            wc.setZdatasource(zdatasource);
            return wc;
        } catch (ResourceException re) {
            if (ZConstants.ERROR_CODE_DB_NOT_AVAILABLE.equals(re.getErrorCode())) {
                throw new DBNotAvailableException(re);
            } else if (ZConstants.ERROR_CODE_CONNECTION_NOT_AVAILABLE.equals(re.getErrorCode())) {
                throw new ConnectionNotAvailableException(re);
            } else if (ZConstants.ERROR_CODE_CONNECTION_TIMEOUT.equals(re.getErrorCode())) {
                throw new TimeOutException(re);
            } else {
                throw new NestedSQLException(re);
            }
        }
    }

    public Connection getConnection(String user, String password) throws SQLException {
        ConnectionRequestInfo cri = new WrappedConnectionRequestInfo(user, password);
        try {
            WrappedConnection wc = (WrappedConnection) cm.allocateConnection(mcf, cri);
            wc.setDataSource(this);
            wc.setZdatasource(zdatasource);
            return wc;
        } catch (ResourceException re) {
            throw new NestedSQLException(re);
        }
    }

    public void setReference(final Reference reference) {
        this.reference = reference;
    }

    public Reference getReference() {
        return reference;
    }

    protected int getTimeLeftBeforeTransactionTimeout() throws SQLException {
        try {
            if (cm instanceof TransactionTimeoutConfiguration) {
                long timeout = ((TransactionTimeoutConfiguration) cm)
                    .getTimeLeftBeforeTransactionTimeout(true);
                // No timeout
                if (timeout == -1) {
                    return -1;
                }

                // Round up to the nearest second
                long result = timeout / 1000;
                if ((result % 1000) != 0) 
                    ++result;

                return (int) result;
            } else {
                return -1;
            }
        } catch (RollbackException e) {
            throw new NestedSQLException(e);
        }
    }

    // jdk 6
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

}
