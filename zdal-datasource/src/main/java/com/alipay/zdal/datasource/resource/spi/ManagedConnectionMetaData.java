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

import com.alipay.zdal.datasource.resource.ResourceException;

/**
 * The ManagedConnectionMetaData interface provides information about the
 * underlying resource associated with a ManagedConnetion. The Application
 * Server can use this information to get information at runtime from the
 * underlying resource.
 */
public interface ManagedConnectionMetaData
{
   /**
	 * Returns product name of the underlying resource.
    * 
    * @return the product name
    * @throws ResourceException for a generic error 
	 */
   public String getEISProductName() throws ResourceException;

   /**
	 * Returns product version of the underlying resource.
    * 
    * @return the product version
    * @throws ResourceException for a generic error 
	 */
   public String getEISProductVersion() throws ResourceException;

   /**
	 * Returns the maximum supported number of connections allowed to the
	 * underlying resource.
    * 
    * @return the maximum number of connections
    * @throws ResourceException for a generic error 
	 */
   public int getMaxConnections() throws ResourceException;

   /**
	 * Returns user name associated with the underlying connection.
    * 
    * @return the user name
    * @throws ResourceException for a generic error 
	 */
   public String getUserName() throws ResourceException;
}