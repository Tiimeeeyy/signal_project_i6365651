package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The interface Patient data generator.
 */
public interface PatientDataGenerator {
    /**
     * Generate.
     *
     * @param patientId      the patient id
     * @param outputStrategy the output strategy
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
