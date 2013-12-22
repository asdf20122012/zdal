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
 * A thread pool.
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37390 $
 */
public interface ThreadPool
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Stop the pool
    *
    * @param immediate whether to shutdown immediately
    */
   public void stop(boolean immediate);

   /** Wait on the queued tasks to complete. This can only be called after
    * after stop.
    * 
    * @throws InterruptedException
    */ 
   public void waitForTasks() throws InterruptedException;

   /** Wait on the queued tasks to complete upto maxWaitTime milliseconds. This
    * can only be called after after stop.
    * 
    * @param maxWaitTime
    * @throws InterruptedException
    */ 
   public void waitForTasks(long maxWaitTime) throws InterruptedException;

   /**
    * Run a task wrapper
    *
    * @param wrapper the task wrapper
    */
   public void runTaskWrapper(TaskWrapper wrapper);

   /**
    * Run a task
    *
    * @param task the task
    * @throws IllegalArgumentException for a null task
    */
   public void runTask(Task task);

   /**
    * Run a runnable
    *
    * @param runnable the runnable
    * @throws IllegalArgumentException for a null runnable
    */
   public void run(Runnable runnable);

   /**
    * 
    * @param runnable
    * @param startTimeout
    * @param completeTimeout
    */ 
   public void run(Runnable runnable, long startTimeout, long completeTimeout);
}
