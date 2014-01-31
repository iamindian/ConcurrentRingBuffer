package com.zml.app;

import com.zml.concurrency.ConcurrentRingBuffer;
import com.zml.concurrency.EmptyBufferException;
import com.zml.concurrency.FullBufferException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit test for simple ConcurrentRingBufferDemo.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /**
     * this test can not be used temporary due to junit doesn't support multi-threading testing, another testing framework will be used here
     * later. Please run the  ConcurrentRingBufferDemo
     */
    public void testConcurrenctCircularBufferPutAndTake() {
        AtomicBoolean done = new AtomicBoolean(false);
        ConcurrentRingBuffer<Integer> bf = new ConcurrentRingBuffer<Integer>(10);
        class Producer implements Runnable {
            private ConcurrentRingBuffer<Integer> bf;
            private AtomicBoolean done;

            public Producer(ConcurrentRingBuffer bf, AtomicBoolean done) {
                this.bf = bf;
                this.done = done;
                done.set(false);
            }

            public void run() {

                Integer index = 100;
                while (index > 0) {
                    try {
                        bf.put(new Integer(index));
                        System.out.println("put: " + index);
                        index--;
                        Thread.sleep(1000);
                    } catch (FullBufferException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                done.set(true);


            }
        }
        class Consumer implements Runnable {
            private int count;
            private ConcurrentRingBuffer<Integer> bf;
            private AtomicBoolean done;

            public Consumer(ConcurrentRingBuffer<Integer> bf, AtomicBoolean done) {
                this.bf = bf;
                this.done = done;
            }

            public void run() {
                while (true) {
                    try {
                        Integer take = bf.take();
                        System.out.println("take: "+take);
                        count++;
                        Thread.sleep(3000);
                    } catch (EmptyBufferException e) {
                        e.printStackTrace();
                        if(done.get()){break;}
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                assertEquals(100,count);

            }
        }
        Thread producer = new Thread(new Producer(bf, done));
        Thread consumer = new Thread(new Consumer(bf, done));
        producer.start();
        consumer.start();
    }
}
