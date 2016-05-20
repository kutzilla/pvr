package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;

public class Main {

    public static void main(String[] args) {
        int trackAmount = 1;
        int sectionAmount = 20;
        double p0 = 0.5;
        double p = 0.2;
        double rho = 0.5;

        TrafficSimulator trafficSimulator = new TrafficSimulator(trackAmount, sectionAmount, rho, p0, p);
        for (int i = 0; i < 200; i++) {
            trafficSimulator.iterate();
            trafficSimulator.printStreetField();
        }
    }
}
