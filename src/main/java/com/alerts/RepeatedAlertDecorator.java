package com.alerts;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RepeatedAlertDecorator extends AlertDecorator{
    private int repeatInterval;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatInterval){
        super(decoratedAlert);
        this.repeatInterval = repeatInterval;
    }

    @Override
    public void triggerAlert() {
        super.triggerAlert();
        scheduleRepeatedAlert();
    }

    private void scheduleRepeatedAlert() {
        final Runnable alertTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("Alert Repeating for Patient: "+ getPatientId() + " with Condition: "+ getCondition() + " at "+ getTimestamp() );
            }
        };

        scheduler.scheduleAtFixedRate(alertTask, repeatInterval, repeatInterval, TimeUnit.SECONDS);
    }

    public void stopRepeating() {
        scheduler.shutdown();
    }
}
