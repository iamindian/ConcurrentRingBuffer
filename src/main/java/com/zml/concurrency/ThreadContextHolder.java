package com.zml.concurrency;


public class ThreadContextHolder {
    public static final ThreadLocal threadLocal = new ThreadLocal();
    public static void setThreadLocal(ThreadContext tc){
        threadLocal.set(tc);
    }

    public static ThreadContext getThreadLocal() {
        return (ThreadContext)threadLocal.get();
    }
}
