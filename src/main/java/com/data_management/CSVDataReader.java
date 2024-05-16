package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVDataReader implements DataReader {

    private final String filePath;

    public CSVDataReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * This Method reads patient data from a CSV file and stores it.
     * @param dataStorage the storage where data will be stored
     * @throws IOException if the buffered reader fails to read a file
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Since we assume a CSV, we can say the regex splitter is a comma:
                String[] values = line.split(",");
                // Here we convert all the information in the CSV file and parse it to
                // appropriate file types:
                int patientID = Integer.parseInt(values[0]);
                double measurementValue = Double.parseDouble(values[1]);
                String recordType = values[2];
                long timestamp = Long.parseLong(values[3]);
                // After reading the input and assigning variables, we add the patient to the
                // Database using the addPatientData Method, which takes all the
                // previously assigned variables as input.
                dataStorage.addPatientData(patientID, measurementValue, recordType, timestamp);
            }
        }
    }
}
