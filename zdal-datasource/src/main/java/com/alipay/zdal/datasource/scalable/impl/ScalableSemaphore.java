/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.datasource.scalable.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * It is a delegate class of Java's Semaphore with scalability of reseting permits.
 * To achieve reset permits without mess up with permit management, we override methods:
 * </br>acquire() acquire permits with available semaphore.
 * </br>tryAcquire()
 * </br>release()
 * Besides, it also provides methods to understand semaphore's status by
 * getSum()
 * </p>
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ScalableSemaphore implements Serializable {

    /**
     * 
     */
    private static final long   serialVersionUID = 3940668817054000505L;

    public static final boolean MASTER           = true;

    public static final boolean DEPUTY           = false;

    //Is this semaphore has been changed. Changed : true, otherwise false;
    private volatile boolean    masterMode       = true;

    private volatile int        version          = 0;

    private ZdalSemaphore       semaphore;

    private ZdalSemaphore       deputySemaphore;

    public ScalableSemaphore(int permits) {
        semaphore = new ZdalSemaphore(permits);
    }

    //Represent all methods defined in Semaphore

    public void acquire() throws InterruptedException {
        getAvailableSemaphore().acquire();
    }

    protected ZdalSemaphore getAvailableSemaphore() {
        ZdalSemaphore availableSeamphore = null;
        if (masterMode == MASTER) {
            availableSeamphore = semaphore;
            //If the semaphore is null temporarily caused by reset permits, here need to make sure return an available one
            if (null == availableSeamphore) {
                availableSeamphore = deputySemaphore;
            }
        } else {
            availableSeamphore = deputySemaphore;
            if (null == availableSeamphore) {
                availableSeamphore = semaphore;
            }
        }
        return availableSeamphore;
    }

    /**
     * Response the invocation from outside to reset the permits.
     * @param permits
     * @throws ScaleConnectionPoolException when master and deputy semaphores still queued threads.
     */
    public synchronized void resetPermits(int permits) throws ScaleConnectionPoolException {
        version++;
        if (masterMode == MASTER) {
            if (null != deputySemaphore && deputySemaphore.hasQueuedThreads()) {
                throw new ScaleConnectionPoolException(
                    ScaleConnectionPoolException.MESSAGE_DEPUTY_SEMAPHORE_ALIVE);
            }
            deputySemaphore = null; //help GC
            deputySemaphore = new ZdalSemaphore(permits);
            masterMode = DEPUTY;
        } else {
            if (null != semaphore && semaphore.hasQueuedThreads()) {
                throw new ScaleConnectionPoolException(
                    ScaleConnectionPoolException.MESSAGE_MASTER_SEMAPHORE_ALIVE);
            }
            semaphore = null; //help GC
            semaphore = new ZdalSemaphore(permits);
            masterMode = MASTER;
        }
    }

    /**
     * Acquires a permit from this semaphore, if one becomes available
     * within the given waiting time and the current thread has not
     * been {@linkplain Thread#interrupt interrupted}.
     *
     * <p>Acquires a permit, if one is available and returns immediately,
     * with the value {@code true},
     * reducing the number of available permits by one.
     *
     * <p>If no permit is available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of three things happens:
     * <ul>
     * <li>Some other thread invokes the {@link #release} method for this
     * semaphore and the current thread is next to be assigned a permit; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts}
     * the current thread; or
     * <li>The specified waiting time elapses.
     * </ul>
     *
     * <p>If a permit is acquired then the value {@code true} is returned.
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting
     * to acquire a permit,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.  If the time is less than or equal to zero, the method
     * will not wait at all.
     *
     * @param timeout the maximum time to wait for a permit
     * @param unit the time unit of the {@code timeout} argument
     * @return {@code true} if a permit was acquired and {@code false}
     *         if the waiting time elapsed before a permit was acquired
     * @throws InterruptedException if the current thread is interrupted
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        ZdalSemaphore availableSeamphore = getAvailableSemaphore();
        boolean acquirePermits = false;
        if (version <= 0) {
            acquirePermits = availableSeamphore.tryAcquire(timeout, unit);
        } else {
            //Master to Deputy or Deputy to Master
            ZdalSemaphore previousSeamphore = null;
            previousSeamphore = masterMode == MASTER ? deputySemaphore : semaphore;
            if (previousSeamphore == availableSeamphore) {
                return availableSeamphore.tryAcquire(timeout, unit);
            } else {
                //Sum total grant permits from 2 semaphores
                int occupiedPermits = previousSeamphore.getInitializedPermits()
                                      - previousSeamphore.availablePermits();
                if (occupiedPermits > 0) {
                    if (occupiedPermits >= availableSeamphore.availablePermits()) {
                        //When the acquired permits more than set permits 
                        occupiedPermits = availableSeamphore.availablePermits();
                    }
                    try {
                        availableSeamphore.acquire(occupiedPermits);//Grants the same permits as total before it does tryAcquire
                        acquirePermits = availableSeamphore.tryAcquire(timeout, unit);
                    } finally {
                        availableSeamphore.release(occupiedPermits);
                    }
                } else {
                    acquirePermits = availableSeamphore.tryAcquire(timeout, unit);
                }
            }
        }
        return acquirePermits;
    }

    /**
     * Releases a permit, returning it to the semaphore.
     *
     * The strategy of release is who permits the acquire who is responsible to release the permits.
     */
    public void release() {
        if (version == 0) {
            semaphore.release();
            return;
        }
        //If the permits has been reseted, then we need to identify which semaphore needs release the permits
        //has been acquired before.
        if (null != semaphore && semaphore.hasHoldingPremits(Thread.currentThread())) {
            semaphore.release();
        } else if (null != deputySemaphore
                   && deputySemaphore.hasHoldingPremits(Thread.currentThread())) {
            deputySemaphore.release();
        }
    }

    /**
     * Returns the current number of permits available in this semaphore.
     *
     * <p>This method is typically used for debugging and testing purposes.
     *
     * @return the number of permits available in this semaphore
     */
    public int availablePermits() {
        return getAvailableSemaphore().availablePermits();
    }

    /**
     * Returns a string identifying this semaphore, as well as its state.
     * The state, in brackets, includes the String {@code "Permits ="}
     * followed by the number of permits.
     *
     * @return a string identifying this semaphore, as well as its state
     */
    public String toString() {
        return super.toString() + "[Permits = " + getAvailableSemaphore().availablePermits() + "]";
    }

    public String getSum() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\n").append("Semaphore info: ");
        strBuilder.append("version is " + version).append(" , master mode in ")
            .append(masterMode == MASTER);
        if (null != semaphore) {
            strBuilder.append(", Master available permits is " + semaphore.availablePermits());
            strBuilder
                .append(", Master queued threads size " + semaphore.getQueuedThreads().size());
        }
        if (null != deputySemaphore) {
            strBuilder
                .append(", Deputy available permits is " + deputySemaphore.availablePermits());
            strBuilder.append(", Deputy queued threads size "
                              + deputySemaphore.getQueuedThreads().size());
        }
        return strBuilder.toString();
    }
}
