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
        setThreadLocalVarsForTakingItem();
        isEmpty();
        incrementTakeIndexForNextTake();
        return returnItem();
    }

    public void put(T item) throws FullBufferException {
        setThreadLocalVarsForPuttingItem();
        isFull();
        incrementPutIndexForNextPut();
        putItem(item);
    }
    public void incrementTakeIndexForNextTake(){
        start.incrementAndGet();
        rotateUnboundTakeIndexToZeroAndFlipStartSmb();
    }
    public void incrementPutIndexForNextPut(){
        end.incrementAndGet();
        rotateUnboundPutIndexToZeroAndFlipEndSmb();
    }

    public T returnItem() {
        return buffer.get(ThreadContextHolder.getThreadLocal().getStart());
    }

    public void putItem(T item) {
        buffer.set(ThreadContextHolder.getThreadLocal().getEnd(), item);
    }

    public void rotateUnboundPutIndexToZeroAndFlipEndSmb() {
        if (end.compareAndSet(this.bufferSize, 0))
            endSmb.flip(0);
    }

    public void rotateUnboundTakeIndexToZeroAndFlipStartSmb() {
        if (start.compareAndSet(this.bufferSize, 0))
            startSmb.flip(0);
    }

    public void setThreadLocalVarsForTakingItem() {
        ThreadContext context = new ThreadContext();
        ThreadContextHolder.setThreadLocal(context);
        context.setEnd(this.end.get());
        context.setStart(this.start.get());
    }

    public void setThreadLocalVarsForPuttingItem() {
        ThreadContext context = new ThreadContext();
        ThreadContextHolder.setThreadLocal(context);
        context.setEnd(this.end.get());
        context.setStart(this.start.get());
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
