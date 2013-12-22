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
 * A task wrapper for a thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37390 $
 */
public interface TaskWrapper extends Runnable
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the type of wait
    *
    * @return the wait type
    */
   int getTaskWaitType();

   /**
    * The priority of the task
    *
    * @return the task priority
    */
   int getTaskPriority();

   /**
    * The time before the task must be accepted
    *
    * @return the start timeout
    */
   long getTaskStartTimeout();

   /**
    * The time before the task must be completed
    *
    * @return the completion timeout
    */
   long getTaskCompletionTimeout();

   /**
    * Wait according the wait type
    */
   void waitForTask();

   /**
    * Invoked by the threadpool when it wants to stop the task
    */
   void stopTask();

   /**
    * The task has been accepted
    *
    */
   void acceptTask();

   /**
    * The task has been rejected
    *
    * @param e any error associated with the rejection
    */
   void rejectTask(RuntimeException e);

   /**
    * Indicate if the task has exited the Runnable#run method
    * @return true if the task has exited the Runnable#run method
    */ 
   boolean isComplete();
   // Inner classes -------------------------------------------------
}
