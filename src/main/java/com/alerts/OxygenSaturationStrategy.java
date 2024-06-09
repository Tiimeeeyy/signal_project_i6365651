package com.alerts;

import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy{
    private static final double O_SATURATION = 0.92;
    private final BloodOxygenAlertFactory alertFactory;

    public OxygenSaturationStrategy(BloodOxygenAlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }
    @Override
    public Alert checkAlert(PatientRecord record) {
        double val = record.getMeasurementValue();
        String id = String.valueOf(record.getPatientId());

        if(record.getRecordType().equalsIgnoreCase("saturation") && val< O_SATURATION){
            return alertFactory.createAlert(id, "OXYGEN SATURATION TOO LOW", record.getTimestamp());
        }
        return null;
    }
}
