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
package com.alipay.zdal.datasource.resource.spi.work;

import com.alipay.zdal.datasource.resource.ResourceException;

/**
 * Thrown when there is an error handling work.
 */
public class WorkException extends ResourceException
{
   /** An internal error */
   public static final String INTERNAL = "-1";
   /** An undefined error */
   public static final String UNDEFINED = "0";
   /** Expiration before work was started */
   public static final String START_TIMED_OUT = "1";
   /** Not allowed to do concurrent work on a transaction */
   public static final String TX_CONCURRENT_WORK_DISALLOWED = "2";
   /** Could not recreate the transaction context */
   public static final String TX_RECREATE_FAILED = "3";

   /**
    * Create an exception.
    */
   public WorkException()
   {
      super();
   }

   /**
    * Create an exception with a reason.
    *
    * @param reason the reason
    */
   public WorkException(String reason)
   {
      super(reason);
   }

   /**
    * Create an exception with a reason and an errorCode.
    *
    * @param reason the reason
    * @param errorCode the error code
    */
   public WorkException(String reason, String errorCode)
   {
      super(reason, errorCode);
   }

   /**
    * Create an exception with a reason and an error.
    *
    * @param reason the reason
    * @param throwable the error
    */
   public WorkException(String reason, Throwable throwable)
   {
      super(reason, throwable);
   }

   /**
    * Create an exception with an error.
    *
    * @param throwable the error
    */
   public WorkException(Throwable throwable)
   {
      super(throwable);
   }
}
