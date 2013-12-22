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

import javax.transaction.xa.XAResource;

/**
 *  The Transaction interface allows operations to be performed on
 *  transactions.
 *
 *  @version $Revision: 37390 $
 */
public interface Transaction
{
    /**
     *  Attempt to commit this transaction.
     *
     *  @throws RollbackException If the transaction was marked for rollback
     *          only, the transaction is rolled back and this exception is
     *          thrown.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     *  @throws HeuristicMixedException If a heuristic decision was made and
     *          some some parts of the transaction have been committed while
     *          other parts have been rolled back.
     *  @throws HeuristicRollbackException If a heuristic decision to roll
     *          back the transaction was made.
     *  @throws SecurityException If the caller is not allowed to commit this
     *          transaction.
     */
    public void commit()
        throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, SystemException;

    /**
     *  Rolls back this transaction.
     * 
     *  @throws IllegalStateException If the transaction is in a state
     *          where it cannot be rolled back. This could be because the
     *          transaction is no longer active, or because it is in the
     *          {@link Status#STATUS_PREPARED prepared state}.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public void rollback()
        throws IllegalStateException, SystemException;

    /**
     *  Mark the transaction so that the only possible outcome is a rollback.
     * 
     *  @throws IllegalStateException If the transaction is not in an active
     *          state.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public void setRollbackOnly()
        throws IllegalStateException, SystemException;

    /**
     *  Get the status of the transaction.
     *
     *  @return The status of the transaction. This is one of the
     *          {@link Status} constants.
     *
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public int getStatus() throws SystemException;

    /**
     *  Enlist an XA resource with this transaction.
     *
     *  @return <code>true</code> if the resource could be enlisted with
     *          this transaction, otherwise <code>false</code>.
     *
     *  @throws RollbackException If the transaction is marked for rollback
     *          only.
     *  @throws IllegalStateException If the transaction is in a state
     *          where resources cannot be enlisted. This could be because the
     *          transaction is no longer active, or because it is in the
     *          {@link Status#STATUS_PREPARED prepared state}.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public boolean enlistResource(XAResource xaRes)
        throws RollbackException, IllegalStateException, SystemException;

    /**
     *  Delist an XA resource from this transaction.
     *
     *  @return <code>true</code> if the resource could be delisted from
     *          this transaction, otherwise <code>false</code>.
     *
     *  @throws IllegalStateException If the transaction is in a state
     *          where resources cannot be delisted. This could be because the
     *          transaction is no longer active.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public boolean delistResource(XAResource xaRes, int flag)
        throws IllegalStateException, SystemException;

    /**
     *  Register a {@link Synchronization} callback with this transaction.
     *
     *  @throws RollbackException If the transaction is marked for rollback
     *          only.
     *  @throws IllegalStateException If the transaction is in a state
     *          where {@link Synchronization} callbacks cannot be registered.
     *          This could be because the transaction is no longer active,
     *          or because it is in the
     *          {@link Status#STATUS_PREPARED prepared state}.
     *  @throws SystemException If the transaction service fails in an
     *          unexpected way.
     */
    public void registerSynchronization(Synchronization sync)
        throws RollbackException, IllegalStateException, SystemException;
}
