package com.alerts;

import com.data_management.PatientRecord;
import com.data_management.Patient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RepeatedAlertDecorator extends AlertDecorator{
    private int repeatInterval; // in seconds
    private AlertGenerator alertGenerator;
    private Patient patient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatInterval, AlertGenerator alertGenerator, Patient patient){
        super(decoratedAlert);
        this.repeatInterval = repeatInterval;
        this.alertGenerator = alertGenerator;
        this.patient = patient;
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
               List<PatientRecord> patientRecords = alertGenerator.getAllRecordsForPatient(patient);
               for(PatientRecord record: patientRecords){
                   if(alertGenerator.checkAlert(decoratedAlert, record)) {
                       decoratedAlert.triggerAlert();
                       System.out.println("Alert repeating for Patient: "+ getPatientId() +", with Condition: "+ getCondition() + " at Time:"+getTimestamp());
                   }
               }
                System.out.println("Condition stabilized for Patient: "+getPatientId());
            }
        };

        scheduler.scheduleAtFixedRate(alertTask, repeatInterval, repeatInterval, TimeUnit.SECONDS);
    }

    public void stopRepeating() {
        scheduler.shutdown();
    }
}
