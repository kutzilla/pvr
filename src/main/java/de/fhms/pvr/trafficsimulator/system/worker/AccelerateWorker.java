package de.fhms.pvr.trafficsimulator.system.worker;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

public class AccelerateWorker extends Thread {

    private Vehicle[][] street;

    private int lowerBorder;

    private int upperBorder;

    public AccelerateWorker(Vehicle[][] street, int lowerBorder, int upperBorder) {
        this.street = street;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
    }

    @Override
    public void run() {
        Vehicle tmp;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x < upperBorder; x++) {
                if ((tmp = street[y][x]) != null) {
                    tmp.incrementCurrentSpeed();
                }
            }
        }
    }
}
