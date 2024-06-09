package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//public class CSVDataReader implements DataReader {
//
//
//    private final String filePath;
//
//    public CSVDataReader(String filePath) {
//        this.filePath = filePath;
//    }
//
//    /**
//     * This Method reads patient data from a CSV file and stores it.
//     * @param dataStorage the storage where data will be stored
//     * @throws IOException if the buffered reader fails to read a file
//     */
//    public void readData(DataStorage dataStorage) throws IOException {
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                // We first split by regex ", " since we have a CSV file
//                String[] values = line.split(", ");
//                // We then split by ": " to separate the label from the value,
//                // We then assign the values to variables and then add it to the record
//                int patientID = Integer.parseInt(values[0].split(": ")[1]);
//
//                long timestamp = Long.parseLong(values[1]. split(": ")[1]);
//
//                String recordType = values[2].split(": ")[1];
//
//                double measurementValue = Double.parseDouble(values[3].split(": ")[1]);
//
//                dataStorage.addPatientData(patientID, measurementValue, recordType, timestamp);
//            }
//        }
//    }
//}
