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
package com.alipay.zdal.datasource.resource.util.threadpool;

/**
 * Management interface for the thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37390 $
 */
public interface BasicThreadPoolMBean extends ThreadPoolMBean
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the current queue size
   *
    * @return the queue size
    */
   int getQueueSize();

   /**
    * Get the maximum queue size
    *
    * @return the maximum queue size
    */
   int getMaximumQueueSize();

   /**
    * Set the maximum queue size
    *
    * @param size the new maximum queue size
    */
   void setMaximumQueueSize(int size);

   /**
    * @return the blocking mode
    */
   BlockingMode getBlockingMode();
   
   /** Set the behavior of the pool when a task is added and the queue is full.
    * The mode string indicates one of the following modes:
    * abort - a RuntimeException is thrown
    * run - the calling thread executes the task
    * wait - the calling thread blocks until the queue has room
    * discard - the task is silently discarded without being run
    * discardOldest - check to see if a task is about to complete and enque
    *    the new task if possible, else run the task in the calling thread
    * 
    * @param mode one of run, wait, discard, discardOldest or abort without
    *    regard to case.
    */ 
   void setBlockingMode(BlockingMode mode);

   /**
    * Retrieve the thread group name
    *
    * @return the thread group name
    */
   String getThreadGroupName();

   /**
    * Set the thread group name
    *
    * @param threadGroupName - the thread group name
    */
   void setThreadGroupName(String threadGroupName);

   /**
    * Get the keep alive time
    *
    * @return the keep alive time
    */
   long getKeepAliveTime();

   /**
    * Set the keep alive time
    *
    * @param time the keep alive time
    */
   void setKeepAliveTime(long time);

   // Inner classes -------------------------------------------------
}
