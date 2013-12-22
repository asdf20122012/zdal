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

import java.util.EventListener;

/**
 * The ConnectionEventListener interface provides for a callback mechanism to
 * enable objects to listen for events of the ConnectionEvent class.
 * 
 * An Application server uses these events to manage its connection pools.
 */
public interface ConnectionEventListener extends EventListener
{

   /**
	 * Notifies the listener that a connection has been closed
    * 
    * @param event the closed event
	*/
   void connectionClosed(ConnectionEvent event);

   /**
	 * Local transaction has been started
    * 
    * @param event the local transaction started event
	 */
   void localTransactionStarted(ConnectionEvent event);

   /**
	 * Local transaction has been committed
    * 
    * @param event the local transaction committed event
	 */
   void localTransactionCommitted(ConnectionEvent event);

   /**
	 * Local transaction has been rolled back
    * 
    * @param the local transaction rolled back event
	 */
   void localTransactionRolledback(ConnectionEvent event);

   /**
	 * Connection error has occurred
    * 
    * @param the connection error event
	 */
   void connectionErrorOccurred(ConnectionEvent event);
}