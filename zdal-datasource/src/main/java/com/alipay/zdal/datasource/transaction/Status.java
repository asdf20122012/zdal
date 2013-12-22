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
 *  The Status interface defines the constants for transaction status codes.
 *
 *  @version $Revision: 37390 $
 */
public interface Status
{
    /**
     *  Status code indicating an active transaction.
     */
    public static final int STATUS_ACTIVE = 0;

    /**
     *  Status code indicating a transaction that has been marked for
     *  rollback only.
     */
    public static final int STATUS_MARKED_ROLLBACK = 1;

    /**
     *  Status code indicating a transaction that has completed the first
     *  phase of the two-phase commit protocol, but not yet begun the
     *  second phase.
     *  Probably the transaction is waiting for instruction from a superior
     *  coordinator on how to proceed.
     */
    public static final int STATUS_PREPARED = 2;

    /**
     *  Status code indicating a transaction that has been committed.
     *  Probably heuristics still exists, or the transaction would no
     *  longer exist.
     */
    public static final int STATUS_COMMITTED = 3;

    /**
     *  Status code indicating a transaction that has been rolled back.
     *  Probably heuristics still exists, or the transaction would no
     *  longer exist.
     */
    public static final int STATUS_ROLLEDBACK = 4;

    /**
     *  Status code indicating that the transaction status could not be
     *  determined.
     */
    public static final int STATUS_UNKNOWN = 5;

    /**
     *  Status code indicating that no transaction exists.
     */
    public static final int STATUS_NO_TRANSACTION = 6;

    /**
     *  Status code indicating a transaction that has begun the first
     *  phase of the two-phase commit protocol, not not yet completed
     *  this phase.
     */
    public static final int STATUS_PREPARING = 7;

    /**
     *  Status code indicating a transaction that has begun the second
     *  phase of the two-phase commit protocol, but not yet completed
     *  this phase.
     */
    public static final int STATUS_COMMITTING = 8;

    /**
     *  Status code indicating a transaction that is in the process of
     *  rolling back.
     */
    public static final int STATUS_ROLLING_BACK = 9;
}
