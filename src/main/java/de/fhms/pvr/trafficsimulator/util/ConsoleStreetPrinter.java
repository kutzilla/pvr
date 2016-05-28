package de.fhms.pvr.trafficsimulator.util;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleStreetPrinter {

    private static final String symbol = "▩";

    public static void printStreet(Vehicle[][] field) {
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
        System.out.println(ansi().reset());
    }
}
