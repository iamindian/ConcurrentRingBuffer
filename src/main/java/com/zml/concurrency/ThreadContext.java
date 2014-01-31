package com.zml.concurrency;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadContext {
    private int start;
    private int end;


    public ThreadContext() {
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }


    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
