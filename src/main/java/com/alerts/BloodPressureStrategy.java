package com.alerts;

import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy{
    private static final int SYSTOLIC_HI = 180;
    private static final int SYSTOLIC_LO = 90;
    private static final int DIASTOLIC_HI = 120;
    private static final int DIASTOLIC_LO = 60;
    private final BloodPressureAlertFactory alertFactory;

    public BloodPressureStrategy(BloodPressureAlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public Alert checkAlert(PatientRecord record) {
        double val = record.getMeasurementValue();
        String id = String.valueOf(record.getPatientId());
        String type = record.getRecordType().toLowerCase();

        if (type.equalsIgnoreCase("systolicpressure")) {
            if (val <= SYSTOLIC_LO) {
                return alertFactory.createAlert(id, "SYSTOLIC TOO LOW", record.getTimestamp());
            } else if (val >= SYSTOLIC_HI) {
                return alertFactory.createAlert(id, "SYSTOLIC TOO HIGH", record.getTimestamp());
            }
        } else if (type.equalsIgnoreCase("diastolicpressure")) {
            if (val <= DIASTOLIC_LO) {
                return alertFactory.createAlert(id, "DIASTOLIC TOO LOW", record.getTimestamp());
            } else if (val >= DIASTOLIC_HI) {
                return alertFactory.createAlert(id, "DIASTOLIC TOO HIGH", record.getTimestamp());
            }
        }
        return null;
    }
}