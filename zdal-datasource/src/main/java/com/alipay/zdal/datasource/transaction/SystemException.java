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
package com.alipay.zdal.datasource.transaction;

/**
 *  This exception is thrown to indicate that the transaction manager has
 *  encountered an unexpected error condition that prevents future
 *  transaction services from proceeding. 
 *
 *  @version $Revision: 37390 $
 */
public class SystemException extends Exception
{
    /**
     *  The error code of this exception. Values of this field are not
     *  specified by JTA.
     */
    public int errorCode;

    /**
     *  Creates a new <code>SystemException</code> without a detail message.
     */
    public SystemException()
    {
    }

    /**
     *  Constructs an <code>SystemException</code> with the specified
     *  detail message.
     *
     *  @param msg the detail message.
     */
    public SystemException(String msg)
    {
        super(msg);
    }

    /**
     *  Constructs an <code>SystemException</code> with the specified
     *  detail message.
     *
     *  @param errcode the error code for the exception
     */
    public SystemException(int errcode)
    {
        this.errorCode = errcode;
    }
}
