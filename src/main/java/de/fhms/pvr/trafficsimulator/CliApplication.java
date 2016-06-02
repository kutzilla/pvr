package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
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
        TrafficSimulator.setThreadAmount(2);
        TrafficSimulator.setTaskAmount(25);
        TrafficSimulator trafficSimulator = new TrafficSimulator(trackAmount, sectionAmount, rho, p0, p, c);
        for (int i = 0; i < 1000; i++) {
            trafficSimulator.iterate();
        }


        LOG.info("Spur wechseln:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.TRACK_SWITCHING) + "ms");
        LOG.info("Beschleunigen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.ACCELERATION) + "ms");
        LOG.info("Bremsen:\t\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.DECELERATION) + "ms");
        LOG.info("Trödeln:\t\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.DAWDLING) + "ms");
        LOG.info("Fortbewegen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.MOVEMENT) + "ms");
        LOG.info("Iterationen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.ITERATION) + "ms");
    }

}
