package com.alerts;
import com.data_management.PatientRecord;

public interface AlertStrategy {
    Alert checkAlert(PatientRecord record);
}
