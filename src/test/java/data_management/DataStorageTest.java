package data_management;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;


import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataStorageTest {
    private DataStorage dataStorage;

    @BeforeEach
    public void setup() {
        dataStorage = new DataStorage();
    }

    @Test
    void addPatientData_shouldAddNewPatientData() {
        dataStorage.addPatientData(1, 100.0, "HeartRate", 1714376789050L);
        assertEquals(1, dataStorage.getAllPatients().size());
    }

    @Test
    void addPatientData_shouldUpdateExistingPatientData() {
        dataStorage.addPatientData(1, 100.0, "HeartRate", 1714376789050L);
        dataStorage.addPatientData(1, 200.0, "HeartRate", 1714376789051L);
        assertEquals(1, dataStorage.getAllPatients().size());
    }

    @Test
    void getRecords_shouldReturnEmptyListForNonexistentPatient() {
        assertTrue(dataStorage.getRecords(1, 1700000000000L, 1800000000000L).isEmpty());
    }

    @Test
    void getRecords_shouldReturnRecordsWithinTimeRange() {
        dataStorage.addPatientData(1, 100.0, "HeartRate", 1714376789050L);
        dataStorage.addPatientData(1, 200.0, "HeartRate", 1714376789051L);
        assertEquals(2, dataStorage.getRecords(1, 1700000000000L, 1800000000000L).size());
    }

    @Test
    void getRecords_shouldReturnEmptyListForRecordsOutsideTimeRange() {
        dataStorage.addPatientData(1, 100.0, "HeartRate", 1714376789050L);
        dataStorage.addPatientData(1, 200.0, "HeartRate", 1714376789051L);
        assertTrue(dataStorage.getRecords(1, 1800000000000L, 1900000000000L).isEmpty());
    }
}