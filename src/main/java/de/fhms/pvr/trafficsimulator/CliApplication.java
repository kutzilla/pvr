package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CliApplication {

    private static final Logger LOG = LogManager.getLogger(CliApplication.class);

    public static void main(String[] args) {
        int trackAmount;
        int sectionAmount;
        int iterations;
        double p0;
        double p;
        double rho;
        double c;
        int workerAmount;
        int taskAmount;
        if (args.length == 9) {
            trackAmount = Integer.valueOf(args[0]);
            sectionAmount = Integer.valueOf(args[1]);
            iterations = Integer.valueOf(args[2]);
            p0 = Double.valueOf(args[3]);
            p = Double.valueOf(args[4]);
            c = Double.valueOf(args[5]);
            rho = Double.valueOf(args[6]);
            workerAmount = Integer.valueOf(args[7]);
            taskAmount = Integer.valueOf(args[8]);
        } else {
            trackAmount = 2;
            sectionAmount = 1000;
            iterations = 1000;
            p0 = 0.2;
            p = 0.2;
            c = 0.5;
            rho = 0.4;
            workerAmount = 1;
            taskAmount = 1;
        }

        TrafficSimulatorBuilder builder = new TrafficSimulatorBuilder(trackAmount, sectionAmount);
        TrafficSimulator trafficSimulator = builder.withSlowDawdleProbability(p0)
                .withFastDawdleProbability(p).withSwitchProbability(c).withRelativeVehicleDensity(rho)
                .withWorkerAmount(workerAmount).withTaskAmount(taskAmount).build();
        for (int i = 0; i < iterations; i++) {
            trafficSimulator.iterate();
        }

        trafficSimulator.shutdown();

        TimeMeasureController controller = trafficSimulator.getTimeMeasureController();

        LOG.info("Sigma:\t" + controller.getMeasuredTimeFor(TimeMeasureType.SIGMA));

        LOG.info("Phi:\t" + controller.getMeasuredTimeFor(TimeMeasureType.PHI));

        LOG.info("Kappa:\t" + controller.getMeasuredTimeFor(TimeMeasureType.KAPPA));
    }

}
