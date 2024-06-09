package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    /**
     * Gets all records available for a given patient.
     * @param patient The patient to get the records for.
     * @return An array list containing all the records for the patient.
     */
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
        evaluateProcedurallyDiastolic(patientData);
        evaluateProcedurallySystolic(patientData);
        checkHypotensiveHypoxemia(patient);
        for (PatientRecord record : patientData) {
            String type = record.getRecordType().toLowerCase();
            if (type.equals("systolicpressure")) {
                checkSystolicBloodPressure(record);
            } else if (type.equals("diastolicpressure")) {
                checkDiastolicBloodPressure(record);
            } else if (type.equals("saturation")) {
                checkBloodSaturation(record);
            } else if (type.equals("ecg")) {
                checkECG(record);
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
        LOGGER.warning("ALERT TRIGGERED: " + alert.getCondition() + " PATIENT " + alert.getPatientId() + " AT TIME " + alert.getTimestamp());
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

    /**
     * Checks if a patient exceeds safe limits for Diastolic blood pressure
     * @param patientRecord The patient record to be evaluated.
     */
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

    /**
     * Checks the blood saturation of the patient to be evaluated.
     * @param patientRecord The patient record to be evaluated.
     */
    public void checkBloodSaturation(PatientRecord patientRecord) {

        double val = patientRecord.getMeasurementValue();
        String id = String.valueOf(patientRecord);

        if (val < O_SATURATION) {
            triggerAlert(new Alert(id, "O SATURATION IS TOO LOW", patientRecord.getTimestamp()));
        }

    }

    /**
     * Checks the ECG Data of a given patient record. We set the threshold to be 0.3.
     * @param record The patient record to be evaluated
     */
    public void checkECG(PatientRecord record) {
        double val = record.getMeasurementValue();
        String id = String.valueOf(record.getPatientId());
        double average = 0.3;
        if (val >= average) {
            triggerAlert(new Alert(id, "ECG over average! please take action", record.getTimestamp()));
        }


    }

    /**
     * Procedurally evaluates the patient data checking for Diastolic pressure.
     * @param recordList The list of Diastolic records for the patient.
     */
    public void evaluateProcedurallyDiastolic(List<PatientRecord> recordList) {
        List<PatientRecord> systolicRecords = recordList.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase("systolicpressure"))
                .collect(Collectors.toList());
        // Sort the List based on the timestamps
        systolicRecords.sort(Comparator.comparing(PatientRecord::getTimestamp));

        // Initialise empty double to store the previous measurement later
        double previousMeasurement = Double.NaN;

        for (PatientRecord record : systolicRecords) {
            if (!Double.isNaN(previousMeasurement)) {
                double difference = Math.abs(record.getMeasurementValue() - previousMeasurement);
                if (difference > BP_DIFFERENCE) {
                    triggerAlert(new Alert(String.valueOf(record.getPatientId()), "Systolic blood pressure difference exceeds threshold! ", record.getTimestamp()));

                }
            }
            previousMeasurement = record.getMeasurementValue();
        }
    }

    /**
     * Procedurally evaluates the patient data checking for Systolic pressure.
     * @param recordList The list of Systolic records for the patient
     */
    private void evaluateProcedurallySystolic(List<PatientRecord> recordList) {
        List<PatientRecord> diastolicRecords = recordList.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase("diastolicpressure"))
                .collect(Collectors.toList());
        // Sort the List based on the timestamps
        diastolicRecords.sort(Comparator.comparing(PatientRecord::getTimestamp));

        // Initialise empty double to store the previous measurement later
        double previousMeasurement = Double.NaN;

        for (PatientRecord record : diastolicRecords) {
            if (!Double.isNaN(previousMeasurement)) {
                double difference = Math.abs(record.getMeasurementValue() - previousMeasurement);
                if (difference > BP_DIFFERENCE) {
                    triggerAlert(new Alert(String.valueOf(record.getPatientId()), "Diastolic blood pressure difference exceeds threshold! ", record.getTimestamp()));

                }
            }
            previousMeasurement = record.getMeasurementValue();
        }
    }

    /**
     * Checks for a Hypotensive Hypoxemia alert
     * @param patient The patient to be evaluated.
     */
    public void checkHypotensiveHypoxemia(Patient patient) {
        List<PatientRecord> patientData = getAllRecordsForPatient(patient);
        List<PatientRecord> systolicRecords = patientData.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase("systolicpressure"))
                .sorted(Comparator.comparing(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
        List<PatientRecord> saturationRecords = patientData.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase("saturation"))
                .sorted(Comparator.comparing(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        int i = 0, j = 0;
        while (i < systolicRecords.size() && j < saturationRecords.size()) {
            PatientRecord systolicRecord = systolicRecords.get(i);
            PatientRecord saturationRecord = saturationRecords.get(j);
            if (systolicRecord.getTimestamp() == saturationRecord.getTimestamp()) {
                if (systolicRecord.getMeasurementValue() < 90 || saturationRecord.getMeasurementValue() < 0.92) {
                    if (systolicRecord.getMeasurementValue() < 90 && saturationRecord.getMeasurementValue() < 0.92) {
                        triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia Alert", systolicRecord.getTimestamp()));
                    }
                }
                i++;
                j++;
            } else if (systolicRecord.getTimestamp() < saturationRecord.getTimestamp()) {
                i++;
            } else {
                j++;
            }
        }
    }
}
