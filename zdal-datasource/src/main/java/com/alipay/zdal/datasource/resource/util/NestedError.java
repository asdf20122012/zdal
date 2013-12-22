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
package com.alipay.zdal.datasource.resource.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A common superclass for <tt>Error</tt> classes that can
 * contain a nested <tt>Throwable</tt> detail object.
 *
 * @version <tt>$Revision: 37390 $</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NestedError extends Error implements NestedThrowable {
    /** The nested throwable */
    protected final Throwable nested;

    /**
     * Construct a <tt>NestedError</tt> with the specified 
     * detail message.
     *
     * @param msg  Detail message.
     */
    public NestedError(final String msg) {
        super(msg);
        this.nested = null;
    }

    /**
     * Construct a <tt>NestedError</tt> with the specified
     * detail message and nested <tt>Throwable</tt>.
     *
     * @param msg     Detail message.
     * @param nested  Nested <tt>Throwable</tt>.
     */
    public NestedError(final String msg, final Throwable nested) {
        super(msg);
        this.nested = nested;
        NestedThrowable.Util.checkNested(this, nested);
    }

    /**
     * Construct a <tt>NestedError</tt> with the specified
     * nested <tt>Throwable</tt>.
     *
     * @param nested  Nested <tt>Throwable</tt>.
     */
    public NestedError(final Throwable nested) {
        this(nested.getMessage(), nested);
    }

    /**
     * Construct a <tt>NestedError</tt> with no detail.
     */
    public NestedError() {
        super();
        this.nested = null;
    }

    /**
     * Return the nested <tt>Throwable</tt>.
     *
     * @return  Nested <tt>Throwable</tt>.
     */
    public Throwable getNested() {
        return nested;
    }

    /**
     * Return the nested <tt>Throwable</tt>.
     *
     * <p>For JDK 1.4 compatibility.
     *
     * @return  Nested <tt>Throwable</tt>.
     */
    public Throwable getCause() {
        return nested;
    }

    /**
     * Returns the composite throwable message.
     *
     * @return  The composite throwable message.
     */
    public String getMessage() {
        return NestedThrowable.Util.getMessage(super.getMessage(), nested);
    }

    /**
     * Prints the composite message and the embedded stack trace to the
     * specified print stream.
     *
     * @param stream  Stream to print to.
     */
    public void printStackTrace(final PrintStream stream) {
        if (nested == null || NestedThrowable.PARENT_TRACE_ENABLED) {
            super.printStackTrace(stream);
        }
        NestedThrowable.Util.print(nested, stream);
    }

    /**
     * Prints the composite message and the embedded stack trace to the
     * specified print writer.
     *
     * @param writer  Writer to print to.
     */
    public void printStackTrace(final PrintWriter writer) {
        if (nested == null || NestedThrowable.PARENT_TRACE_ENABLED) {
            super.printStackTrace(writer);
        }
        NestedThrowable.Util.print(nested, writer);
    }

    /**
     * Prints the composite message and the embedded stack trace to 
     * <tt>System.err</tt>.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }
}
