package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import de.fhms.pvr.trafficsimulator.system.util.StreetConfigurationParser;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CliApplication {

    private static final Logger LOG = LogManager.getLogger(CliApplication.class);


    private static final double SWITCH_PROBABILITY = 0.5;

    private static final double SLOW_DAWDLE_PROBABILTY = 0.2;

    private static final double FAST_DAWDLE_PROBABILTY = 0.2;


    public static void main(String[] args) {

        String streetConfigurationsPath = null;
        if (args.length == 1) {
            streetConfigurationsPath = args[0];
        }

        File resultsFile = new File("results.csv");
        if (resultsFile.exists()) {
            System.exit(-1);
        }
        try {
            resultsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int runtimes = 3;
        ArrayList<Pair<Integer, Integer>> workerAndTask = new ArrayList<Pair<Integer, Integer>>();
        workerAndTask.add(Pair.of(1, 1));
        workerAndTask.add(Pair.of(1, 50));
        workerAndTask.add(Pair.of(1, 100));
        workerAndTask.add(Pair.of(2, 2));
        workerAndTask.add(Pair.of(2, 50));
        workerAndTask.add(Pair.of(2, 100));
        workerAndTask.add(Pair.of(4, 4));
        workerAndTask.add(Pair.of(4, 50));
        workerAndTask.add(Pair.of(4, 100));
        workerAndTask.add(Pair.of(8, 8));
        workerAndTask.add(Pair.of(8, 50));
        workerAndTask.add(Pair.of(8, 100));
        workerAndTask.add(Pair.of(12, 12));
        workerAndTask.add(Pair.of(12, 50));
        workerAndTask.add(Pair.of(12, 100));
        workerAndTask.add(Pair.of(16, 16));
        workerAndTask.add(Pair.of(16, 50));
        workerAndTask.add(Pair.of(16, 100));


        int iterations = 100;

        TrafficSimulatorBuilder builder;

        TrafficSimulator trafficSimulator;


        File file = new File(streetConfigurationsPath);
        for (File f : file.listFiles()) {
            System.out.println();


            builder = new TrafficSimulatorBuilder(f);
            builder.withSwitchProbability(SWITCH_PROBABILITY)
                    .withSlowDawdleProbability(SLOW_DAWDLE_PROBABILTY)
                    .withFastDawdleProbability(FAST_DAWDLE_PROBABILTY);

            for (Pair<Integer, Integer> workerTask: workerAndTask) {
                builder.withWorkerAmount(workerTask.getLeft())
                        .withTaskAmount(workerTask.getRight()).withAbsoluteVehicleDensity(countVehicles(f));
                for (int j = 0; j < runtimes; j++) {
                    trafficSimulator = builder.build();
                    for (int k = 0; k < iterations; k++) {
                        trafficSimulator.iterate();
                    }
                    writeResults(trafficSimulator, resultsFile);
                    trafficSimulator.shutdown();
                }


            }

        }

        ArrayList<Triple<Double, Double, Double>> probabilities = new ArrayList<Triple<Double, Double, Double>>();
        probabilities.add(Triple.of(0.15, 0.15, 0.2));
        probabilities.add(Triple.of(0.15, 0.15, 0.5));
        probabilities.add(Triple.of(0.15, 0.15, 0.8));

        probabilities.add(Triple.of(0.15, 0.15, 0.5));
        probabilities.add(Triple.of(0.15, 0.30, 0.5));
        probabilities.add(Triple.of(0.30, 0.15, 0.5));
        probabilities.add(Triple.of(0.30, 0.30, 0.5));


        File millionSectionsFile = new File(streetConfigurationsPath + "/t2_s1000000_r040.csv");
        builder = new TrafficSimulatorBuilder(millionSectionsFile);
        builder.withWorkerAmount(16).withTaskAmount(100).withAbsoluteVehicleDensity(countVehicles(millionSectionsFile));
        for (Triple<Double, Double, Double> p : probabilities) {
            builder.withSlowDawdleProbability(p.getLeft())
                    .withFastDawdleProbability(p.getMiddle()).withSwitchProbability(p.getRight());
            for (int i = 0; i < runtimes; i++) {
                trafficSimulator = builder.build();
                for (int j = 0; j < iterations; j++) {
                    trafficSimulator.iterate();
                }
                writeResults(trafficSimulator, resultsFile);
                trafficSimulator.shutdown();
            }
        }
    }



    private static void writeResults(TrafficSimulator trafficSimulator, File file) {
        TimeMeasureController timeMeasureController = trafficSimulator.getTimeMeasureController();
        LOG.info(trafficSimulator.getSectionAmount() + "," + trafficSimulator.getTrackAmount() + ","
                + trafficSimulator.getVehicleAmount() + "," + trafficSimulator.getWorkerAmount()
                + "," + trafficSimulator.getTaskAmount() + "," + timeMeasureController.getMeasuredTimeFor(TimeMeasureType.SIGMA)
                + "," + timeMeasureController.getMeasuredTimeFor(TimeMeasureType.PHI) + ","
                + timeMeasureController.getMeasuredTimeFor(TimeMeasureType.KAPPA));

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(String.valueOf(trafficSimulator.getSectionAmount()) + "," + String.valueOf(trafficSimulator.getTrackAmount()) + ","
                    + String.valueOf(trafficSimulator.getVehicleAmount()) + "," + String.valueOf(trafficSimulator.getWorkerAmount())
                    + "," + String.valueOf(trafficSimulator.getTaskAmount()) + "," + String.valueOf(timeMeasureController.getMeasuredTimeFor(TimeMeasureType.SIGMA))
                    + "," + String.valueOf(timeMeasureController.getMeasuredTimeFor(TimeMeasureType.PHI)) + ","
                    + String.valueOf(timeMeasureController.getMeasuredTimeFor(TimeMeasureType.KAPPA)) + "\r\n");
            writer.close();
        } catch (IOException e) {
            LOG.fatal(e.getMessage());
        }

    }

    private static int countVehicles(File file) {
        Vehicle[][] street;
        try {
            street = StreetConfigurationParser.parseStreetConfigurationFrom(file);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        int count = 0;
        for (int i = 0; i < street.length; i++) {
            for (int j = 0; j < street[i].length; j++) {
                if (street[i][j] != null) {
                    count++;
                }
            }
        }
        return count;
    }

}
