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
package com.alipay.zdal.datasource.resource.spi;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;

import javax.security.auth.Subject;

import com.alipay.zdal.datasource.resource.ResourceException;
import com.alipay.zdal.datasource.transaction.NotSupportedException;

/**
 * A ManagedConnectionFactory is a factory for the creation of
 * ManagedConnection objects and ConnectionFactory objects. It provides methods
 * which can be used to match ManagedConnetions.
 */
public interface ManagedConnectionFactory extends Serializable {
    /**
     * Creates a connection factory instance. The connection manager is provided
     * by the resource adapter.
     * 
     * @return the connection factory
     * @throws ResourceException for a generic error 
     * @throws ResourceAdapterInternalException for an internal error in the
     *            resource adapter
     */
    public Object createConnectionFactory() throws ResourceException;

    /**
     * Creates a connection factory instance. the connection manager is provided
     * by the application server
     * 
     * @param cxManager the connection manager
     * @return the connection factory
     * @throws ResourceException for a generic error 
     * @throws ResourceAdapterInternalException for an internal error in the
     *            resource adapter
     */
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException;

    /**
     * Creates a connection factory instance. the connection manager is provided
    * by the application server
    * 
    * @param cxManager the connection manager
    * @return the connection factory
    * @throws ResourceException for a generic error 
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
     */
    public Object createConnectionFactory(ConnectionManager cxManager, String dataSourceName)
                                                                                             throws ResourceException;

    /**
     * Creates a new ManagedConnection
     * 
     * @param subject the subject
     * @param cxRequestInfo the connection request info
     * @return the managed connection
     * @throws ResourceException for a generic error 
     */
    public ManagedConnection createManagedConnection(Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo)
                                                                                         throws ResourceException;

    /**
     * Returns a matching connection from the set.
     * 
     * @param connectionSet the connection set
     * @param subject the subject
     * @param cxRequestInfo the connection request info
     * @return the managed connection
     * @throws ResourceException for a generic error 
     * @throws ResourceAdapterInternalException for an internal error in the
     *            resource adapter
     * @throws SecurityException for a security problem
     * @throws NotSupportedException if not supported
     */
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo)
                                                                                         throws ResourceException;

    /**
     * Gets the logwriter for this instance.
     * 
     * @return the log writer
     * @throws ResourceException for a generic error 
     */
    public PrintWriter getLogWriter() throws ResourceException;

    /**
     * Sets the logwriter for this instance.
     * 
     * @param out the log writer
     * @throws ResourceException for a generic error 
     * @throws ResourceAdapterInternalException for an internal error in the
     *            resource adapter
     */
    public void setLogWriter(PrintWriter out) throws ResourceException;

    /**
     * Tests object for equality
     * 
     * @param other the other object
     * @return true when equals, false otherwise
     */
    public boolean equals(Object other);

    /**
     * Generates a hashCode for this object
     * 
     * @return the hash code
     */
    public int hashCode();
}