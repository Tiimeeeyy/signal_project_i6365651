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

    private final AlertStrategy bloodPressureStrategy;
    private final AlertStrategy oxygenSaturationStrategy;
    private final AlertStrategy ecgStrategy;

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
        // Initializing strategies with factory subclasses

        this.bloodPressureStrategy = new BloodPressureStrategy(new BloodPressureAlertFactory());
        this.oxygenSaturationStrategy = new OxygenSaturationStrategy(new BloodOxygenAlertFactory());
        this.ecgStrategy = new ECGStrategy(new ECGAlertFactory());
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
            Alert alert = null;
            String type = record.getRecordType().toLowerCase();
            if (type.equals("systolicpressure") || type.equals("diastolicpressure")) {
                alert = bloodPressureStrategy.checkAlert(record);
            } else if (type.equals("saturation")) {
                alert = oxygenSaturationStrategy.checkAlert(record);
            } else if (type.equals("ecg")) {
                alert = ecgStrategy.checkAlert(record);
            }
            if (alert != null) {
                triggerAlert(alert);
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

    boolean checkAlert(Alert alert, PatientRecord record){
        String type = record.getRecordType().toLowerCase();
        double val = record.getMeasurementValue();

        switch(type) {
            case "systolicpressure":
                return val <= SYSTOLIC_LO || val >= SYSTOLIC_HI;
            case "diastolicpressure":
                return val <= DIASTOLIC_LO || val >= DIASTOLIC_LO;
            case "saturation":
                return val < O_SATURATION;
            case "ecg":
                return val >=  0.3;
            default:
                return false;
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
