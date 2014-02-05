package com.zml.concurrency;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ConcurrentRingBuffer<T> {
    private BitSet startSmb = new BitSet(1);
    private BitSet endSmb = new BitSet(1);
    private AtomicReferenceArray<T> buffer;
    private AtomicInteger start = new AtomicInteger(0);
    private AtomicInteger end = new AtomicInteger(0);
    private int bufferSize;

    public ConcurrentRingBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new AtomicReferenceArray(bufferSize);
        startSmb.set(0,false);
        endSmb.set(0,false);
    }

    public T take() throws EmptyBufferException {
        setCurrentIndexToThreadLocal();
        isEmpty();
        flipStartSmb();
        takeIndexIncrementForNextTake();
        return returnItem();
    }

    public void put(T item) throws FullBufferException {
        setCurrentIndexToThreadLocal();
        isFull();
        flipEndSmb();
        putIndexIncrementForNextPut();
        putItem(item);
    }
    private void flipStartSmb(){
        if(ThreadContextHolder.getThreadLocal().getStart()==0)
            this.startSmb.flip(0);
    }
    private void flipEndSmb(){
        if(ThreadContextHolder.getThreadLocal().getEnd()==0)
            this.endSmb.flip(0);
    }
    private void putIndexIncrementForNextPut(){
        this.end.getAndIncrement();

    }
    private void takeIndexIncrementForNextTake(){
        this.start.getAndIncrement();
    }

    private T returnItem() {
        return buffer.get(ThreadContextHolder.getThreadLocal().getStart());
    }

    private void putItem(T item) {
        buffer.set(ThreadContextHolder.getThreadLocal().getEnd(), item);
    }


    private void setCurrentIndexToThreadLocal() {
        ThreadContext context = new ThreadContext();
        ThreadContextHolder.setThreadLocal(context);
        context.setEnd(this.end.get()%bufferSize);
        context.setStart(this.start.get()%bufferSize);
    }

    private void isEmpty() throws EmptyBufferException {
        int takeIndex = ThreadContextHolder.getThreadLocal().getStart();
        int putIndex = ThreadContextHolder.getThreadLocal().getEnd();
        if (takeIndex == putIndex && startSmb.get(0) == endSmb.get(0))
            throw new EmptyBufferException("Buffer is empty");
    }

    private void isFull() throws FullBufferException {
        int takeIndex = ThreadContextHolder.getThreadLocal().getStart();
        int putIndex = ThreadContextHolder.getThreadLocal().getEnd();
        if (takeIndex == putIndex && startSmb.get(0) != endSmb.get(0))
            throw new FullBufferException("Buffer is full");
    }


}
