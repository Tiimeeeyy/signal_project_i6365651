package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    // We declare the thresholds here to make them easily editable and
    // reduce hardcoding variables
    private static final int SYSTOLIC_HI = 180;
    private static final int SYSTOLIC_LO = 90;
    private static final int DIASTOLIC_HI = 120;
    private static final int DIASTOLIC_LO = 60;
    private static final int BP_DIFFERENCE = 10;
    private static final double O_SATURATION = 0.92;
    private static final double O_DROP = 0.5;
    private static final int HEART_RATE_LO = 50;
    private static final int HEART_RATE_HI = 100;
    // We're using a logger for more comprehensive outputs
    private static final Logger LOGGER = Logger.getLogger(AlertGenerator.class.getName());
    private final DataStorage dataStorage;


    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    private void evaluateAllPatients() {
        for (Patient patient : dataStorage.getAllPatients()) {
            evaluateData(patient);
        }
    }

    public List<PatientRecord> getAllRecordsForPatient(Patient patient) {
        int id = patient.getPatientId();

        return dataStorage.getRecords(id, 0, Long.MAX_VALUE);

    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> patientData = getAllRecordsForPatient(patient);
        for (PatientRecord record : patientData) {
            String type = record.getRecordType().toLowerCase();
            if (type.equals("systolicpressure")) {
                checkSystolicBloodPressure(record);
            } else if (type.equals("diastolicpressure")) {
                checkDiastolicBloodPressure(record);
            } else if (type.equals("saturation")) {
                checkBloodSaturation(record);
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        LOGGER.warning("ALERT TRIGGERED: " + alert.getCondition() + " PATIENT" + alert.getPatientId() + " AT TIME" + alert.getTimestamp());
    }

    public void checkSystolicBloodPressure(PatientRecord patientRecord) {
        //  Logic: If blood pressure exceeds call trigger Alert

        double val = patientRecord.getMeasurementValue();
        String id = String.valueOf(patientRecord.getPatientId());
        if (val <= SYSTOLIC_LO) {
            Alert alert = new Alert(id, "SYSTOLIC TOO LOW", patientRecord.getTimestamp());
            triggerAlert(alert);
        } else if (val >= SYSTOLIC_HI) {
            Alert alert = new Alert(id, "SYSTOLIC TOO HIGH", patientRecord.getTimestamp());
            triggerAlert(alert);
        }

    }

    public void checkDiastolicBloodPressure(PatientRecord patientRecord) {

        double val = patientRecord.getMeasurementValue();
        String id = String.valueOf(patientRecord.getPatientId());
        if (val <= DIASTOLIC_LO) {
            // Trigger if under threshold:
            triggerAlert(new Alert(id, "DIASTOLIC TOO LOW", patientRecord.getTimestamp()));

        } else if (val >= DIASTOLIC_HI) {
            //Trigger if over threshold:
            triggerAlert(new Alert(id, "DIASTOLIC TOO HIGH", patientRecord.getTimestamp()));
        }

    }

    public void checkBloodSaturation(PatientRecord patientRecord) {

        double val = patientRecord.getMeasurementValue();
        String id = String.valueOf(patientRecord);

        if (val < O_SATURATION) {
            triggerAlert(new Alert(id, "O SATURATION IS TOO LOW", patientRecord.getTimestamp()));
        }

    }

    public void checkECG(PatientRecord record) {

    }

    public void checkCombined(PatientRecord record) {

    }

}
