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

import com.alipay.zdal.datasource.resource.util.concurrent.Channel;
import com.alipay.zdal.datasource.resource.util.concurrent.PooledExecutor;

/** A pooled executor where the minimum pool size threads are kept alive. This
is needed in order for the waitWhenBlocked option to work because of a
race condition inside the Executor. The race condition goes something like:

RT - Requesting Thread wanting to use the pool
LT - Last Thread in the pool

RT: Check there are enough free threads to process,
   yes LT is there, so no need to create a new thread.
LT: Times out on the keep alive, LT is destroyed.
RT: Try to execute, blocks because there are no available threads.
   In fact, the pool is now empty which the executor mistakenly
   inteprets as all of them being in use.

Doug Lea says he isn't going to fix. In fact, the version in j2se 
1.5 doesn't have this option. In order for this to work, the min pool
size must be > 0.

@author Scott.Stark@jboss.org
@author adrian@jboss.org
@version $Revision: 37390 $
 */
public class MinPooledExecutor extends PooledExecutor {
    // Constants -----------------------------------------------------

    // Attributes ----------------------------------------------------

    /** The number of threads to keep alive threads */
    protected int keepAliveSize;

    // Static --------------------------------------------------------

    // Constructors --------------------------------------------------

    /**
     * Construct a new executor
     * 
     * @param poolSize the maximum pool size
     */
    public MinPooledExecutor(int poolSize) {
        super(poolSize);
    }

    /**
     * Construct a new executor
     * 
     * @param channel the queue for any requests
     * @param poolSize the maximum pool size
     */
    public MinPooledExecutor(Channel channel, int poolSize) {
        super(channel, poolSize);
    }

    // Public --------------------------------------------------------

    /**
     * @return the number of threads to keep alive
     */
    public int getKeepAliveSize() {
        return keepAliveSize;
    }

    /**
     * @param keepAliveSize the number of threads to keep alive
     */
    public void setKeepAliveSize(int keepAliveSize) {
        this.keepAliveSize = keepAliveSize;
    }

    // PooledExecutor overrides --------------------------------------

    @Override
    protected Runnable getTask() throws InterruptedException {
        Runnable task = super.getTask();
        while (task == null && keepAlive()) {
            task = super.getTask();
        }
        return task;
    }

    // Package protected ---------------------------------------------

    // Protected -----------------------------------------------------

    /**
     * We keep alive unless we are told to shutdown
     * or there are more than keepAliveSize threads in the pool
     *
     * @return whether to keep alive
     */
    protected synchronized boolean keepAlive() {
        if (shutdown_)
            return false;

        return poolSize_ <= keepAliveSize;
    }

    // Private -------------------------------------------------------

    // Inner classes -------------------------------------------------
}
