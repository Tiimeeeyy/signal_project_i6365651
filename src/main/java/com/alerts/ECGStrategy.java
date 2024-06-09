package com.alerts;

import com.data_management.PatientRecord;

public class ECGStrategy implements AlertStrategy {
    private static final double ECG_TRESHOLD = 0.3;
    private final ECGAlertFactory alertFactory;

    public ECGStrategy(ECGAlertFactory alertFactory){
        this.alertFactory = alertFactory;
    }
    @Override
    public Alert checkAlert(PatientRecord record) {
        double val = record.getMeasurementValue();
        String id = String.valueOf(record.getPatientId());

        if (record.getRecordType().equalsIgnoreCase("ecg") && val >= ECG_TRESHOLD){
            return alertFactory.createAlert(id, "ECG ABOVE AVERAGE!", record.getTimestamp());
        }
        return null;
    }
}
