package com.cardio_generator.outputs;

/**
 * The interface Output strategy.
 */
public interface OutputStrategy {
    /**
     * Output.
     *
     * @param patientId the patient id
     * @param timestamp the timestamp
     * @param label     the label
     * @param data      the data
     */
    void output(int patientId, long timestamp, String label, String data);
}
