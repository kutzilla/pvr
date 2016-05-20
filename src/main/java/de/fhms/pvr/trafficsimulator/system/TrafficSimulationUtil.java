package de.fhms.pvr.trafficsimulator.system;

import java.util.Random;

public class TrafficSimulationUtil {

    public static boolean isCarPixelLindering(double probability) {
        Random randomGenerator = new Random();
        double randomDouble = ((double) randomGenerator.nextInt(100)) / 100;
        if (randomDouble <= probability) {
            return true;
        } else {
            return false;
        }
    }
}
