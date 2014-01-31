package com.zml.app;

import com.zml.concurrency.ConcurrentRingBuffer;
import com.zml.concurrency.EmptyBufferException;
import com.zml.concurrency.FullBufferException;

import java.util.concurrent.atomic.AtomicBoolean;


public class ConcurrentRingBufferDemo {
    public static void main(String[] args) {
        AtomicBoolean done = new AtomicBoolean(false);
        ConcurrentRingBuffer<Integer> bf = new ConcurrentRingBuffer<Integer>(1000);
        class Producer implements Runnable {
            private ConcurrentRingBuffer<Integer> bf;
            private AtomicBoolean done;

            public Producer(ConcurrentRingBuffer bf, AtomicBoolean done) {
                this.bf = bf;
                this.done = done;
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
                        Thread.sleep(3000);
                    } catch (EmptyBufferException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        Thread producer = new Thread(new Producer(bf, done));
        Thread consumer = new Thread(new Consumer(bf, done));
        producer.start();
        consumer.start();
    }
}
