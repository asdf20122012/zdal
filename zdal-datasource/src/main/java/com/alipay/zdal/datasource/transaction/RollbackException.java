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
 *  The RollbackException exception indicates that either the transaction
 *  has been rolled back or an operation cannot complete because the
 *  transaction is marked for rollback only.
 *  <p>
 *  It is thrown under two circumstances:
 *  <ul>
 *    <li>
 *    At transaction commit time, if the transaction has been marked for
 *    rollback only. In this case, the <code>commit</code> method will roll
 *    back the transaction and throw this exception to indicate that the
 *    transaction could not be committed.
 *    </li>
 *    <li>
 *    At other times, if an operation cannot be completed because the
 *    transaction is marked for rollback only.
 *    The {@link Transaction#enlistResource(javax.transaction.xa.XAResource) enlistResource}
 *    and {@link Transaction#registerSynchronization(Synchronization) registerSynchronization}
 *    methods in the {@link Transaction} interface throw this exception to
 *    indicate that the operation cannot be completed because the transaction
 *    is marked for rollback only. In this case, the state of the transaction
 *    remains unchanged.
 *    </li>
 *  </ul>
 *
 *  @version $Revision: 37390 $
 */
public class RollbackException extends Exception
{

    /**
     *  Creates a new <code>RollbackException</code> without a detail message.
     */
    public RollbackException()
    {
    }

    /**
     *  Constructs an <code>RollbackException</code> with the specified
     *  detail message.
     *
     *  @param msg the detail message.
     */
    public RollbackException(String msg)
    {
        super(msg);
    }
}
