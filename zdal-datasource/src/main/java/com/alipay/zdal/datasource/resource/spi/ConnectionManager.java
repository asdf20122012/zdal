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

import java.io.Serializable;

import com.alipay.zdal.datasource.resource.ResourceException;

/**
 * The ConnectionManager interface provides the hook which allows a resource
 * adapter to pass a connection to the Application Server. The Application
 * Server implements this interface in order to control QoS services to the
 * resource adapter for connection pools.
 */

public interface ConnectionManager extends Serializable {
    /**
     * Gets called by the resource adapter's connection factory. The resource adapter
     * uses this method to pass its managed connection factory to the connection manager.
     * 
     * @param mcf the managed connection factory
     * @param cxRequestInfo the connection request info
     * @return the connection handle
     * @throws ResourceException for an generic error
     */
    Object allocateConnection(ManagedConnectionFactory mcf,
                                     ConnectionRequestInfo cxRequestInfo) throws ResourceException;
}