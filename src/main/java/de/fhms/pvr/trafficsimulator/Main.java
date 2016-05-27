package de.fhms.pvr.trafficsimulator;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {

    public static void main(String[] args) {
        int trackAmount = 3;
        int sectionAmount = 100000;
        double p0 = 0.2;
        double p = 0.2;
        double rho = 0.5;
        double c = 0.4;
        int threadAmount = 2;

        TrafficSimulator trafficSimulator = new TrafficSimulator(trackAmount, sectionAmount, rho, p0, p, c, threadAmount);
        for (int i = 0; i < 20; i++) {
            trafficSimulator.iterate();
            //printField(trafficSimulator.getStreet());
        }
        System.out.println(ansi().reset());
        System.out.println("Spur wechseln:\t" + trafficSimulator.getTotalSwitchingTrackTime() + "ms");
        System.out.println("Beschleunigen:\t" + trafficSimulator.getTotalAccelerateTime() + "ms");
        System.out.println("Bremsen:\t\t" + trafficSimulator.getTotalDecelerationTime() + "ms");
        System.out.println("Trödeln:\t\t" + trafficSimulator.getTotalDawdleTime() + "ms");
        System.out.println("Fortbewegen:\t" + trafficSimulator.getTotalMovementTime() + "ms");
        System.out.println("\r\nGesamt:\t\t\t" + trafficSimulator.getTotalSimulationTime() + "ms");
    }

    private static void printField(Vehicle[][] field) {
        String symbol = "▩";
        Vehicle tmp;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                tmp = field[i][j];
                System.out.print(ansi().eraseScreen());
                if (tmp != null && tmp.getCurrentSpeed() >= 0) {
                    switch (tmp.getCurrentSpeed()) {
                        case 0:
                            System.out.print(ansi().fgRed().a(symbol));
                            break;
                        case 1:
                            System.out.print(ansi().fgMagenta().a(symbol));
                            break;
                        case 2:
                            System.out.print(ansi().fgYellow().a(symbol));
                            break;
                        case 3:
                            System.out.print(ansi().fgGreen().a(symbol));
                            break;
                        case 4:
                            System.out.print(ansi().fgBlue().a(symbol));
                            break;
                        case 5:
                            System.out.print(ansi().fgCyan().a(symbol));
                            break;
                    }
                } else {
                    System.out.print(ansi().fgBlack().a(symbol));
                }
            }
            System.out.print("\t\t");
        }
        System.out.println();
    }
}
