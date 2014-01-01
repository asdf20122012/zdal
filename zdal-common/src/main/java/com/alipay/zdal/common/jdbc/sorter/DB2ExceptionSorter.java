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
package com.alipay.zdal.common.jdbc.sorter;

import java.io.Serializable;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * A DB2ExceptionSorter current only supporting the Type 4 Universal driver.
 * Note, currently the DB2 JDBC developers guide only reports a few error codes.
 * The code -9999 implies that the condition does not have a related code.
 * 
 * TODO DB2 CLI
 * 需要根据DB2的错误码判断是否需要把对应的连接踢掉.
 * @author 伯牙
 * @version $Id: DB2ExceptionSorter.java, v 0.1 2014-1-1 下午08:29:16 Exp $
 */
public class DB2ExceptionSorter implements ExceptionSorter, Serializable {

    /** The logger */
    private static final Logger  logger           = Logger.getLogger(DB2ExceptionSorter.class);

    /** The trace */
    private static final boolean trace            = logger.isTraceEnabled();

    /** The serialVersionUID */
    private static final long    serialVersionUID = -4724550353693159378L;

    public boolean isExceptionFatal(final SQLException e) {

        final int code = Math.abs(e.getErrorCode());
        boolean isFatal = false;

        if (code == 4499) {
            isFatal = true;
        }

        if (trace) {
            logger.trace("Evaluated SQL error code " + code + " isException returned " + isFatal);
        }

        return isFatal;

    }

}
