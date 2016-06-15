package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import de.fhms.pvr.trafficsimulator.system.util.StreetConfigurationParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        int[] worker = {1, 2, 4, 8, 12};
        int[] tasks = {1, 4, 8, 16, 24};
        int iterations = 1000;

        TrafficSimulatorBuilder builder;

        TrafficSimulator trafficSimulator;



        File file = new File(streetConfigurationsPath);
        for (File f: file.listFiles()) {
            System.out.println();


            try {
                Vehicle[][] street = StreetConfigurationParser.parseStreetConfigurationFrom(f);
                builder = new TrafficSimulatorBuilder(street);
                builder.withSwitchProbability(SWITCH_PROBABILITY)
                        .withSlowDawdleProbability(SLOW_DAWDLE_PROBABILTY)
                        .withFastDawdleProbability(FAST_DAWDLE_PROBABILTY);

                for (int i = 0; i < worker.length; i++) {
                    trafficSimulator = builder.withWorkerAmount(worker[i])
                            .withAbsoluteVehicleDensity(countVehicles(street)).withTaskAmount(tasks[i]).build();

                    for (int j = 0; j < runtimes; j++) {
                        trafficSimulator = builder.build();
                        for (int k = 0; k < iterations; k++) {
                            trafficSimulator.iterate();
                        }
                        writeResults(trafficSimulator, resultsFile);
                        trafficSimulator.shutdown();
                    }


                }




            } catch (IOException e) {
                e.printStackTrace();
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
            writer = new BufferedWriter(new FileWriter(file,true));
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

    private static int countVehicles(Vehicle[][] street) {
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
