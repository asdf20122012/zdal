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
public interface ThreadPoolMBean
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   /**
    * Get the thread pool name
    *
    * @return the thread pool name
    */
   String getName();

   /**
    * Set the thread pool name
    *
    * @param name the name
    */
   void setName(String name);

   /**
    * Get the internal pool number
    *
    * @return the internal pool number
    */
   int getPoolNumber();

   /**
    * Get the minimum pool size
    *
    * @return the minimum pool size
    */
   int getMinimumPoolSize();

   /**
    * Set the minimum pool size
    *
    * @param size the minimum pool size
    */
   void setMinimumPoolSize(int size);

   /**
    * Get the maximum pool size
    *
    * @return the maximum pool size
    */
   int getMaximumPoolSize();

   /**
    * Set the maximum pool size
    *
    * @param size the maximum pool size
    */
   void setMaximumPoolSize(int size);

   /**
    * Get the instance
    */
   ThreadPool getInstance();

   /**
    * Stop the thread pool
    */
   void stop();

   // Inner classes -------------------------------------------------
}
