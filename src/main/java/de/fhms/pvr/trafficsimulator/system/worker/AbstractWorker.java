package de.fhms.pvr.trafficsimulator.system.worker;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

public abstract class AbstractWorker extends Thread {

    protected Vehicle[][] street;

    protected int lowerBorder;

    protected int upperBorder;

    AbstractWorker(Vehicle[][] street, int lowerBorder, int upperBorder) {
        this.street = street;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
    }
}
