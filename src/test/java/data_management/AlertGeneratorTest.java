package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class AlertGeneratorTest {
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    public void setup() {
        dataStorage = mock(DataStorage.class);
        alertGenerator = new AlertGenerator(dataStorage);
    }

    @Test
    void evaluateData_shouldTriggerAlert_whenSystolicPressureIsTooLow() {
        Patient patient = new Patient(1);
        PatientRecord record = new PatientRecord(1, 80.0, "systolicpressure", 1622544000L);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(record));

        alertGenerator.evaluateData(patient);

        verify(dataStorage, times(2)).getRecords(anyInt(), anyLong(), anyLong());
    }

    @Test
    void evaluateData_shouldTriggerAlert_whenDiastolicPressureIsTooHigh() {
        Patient patient = new Patient(1);
        PatientRecord record = new PatientRecord(1, 130.0, "diastolicpressure", 1622544000L);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(record));

        alertGenerator.evaluateData(patient);

        verify(dataStorage, times(2)).getRecords(anyInt(), anyLong(), anyLong());
    }

    @Test
    void evaluateData_shouldTriggerAlert_whenBloodSaturationIsTooLow() {
        Patient patient = new Patient(1);
        PatientRecord record = new PatientRecord(1, 0.8, "saturation", 1622544000L);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(record));

        alertGenerator.evaluateData(patient);

        verify(dataStorage, times(2)).getRecords(anyInt(), anyLong(), anyLong());
    }

    @Test
    public void evaluateData_shouldTriggerAlert_whenECGIsOverAverage() {
        Patient patient = new Patient(1);
        PatientRecord record = new PatientRecord(1, 0.4, "ecg", 1622544000L);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(record));

        alertGenerator.evaluateData(patient);

        verify(dataStorage, times(2)).getRecords(anyInt(), anyLong(), anyLong());
    }

    @Test
    void evaluateData_shouldTriggerAlert_whenHypotensiveHypoxemiaOccurs() {
        Patient patient = new Patient(1);
        PatientRecord systolicRecord = new PatientRecord(1, 80.0, "systolicpressure", 1622544000L);
        PatientRecord saturationRecord = new PatientRecord(1, 0.8, "saturation", 1622544000L);
        when(dataStorage.getRecords(anyInt(), anyLong(), anyLong())).thenReturn(Arrays.asList(systolicRecord, saturationRecord));

        alertGenerator.evaluateData(patient);

        verify(dataStorage, times(2)).getRecords(anyInt(), anyLong(), anyLong());
    }
}

