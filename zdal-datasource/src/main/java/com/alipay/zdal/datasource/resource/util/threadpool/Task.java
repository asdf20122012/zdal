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
 * A task for a thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37390 $
 */
public interface Task
{
   // Constants -----------------------------------------------------

   /** Don't wait for task */
   static final int WAIT_NONE = 0;

   /** Synchronized start, wait for task to start */
   static final int WAIT_FOR_START = 1;

   /** Synchronized task, wait for task to complete */
   static final int WAIT_FOR_COMPLETE = 2;

   // Public --------------------------------------------------------

   /**
    * Get the type of wait
    *
    * @return the wait type
    */
   int getWaitType();

   /**
    * The priority of the task
    *
    * @return the task priority
    */
   int getPriority();

   /**
    * The time before the task must be accepted
    *
    * @return the start timeout
    */
   long getStartTimeout();

   /**
    * The time before the task must be completed
    *
    * @return the completion timeout
    */
   long getCompletionTimeout();

   /**
    * Execute the task
    */
   void execute();

   /**
    * Invoked by the threadpool when it wants to stop the task
    */
   void stop();

   /**
    * The task has been accepted
    *
    * @param time the time taken to accept the task
    */
   void accepted(long time);

   /**
    * The task has been rejected
    *
    * @param time the time taken to reject the task
    * @param throwable any error associated with the rejection
    */
   void rejected(long time, Throwable t);

   /**
    * The task has been started
    *
    * @param time the time taken to start the task
    */
   void started(long time);

   /**
    * The task has been completed
    *
    * @param time the time taken to reject the task
    * @param throwable any error associated with the completion
    */
   void completed(long time, Throwable t);

   // Inner classes -------------------------------------------------
}
