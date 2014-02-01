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
    public void flipStartSmb(){
        if(ThreadContextHolder.getThreadLocal().getStart()%bufferSize==0)
            this.startSmb.flip(0);
    }
    public void flipEndSmb(){
        if(ThreadContextHolder.getThreadLocal().getEnd()%bufferSize==0)
            this.endSmb.flip(0);
    }
    public void putIndexIncrementForNextPut(){
        this.end.getAndIncrement();
    }
    public void takeIndexIncrementForNextTake(){
        this.start.getAndIncrement();
    }

    public T returnItem() {
        return buffer.get(ThreadContextHolder.getThreadLocal().getStart());
    }

    public void putItem(T item) {
        buffer.set(ThreadContextHolder.getThreadLocal().getEnd(), item);
    }


    public void setCurrentIndexToThreadLocal() {
        ThreadContext context = new ThreadContext();
        ThreadContextHolder.setThreadLocal(context);
        context.setEnd(this.end.get()%bufferSize);
        context.setStart(this.start.get()%bufferSize);
    }

    public void isEmpty() throws EmptyBufferException {
        int takeIndex = ThreadContextHolder.getThreadLocal().getStart();
        int putIndex = ThreadContextHolder.getThreadLocal().getEnd();
        if (takeIndex == putIndex && startSmb.get(0) == endSmb.get(0))
            throw new EmptyBufferException("Buffer is empty");
    }

    public void isFull() throws FullBufferException {
        int takeIndex = ThreadContextHolder.getThreadLocal().getStart();
        int putIndex = ThreadContextHolder.getThreadLocal().getEnd();
        if (takeIndex == putIndex && startSmb.get(0) != endSmb.get(0))
            throw new FullBufferException("Buffer is full");
    }


}
