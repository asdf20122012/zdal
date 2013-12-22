/**
 * 
 */
package com.alipay.zdal.datasource.scalabe;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.alipay.zdal.datasource.scalable.impl.ScalableSemaphore;
import com.alipay.zdal.datasource.scalable.impl.ScaleConnectionPoolException;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class TestScalableSemaphore {

	private static final int PERMITS_FIVE = 5;

	private static final int PERMITS_TEN = 10;
	
	private static final int THREAD_SIZE = 20;
	
	private static final int GIVEN_COUNT = 20;
	
	private ThreadPoolExecutor threadPool;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		threadPool = new ThreadPoolExecutor(10, 100, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		threadPool = null;
	}
/*
	@Test
	public void testSingleThreadAccess() throws InterruptedException {
		ScalableSemaphore semaphore = new ScalableSemaphore(PERMITS_TEN);
		int count = 0, newCount = 0;
		semaphore.acquire();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_TEN - ++count );
		
		semaphore.acquire();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_TEN - ++count );
		
		semaphore.acquire();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_TEN - ++count );
		
		//Reset 
		try {
			semaphore.resetPermits(PERMITS_FIVE);
		} catch (ScaleConnectionPoolException e) {
			e.printStackTrace();
			Assert.isTrue(false);
		}
		Assert.isTrue(semaphore.availablePermits() == PERMITS_FIVE );
		//Verify who release permits bases on who hold the permits.
		semaphore.release();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_FIVE );
		
		semaphore.release();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_FIVE );
		
		//Acquire the new permits
		semaphore.acquire();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_FIVE - ++newCount);
		
		semaphore.acquire();
		Assert.isTrue(semaphore.availablePermits() == PERMITS_FIVE - ++newCount);
	}
*/
	@Test
	public void testMultipleThreadsAccess() throws InterruptedException {
		final ScalableSemaphore semaphore = new ScalableSemaphore(PERMITS_TEN);
		System.out.println("Summary " + semaphore.getSum());
		final AtomicInteger totalCount = new AtomicInteger();
		for( int index = 0; index < THREAD_SIZE ; index++ ){
			threadPool.execute(new Runnable(){
				public void run(){
					for( int count = 0; count < GIVEN_COUNT; count++ ){
						try {
							semaphore.tryAcquire(50, TimeUnit.MILLISECONDS);
							Thread.sleep(1);
							semaphore.release();
							totalCount.addAndGet(1);
						} catch (Exception e) {
							e.printStackTrace();
							Assert.isTrue( false );
						}
					}
				}
			});
		}
		
		while( true ){
			Thread.sleep(30);
			//Reset the permits in middle of accessing
			try {
				semaphore.resetPermits(PERMITS_FIVE);
				System.out.println("Reset permits at " + totalCount.get() );
				System.out.println("Summary " + semaphore.getSum());
				Thread.sleep(30);
				//Reset the permits in middle of accessing
				semaphore.resetPermits(PERMITS_TEN);
				System.out.println("Reset permits at " + totalCount.get() );
				System.out.println("Summary " + semaphore.getSum());
				Thread.sleep(2000);
				break;
			} catch (ScaleConnectionPoolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		Assert.isTrue(totalCount.get() == THREAD_SIZE * GIVEN_COUNT );
		System.out.println("testMultipleThreadsAccess is finished at " + totalCount.get());
		System.out.println("Summary " + semaphore.getSum());
	}
	
	@Test
	public void testMultipleThreadsAccessWithKeepingCheck() throws InterruptedException {
		final ScalableSemaphore semaphore = new ScalableSemaphore(PERMITS_TEN);
		System.out.println("Summary " + semaphore.getSum());
		final AtomicInteger totalCount = new AtomicInteger();
		ScalableSemaphoreChecker checker = new ScalableSemaphoreChecker(semaphore, PERMITS_TEN);
		checker.start();
		for( int index = 0; index < THREAD_SIZE ; index++ ){
			threadPool.execute(new Runnable(){
				public void run(){
					for( int count = 0; count < GIVEN_COUNT; count++ ){
						try {
							semaphore.tryAcquire(50, TimeUnit.MILLISECONDS);
							Thread.sleep(1);
							semaphore.release();
							totalCount.addAndGet(1);
						} catch (Exception e) {
							e.printStackTrace();
							Assert.isTrue( false );
						}
					}
				}
			});
		}
		
		while( true ){
			Thread.sleep(30);
			//Reset the permits in middle of accessing
			try {
				semaphore.resetPermits(PERMITS_FIVE);
				System.out.println("Reset permits at " + totalCount.get() );
				System.out.println("Summary " + semaphore.getSum());
				Thread.sleep(30);
				//Reset the permits in middle of accessing
				semaphore.resetPermits(PERMITS_TEN);
				System.out.println("Reset permits at " + totalCount.get() );
				System.out.println("Summary " + semaphore.getSum());
				Thread.sleep(2000);
				break;
			} catch (ScaleConnectionPoolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Assert.isTrue(totalCount.get() == THREAD_SIZE * GIVEN_COUNT );
		System.out.println("testMultipleThreadsAccess is finished at " + totalCount.get());
		System.out.println("Summary " + semaphore.getSum());
	}
}

class ScalableSemaphoreChecker extends Thread{
	
	ScalableSemaphore semaphore;
	
	AtomicBoolean isStop = new AtomicBoolean(false);
	
	int limitedPermits;
	
	public ScalableSemaphoreChecker(ScalableSemaphore semaphore, int limitedPermits){
		this.semaphore = semaphore;
		this.limitedPermits = limitedPermits;
	}
	
	public void run(){
		while( !isStop.get() ){
			try {
				sleep(5);
				if( null != semaphore ){
					Assert.isTrue(semaphore.availablePermits() <= limitedPermits);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
