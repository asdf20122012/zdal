/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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
package com.alipay.zdal.datasource.exception;

import java.sql.SQLException;

/**
 * A common superclass for <tt>SQLException</tt> classes that can contain
 * a nested <tt>Throwable</tt> detail object.
 *
 * @version <tt>$Revision: 37390 $</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NestedSQLException extends SQLException {
    /**  */
    private static final long serialVersionUID = -441747636494736964L;

    /**
     * Construct a <tt>NestedSQLException</tt> with the specified detail 
     * message.
     *
     * @param msg  Detail message.
     */
    public NestedSQLException(final String msg) {
        super(msg);
    }

    /**
     * Construct a <tt>NestedSQLException</tt> with the specified detail 
     * message and nested <tt>Throwable</tt>.
     *
     * @param msg     Detail message.
     * @param nested  Nested <tt>Throwable</tt>.
     */
    public NestedSQLException(final String msg, final Throwable nested) {
        super(msg, nested);
    }

    /**
     * Construct a <tt>NestedSQLException</tt> with the specified
     * nested <tt>Throwable</tt>.
     *
     * @param nested  Nested <tt>Throwable</tt>.
     */
    public NestedSQLException(final Throwable nested) {
        this(nested.getMessage(), nested);
    }

    /**
     * Construct a <tt>NestedSQLException</tt>.
     *
     * @param msg     Detail message.
     * @param state   SQL state message.
     */
    public NestedSQLException(final String msg, final String state) {
        super(msg, state);
    }

    /**
     * Construct a <tt>NestedSQLException</tt>.
     *
     * @param msg     Detail message.
     * @param state   SQL state message.
     * @param code    SQL vendor code.
     */
    public NestedSQLException(final String msg, final String state, final int code) {
        super(msg, state, code);
    }

}
