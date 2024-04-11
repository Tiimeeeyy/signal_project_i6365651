package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

import java.util.Random;

/**
 * The type Alert generator.
 */
public class AlertGenerator implements PatientDataGenerator {

    /**
     * The constant randomGenerator.
     */
    public static final Random randomGenerator = new Random();
    private boolean[] AlertStates; // false = resolved, true = pressed

    /**
     * Instantiates a new Alert generator.
     *
     * @param patientCount the patient count
     */
    public AlertGenerator(int patientCount) {
        AlertStates = new boolean[patientCount + 1];
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (AlertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    AlertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Uppercase names only for classes!
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    AlertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
