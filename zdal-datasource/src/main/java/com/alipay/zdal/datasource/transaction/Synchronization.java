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
 *  This is the callback interface that has to be implemented by objects
 *  interested in receiving notification before and after a transaction
 *  commits or rolls back.
 *
 *  An interested party can give an instance implementing this interface
 *  as an argument to the
 *  {@link Transaction#registerSynchronization(Synchronization) Transaction.registerSynchronization}
 *  method to receive callbacks before and after a transaction commits or
 *  rolls back.
 *
 *  @version $Revision: 37390 $
 */
public interface Synchronization
{
    /**
     *  This method is invoked before the start of the commit
     *  process. The method invocation is done in the context of the
     *  transaction that is about to be committed.
     */
    public void beforeCompletion();

    /**
     *  This method is invoked after the transaction has committed or
     *  rolled back.
     *
     *  @param status The status of the completed transaction.
     */
    public void afterCompletion(int status);
}
