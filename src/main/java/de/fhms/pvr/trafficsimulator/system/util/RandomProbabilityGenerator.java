package de.fhms.pvr.trafficsimulator.system.util;

import java.util.SplittableRandom;

public class RandomProbabilityGenerator {

    private static final SplittableRandom randomGenerator = new SplittableRandom();

    public static boolean generate(double probability) {
        return randomGenerator.nextDouble() >= 1.0 - probability;
    }
}
