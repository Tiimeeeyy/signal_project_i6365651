package com.alerts;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeUpdater {
    private long time;

    public TimeUpdater() {
        this.time = System.currentTimeMillis();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::updateTime, 1, 1, TimeUnit.SECONDS);
    }
    private void updateTime() {
        this.time = System.currentTimeMillis();
    }
    public long getTime() {
        return this.time;
    }
}
