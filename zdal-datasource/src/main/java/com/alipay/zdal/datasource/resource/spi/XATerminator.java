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

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

/**
 * Transaction completion and crash recovery
 */
public interface XATerminator
{
   /**
    * Commit the transaction
    *
    * @param xid the xid
    * @param onePhase true for one phase commit, false for two phase
    * @throws XAException for an error
    */
   void commit(Xid xid, boolean onePhase) throws XAException;

   /**
    * Forget the transaction
    *
    * @param xid the xid
    * @throws XAException for an error
    */
   void forget(Xid xid) throws XAException;

   /**
    * Prepare the transaction
    *
    * @param xid the xid
    * @return Either XA_RDONLY or XA_OK
    * @throws XAException for an error
    */
   int prepare(Xid xid) throws XAException;

   /**
    * Rollback the transaction
    *
    * @param xid the xid
    * @throws XAException for an error
    */
   void rollback(Xid xid) throws XAException;
   
   /**
    * Retrieve xids that are recoverable
    *
    * @param flag the recovery option
    * @throws XAException for an error
    */
   Xid[] recover(int flag) throws XAException;
}