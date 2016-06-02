package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CliApplication {

    private static final Logger LOG = LogManager.getLogger(CliApplication.class);

    public static void main(String[] args) {
        int trackAmount = 2;
        int sectionAmount = 1000;
        double p0 = 0.2;
        double p = 0.2;
        double rho = 0.4;
        double c = 0.5;
        int workerAmount = 4;
        int taskAmount = 4;
        TrafficSimulatorBuilder builder = new TrafficSimulatorBuilder(trackAmount, sectionAmount, rho);
        TrafficSimulator trafficSimulator = builder.withSlowDawdleProbability(p0)
                .withFastDawdleProbability(p).withSwitchProbability(c)
                .withWorkerAmount(workerAmount).withTaskAmount(taskAmount).build();
        for (int i = 0; i < 1000; i++) {
            trafficSimulator.iterate();
        }
        LOG.info("Aktionen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.DRIVE_ACTION) + "ms");
        LOG.info("Bewegung:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.MOVEMENT) + "ms");
        LOG.info("Iterationen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.ITERATION) + "ms");
    }

}
