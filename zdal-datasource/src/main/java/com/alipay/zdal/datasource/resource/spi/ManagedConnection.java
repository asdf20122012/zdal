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

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import com.alipay.zdal.datasource.resource.ResourceException;
import com.alipay.zdal.datasource.transaction.NotSupportedException;

/**
 * A ManagedConnection instance represents a connection to the underlying
 * recource. A ManagedConnection provides access to the two transaction
 * interfaces, XAResource and LocalTransaction. These interfaces are used to
 * manage transactions on the resource.
 */
public interface ManagedConnection
{
   /**
	 * Creates a new connection handle for the underlying connection.
	 * 
	 * @param subject the subject
	 * @param cxRequestInfo the connection request info
	 * @throws ResourceException for a generic error 
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
    * @throws SecurityException for a security problem
    * @throws CommException for a communication failure with the EIS
    * @throws EISSystemException for an error from the EIS
	 */
   Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException;

   /**
    * Destroys the connection to the underlying resource.
    * 
    * @throws ResourceException for a generic error
    * @throws IllegalStateException if the connection is not a legal state for destruction 
    */
   void destroy() throws ResourceException;

   /**
    * Application server calls this to force cleanup of connection.
    * @throws ResourceException for a generic error
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
    * @throws IllegalStateException if the connection is not a legal state for cleanup 
    */
   void cleanup() throws ResourceException;

   /**
    * Associates a new application level connection handle with the connection.
    * 
    * @param connection the connection
    * @throws ResourceException for a generic error
    * @throws IllegalStateException for an illegal state
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
    */
   void associateConnection(Object connection) throws ResourceException;
   
   /**
	 * Adds a connection event listener
	 * 
	 * @param listener the listener
	 */
   void addConnectionEventListener(ConnectionEventListener listener);

   /**
	 * Removes a connection event listener
	 * 
	 * @param listener the listener
	 */
   void removeConnectionEventListener(ConnectionEventListener listener);

   /**
    * Returns an XAResource instance.
    * 
    * @return the XAResource
    * @throws ResourceException for a generic error
    * @throws NotSupportedException if not supported
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
    */
   XAResource getXAResource() throws ResourceException;
   
   /**
	 * Returns a LocalTransaction instance.
    * 
    * @return the local transaction
    * @throws ResourceException for a generic error
    * @throws NotSupportedException if not supported
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
	 */
   LocalTransaction getLocalTransaction() throws ResourceException;

   /**
    * Gets metadata inormation for this instances underlying resource manager
    * instance.
    * 
    * @return the managed connection meta data
    * @throws ResourceException for a generic error
    * @throws NotSupportedException if not supported
    */
   ManagedConnectionMetaData getMetaData() throws ResourceException;
   
   /**
	 * Gets the logwriter for this instance.
    * 
    * @return the log writer
    * @throws ResourceException for a generic error
	 */
   PrintWriter getLogWriter() throws ResourceException;

   /**
	 * Sets the logwriter for this instance.
    * 
    * @param out the writer
    * @throws ResourceException for a generic error
    * @throws ResourceAdapterInternalException for an internal error in the
    *            resource adapter
	 */
   void setLogWriter(PrintWriter out) throws ResourceException;
}