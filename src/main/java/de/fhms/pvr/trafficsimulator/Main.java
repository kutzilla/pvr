package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {

    public static void main(String[] args) {
        int trackAmount = 1;
        int sectionAmount = 20;
        double p0 = 0.2;
        double p = 0.2;
        double rho = 0.4;
        double c = 0.5;
        int threadAmount = 1;


        TrafficSimulator trafficSimulator = new TrafficSimulator(trackAmount, sectionAmount, rho, p0, p, c, threadAmount);
        for (int i = 0; i < 10; i++) {
            trafficSimulator.iterate();
        }

        System.out.println("Spur wechseln:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.TRACK_SWITCHING) + "ms");
        System.out.println("Beschleunigen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.ACCELERATION) + "ms");
        System.out.println("Bremsen:\t\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.DECELERATION) + "ms");
        System.out.println("Trödeln:\t\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.DAWDLING) + "ms");
        System.out.println("Fortbewegen:\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.MOVEMENT) + "ms");
        System.out.println("\r\nGesamt:\t\t\t" + trafficSimulator.getTimeMeasureController()
                .getMeasuredTimeFor(TimeMeasureType.ITERATION) + "ms");
    }

}
