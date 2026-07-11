package com.redis.demo.spring.ai;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorRejectHandler
        implements RejectedExecutionHandler {

    private final AtomicLong rejectCount =
            new AtomicLong();

    @Override
    public void rejectedExecution(
            Runnable r,
            ThreadPoolExecutor executor) {

        rejectCount.incrementAndGet();
    }

    public long getRejectCount() {
        return rejectCount.get();
    }
}
