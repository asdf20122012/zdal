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
 * A ResourceAdapterInternalException indicates any system level error
 * conditions related to a resource adapter. Examples are invalid
 * configuration, failure to create a connection to an underlying resource,
 * other error condition internal to the resource adapter.
 */
public class ResourceAdapterInternalException extends ResourceException
{
   /**
    * Create an exception.
    */
   public ResourceAdapterInternalException()
   {
      super();
   }
   
   /**
	 * Create an exception with a reason.
	 */
   public ResourceAdapterInternalException(String reason)
   {
      super(reason);
   }

   /**
	 * Create an exception with a reason and an errorCode.
	 */
   public ResourceAdapterInternalException(String reason, String errorCode)
   {
      super(reason, errorCode);
   }

   /**
    * Create an exception with a reason and cause.
    * 
    * @param reason the reason
    * @param cause the cause
    */
   public ResourceAdapterInternalException(String reason, Throwable cause)
   {
      super(reason, cause);
   }

   /**
    * Create an exception with a cause.
    * 
    * @param cause the cause
    */
   public ResourceAdapterInternalException(Throwable cause)
   {
      super(cause);
   }
}